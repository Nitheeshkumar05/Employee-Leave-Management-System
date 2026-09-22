package com.example.employeemanagement.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean AuthenticationProvider authenticationProvider(CustomUserDetailsService uds,PasswordEncoder encoder){
        DaoAuthenticationProvider p=new DaoAuthenticationProvider(uds); p.setPasswordEncoder(encoder); return p;
    }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
    @Bean SecurityFilterChain filterChain(HttpSecurity http,JwtAuthenticationFilter jwt)throws Exception{
        http.csrf(csrf->csrf.disable()).cors(c->c.configurationSource(corsSource())).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/departments").authenticated()
                .requestMatchers("/api/departments/**").hasRole("HR")
                .requestMatchers(HttpMethod.GET,"/api/employees/me").authenticated()
                .requestMatchers("/api/employees/**").hasRole("HR")
                .requestMatchers(HttpMethod.POST,"/api/leaves").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET,"/api/leaves/my").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET,"/api/leaves/pending").hasRole("HR")
                .requestMatchers(HttpMethod.GET,"/api/leaves").hasRole("HR")
                .requestMatchers(HttpMethod.PUT,"/api/leaves/*/approve","/api/leaves/*/reject").hasRole("HR")
                .requestMatchers(HttpMethod.DELETE,"/api/leaves/*").hasRole("EMPLOYEE")
                .anyRequest().authenticated())
            .formLogin(f->f.disable()).httpBasic(b->b.disable())
            .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"error\":\"Authentication required\"}");})
                .accessDeniedHandler((req,res,ex)->{res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"error\":\"Access denied\"}");}))
            .addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean CorsConfigurationSource corsSource(){
        CorsConfiguration c=new CorsConfiguration(); c.setAllowedOrigins(List.of("http://localhost:5173")); c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); c.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**",c); return s;
    }
}
