package lopputyo.salipaivakirja;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createNewUser() {
        User user = new User("testimatti", "salasana123", "matti@testi.fi", "ROLE_USER");
        userRepository.save(user);

        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void findByUsernameReturnUser() {
        User user = new User("etsikayttaja", "salasana123", "etsi@testi.fi", "ROLE_USER");
        userRepository.save(user);

        User found = userRepository.findByUsername("etsikayttaja");
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("etsi@testi.fi");
    }

    @Test
    public void findByUsernameNotReturnNull() {
        User found = userRepository.findByUsername("eitallane");
        assertThat(found).isNull();
    }
}
