package lopputyo.salipaivakirja;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import lopputyo.salipaivakirja.domain.MuscleGroup;
import lopputyo.salipaivakirja.domain.MuscleGroupRepository;
import lopputyo.salipaivakirja.domain.User;
import lopputyo.salipaivakirja.domain.UserRepository;

@SpringBootApplication
public class SalipaivakirjaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalipaivakirjaApplication.class, args);
	}

	@Bean
	public CommandLineRunner workoutDemo(UserRepository userRepository, PasswordEncoder passwordEncoder, MuscleGroupRepository muscleGroupRepository) {
		return (args) -> {
			if (userRepository.findByUsername("admin") == null) {
				User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@sali.fi", "ROLE_ADMIN");
				userRepository.save(admin);
			}
			if (userRepository.findByUsername("user") == null) {
				User user = new User("user", passwordEncoder.encode("user123"), "user@sali.fi", "ROLE_USER");
				userRepository.save(user);
			}

			if (!muscleGroupRepository.findAll().iterator().hasNext()) {
            	muscleGroupRepository.save(new MuscleGroup("Rintalihas"));
            	muscleGroupRepository.save(new MuscleGroup("Selkä"));
            	muscleGroupRepository.save(new MuscleGroup("Hauislihas"));
            	muscleGroupRepository.save(new MuscleGroup("Ojentajalihas"));
            	muscleGroupRepository.save(new MuscleGroup("Olkapäät"));
            	muscleGroupRepository.save(new MuscleGroup("Jalat"));
            	muscleGroupRepository.save(new MuscleGroup("Vatsa"));
        	}
    	};
	}

}



