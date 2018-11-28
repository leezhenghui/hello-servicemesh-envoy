package hello.spring.cloud.calculator.gateway;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DefaultFallbackProvider implements FallbackProvider {

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable throwable) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 400;
            }

            @Override
            public String getStatusText() throws IOException {
                return "Failed";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                System.err.println("[ERROR]: Fall into fallback provider");
                return new ByteArrayInputStream("Fallback processing".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);
                return headers;
            }
        };
    }
}
