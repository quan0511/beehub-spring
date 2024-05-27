package vn.aptech.beehub.security.service;

import java.util.Optional;

import vn.aptech.beehub.dto.UserDto;


public interface UserService {
	Optional<UserDto> findByEmail(String email);
}
