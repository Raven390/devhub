package ru.devhub.web.infrastructure.redis;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RedisConfigTest {

    @Test
    void redisTemplateUsesStringKeysAndJsonValues() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);

        RedisTemplate<String, Object> template = new RedisConfig().redisTemplate(factory);

        assertThat(template.getConnectionFactory()).isSameAs(factory);
        assertThat(template.getKeySerializer()).isInstanceOf(StringRedisSerializer.class);
        assertThat(template.getValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);
    }
}
