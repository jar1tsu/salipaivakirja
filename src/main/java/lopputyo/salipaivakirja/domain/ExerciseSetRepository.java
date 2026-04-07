package lopputyo.salipaivakirja.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ExerciseSetRepository extends CrudRepository<ExerciseSet, Long> {
    List<ExerciseSet> findByExerciseId(Long exerciseId);
}
