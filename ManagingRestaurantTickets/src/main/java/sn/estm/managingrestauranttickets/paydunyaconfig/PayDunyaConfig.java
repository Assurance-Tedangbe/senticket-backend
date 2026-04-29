package sn.estm.managingrestauranttickets.paydunyaconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// Charge les configurations PayDunya depuis application.properties
@Data
@Configuration
@ConfigurationProperties(prefix = "paydunya")
public class PayDunyaConfig {

    /** Mode: "test" (sandbox) ou "live" (production) */
    private String mode;

    /** Clé principale pour l'authentification PayDunya */
    private String masterKey;

    /** Clé privée pour signer les requêtes */
    private String privateKey;

    /** Clé publique pour identifier votre boutique */
    private String publicKey;

    /** Token unique de votre compte PayDunya */
    private String token;

    /** URL de retour après paiement réussi */
    private String returnUrl;

    /** URL de retour après annulation */
    private String cancelUrl;

    /** URL pour les notifications IPN */
    private String callbackUrl;

    /**
     * Retourne l'URL de base de l'API PayDunya selon le mode
     */
    public String getApiUrl() {
        // Pour le sandbox (test)
        return "test".equals(mode)
                ? "https://sandbox.paydunya.com/api/v1"
                : "https://app.paydunya.com/api/v1";
    }
}

/*
// Cette classe charge les configurations depuis application.properties
// et fournit les URLs de l'API PayDunya selon le mode (test/production)
@Data
@Configuration
@ConfigurationProperties(prefix = "paydunya")
public class PayDunyaConfig {

    *//** Clé principale pour l'authentification PayDunya *//*
    private String masterKey;

    *//** Clé privée pour signer les requêtes *//*
    private String privateKey;

    *//** Clé publique pour identifier votre boutique *//*
    private String publicKey;

    *//** Token unique de votre compte PayDunya *//*
    private String token;

    *//** Mode: "test" (sandbox) ou "live" (production) *//*
    private String mode;

    *//** URL de retour après paiement réussi *//*
    private String returnUrl;

    *//** URL de retour après annulation *//*
    private String cancelUrl;

    *//** URL pour les notifications automatiques *//*
    private String webhookUrl;

    *//**
     * Retourne l'URL de base de l'API PayDunya
     * @return URL du sandbox ou de production
     *//*
    public String getApiUrl() {
        return "test".equals(mode)
                ? "https://sandbox.paydunya.com/api/v1"
                : "https://app.paydunya.com/api/v1";
    }
}*/

/*
package sn.estm.managingrestauranttickets.paydunyaconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "paydunya")
@Data
public class PaydunyaConfig {
    private String masterKey;
    private String privateKey;
    private String publicKey;
    private String token;
    private String mode;
    private String baseUrlTest;
    private String baseUrlLive;
    private String storeName;
    private String returnUrl;
    private String cancelUrl;
    private String callbackUrl;

    public String getBaseUrl() {
        return "live".equals(mode) ? baseUrlLive : baseUrlTest;
    }
}*/
