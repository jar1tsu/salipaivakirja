package lopputyo.salipaivakirja.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;
import lopputyo.salipaivakirja.domain.Workout;
import lopputyo.salipaivakirja.domain.WorkoutRepository;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutRestController {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    public WorkoutRestController(WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Iterable<Workout> getWorkouts(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        if (user.getRole().equals("ROLE_ADMIN")) {
            return workoutRepository.findAll();
        }
        return workoutRepository.findByUserId(user.getId());
    }

    @GetMapping("/{id}")
    public Workout getWorkout(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy: " + id));
        if (!workout.getUser().getId().equals(user.getId())
                && !user.getRole().equals("ROLE_COACH")
                && !user.getRole().equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        return workout;
    }

    @PostMapping
    public Workout addWorkout(@Valid @RequestBody Workout workout, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        workout.setUser(user);
        return workoutRepository.save(workout);
    }

    @PutMapping("/{id}")
    public Workout updateWorkout(@PathVariable Long id, @Valid @RequestBody Workout updatedWorkout, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout existing = workoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy: " + id));
        if (!existing.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia muokata treeniä");
        }
        updatedWorkout.setId(id);
        updatedWorkout.setUser(existing.getUser());
        return workoutRepository.save(updatedWorkout);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkout(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy: " + id));
        if (user.getRole().equals("ROLE_COACH")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia poistaa treeniä");
        }
        if (!workout.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia poistaa treeniä");
        }
        workoutRepository.deleteById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Workout> getUserWorkouts(@PathVariable Long userId, Authentication auth) {
        String role = userRepository.findByUsername(auth.getName()).getRole();
        if (!role.equals("ROLE_COACH") && !role.equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        return workoutRepository.findByUserId(userId);
    }
}
