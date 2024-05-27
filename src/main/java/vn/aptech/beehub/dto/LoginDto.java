package vn.aptech.beehub.dto;

import jakarta.validation.constraints.NotEmpty;

public class LoginDto {
	@NotEmpty(message = "Email can not emty")
	private String email;
	@NotEmpty(message = "Password can not be emty")
	private String password;
}
