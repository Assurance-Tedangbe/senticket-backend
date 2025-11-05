package sn.estm.managingrestauranttickets.services.serviceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.AccountDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CreationTicketsRequestDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode.QRCodeCryptoSecurity;
import sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode.QRCodeResponse;
import sn.estm.managingrestauranttickets.dto.customisedto.dtoforQRcode.QrCodeDataDTO;
import sn.estm.managingrestauranttickets.entities.Account;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.exceptions.*;
import sn.estm.managingrestauranttickets.mappers.AccountMapper;
import sn.estm.managingrestauranttickets.repositories.AccountRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.AccountService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;

import java.text.MessageFormat;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import org.apache.commons.codec.digest.HmacUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final AccountMapper accountMapper;
  private final TicketService ticketService;
  private final QRCodeCryptoSecurity cryptoSecurity;

  private static final int QR_CODE_WIDTH = 400;
  private static final int QR_CODE_HEIGHT = 400;
  private static final int QR_EXPIRY_SECONDS = 300; // 5 minutes
  private static final String QR_SECRET_KEY = "your-32-byte-secret-key-for-qr-codes-2024";

  @Override
  public AccountDTO createAccount(AccountDTO accountDto) {
      log.info("Creating account with details: {}", accountDto);

      Account account = accountMapper.toEntity(accountDto);
      
      Account savedAccount = accountRepository.save(account);

      log.info("Account created successfully with ID: {}", savedAccount.getAccountId());

      CreationTicketsRequestDTO creationTicketsRequestDTO = CreationTicketsRequestDTO.builder()
              .countA(5)
              .countB(5)
              .build();

      ticketService.createTickets(creationTicketsRequestDTO);

      return accountMapper.toDto(savedAccount);
    }


    @Override
    public List<AccountDTO> readAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public AccountDTO updateAccount(AccountDTO accountDto) {

        log.info("Updating account details: {}", accountDto);

        Account existingAccount = accountRepository.findById(accountDto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountDto.getAccountId())));

        existingAccount.setAccountNumber(accountDto.getAccountNumber());
        existingAccount.setBalance(accountDto.getBalance());
        existingAccount.setDateCreation(accountDto.getDateCreation());
        existingAccount.setActive(accountDto.isActive());

        Account updatedAccount = accountRepository.save(existingAccount);

        log.info("Account updated successfully with account number: {}", updatedAccount.getAccountNumber());
       
        return accountMapper.toDto(updatedAccount);
    }


    @Override
    public void deleteAccount(Long accountId) {
        log.info("Deleting account with accountId: {}", accountId);

        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException(MessageFormat.format(
              "Account not found with ID: {0}", accountId));
        }
        accountRepository.deleteById(accountId);

        log.info("deleteAccount end ok - accountId: {}", accountId);
    }


    @Override
    public AccountDTO readAccountById(Long accountId) {
        log.info("Reading account by accountId: {}", accountId);

        Account account = getAccountById(accountId);
        /*Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));*/

        return accountMapper.toDto(account);
    }


    @Override
    public void linkAccountToUser(Long accountId, Long userId) {
      log.info("Linking account {} to user with userId: {}", accountId, userId);

        Account account = getAccountById(accountId);
        /*Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));*/

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "User not found with ID: {0}", userId)));

       /* if (account.getUser() != null) {
            throw new IllegalArgumentException("Account is already linked to a user.");
        }*/

        /* check if user is already linked to another account */
        /* if (user.getAccount() != null) {
            throw new IllegalArgumentException("User is already linked to another account.");
        } */

        account.setUser(user);

        accountRepository.save(account);

        log.info("Account {} linked to user with userId: {}", accountId, userId);
    }


    @Override
    public void unlinkAccountFromUser(Long accountId, Long userId) {

      log.info("Unlinking account {} from user with userId: {}", accountId, userId);

        Account account = getAccountById(accountId);
        /*Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));*/
                  
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "User not found with ID: {0}", userId)));

        if (account.getUser() == null || !account.getUser().equals(user)) {
            throw new IllegalArgumentException(
              "Account does not have the specified user.");
        }
        account.setUser(null);

        accountRepository.save(account);
        
        log.info("Account {} unlinked from user with userId: {}", accountId, userId);
    }


    @Override
    public void updateBalance(Long accountId, Double newBalance) {
        log.info("Updating balance for account with accountId: {}", accountId);

        Account account = getAccountById(accountId);
       /* Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));*/

        account.setBalance(newBalance);

        log.info("Account balance updated successfully for account ID: {}", accountId);

        accountRepository.save(account);
    }


    @Override
    public void updateAccountNumber(Long accountId, String newAccountNumber) {
        log.info("Updating account number for account with accountId: {}", accountId);

        Account account = getAccountById(accountId);
        /*Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));*/

        account.setAccountNumber(newAccountNumber);

        log.info("Account number updated successfully for account ID: {}", accountId);

        accountRepository.save(account);
    }


    @Override
    public void activateAccount(Long accountId) {
        log.info("Activating account with accountId: {}", accountId);

        Account account = getAccountById(accountId);
        /*Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));*/

        account.setActive(true);

        accountRepository.save(account);

        log.info("Account activated successfully for account ID: {}", accountId);
    }


    @Override
    public void deactivateAccount(Long accountId) {
        log.info("Deactivating account with accountId: {}", accountId);

        Account account = getAccountById(accountId);
        /*Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                  "Account not found with ID: {0}", accountId)));*/

        account.setActive(false);

        accountRepository.save(account);
        
        log.info("Account deactivated successfully for account ID: {}", accountId);
    }

    @Override
    public void transferFunds(Long fromAccountId, Long toAccountId, Double amount) {
      
      log.info("Transferring {} from account ID {} to account ID {}",
       amount, fromAccountId, toAccountId);

      if (amount <= 0) {
        throw new IllegalArgumentException("Transfer amount must be positive.");
      }

      Account fromAccount = accountRepository.findById(fromAccountId)
          .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
            "Source account not found with ID: {0}", fromAccountId)));

      Account toAccount = accountRepository.findById(toAccountId)
          .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
            "Destination account not found with ID: {0}", toAccountId)));

      if (fromAccount.getBalance() < amount) {
        throw new IllegalArgumentException("Insufficient funds in the source account.");
      }

      fromAccount.setBalance(fromAccount.getBalance() - amount);

      toAccount.setBalance(toAccount.getBalance() + amount);

      accountRepository.save(fromAccount);
      
      accountRepository.save(toAccount);

      log.info("Transfer of {} from account ID {} to account ID {} completed successfully",
       amount, fromAccountId, toAccountId);
    }

    @Override
    public void cancelTransferFunds(Long fromAccountId, Long toAccountId, Double amount) {
      
      log.info("Cancelling transfer of {} from account ID {} to account ID {}",
       amount, fromAccountId, toAccountId);

      if (amount <= 0) {
        throw new IllegalArgumentException("Cancel amount must be positive.");
      }

      Account fromAccount = accountRepository.findById(fromAccountId)
          .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
            "Source account not found with ID: {0}", fromAccountId)));

      Account toAccount = accountRepository.findById(toAccountId)
          .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
            "Destination account not found with ID: {0}", toAccountId)));

      if (toAccount.getBalance() < amount) {
        throw new IllegalArgumentException(
          "Insufficient funds in the destination account to cancel the transfer.");
      }

      // Reverse the transfer
      toAccount.setBalance(toAccount.getBalance() - amount);
      fromAccount.setBalance(fromAccount.getBalance() + amount);

      accountRepository.save(toAccount);
      accountRepository.save(fromAccount);

      log.info("Cancelled transfer of {} from account ID {} to account ID {} successfully",
       amount, fromAccountId, toAccountId);
    }


    /**
     * generates a method that allows you to credit an account, in this context,
     * to top up the account. We have two input parameters: Long accountId and Double amount.
     * The method signature is what I highlighted
     */
    @Override
    public void creditAccount(Long accountId, Double amount) {
      log.info("Crediting account with accountId: {} amount: {}", accountId, amount);

      if (amount == null || amount <= 0) {
        throw new IllegalArgumentException("Credit amount must be positive.");
      }

        Account account = getAccountById(accountId);
      /*Account account = accountRepository.findById(accountId)
          .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
              "Account not found with ID: {0}", accountId)));*/

      if (!account.isActive()) {
        throw new IllegalArgumentException("Cannot credit an inactive account.");
      }

      Double currentBalance = account.getBalance() == null ? 0.0 : account.getBalance();
      account.setBalance(currentBalance + amount);

      accountRepository.save(account);

      log.info("Account {} credited with {}. New balance: {}", 
      accountId, amount, account.getBalance());
    }


    /**
     * Generates a method that allows you to cancel the top-up made to an account.
     * We have two input parameters: Long accountId and Double amount.
     * The method signature is what I highlighted
     */
    @Override
    public void cancelCreditAccount(Long accountId, Double amount) {
      log.info("Cancelling credit for accountId: {} amount: {}", accountId, amount);

      if (amount == null || amount <= 0) {
        throw new IllegalArgumentException("Cancel amount must be positive.");
      }

        Account account = getAccountById(accountId);
      /*Account account = accountRepository.findById(accountId)
          .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
              "Account not found with ID: {0}", accountId)));*/

      Double currentBalance = account.getBalance() == null ? 0.0 : account.getBalance();

      if (currentBalance < amount) {
        throw new IllegalArgumentException("Insufficient funds to cancel the credit.");
      }

      account.setBalance(currentBalance - amount);

      accountRepository.save(account);

      log.info("Cancelled credit of {} for accountId: {}. New balance: {}",
       amount, accountId, account.getBalance());
    }

    @Override
    /**
     * Generate ONE-TIME QR code for debit operation - STATELESS approach
     * Exact signature: QRCodeResponse qrCode(QRCodeDataDTO qrCodeDataDTO)
     */
    public QRCodeResponse qrCode(QrCodeDataDTO qrCodeDataDTO) {
        Long accountId = qrCodeDataDTO.getAccountId();
        log.info("Generating ONE-TIME QR code for account: {}", accountId);

        // Validate account exists and is active
        Account account = getAccountById(accountId);
       /* Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));*/

        if (!account.isActive()) {
            throw new InvalidAccountException("Account is not active");
        }

        // Only ETUDIANT accounts can generate debit QR codes
        if (!"ETUDIANT".equalsIgnoreCase(account.getUser().getRole().getName())) {
            throw new AccessDeniedException("Only ETUDIANT accounts can generate debit QR codes");
        }
        /*if (!UserRole.ETUDIANT.equals(account.getUser().getRole())) {
            throw new AccessDeniedException("Only ETUDIANT accounts can generate debit QR codes");
        }*/

        // 🔄 Generate NEW unique code EVERY time (ONE-TIME)
        // Override any uniqueCode passed in the DTO to ensure it's always new
        String uniqueCode = generateOneTimeUniqueCode();

        // 🔥 Create STATELESS QR data with cryptographic security
        QrCodeDataDTO securedQRData = createSecuredQRData(accountId, uniqueCode,
                QR_EXPIRY_SECONDS);

        // Generate QR code image using ZXing
        String qrCodeImageBase64 = generateQRCodeImage(securedQRData);

        // Calculate expiration time
        java.time.Instant expiresAt = java.time.Instant.ofEpochSecond(
                securedQRData.getTimestamp() + securedQRData.getExpiresIn()
        );

        log.info("ONE-TIME QR code generated successfully for account: {}, uniqueCode: {}",
                accountId, uniqueCode);

        return QRCodeResponse.builder()
                .accountId(accountId)
                .uniqueCode(uniqueCode)
                .qrCodeImageBase64(qrCodeImageBase64)
                .expiresAt(expiresAt)
                .generatedAt(java.time.Instant.ofEpochSecond(securedQRData.getTimestamp()))
                .build();
    }

    /**
     * Create secured QR code data with HMAC signature
     */
    public QrCodeDataDTO createSecuredQRData(Long accountId, String uniqueCode, long expiresIn) {
        long timestamp = System.currentTimeMillis() / 1000;

        QrCodeDataDTO qrData = QrCodeDataDTO.builder()
                .accountId(accountId)
                .uniqueCode(uniqueCode)
                .timestamp(timestamp)
                .expiresIn(expiresIn)
                .build();

        // Add cryptographic signature
        String signature = generateSignature(qrData);
        qrData.setSignature(signature);

        return qrData;
    }

    /**
     * Generate HMAC signature for QR code data
     */
    public String generateSignature(QrCodeDataDTO data) {
        String payload = data.getAccountId() + ":" +
                data.getUniqueCode() + ":" +
                data.getTimestamp() + ":" +
                data.getExpiresIn();

        return HmacUtils.hmacSha256Hex(QR_SECRET_KEY, payload);
    }

    /**
     * Generate QR code image using ZXing library
     */
    public String generateQRCodeImage(QrCodeDataDTO qrCodeDataDTO) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.
                    ErrorCorrectionLevel.M);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrCodeDataDTO.toJsonString(),
                    BarcodeFormat.QR_CODE,
                    QR_CODE_WIDTH,
                    QR_CODE_HEIGHT,
                    hints
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (Exception e) {
            log.error("Failed to generate QR code image for account: {}",
                    qrCodeDataDTO.getAccountId(), e);
            throw new QRCodeGenerationException("Failed to generate QR code image");
        }
    }

    /**
     * Generate unique code for ONE-TIME QR
     */
    public String generateOneTimeUniqueCode() {
        // 12-character random code - NEW each time
        return "DEBIT_" + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    /**
     * Validate ONE-TIME QR code for debit operation - STATELESS validation
     * Companion method for QR code validation
     */
    public void validateDebitQRCode(QrCodeDataDTO qrCodeDataDTO, User scanningUser) {
        try {
            // STATELESS Cryptographic validation (NO database call)
            if (!verifySignature(qrCodeDataDTO)) {
                throw new InvalidQRCodeException("Invalid QR code signature");
            }

            // STATELESS Expiration check (NO database call)
            if (qrCodeDataDTO.isExpired()) {
                throw new QRCodeExpiredException("QR code has expired");
            }

            // Business validation (ONLY database call - for account/permissions)
            validateDebitPermissions(qrCodeDataDTO.getAccountId(), scanningUser);

            log.info("ONE-TIME debit QR validation successful for account: {}, scannedBy: {}",
                    qrCodeDataDTO.getAccountId(), scanningUser.getUserId());

        } catch (Exception e) {
            throw new InvalidQRCodeException("QR code validation failed: " + e.getMessage());
        }
    }

    /**
     * Verify HMAC signature of QR code data
     */
    public boolean verifySignature(QrCodeDataDTO data) {
        String expectedSignature = generateSignature(data);
        return expectedSignature.equals(data.getSignature());
    }

    /**
     * Validate debit permissions and account status
     */
    public void validateDebitPermissions(Long accountId, User scanningUser) {

        // 1. Validate account exists and is active
        Account account = getAccountById(accountId);
       /* Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));*/

        if (!account.isActive()) {
            throw new InvalidAccountException("Account is not active");
        }

        // 2. 🔥 Validate scanning user is PORTIER (role is a class)
        if (scanningUser.getRole() == null) {
            throw new AccessDeniedException("User role is not defined");
        }

        if (!"PORTIER".equalsIgnoreCase(scanningUser.getRole().getName())) {
            throw new AccessDeniedException("Only PORTIER can scan debit QR codes");
        }

        // 3. Validate account belongs to an ETUDIANT
        if (!"ETUDIANT".equalsIgnoreCase(account.getUser().getRole().getName())) {
            throw new AccessDeniedException("Can only debit ETUDIANT accounts");
        }
    }

    /**
     * Utility method to get account by ID (used by other methods)
     */
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(MessageFormat.format(
                        "Account not found with ID: {0}", accountId)));
    }

}
