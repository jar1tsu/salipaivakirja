package lopputyo.salipaivakirja.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;
import lopputyo.salipaivakirja.domain.Workout;
import lopputyo.salipaivakirja.domain.WorkoutRepository;

@Controller
public class WorkoutController {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public WorkoutController(WorkoutRepository workoutRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("role") String role, Model model) {

        if (username.length() < 3) {
            model.addAttribute("error", "Käyttäjätunnuksen on oltava vähintään 3 merkkiä pitkä.");
            return "register";
        }

        if (password.length() < 5) {
            model.addAttribute("error", "Salasanan on oltava vähintään 5 merkkiä pitkä.");
            return "register";
        }

        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Käyttäjätunnus on jo käytössä.");
            return "register";
        }

        String validatedRole = "ROLE_COACH".equals(role) ? "ROLE_COACH" : "ROLE_USER";
        User user = new User(username, passwordEncoder.encode(password), email, validatedRole);
        userRepository.save(user);
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/workouts")
    public String getWorkouts(Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        if (user.getRole().equals("ROLE_ADMIN")) {
            model.addAttribute("workouts", workoutRepository.findAll());
        } else {
            model.addAttribute("workouts", workoutRepository.findByUserId(user.getId()));
        }
        model.addAttribute("username", auth.getName());
        model.addAttribute("userId", user.getId());
        return "workouts";
    }

    @GetMapping("/workouts/add")
    public String addWorkout(Model model) {
        model.addAttribute("workout", new Workout());
        return "addworkout";
    }

    @PostMapping("/workouts/save")
    public String saveWorkout(@Valid @ModelAttribute Workout workout, BindingResult result, Authentication auth) {
        if (result.hasErrors()) {
            return "addworkout";
        }
        User user = userRepository.findByUsername(auth.getName());
        workout.setUser(user);
        workoutRepository.save(workout);
        return "redirect:/workouts";
    }

    @GetMapping("/workouts/edit/{id}")
    public String editWorkout(@PathVariable("id") Long id, Model model, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout workout = workoutRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        if (!workout.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/workouts";
        }
        model.addAttribute("workout", workout);
        return "editworkout";
    }

    @PostMapping("/workouts/update/{id}")
    public String updateWorkout(@PathVariable("id") Long id, @Valid @ModelAttribute("workout") Workout workout, BindingResult result, Authentication auth) {
        if (result.hasErrors()) {
            return "editworkout";
        }
        User user = userRepository.findByUsername(auth.getName());
        Workout existing = workoutRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        if (!existing.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/workouts";
        }
        workout.setId(id);
        workout.setUser(existing.getUser());
        workoutRepository.save(workout);
        return "redirect:/workouts";
    }

    @GetMapping("/workouts/delete/{id}")
    public String deleteWorkout(@PathVariable("id") Long id, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        Workout workout = workoutRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ei ole oikea Id:" + id));
        if (!workout.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/workouts";
        }
        workoutRepository.deleteById(id);
        return "redirect:/workouts";
    }  

    @GetMapping("/workouts/user/{userId}")
    public String getUserWorkouts(@PathVariable Long userId, Model model, Authentication auth) {
        String role = userRepository.findByUsername(auth.getName()).getRole();
    
        if (!role.equals("ROLE_COACH") && !role.equals("ROLE_ADMIN")) {
        return "redirect:/workouts";
        }
    
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Ei löydy: " + userId));
        model.addAttribute("workouts", workoutRepository.findByUserId(userId));
        model.addAttribute("username", targetUser.getUsername());
        model.addAttribute("userId", userId);
        return "workouts";
    }
}
