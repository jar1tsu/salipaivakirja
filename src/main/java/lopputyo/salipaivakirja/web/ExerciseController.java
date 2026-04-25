package lopputyo.salipaivakirja.web;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
            Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new IllegalArgumentException("Ei löydy: " + workoutId));
            if (!workout.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN") && !user.getRole().equals("ROLE_COACH")) {
                return "redirect:/exercises";
            }
            model.addAttribute("exercises", exerciseRepository.findByWorkoutId(workoutId));
            model.addAttribute("workoutId", workoutId);
        } else {
            model.addAttribute("exercises", exerciseRepository.findByWorkoutUserId(user.getId()));
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
        public String saveExercise(@RequestParam("name") String name, @RequestParam("workoutId") Long workoutId, @RequestParam("muscleGroupId") Long muscleGroupId, @RequestParam("weights") List<Double> weights, @RequestParam("reps") List<Integer> reps, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + workoutId));
        if (!workout.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/exercises";
        }

        Exercise exercise = new Exercise();
        exercise.setName(name);
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
        User user = userRepository.findByUsername(auth.getName());
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        if (!exercise.getWorkout().getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/exercises";
        }
        model.addAttribute("exercise", exercise);
        model.addAttribute("workouts", workoutRepository.findByUserId(exercise.getWorkout().getUser().getId()));
        return "editexercise";
    }

    @PostMapping("/exercises/update/{id}")
    public String updateExercise(@PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("workoutId") Long workoutId,
            @RequestParam(value = "setIds", required = false) List<Long> setIds,
            @RequestParam(value = "weights", required = false) List<Double> weights,
            @RequestParam(value = "reps", required = false) List<Integer> reps,
            Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        if (!exercise.getWorkout().getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/exercises";
        }
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + workoutId));
        exercise.setName(name);
        exercise.setWorkout(workout);
        exerciseRepository.save(exercise);

        if (setIds != null && weights != null && reps != null
                && setIds.size() == weights.size() && setIds.size() == reps.size()) {
            for (int i = 0; i < setIds.size(); i++) {
                if (setIds.get(i) == 0) {
                    exerciseSetRepository.save(new ExerciseSet(i + 1, weights.get(i), reps.get(i), false, exercise));
                } else {
                    ExerciseSet set = exerciseSetRepository.findById(setIds.get(i)).orElse(null);
                    if (set != null) {
                        set.setWeight(weights.get(i));
                        set.setReps(reps.get(i));
                        exerciseSetRepository.save(set);
                    }
                }
            }
        }
        return "redirect:/exercises?workoutId=" + workoutId;
    }

    @GetMapping("/exercises/delete/{id}")
    public String deleteExercise(@PathVariable("id") Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        Long workoutId = exercise.getWorkout().getId();
        if (!exercise.getWorkout().getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/exercises?workoutId=" + workoutId;
        }

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
