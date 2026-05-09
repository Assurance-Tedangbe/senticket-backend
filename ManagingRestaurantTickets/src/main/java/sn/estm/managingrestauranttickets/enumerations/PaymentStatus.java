package sn.estm.managingrestauranttickets.enumerations;

 /// Statuts possibles d'un paiement PayDunya
public enum PaymentStatus {
    PENDING,    // En attente de paiement
    COMPLETED,  // Paiement confirmé par PayDunya
    FAILED,     // Paiement échoué
}