package com.team9.anicare.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * RedisConfig 클래스는 Redis 설정을 관리합니다.
 * - RedisMessageListenerContainer: Redis Pub/Sub 메시지 리스너를 관리합니다.
 * - StringRedisTemplate: Redis와의 문자열 데이터를 처리하기 위한 템플릿을 제공합니다.
 */
@Configuration
public class RedisConfig {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final StringRedisTemplate redisTemplate;

    /**
     * RedisConfig 생성자
     * RedisConnectionFactory를 통해 Redis 관련 객체들을 초기화합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     */
    public RedisConfig(RedisConnectionFactory connectionFactory) {
        this.redisMessageListenerContainer = createRedisMessageListenerContainer(connectionFactory);
        this.redisTemplate = createRedisTemplate(connectionFactory);
    }

    /**
     * RedisMessageListenerContainer 생성 메서드
     * Redis Pub/Sub 메시지 리스너 컨테이너를 생성하고 연결 팩토리를 설정합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return RedisMessageListenerContainer 객체
     */
    private RedisMessageListenerContainer createRedisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    /**
     * StringRedisTemplate 생성 메서드
     * 문자열 데이터를 Redis와 상호작용하기 위한 템플릿 객체를 생성합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return StringRedisTemplate 객체
     */
    private StringRedisTemplate createRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * RedisMessageListenerContainer 객체 반환 메서드
     * 외부에서 RedisMessageListenerContainer 객체를 접근할 수 있도록 제공합니다.
     *
     * @return RedisMessageListenerContainer 객체
     */
    public RedisMessageListenerContainer getRedisMessageListenerContainer() {
        return redisMessageListenerContainer;
    }

    /**
     * StringRedisTemplate 객체 반환 메서드
     * 외부에서 StringRedisTemplate 객체를 접근할 수 있도록 제공합니다.
     *
     * @return StringRedisTemplate 객체
     */
    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

}
