package com.internship.amazingtaxiservice.taxiservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http.cors().and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/setTaxiToBooking").permitAll()
                .antMatchers(HttpMethod.GET, "/api/searchBooking").permitAll()
                .antMatchers(HttpMethod.GET, "/api/userByToken").permitAll()
                .antMatchers(HttpMethod.GET, "/api/searchTaxi").permitAll()
                .antMatchers(HttpMethod.GET, "/api/searchUser").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/user/changeUserRole").hasAnyAuthority("ADMIN","USER")
                .antMatchers(HttpMethod.POST, "/api/auth/user/changeEmail").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/user/requestChangeEmail").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/auth/user/userProfile").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/api/taxi/taxisByStatus").permitAll()
                .antMatchers(HttpMethod.GET, "/api/booking/pastBookings").permitAll()
                .antMatchers(HttpMethod.GET, "/api/booking/reservedTaxis").permitAll()
                .antMatchers(HttpMethod.POST, "/api/user/changePassword").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/resendActivationEmail/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/register/").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/signin/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/activateUser").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/signin/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/activateUser").permitAll()
                .antMatchers(HttpMethod.GET, "/bookings/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/bookings/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/bookings/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/booking/{id}**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/taxis/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/taxis/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/taxis/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/taxi/{id}**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/api/auth/signin/").permitAll()
                .antMatchers(HttpMethod.POST, "/api/user/savePassword").permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/user/forgotPassword").permitAll()
                .antMatchers(HttpMethod.GET, "/bookings/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/bookings/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/bookings/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/booking/{id}**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/taxis/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.DELETE, "/taxis/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/taxis/**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/taxi/{id}**").hasAnyAuthority("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/user/{id}**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/users/**").hasAuthority("ADMIN")
                .antMatchers("/user/changePassword*", "/user/savePassword*", "/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
        //@formatter:on
    }
}
