package lopputyo.salipaivakirja.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @JsonIgnoreProperties("exercises")
    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @JsonIgnore
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private List<ExerciseMuscleGroup> muscleGroups;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    private List<ExerciseSet> exerciseSets; 

    public Exercise() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Workout getWorkout() {
        return workout;
    }
    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public List<ExerciseMuscleGroup> getMuscleGroups() {
        return muscleGroups;
    }
    public void setMuscleGroups(List<ExerciseMuscleGroup> muscleGroups) {
        this.muscleGroups = muscleGroups;
    }

    public List<ExerciseSet> getExerciseSets() {
        return exerciseSets;
    }
    public void setExerciseSets(List<ExerciseSet> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    
}
