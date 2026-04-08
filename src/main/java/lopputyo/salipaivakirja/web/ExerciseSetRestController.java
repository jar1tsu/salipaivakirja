package lopputyo.salipaivakirja.web;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lopputyo.salipaivakirja.domain.Exercise;
import lopputyo.salipaivakirja.domain.ExerciseRepository;
import lopputyo.salipaivakirja.domain.ExerciseSet;
import lopputyo.salipaivakirja.domain.ExerciseSetRepository;
import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;

@RestController
@RequestMapping("/api/exercisesets")
public class ExerciseSetRestController {

    private final ExerciseSetRepository exerciseSetRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public ExerciseSetRestController(ExerciseSetRepository exerciseSetRepository,
            ExerciseRepository exerciseRepository, UserRepository userRepository) {
        this.exerciseSetRepository = exerciseSetRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/exercise/{exerciseId}")
    public List<ExerciseSet> getSetsByExercise(@PathVariable Long exerciseId, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Ei löydy: " + exerciseId));
        if (!exercise.getWorkout().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Ei oikeuksia");
        }
        return exerciseSetRepository.findByExerciseId(exerciseId);
    }

    @PostMapping
    public ExerciseSet addSet(@RequestBody ExerciseSet exerciseSet, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Exercise exercise = exerciseRepository.findById(exerciseSet.getExercise().getId())
                .orElseThrow(() -> new IllegalArgumentException("Ei löydy"));
        if (!exercise.getWorkout().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Ei oikeuksia");
        }
        return exerciseSetRepository.save(exerciseSet);
    }

    @PutMapping("/{id}")
    public ExerciseSet updateSet(@PathVariable Long id, @RequestBody ExerciseSet updatedSet, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        ExerciseSet existing = exerciseSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ei löydy: " + id));
        if (!existing.getExercise().getWorkout().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Ei oikeuksia");
        }
        existing.setSetNumber(updatedSet.getSetNumber());
        existing.setWeight(updatedSet.getWeight());
        existing.setReps(updatedSet.getReps());
        existing.setCompleted(updatedSet.getCompleted());
        return exerciseSetRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteSet(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        ExerciseSet exerciseSet = exerciseSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ei löydy: " + id));
        if (!exerciseSet.getExercise().getWorkout().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Ei oikeuksia");
        }
        exerciseSetRepository.deleteById(id);
    }
}
