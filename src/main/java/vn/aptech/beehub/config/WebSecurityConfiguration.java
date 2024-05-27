package vn.aptech.beehub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import vn.aptech.beehub.security.service.AuthenticationService;


@Configuration
public class WebSecurityConfiguration {
	@Autowired
	private AuthenticationService authenticationService;
	
	
	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	protected void globalConfiguration(AuthenticationManagerBuilder manager) throws Exception {
		manager.userDetailsService(authenticationService).passwordEncoder(encoder());
	}
	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(new AntPathRequestMatcher("/**"));
    }
}
