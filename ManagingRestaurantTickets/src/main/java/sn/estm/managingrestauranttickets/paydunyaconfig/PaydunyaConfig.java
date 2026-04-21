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
