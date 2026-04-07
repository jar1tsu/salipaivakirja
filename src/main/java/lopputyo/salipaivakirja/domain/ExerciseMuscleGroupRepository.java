package lopputyo.salipaivakirja.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


public interface ExerciseMuscleGroupRepository extends CrudRepository<ExerciseMuscleGroup, Long> {
    List<ExerciseMuscleGroup> findByExerciseId(Long id);
}
