package ar.com.uade.pds.final_project.scrim.entity;

import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "player_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrim_id", nullable = false)
    private Scrim scrim;

    @Enumerated(EnumType.STRING)
    @Column(name = "assigned_role")
    private Role assignedRole;

    @Column(length = 100)
    private String game;

    @Column(length = 50)
    private String region;

    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MatchResult result;

    @Column(name = "mmr_before")
    private Integer mmrBefore;

    @Column(name = "mmr_after")
    private Integer mmrAfter;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public enum MatchResult {
        WIN, LOSE, DRAW
    }

    private PlayerStats(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.scrim = builder.scrim;
        this.assignedRole = builder.assignedRole;
        this.game = builder.game;
        this.region = builder.region;
        this.score = builder.score;
        this.result = builder.result;
        this.mmrBefore = builder.mmrBefore;
        this.mmrAfter = builder.mmrAfter;
    }

    public static class Builder {
        private Long id;
        private User user;
        private Scrim scrim;
        private Role assignedRole;
        private String game;
        private String region;
        private Integer score;
        private MatchResult result;
        private Integer mmrBefore;
        private Integer mmrAfter;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder scrim(Scrim scrim) {
            this.scrim = scrim;
            return this;
        }

        public Builder assignedRole(Role assignedRole) {
            this.assignedRole = assignedRole;
            return this;
        }

        public Builder game(String game) {
            this.game = game;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder score(Integer score) {
            this.score = score;
            return this;
        }

        public Builder result(MatchResult result) {
            this.result = result;
            return this;
        }

        public Builder mmrBefore(Integer mmrBefore) {
            this.mmrBefore = mmrBefore;
            return this;
        }

        public Builder mmrAfter(Integer mmrAfter) {
            this.mmrAfter = mmrAfter;
            return this;
        }

        public PlayerStats build() {
            return new PlayerStats(this);
        }
    }
}
