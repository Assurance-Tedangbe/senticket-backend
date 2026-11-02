package sn.estm.managingrestauranttickets.services.serviceInterfaces;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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
        } catch (Exception e) {
            // Ne jamais faire échouer l'opération principale
            log.error("Erreur notification (ignorée): {}", e.getMessage());
        }
    }

    /** Notifie l'étudiant après un débit de son compte */
    public void notifyDebit(String fcmToken, String porterName,
                            int ticketCount, String ticketType) {
        send(fcmToken,
                " Débit effectué",
                ticketCount + " ticket(s) de type " + ticketType
                        + " débité(s) par " + porterName,
                Map.of("type", "DEBIT",
                        "count", String.valueOf(ticketCount),
                        "ticketType", ticketType));
    }

    /** Notifie l'étudiant destinataire après réception de tickets */
    public void notifyTransferReceived(String fcmToken, String senderName,
                                       int ticketCount, String ticketType) {
        send(fcmToken,
                " Tickets transférés",
                ticketCount + " ticket(s) de type " + ticketType +  " transféré(s)",
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
}