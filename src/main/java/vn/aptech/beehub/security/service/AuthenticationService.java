package vn.aptech.beehub.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.aptech.beehub.repository.UserRepository;

import org.springframework.security.core.userdetails.User;
@Service
public class AuthenticationService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRep;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		vn.aptech.beehub.models.User user= userRep.findByEmail(email).orElseThrow(()->
			new UsernameNotFoundException("Invaild Email or Password"));
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
		List<SimpleGrantedAuthority> authorities = List.of(authority);
		return new User(email, user.getPassword(), false, false, false, false, authorities);
	}

}
