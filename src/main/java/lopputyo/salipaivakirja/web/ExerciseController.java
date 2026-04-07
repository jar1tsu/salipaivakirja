package lopputyo.salipaivakirja.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lopputyo.salipaivakirja.domain.Exercise;
import lopputyo.salipaivakirja.domain.ExerciseMuscleGroup;
import lopputyo.salipaivakirja.domain.ExerciseMuscleGroupRepository;
import lopputyo.salipaivakirja.domain.ExerciseRepository;
import lopputyo.salipaivakirja.domain.ExerciseSet;
import lopputyo.salipaivakirja.domain.ExerciseSetRepository;
import lopputyo.salipaivakirja.domain.MuscleGroup;
import lopputyo.salipaivakirja.domain.MuscleGroupRepository;
import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;
import lopputyo.salipaivakirja.domain.Workout;
import lopputyo.salipaivakirja.domain.WorkoutRepository;

@Controller
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final MuscleGroupRepository muscleGroupRepository;
    private final ExerciseMuscleGroupRepository exerciseMuscleGroupRepository;
    private final ExerciseSetRepository exerciseSetRepository;
    private final UserRepository userRepository;
   

    public ExerciseController(ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository, MuscleGroupRepository muscleGroupRepository, ExerciseMuscleGroupRepository exerciseMuscleGroupRepository, ExerciseSetRepository exerciseSetRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
        this.muscleGroupRepository = muscleGroupRepository;
        this.exerciseSetRepository = exerciseSetRepository;
        this.exerciseMuscleGroupRepository = exerciseMuscleGroupRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/exercises")
    public String getExercises(Model model, @RequestParam(value = "workoutId", required = false) Long workoutId, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userId", user.getId());

        if (workoutId != null) {
            model.addAttribute("exercises", exerciseRepository.findByWorkoutId(workoutId));
            model.addAttribute("workoutId", workoutId);
        } else {
            List<Exercise> userExercises = new ArrayList<>();
            for (Workout w : workoutRepository.findByUserId(user.getId())) {
                userExercises.addAll(exerciseRepository.findByWorkoutId(w.getId()));
            }
            model.addAttribute("exercises", userExercises);
        }
        return "exercises";
    }

    @GetMapping("/exercises/add")
    public String addExerciseForm(Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        model.addAttribute("exercise", new Exercise());
        model.addAttribute("workouts", workoutRepository.findByUserId(user.getId()));
        model.addAttribute("muscleGroups", muscleGroupRepository.findAll());
        return "addexercise";
    }

    @PostMapping("/exercises/save")
        public String saveExercise(@RequestParam("name") String name, @RequestParam("workoutId") Long workoutId, @RequestParam("muscleGroupId") Long muscleGroupId, @RequestParam("weights") List<Double> weights, @RequestParam("reps") List<Integer> reps) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + workoutId));

        Exercise exercise = new Exercise(name);
        exercise.setWorkout(workout);
        exerciseRepository.save(exercise);

        MuscleGroup muscleGroup = muscleGroupRepository.findById(muscleGroupId).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + muscleGroupId));
        exerciseMuscleGroupRepository.save(new ExerciseMuscleGroup(exercise, muscleGroup));

        for (int i = 0; i < weights.size(); i++) {
            ExerciseSet set = new ExerciseSet(i + 1, weights.get(i), reps.get(i), false, exercise);
            exerciseSetRepository.save(set);
        }
        return "redirect:/exercises?workoutId=" + workoutId;
    }

    @GetMapping("/exercises/edit/{id}")
    public String editExerciseForm(@PathVariable("id") Long id, Model model, Authentication auth) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        User user = userRepository.findByUsername(auth.getName());
        model.addAttribute("exercise", exercise);
        model.addAttribute("workouts", workoutRepository.findByUserId(user.getId()));
        return "editexercise";
    }

    @PostMapping("/exercises/update/{id}")
    public String updateExercise(@PathVariable("id") Long id, @ModelAttribute("exercise") Exercise updatedExercise, @RequestParam("workoutId") Long workoutId) {
        updatedExercise.setId(id);
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + workoutId));
        updatedExercise.setWorkout(workout);
        exerciseRepository.save(updatedExercise);
        return "redirect:/exercises?workoutId=" + workoutId;
    }

    @GetMapping("/exercises/delete/{id}")
    public String deleteExercise(@PathVariable("id") Long id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        Long workoutId = exercise.getWorkout().getId();

        for (ExerciseMuscleGroup emg : exerciseMuscleGroupRepository.findByExerciseId(id)) {
            exerciseMuscleGroupRepository.delete(emg);
        }
        for (ExerciseSet set : exerciseSetRepository.findByExerciseId(id)) {
            exerciseSetRepository.delete(set);
        }

        exerciseRepository.deleteById(id);
        return "redirect:/exercises?workoutId=" + workoutId;
    }
}
