package com.ssafy.backend.global.component.jwt.repository;

import com.ssafy.backend.global.component.jwt.JwtTokenPropsInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;


/**
 * RefreshTokenRepository는 사용자의 이메일과 연관된 리프레시 토큰을 관리하는 데 사용됩니다.
 * 이 클래스는 Redis를 사용하여 토큰을 저장, 조회, 삭제하는 기능을 제공합니다.
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "refreshToken::";
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenPropsInfo tokenPropsInfo;

    /**
     * 사용자의 이메일과 리프레시 토큰을 Redis에 저장합니다.
     *
     * @param email 사용자의 이메일 주소, Redis에 저장될 키의 일부로 사용됩니다.
     * @param token 사용자의 리프레시 토큰, Redis에 값으로 저장됩니다.
     */
    public void save(String email, String token) {
        String key = KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, token, tokenPropsInfo.refreshExpiration());
    }

    /**
     * 주어진 이메일에 해당하는 리프레시 토큰을 조회합니다.
     *
     * @param email 사용자의 이메일 주소, 조회할 키를 결정하는 데 사용됩니다.
     * @return Optional<String> 조회된 리프레시 토큰. 토큰이 존재하지 않을 경우 Optional.empty() 반환.
     */
    public Optional<String> find(String email) {
        String token = redisTemplate.opsForValue().get(KEY_PREFIX + email);
        return Optional.ofNullable(token);
    }

    /**
     * 주어진 이메일과 관련된 리프레시 토큰을 삭제합니다.
     *
     * @param email 사용자의 이메일 주소, 삭제할 키를 결정하는 데 사용됩니다.
     */
    public void delete(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}
