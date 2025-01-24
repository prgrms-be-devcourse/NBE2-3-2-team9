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
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", "{"
                + "\"object_type\":\"text\","
                + "\"text\":\"텍스트 영역입니다. 최대 200자 표시 가능합니다.\","
                + "\"link\":{"
                + "    \"web_url\":\"https://developers.kakao.com\","
                + "    \"mobile_web_url\":\"https://developers.kakao.com\""
                + "},"
                + "\"button_title\":\"바로 확인\""
                + "}");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("메시지 전송 성공: " + response.getBody());
        } else {
            System.out.println("메시지 전송 실패: " + response.getStatusCode());
        }


    }
}
