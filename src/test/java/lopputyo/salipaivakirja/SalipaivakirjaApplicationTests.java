package lopputyo.salipaivakirja;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lopputyo.salipaivakirja.web.AuthRestController;
import lopputyo.salipaivakirja.web.ExerciseController;
import lopputyo.salipaivakirja.web.ExerciseRestController;
import lopputyo.salipaivakirja.web.WorkoutController;
import lopputyo.salipaivakirja.web.WorkoutRestController;

@SpringBootTest
class SalipaivakirjaApplicationTests {

	@Autowired
	private WorkoutController workoutController;

	@Autowired
	private ExerciseController exerciseController;

	@Autowired
	private WorkoutRestController workoutRestController;

	@Autowired
	private ExerciseRestController exerciseRestController;

	@Autowired
	private AuthRestController authRestController;

	@Test
	void contextLoads() {
	}

	@Test
	void workoutControllerNotNull() {
		assertThat(workoutController).isNotNull();
	}

	@Test
	void exerciseControllerNotNull() {
		assertThat(exerciseController).isNotNull();
	}

	@Test
	void workoutRestControllerNotNull() {
		assertThat(workoutRestController).isNotNull();
	}

	@Test
	void exerciseRestControllerNotNull() {
		assertThat(exerciseRestController).isNotNull();
	}

	@Test
	void authRestControllerNotNull() {
		assertThat(authRestController).isNotNull();
	}
}
