package ar.com.uade.pds.final_project.users.entity;

import ar.com.uade.pds.final_project.scrim.entity.PlayerStats;
import ar.com.uade.pds.final_project.users.constants.Rank;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private List<Role> preferredRoles;
    private String region;
    @Column(nullable = false, name = "email_verified")
    private boolean emailVerified;

    @Enumerated(EnumType.STRING)
    private Rank userRank;
    private Integer mmr;
    private int latency;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerStats> playerStats = new ArrayList<>();

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.preferredRoles = builder.preferredRoles;
        this.region = builder.region;
        this.emailVerified = builder.emailVerified;
        this.mmr = builder.mmr;
        this.latency = builder.latency;
        this.userRank = builder.userRank;
    }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String passwordHash;
        private List<Role> preferredRoles;
        private String region;
        private boolean emailVerified = false;
        private Integer mmr;
        private int latency;
        private Rank userRank;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder userRank(Rank userRank) { this.userRank = userRank; return this; }
        public Builder preferredRoles(List<Role> preferredRoles) { this.preferredRoles = preferredRoles; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }
        public Builder mmr(Integer mmr) { this.mmr = mmr; return this; }
        public Builder latency(int latency) { this.latency = latency; return this; }

        public User build() {
            return new User(this);
        }
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void addPlayerStat(PlayerStats stat) {
        this.playerStats.add(stat);
    }
}