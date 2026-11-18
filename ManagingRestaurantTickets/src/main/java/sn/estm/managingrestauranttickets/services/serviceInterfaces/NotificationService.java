package sn.estm.managingrestauranttickets.services.serviceInterfaces;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FirebaseMessaging firebaseMessaging;

    private void send(String fcmToken, String title,
                      String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isBlank()) return;
        try {
            Message.Builder builder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());
            if (data != null) builder.putAllData(data);
            String response = firebaseMessaging.send(builder.build());
            log.info("Notification envoyée: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Erreur notification (ignorée): {}", e.getMessage());
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                log.warn("Token périmé, à supprimer: {}", fcmToken);
                // optionnel : appeler userRepository pour mettre fcm_token à null
            }
        } catch (Exception e) {
            log.error("Erreur notification ignorée: {}", e.getMessage());
        }
    }

    /** Notifie l'étudiant après un débit de son compte */
    public void notifyDebit(String fcmToken, String porterName,
                            int ticketCount, String ticketType) {
        send(fcmToken,
                " Débit effectué",
                ticketCount + " ticket(s) de type " + ticketType
                        + " débité(s)",
                Map.of("type", "DEBIT",
                        "count", String.valueOf(ticketCount),
                        "ticketType", ticketType));
    }

    /** Notifie l'étudiant destinataire après réception de tickets */
    public void notifyTransferReceived(String fcmToken, String senderName,
                                       int ticketCount, String ticketType) {
        send(fcmToken,
                " Tickets reçus",
                senderName + " vous a transféré " + ticketCount + " ticket(s) de type " + ticketType,
                Map.of("type", "TRANSFER_RECEIVED",
                        "count", String.valueOf(ticketCount),
                        "ticketType", ticketType,
                        "senderName", senderName));
    }

    /** Notifie l'étudiant après confirmation du paiement PayDunya */
    public void notifyPaymentSuccess(String fcmToken,
                                     int countA, int countB, double amount) {
        String details = (countA > 0 ? countA + " type A " : "")
                + (countB > 0 ? countB + " type B" : "");
        send(fcmToken,
                " Paiement confirmé",
                "Vos tickets ont été ajoutés : " + details
                        + " — " + (int) amount + " FCFA",
                Map.of("type", "PAYMENT_SUCCESS",
                        "countA", String.valueOf(countA),
                        "countB", String.valueOf(countB),
                        "amount", String.valueOf(amount)));
    }

    /** Confirme à l'expéditeur que son transfert a été effectué */
    public void notifyTransferSent(String fcmToken, String recipientName,
                                   int ticketCount, String ticketType) {
        send(fcmToken,
                "Transfert effectué",
                ticketCount + " ticket(s) de type " + ticketType
                        + " envoyé(s) à " + recipientName,
                Map.of("type", "TRANSFER_SENT",
                        "count", String.valueOf(ticketCount),
                        "ticketType", ticketType,
                        "recipientName", recipientName));
    }

    public void notifyDebitConfirmed(String fcmToken, String studentName,
                                     int ticketCount, String ticketType) {
        send(fcmToken, "Débit confirmé",
                 ticketCount + " ticket(s) de type "
                        + ticketType + " debité(s) du compte de " + studentName,
                Map.of("type", "DEBIT_CONFIRMED",
                        "count", String.valueOf(ticketCount),
                        "ticketType", ticketType,
                        "studentName", studentName));
    }
}