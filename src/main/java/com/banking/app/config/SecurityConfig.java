package com.banking.app.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.banking.app.security.CustomUserDetailsService;
import com.banking.app.security.JwtAuthenticationEntryPoint;
import com.banking.app.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	
	public static final String[] PUBLIC_URLS = {
			"/api/v1/auth/login",
			"/api/v1/auth/register",
			"/v3/api-docs",
			"/v2/api-docs",
			"/swagger-resources/**",
			"/swagger-ui/**",
			"/webjars/**"
	};
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Autowired
	private JwtAuthenticationFilter jwtauthenticationFilter;
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// TODO Auto-generated method stub
//			http.csrf().disable()
//			.authorizeHttpRequests()
//			.antMatchers("/api/v1/auth/**").permitAll()
//			.antMatchers(HttpMethod.GET).permitAll()
//			.anyRequest()
//			.authenticated()
//			.and()
//			.exceptionHandling()
//			.authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
//			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        
//			http.addFilterBefore(this.jwtauthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//	}
//	
	
	    @Bean
	    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf().disable()
	        .authorizeHttpRequests()
	        .antMatchers(PUBLIC_URLS).permitAll()
	        .antMatchers(HttpMethod.GET).permitAll()
	        .antMatchers(HttpMethod.DELETE).authenticated()
	        .antMatchers(HttpMethod.PUT).permitAll()
	        .antMatchers(HttpMethod.POST).permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.exceptionHandling()
			.authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	
		http.addFilterBefore(this.jwtauthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//		return http.build();
		
		http.authenticationProvider(daoAuthenticationProvider());
		
		 DefaultSecurityFilterChain  defaultSecurityFilterChain = http.build();
		 return defaultSecurityFilterChain;
	    }
		
	
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//    	auth.userDetailsService(this.customUserDetailService).passwordEncoder(passwordEncoder());
//    }
//    
//    @Bean
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		// TODO Auto-generated method stub
//    	auth.userDetailsService(this.customUserDetailsService).passwordEncoder(passwordEncoder());
//	}
//    
//    

	    

	@Bean
    public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
    	
    }

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.customUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
		
	}
	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception{
		// TODO Auto-generated method stub
		return  configuration.getAuthenticationManager();
	}
	
	@Bean
    public FilterRegistrationBean coresFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("Authorization");
        corsConfiguration.addAllowedHeader("Content-Type");
        corsConfiguration.addAllowedHeader("Accept");
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("DELETE");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.addAllowedMethod("OPTIONS");
        corsConfiguration.setMaxAge(3600L);
        
        
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        
        bean.setOrder(-110);
        return bean;
    }

}
