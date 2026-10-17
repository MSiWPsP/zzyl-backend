package com.zzyl;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "AI_DASHSCOPE_API_KEY", matches = ".+")
public class AiTest {

    @Resource(name = "dashScopeRestTemplate")
    private RestTemplate restTemplate;

    @Value("${ai.bailian.api-key}")
    private String apiKey;

    @Value("${ai.bailian.base-url}")
    private String baseUrl;

    @Value("${ai.bailian.model}")
    private String model;

    @Test
    public void assess() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful assistant."),
                        Map.of("role", "user", "content", "你好")));

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/chat/completions",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                JsonNode.class);

        System.out.println(response.getBody().at("/choices/0/message/content").asText());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().at("/choices/0/message/content").asText().isEmpty());
    }

}
