package ru.gamehub.web.infrastructure.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Конфигурация безопасности Spring Security для REST API приложения.
 * <p>
 * Включает интеграцию с OAuth2/JWT (Keycloak), централизует настройку CORS, определяет публичные и защищённые эндпоинты.
 * Гарантирует маппинг ролей из JWT (realm_access.roles) в Spring Security authorities.
 * </p>
 *
 * <b>Особенности:</b>
 * <ul>
 *   <li>Открыты только /actuator/** и /auth/** (health-check, регистрация/логин).</li>
 *   <li>Все остальные эндпоинты требуют аутентификации по JWT.</li>
 *   <li>Из JWT токена подтягиваются роли из claim <code>realm_access.roles</code> и конвертируются в {@code ROLE_*} для Spring Security.</li>
 *   <li>CORS разрешён для всех источников (по умолчанию для разработки).</li>
 *   <li>CSRF отключён (актуально для stateless REST API).</li>
 * </ul>
 *
 * <b>Безопасность и ограничения:</b>
 * <ul>
 *   <li>Для production CORS-конфигурация должна быть ужесточена (разрешать только доверенные источники).</li>
 *   <li>Открытые эндпоинты ограничены минимальным набором для безопасности.</li>
 *   <li>Требует настройки OAuth2 ресурса в Keycloak и соответствующих прав в токенах пользователей.</li>
 * </ul>
 *
 * <b>Связанные компоненты:</b>
 * <ul>
 *   <li>{@link org.springframework.security.oauth2.jwt.Jwt}</li>
 *   <li>{@link org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter}</li>
 *   <li>{@link org.springframework.security.core.GrantedAuthority}</li>
 * </ul>
 *
 * <b>Пример расширения:</b>
 * <ul>
 *   <li>Для ограничения CORS: заменить <code>config.setAllowedOrigins(List.of("*"))</code> на whitelisted origins.</li>
 *   <li>Для кастомных правил авторизации — расширить {@code authorizeHttpRequests}.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auths -> auths
                        .requestMatchers("/actuator/**", "/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
        return http.build();
    }


    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return converter;
    }

    private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return jwt -> {
            Collection<GrantedAuthority> authorities = new JwtGrantedAuthoritiesConverter().convert(jwt);
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                if (roles != null) {
                    authorities.addAll(
                            roles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                    .toList()
                    );
                }
            }
            return authorities;
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
