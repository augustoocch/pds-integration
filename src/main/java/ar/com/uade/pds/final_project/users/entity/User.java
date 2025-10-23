package ar.com.uade.pds.final_project.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(name = "range_per_game")
    private String rangePerGame;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private List<Role> preferredRoles;

    private String region;
    private String preference;

    @Column(nullable = false, name = "email_verified")
    private boolean emailVerified;

    // Constructor privado para el Builder
    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.rangePerGame = builder.rangePerGame;
        this.preferredRoles = builder.preferredRoles;
        this.region = builder.region;
        this.preference = builder.preference;
        this.emailVerified = builder.emailVerified;
    }

    // Builder manual
    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String passwordHash;
        private String rangePerGame;
        private List<Role> preferredRoles;
        private String region;
        private String preference;
        private boolean emailVerified = false;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder rangePerGame(String rangePerGame) { this.rangePerGame = rangePerGame; return this; }
        public Builder preferredRoles(List<Role> preferredRoles) { this.preferredRoles = preferredRoles; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder preference(String preference) { this.preference = preference; return this; }
        public Builder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }

        public User build() {
            return new User(this);
        }
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }
}