package sn.estm.managingrestauranttickets.enumerations;

 /// Statuts possibles d'un paiement PayDunya
public enum PaymentStatus {
    PENDING,    // paiement en cours, l'utilisateur n'a pas encore payé
    COMPLETED,  // Paiement effectué par PayDunya
    CANCELLED,  // Paiement annulé par l'utilisateur
    UNKNOWN     // erreur technique
}