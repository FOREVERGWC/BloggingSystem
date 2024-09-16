package org.example.springboot.common.config;

import jakarta.annotation.Resource;
import org.example.springboot.common.config.security.*;
import org.example.springboot.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    @Resource
    private CheckTokenFilter checkTokenFilter;
    @Resource
    private LoginAuthenticationHandler loginAuthenticationHandler;
    @Resource
    private LoginAccessDefineHandler loginAccessDefineHandler;
    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(bCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public EmailCodeAuthenticationProvider emailCodeAuthenticationProvider() {
        return new EmailCodeAuthenticationProvider(userDetailsService);
    }

    @Bean
    public PhoneCodeAuthenticationProvider phoneCodeAuthenticationProvider() {
        return new PhoneCodeAuthenticationProvider(userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerBuilder authBuilder) throws Exception {
        authBuilder.authenticationProvider(daoAuthenticationProvider());
        authBuilder.authenticationProvider(emailCodeAuthenticationProvider());
        authBuilder.authenticationProvider(phoneCodeAuthenticationProvider());
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(checkTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/captcha", "/email/**").permitAll()
                                .requestMatchers("/login", "/login/wechat", "/register", "/password/reset").permitAll()
                                .requestMatchers("/static/**", "/file/**").permitAll()
                                .requestMatchers("/doc.html", "/favicon.ico", "/webjars/**", "/swagger-resources", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // 匿名用户访问处理
                        .authenticationEntryPoint(loginAuthenticationHandler)
                        // 认证用户无权访问处理
                        .accessDeniedHandler(loginAccessDefineHandler)
                )
                .headers(headers -> headers.frameOptions((HeadersConfigurer.FrameOptionsConfig::disable)))
                .headers(headers -> headers.frameOptions((HeadersConfigurer.FrameOptionsConfig::sameOrigin)));
        return http.build();
    }
}
