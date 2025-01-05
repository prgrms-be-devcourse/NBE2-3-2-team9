package com.team9.anicare.domain.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.anicare.domain.chat.dto.ChatMessageDTO;
import com.team9.anicare.domain.chat.dto.ChatRoomDTO;
import com.team9.anicare.domain.chat.service.ChatLogService;
import com.team9.anicare.domain.chat.service.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * RedisConfig 클래스
 * - Redis의 Pub/Sub 설정, RedisTemplate 직렬화 및 ListenerContainer 설정을 관리합니다.
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Value("${redis.channel.topic}")
    private String redisChannelTopic;




    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;


    /**
     * RedisMessageListenerContainer 설정
     * - Redis Pub/Sub 메시지를 수신하고 MessageListener로 전달합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     * @param redisMessageSubscriberAdapter 메시지 리스너 어댑터
     * @param topic Pub/Sub 채널 주제
     * @return RedisMessageListenerContainer 빈
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter redisMessageSubscriberAdapter,
            ChannelTopic topic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisMessageSubscriberAdapter, topic);
        return container;
    }

    /**
     * RedisMessageSubscriber를 MessageListenerAdapter로 래핑
     * - handleMessage 메서드를 호출하도록 어댑터 설정
     *
     * @return MessageListenerAdapter 빈
     */
    @Bean
    public MessageListenerAdapter redisMessageSubscriberAdapter(
            ObjectMapper objectMapper,
            SimpMessagingTemplate messagingTemplate,
            ChatLogService chatLogService) {
        RedisMessageSubscriber redisMessageSubscriber = new RedisMessageSubscriber(objectMapper, messagingTemplate, chatLogService);
        return new MessageListenerAdapter(redisMessageSubscriber, "handleMessage");
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // RedisStandaloneConfiguration 설정
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisPort);
        redisConfiguration.setPassword(redisPassword);
        System.out.println("22321312312" + redisHost);
        // LettuceConnectionFactory를 사용하여 Redis와 연결
        return new LettuceConnectionFactory(redisConfiguration);
    }


    /**
     * Redis Pub/Sub 채널 주제 정의
     * - 채팅 서비스에서 메시지를 송수신하는 채널 이름
     *
     * @return ChannelTopic 빈
     */
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("chatroom");
    }

    /**
     * RedisTemplate 설정
     * - StringRedisSerializer를 키에 사용하고, 값은 Jackson2JsonRedisSerializer로 직렬화 설정
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return RedisTemplate 빈
     */
    @Bean(name = "chatMessageRedisTemplate")
    public RedisTemplate<String, ChatMessageDTO> chatMessageRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatMessageDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDTO.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDTO.class));
        return template;
    }

    @Bean(name = "chatLogRedisTemplate")
    public RedisTemplate<String, ChatMessageDTO> chatLogRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatMessageDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDTO.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessageDTO.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, ChatRoomDTO> chatRoomRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ChatRoomDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatRoomDTO.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ChatRoomDTO.class));
        return template;
    }


    @Bean(name = "defaultRedisTemplate")
    public RedisTemplate<String, Object> defaultRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}