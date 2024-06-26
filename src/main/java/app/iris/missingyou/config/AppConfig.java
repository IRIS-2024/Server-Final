package app.iris.missingyou.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

@Configuration
public class AppConfig {

    @Bean
    public Key key(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public WebClient webClient() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // to unlimited memory size
                .build();

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    @Bean
    public Storage storage(@Value("${spring.cloud.gcp.credentials.location}") String secretPath ,@Value("${spring.cloud.gcp.project-id}") String projectId) throws IOException {
        PathResource resource = new PathResource(secretPath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(@Value("${firebase.secret.key}") String secretPath) throws IOException {
        PathResource resource = new PathResource(secretPath);
        InputStream refreshToken = resource.getInputStream();


        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken))
                .build();

        FirebaseApp.initializeApp(options);

        return FirebaseMessaging.getInstance();
    }
}
