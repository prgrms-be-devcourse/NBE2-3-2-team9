package com.team9.anicare.domain.schedule.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;

    @Value("${KAKAO_REDIRECT_URI_SCHEDULE}")
    private String kakaoRedirectUri;

    @Value("${KAKAO_TOKEN_URI}")
    private String kakaoTokenUri;

    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format(
                "grant_type=authorization_code&client_id=%s&redirect_uri=%s&code=%s",
                kakaoClientId,
                kakaoRedirectUri,
                code
        );

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoTokenUri,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            } catch (Exception e) {
                throw new RuntimeException("Access Token 응답 파싱에 실패했습니다.\"", e);
            }
        } else {
            throw new RuntimeException("Access Token을 가져오지 못했습니다. 상태 코드: " + response.getStatusCode());
        }
    }

    public void requestMessage(String accessToken) {
            // 1. 요청 URL
            String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

            // 2. HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // application/x-www-form-urlencoded

            // 3. 요청 바디 설정
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("template_object", "{\"object_type\":\"text\",\"text\":\"스케줄 알림: 내일 오전 9시에 회의가 있습니다.\",\"link\":{\"web_url\":\"https://yourapp.com/schedule\",\"mobile_web_url\":\"https://yourapp.com/schedule\"}}");

            // 4. HttpEntity 생성 (헤더 + 바디)
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            // 5. API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // 6. 응답 처리
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("메시지 전송 성공: " + response.getBody());
            } else {
                System.out.println("메시지 전송 실패: " + response.getStatusCode());
            }


    }
}
