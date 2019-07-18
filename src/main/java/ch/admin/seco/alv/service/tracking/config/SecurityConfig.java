package ch.admin.seco.alv.service.tracking.config;

import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import ch.admin.seco.alv.service.tracking.service.security.Role;
import ch.admin.seco.alv.shared.jwt.JWTFilterConfigurer;
import ch.admin.seco.alv.shared.jwt.TokenToAuthenticationConverter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Import(SecurityProblemSupport.class)
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;

    private final TokenToAuthenticationConverter tokenToAuthenticationConverter;

    public SecurityConfig(SecurityProblemSupport problemSupport,
        TokenToAuthenticationConverter tokenToAuthenticationConverter) {

        this.problemSupport = problemSupport;
        this.tokenToAuthenticationConverter = tokenToAuthenticationConverter;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/webjars/springfox-swagger-ui/**")
            .antMatchers("/swagger-resources/**")
            .antMatchers("/v2/api-docs")
            .antMatchers("/swagger-ui.html");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .anonymous().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/tracking-item").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/health").permitAll()
                .antMatchers("/management/**").hasAuthority(Role.ADMIN.getValue())
		        .anyRequest().authenticated()
                .and()
                .apply(jwtFilterConfigurer());
        // @formatter:on
    }

    private JWTFilterConfigurer jwtFilterConfigurer() {
        return new JWTFilterConfigurer(this.tokenToAuthenticationConverter);
    }
}
