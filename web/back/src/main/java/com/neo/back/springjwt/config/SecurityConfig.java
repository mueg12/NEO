package com.neo.back.springjwt.config;


import com.neo.back.springjwt.jwt.JWTFilter;
import com.neo.back.springjwt.jwt.JWTUtil;
import com.neo.back.springjwt.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JWTUtil jwtUtil;
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration=authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        //CORS(교차 검증 해결)
        http.
                cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();

                                //프론트단 port 허용
                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                //허용할 메소드 GET,POST,DELETE 등 모두 허용
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                //프론트에서 credential 허용했으면 똑같이 true
                                configuration.setAllowCredentials(true);
                                //헤더 허용
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);
                                //Authorization 헤더에 jwt 토큰 보낼거기떄문.
                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;

                            }
                        }));

        //csrf disable
        http
                .csrf((auth)->auth.disable());

        //from 로그인 방식 disable
        http
                .formLogin((auth)->auth.disable());

        //http basic 인증 방식 disable
        http.
                httpBasic((auth) -> auth.disable());
        //경로별 인가 작업
        http.
                authorizeHttpRequests((auth)-> auth
                        .requestMatchers("/login","/","/join").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        //JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        //addfilterAt 원하는 자리에 등록, usernameauthentication대체하는거이기에 at
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));



        return http.build();
    }

}
