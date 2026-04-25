package lopputyo.salipaivakirja.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Iterable<User> getUsers(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        if (!user.getRole().equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        return userRepository.findAll();
    }
}
