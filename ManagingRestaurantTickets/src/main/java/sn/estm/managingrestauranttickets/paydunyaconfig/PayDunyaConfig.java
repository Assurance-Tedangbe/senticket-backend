// Cette classe configure la connexion à l'API PayDunya.
// Il charge les config (clés API) depuis application.properties et fournit
//  les URLs de l'API PayDunya selon le mode (test/production)

package sn.estm.managingrestauranttickets.paydunyaconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "paydunya")
public class PayDunyaConfig {

    /**
     * Mode de fonctionnement: "test" pour le sandbox, "live" pour la production
     * En mode test, les transactions sont simulées sans argent réel
     */
    private String mode;

    /** Clé principale pour l'authentification auprès de PayDunya */
    private String masterKey;

    /** Clé privée pour signer les requêtes */
    private String privateKey;

    /** Clé publique pour identifier votre boutique */
    private String publicKey;

    /** Token unique de votre compte PayDunya */
    private String token;

    /** URL où l'utilisateur est redirigé après un paiement réussi */
    private String returnUrl;

    /** URL où l'utilisateur est redirigé après avoir annulé le paiement */
    private String cancelUrl;

    /** URL qui reçoit les notifications(IPN) automatiques de PayDunya (webhook) */
    private String callbackUrl;

    /**
     * Retourne l'URL de base de l'API PayDunya selon le mode
     * @return URL du sandbox ou de production
     */
    public String getApiUrl() {
        // Pour le sandbox (test)
        return "test".equals(mode)
                ? "https://app.paydunya.com/sandbox-api/v1"
                : "https://app.paydunya.com/api/v1/";
    }
}
