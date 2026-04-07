package lopputyo.salipaivakirja.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercise_set")
public class ExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int setNumber;

    private double weight;

    private int reps;

    private Boolean completed;

    @JsonIgnoreProperties("exerciseSets")
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    public ExerciseSet() {}

    public ExerciseSet(int setNumber, double weight, int reps, Boolean completed, Exercise exercise) {
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.completed = completed;
        this.exercise = exercise;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public int getSetNumber() {
        return setNumber;
    }
    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }

    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Exercise getExercise() {
        return exercise;
    }
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    public String toString() {
        return "ExerciseSet{" +
                "id=" + id +
                ", setNumber=" + setNumber +
                ", weight=" + weight +
                ", reps=" + reps +
                ", completed=" + completed +
                '}';
    }

    
}
