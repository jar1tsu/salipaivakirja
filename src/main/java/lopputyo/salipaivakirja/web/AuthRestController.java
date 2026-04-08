package lopputyo.salipaivakirja.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthRestController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    try {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.get("username"),
                request.get("password")
            )
        );
        String token = jwtTokenProvider.generateToken(auth.getName());
        return ResponseEntity.ok(Map.of("token", token));
    } catch (Exception e) {
        return ResponseEntity.status(401).body(Map.of("error", "Väärä käyttäjätunnus tai salasana"));
    }
}

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Käyttäjätunnus ei saa olla tyhjä");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Salasana ei saa olla tyhjä");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Sähköposti ei saa olla tyhjä");
        }
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Käyttäjätunnus on jo käytössä");
        }

        String requestedRole = request.get("role");
        String role = "ROLE_COACH".equals(requestedRole) ? "ROLE_COACH" : "ROLE_USER";
        User user = new User(
            username,
            passwordEncoder.encode(password),
            email,
            role
        );
        userRepository.save(user);
        return Map.of("message", "Rekisteröityminen onnistui");
    }
}
