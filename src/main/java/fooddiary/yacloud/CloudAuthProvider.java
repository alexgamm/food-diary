package fooddiary.yacloud;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CloudAuthProvider {
    private static final String METADATA_SERVICE_URL = "http://169.254.169.254/computeMetadata/v1/instance/service-accounts/default/token";
    private final WebClient webClient;

    public CloudAuthProvider(@Qualifier("webClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public String getIamToken() {
        String iamToken = System.getenv("IAM_TOKEN");
        if (iamToken == null) {
            TokenResponse tokenResponse = webClient.get()
                    .uri(METADATA_SERVICE_URL)
                    .header("Metadata-Flavor", "Google")
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();
            assert tokenResponse != null;
            return tokenResponse.getAccess_token();
        }
        return iamToken;
    }

    @Data
    private static class TokenResponse {
        private String access_token;
    }
}
