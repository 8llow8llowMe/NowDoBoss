package com.ssafy.backend.global.config;

import com.ssafy.backend.global.component.redis.RedisKeyExpirationListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 데이터 저장소 및 캐시 구성을 위한 설정 클래스입니다.
 * 이 클래스는 Redis와의 연결 및 데이터 직렬화, 캐시 설정, 메시지 리스너 등을 설정합니다.
 */
@Configuration
@EnableCaching
@EnableRedisRepositories
public class RedisConfig {

    /**
     * Redis 서버의 호스트 주소
     * 해당 값은 application.properties 파일에서 로드됩니다.
     */
    @Value("${spring.data.redis.host}")
    private String host;

    /**
     * Redis 서버의 포트 번호
     * 해당 값은 application.properties 파일에서 로드됩니다.
     */
    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * Redis 서버와의 연결을 관리하는 커넥션 팩토리 빈을 생성합니다.
     * 이 팩토리는 Lettuce 클라이언트를 사용하여 Redis에 연결합니다.
     *
     * @return LettuceConnectionFactory RedisConnectionFactory 인스턴스
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * Redis 작업을 수행하기 위한 RedisTemplate 빈을 생성합니다.
     * 이 템플릿은 문자열 키를 사용하며, JSON 직렬화 방식으로 객체 값을 저장합니다.
     *
     * @return RedisTemplate<String, Object> 문자열 키와 JSON 직렬화된 값을 사용하는 Redis 템플릿
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 키는 문자열로 직렬화
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 값은 JSON으로 직렬화
        redisTemplate.setConnectionFactory(redisConnectionFactory()); // Redis 연결 설정
        return redisTemplate;
    }

    /**
     * Redis 캐시 설정을 위한 CacheManager 빈을 생성합니다.
     * 이 CacheManager는 RedisCacheManager를 기반으로 하며, 캐시 키와 값을 JSON 직렬화 방식으로 처리합니다.
     * 캐시 TTL(Time To Live)은 30일로 설정됩니다.
     *
     * @param cf RedisConnectionFactory Redis 연결 팩토리
     * @return RedisCacheManager 캐시 관리를 위한 RedisCacheManager 인스턴스
     */
    @Bean
    public CacheManager contentCacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // 키 직렬화 설정
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())) // 값 직렬화 설정
                .entryTtl(Duration.ofDays(30)); // 캐시 만료 기간 설정 (30일)

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf).cacheDefaults(redisCacheConfiguration).build();
    }

    /**
     * Redis 메시지 리스너 컨테이너 빈을 생성합니다.
     * Redis에서 발생하는 특정 이벤트를 수신하기 위한 메시지 리스너를 관리합니다.
     * 특히, "__keyevent@*__:expired" 패턴을 구독하여 Redis 키 만료 이벤트를 처리합니다.
     *
     * @param connectionFactory RedisConnectionFactory Redis 연결 팩토리
     * @param listenerAdapter   MessageListenerAdapter 메시지 리스너 어댑터
     * @return RedisMessageListenerContainer Redis 메시지 리스너 컨테이너 인스턴스
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory); // Redis 연결 설정
        // Redis 키 만료 이벤트에 대한 리스너 등록
        container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@*__:expired"));
        return container;
    }

    /**
     * Redis 키 만료 이벤트를 처리하기 위한 MessageListenerAdapter 빈을 생성합니다.
     * 이 어댑터는 RedisKeyExpirationListener의 onMessage 메서드를 호출하여 이벤트를 처리합니다.
     *
     * @param listener RedisKeyExpirationListener Redis 키 만료 이벤트 리스너
     * @return MessageListenerAdapter 메시지 리스너 어댑터 인스턴스
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisKeyExpirationListener listener) {
        // RedisKeyExpirationListener의 onMessage 메서드를 호출하도록 설정
        return new MessageListenerAdapter(listener, "onMessage");
    }
}
