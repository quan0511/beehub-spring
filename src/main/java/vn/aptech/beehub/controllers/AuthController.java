package vn.aptech.beehub.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import vn.aptech.beehub.exception.TokenRefreshException;
import vn.aptech.beehub.models.ERole;
import vn.aptech.beehub.models.RefreshToken;
import vn.aptech.beehub.models.Role;
import vn.aptech.beehub.models.User;
import vn.aptech.beehub.payload.request.LoginRequest;
import vn.aptech.beehub.payload.request.SignupRequest;
import vn.aptech.beehub.payload.request.TokenRefreshRequest;
import vn.aptech.beehub.payload.response.JwtResponse;
import vn.aptech.beehub.payload.response.MessageResponse;
import vn.aptech.beehub.payload.response.TokenRefreshResponse;
import vn.aptech.beehub.repository.RoleRepository;
import vn.aptech.beehub.repository.UserRepository;
import vn.aptech.beehub.security.jwt.JwtUtils;
import vn.aptech.beehub.security.services.RefreshTokenService;
import vn.aptech.beehub.security.services.UserDetailsImpl;

import java.util.*;

@Tag(name = "Auth")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User user = userRepository.findById(userDetails.getId()).get();
        if(!user.is_active()) {
        	user.set_active(true);
        	userRepository.save(user);
        }
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(JwtResponse.builder()
                        .id(userDetails.getId())
                        .username(user.getUsername())
                        .email(userDetails.getEmail())
                        .image(user.getImage() != null ? user.getImage().getMedia() : "")
                        .background(user.getBackground() != null ? user.getBackground().getMedia() : "")
                        .createdAt(user.getCreate_at())
                        .token(jwt)
                        .type("Bearer")
                        .roles(roles)
                        .build()
                );
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .fullname(signUpRequest.getFullName())
                .password(encoder.encode(signUpRequest.getPassword()))
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);

        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String token = jwtUtils.generateTokenFromEmail(user.getEmail());
                        return ResponseEntity.ok(JwtResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .image(user.getImage() != null ? user.getImage().getMedia() : "")
                                .background(user.getBackground() != null ? user.getBackground().getMedia() : "")
                                .createdAt(user.getCreate_at())
                                .token(token)
                                .type("Bearer")
                                .roles(user.getRoles().stream().map(role -> role.getName().name()).toList())
                                .build());
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not in database!"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> validationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
