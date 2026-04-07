package lopputyo.salipaivakirja;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;
import lopputyo.salipaivakirja.domain.Workout;
import lopputyo.salipaivakirja.domain.WorkoutRepository;

@SpringBootTest
@Transactional
public class WorkoutRepositoryTest {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createNewWorkout() {
        User user = new User("testikayttaja", "salasana", "testi@testi.fi", "ROLE_USER");
        userRepository.save(user);

        Workout workout = new Workout(LocalDate.now(), 60, "Testitreeni");
        workout.setUser(user);
        workoutRepository.save(workout);

        assertThat(workout.getId()).isNotNull();
    }

    @Test
    public void findByUserIdShouldReturnWorkouts() {
        User user = new User("testikayttaja2", "salasana", "testi2@testi.fi", "ROLE_USER");
        userRepository.save(user);

        Workout workout = new Workout(LocalDate.now(), 45, "Jalkatreeni");
        workout.setUser(user);
        workoutRepository.save(workout);

        List<Workout> workouts = workoutRepository.findByUserId(user.getId());
        assertThat(workouts).hasSize(1);
        assertThat(workouts.get(0).getNotes()).isEqualTo("Jalkatreeni");
    }

    @Test
    public void findByUserIdShouldNotReturnOtherUsersWorkouts() {
        User user1 = new User("kayttaja1", "salasana", "u1@testi.fi", "ROLE_USER");
        User user2 = new User("kayttaja2", "salasana", "u2@testi.fi", "ROLE_USER");
        userRepository.save(user1);
        userRepository.save(user2);

        Workout workout = new Workout(LocalDate.now(), 30, "Käyttäjä1 treeni");
        workout.setUser(user1);
        workoutRepository.save(workout);

        List<Workout> workouts = workoutRepository.findByUserId(user2.getId());
        assertThat(workouts).isEmpty();
    }
}
