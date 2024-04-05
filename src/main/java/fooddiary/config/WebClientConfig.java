package fooddiary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {
    @Bean
    @Qualifier("webClient")
    public WebClient webClient(Jackson2JsonDecoder textAsJsonDecoder) {
        return WebClient.builder()
                .codecs(configurer -> configurer.customCodecs().registerWithDefaultConfig(textAsJsonDecoder))
                .build();
    }

    @Bean
    public Jackson2JsonDecoder textAsJsonDecoder(ObjectMapper objectMapper) {
        return new Jackson2JsonDecoder(objectMapper, MimeTypeUtils.parseMimeType(MediaType.TEXT_PLAIN_VALUE));
    }

    @Bean
    @Qualifier("noSslWebClient")
    public WebClient noSslWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
