package sn.estm.managingrestauranttickets.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

/**
 * CONFIGURATION CORS POUR SPRING BOOT
 *
 * Pourquoi cette configuration est nécessaire ?
 * - Par défaut, les navigateurs et apps mobiles bloquent les requêtes
 *   entre domaines différents (Cross-Origin Resource Sharing)
 * - Cette classe autorise Flutter à communiquer avec Spring Boot
 */
@Configuration  // Marque cette classe comme configuration Spring
public class CorsConfig {

    @Bean  // Crée un bean Spring disponible dans toute l'application
    public CorsFilter corsFilter() {
        // Crée une configuration CORS
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // ============ SECTION 1: ORIGINES AUTORISÉES ============
        // Définit quelles applications peuvent accéder à l'API
        corsConfiguration.setAllowedOrigins(Arrays.asList(
                "http://localhost",           // Sans port spécifique
                "http://localhost:*",         // Tous les ports locaux ← IMPORTANT!
                "http://127.0.0.1",
                "http://127.0.0.1:*",        // Tous les ports pour 127.0.0.1
                "http://10.0.2.2",
                "http://10.0.2.2:*",         // Tous les ports pour l'émulateur
                "http://0.0.0.0",
                "http://0.0.0.0:*",
                "http://[::1]",              // IPv6 localhost
                "http://[::1]:*"             // Tous les ports IPv6
               /* "http://localhost",           // Pour tests en local depuis un navigateur
                "http://localhost:*",         // Tous les ports locaux (ex: 3000, 4200, 8081)
                "http://10.0.2.2",           // ADRESSE CRITIQUE: Émulateur Android
                "http://10.0.2.2:*",         // Émulateur Android avec tous ports
                "http://127.0.0.1",          // Localhost alternative
                "http://192.168.0.0/16",     // Tout le réseau local (192.168.x.x)
                "http://0.0.0.0",            // Toutes interfaces réseau
                "http://[::1]"               // Localhost IPv6*/
        ));

        // ============ SECTION 2: MÉTHODES HTTP AUTORISÉES ============
        // Définit quelles opérations HTTP sont permises
        corsConfiguration.setAllowedMethods(Arrays.asList(
                "GET",     // Récupérer des données
                "POST",    // Créer des données
                "PUT",     // Mettre à jour des données
                "DELETE",  // Supprimer des données
                "PATCH",   // Mettre à jour partiellement
                "OPTIONS", // Pré-vérification CORS
                "HEAD"     // Récupérer uniquement les en-têtes
        ));

        // ============ SECTION 3: EN-TÊTES AUTORISÉS ============
        // Définit quels en-têtes HTTP peuvent être envoyés
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Authorization",     // Pour l'authentification (Bearer token)
                "Content-Type",      // Type de contenu (JSON, form-data)
                "Accept",            // Format de réponse accepté
                "Origin",            // Origine de la requête
                "X-Requested-With",  // Identifie les requêtes AJAX
                "Access-Control-Allow-Origin",
                "Access-Control-Request-Headers",
                "Access-Control-Request-Method",
                "X-Api-Key",         // Pour les clés API
                "Cache-Control"      // Contrôle du cache
        ));

        // ============ SECTION 4: EN-TÊTES EXPOSÉS ============
        // Définit quels en-têtes peuvent être lus par Flutter
        corsConfiguration.setExposedHeaders(Arrays.asList(
                "Authorization",     // Pour récupérer le token JWT
                "Content-Type",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // ============ SECTION 5: CREDENTIALS ============
        // Autorise l'envoi de cookies/sessions (important pour l'auth)
        corsConfiguration.setAllowCredentials(true);

        // ============ SECTION 6: CACHE CORS ============
        // Durée de mise en cache des pré-vérifications CORS (1 heure)
        corsConfiguration.setMaxAge(3600L);

        // ============ SECTION 7: APPLICATION À TOUTES LES ROUTES ============
        // Applique cette configuration à toutes les routes de l'API
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // ** = toutes les routes

        // Retourne le filtre CORS configuré
        return new CorsFilter(source);
    }
}