package lopputyo.salipaivakirja.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;


@Column(unique = true, nullable = false)
@NotBlank(message = "Käyttäjätunnus ei saa olla tyhjä")
@Size(min = 3, max = 50, message = "Käyttäjätunnuksen tulee olla 3-50 merkkiä pitkä")
private String username;

@JsonIgnore
@Column(nullable = false)
@NotBlank(message = "Salasana ei saa olla tyhjä")
@Size(min = 5, message = "Salasanan tulee olla vähintään 5 merkkiä pitkä")
private String password;

@Column(nullable = false)
@NotBlank(message = "Sähköposti ei saa olla tyhjä")
private String email;

@Column(nullable = false)
private String role;

@JsonIgnore
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<Workout> workouts;

public User() {}

public User(String username, String password, String email, String role) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
}

public Long getId() {
    return id;
}
public void setId(Long id) {
    this.id = id;
}

public String getUsername() {
    return username;
}
public void setUsername(String username) {
    this.username = username;
}

public String getPassword() {
    return password;
}
public void setPassword(String password) {
    this.password = password;
}

public String getEmail() {
    return email;
}
public void setEmail(String email) {
    this.email = email;
}

public String getRole() {
    return role;
}
public void setRole(String role) {
    this.role = role;
}

public List<Workout> getWorkouts() {
    return workouts;
}
public void setWorkouts(List<Workout> workouts) {
    this.workouts = workouts;
}

@Override
public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", role='" + role + '\'' +
            '}';
}

}
