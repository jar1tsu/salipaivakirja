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

import lopputyo.salipaivakirja.domain.Exercise;
import lopputyo.salipaivakirja.domain.ExerciseRepository;
import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;
import lopputyo.salipaivakirja.domain.Workout;
import lopputyo.salipaivakirja.domain.WorkoutRepository;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseRestController {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    public ExerciseRestController(ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/workout/{workoutId}")
    public List<Exercise> getExercisesByWorkout(@PathVariable Long workoutId, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy: " + workoutId));
        if (!workout.getUser().getId().equals(user.getId())
                && !user.getRole().equals("ROLE_ADMIN")
                && !user.getRole().equals("ROLE_COACH")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        return exerciseRepository.findByWorkoutId(workoutId);
    }

    @GetMapping("/{id}")
    public Exercise getExercise(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy: " + id));
        if (!exercise.getWorkout().getUser().getId().equals(user.getId())
                && !user.getRole().equals("ROLE_ADMIN")
                && !user.getRole().equals("ROLE_COACH")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        return exercise;
    }

    @PostMapping
    public Exercise addExercise(@RequestBody Exercise exercise, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout workout = workoutRepository.findById(exercise.getWorkout().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy"));
        if (!workout.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        return exerciseRepository.save(exercise);
    }

    @PutMapping("/{id}")
    public Exercise updateExercise(@PathVariable Long id, @RequestBody Exercise updatedExercise, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy: " + id));
        if (!existing.getWorkout().getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        existing.setName(updatedExercise.getName());
        if (updatedExercise.getWorkout() != null) {
            existing.setWorkout(updatedExercise.getWorkout());
        }
        return exerciseRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteExercise(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ei löydy: " + id));
        if (!exercise.getWorkout().getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ei oikeuksia");
        }
        exerciseRepository.deleteById(id);
    }
}
