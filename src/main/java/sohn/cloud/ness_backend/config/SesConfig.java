package sohn.cloud.ness_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Configuration
public class SesConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    @Profile("!local")
    public SesV2Client sesV2Client() {
        return SesV2Client.builder()
                .region(Region.of(region))
                .build(); // uses instance role in prod
    }

    @Bean
    @Profile("local")
    public SesV2Client sesV2ClientLocal() {
        return SesV2Client.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.create("ness-local"))
                .build();
    }
}