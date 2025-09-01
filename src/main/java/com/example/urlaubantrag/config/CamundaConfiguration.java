package com.example.urlaubantrag.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.Topology;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Configuration
public class CamundaConfiguration {

    @Value("${camunda.client.zeebe.gateway-address:localhost:26500}")
    private String gatewayAddress;

    @Value("${camunda.client.auth.client-id:}")
    private String clientId;

    @Value("${camunda.client.auth.client-secret:}")
    private String clientSecret;

    @Value("${camunda.client.auth.audience:zeebe.camunda.io}")
    private String audience;

    @Value("${camunda.client.auth.auth-url:https://login.cloud.camunda.io/oauth/token}")
    private String authUrl;

    @Bean
    @Profile("!test")
    public ZeebeClient camundaClient() {
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            System.out.println("⚠️  Camunda SaaS Credentials nicht konfiguriert - starte ohne Camunda-Verbindung");
            return null;
        }

        try {
            System.out.println("🔄 Connecting to Camunda SaaS at: " + gatewayAddress);

            ZeebeClient client = ZeebeClient.newClientBuilder()
                    .gatewayAddress(gatewayAddress)
                    .credentialsProvider(
                            new OAuthCredentialsProviderBuilder()
                                    .authorizationServerUrl(authUrl)
                                    .audience(audience)
                                    .clientId(clientId)
                                    .clientSecret(clientSecret)
                                    .build())
                    .defaultRequestTimeout(Duration.ofSeconds(30))
                    .build();

            // Test connection
            Topology topology = client.newTopologyRequest().send().join();
            System.out.println("✅ Successfully connected to Camunda SaaS!");
            System.out.println("📊 Cluster size: " + topology.getBrokers().size());

            return client;

        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Camunda SaaS: " + e.getMessage());
            System.err.println("🚀 Application will start without Camunda connection for testing purposes");
            return null;
        }
    }
}
