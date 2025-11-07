package ar.com.uade.pds.final_project.scrim.entity;

import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scrim_participant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrimParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Role assignedRole;

    private boolean captain;
    private boolean confirmed;
    private boolean isWinner;
    private int score;
    private int mmr;


    private ScrimParticipant(Builder builder) {
        this.captain = builder.captain;
        this.confirmed = builder.confirmed;
        this.id = builder.id;
        this.user = builder.user;
        this.assignedRole = builder.assignedRole;
        this.isWinner = builder.isWinner;
        this.score = builder.score;
        this.mmr = builder.mmr;
        this.team = builder.team;
    }

    public static class Builder {
        private Long id;
        private boolean captain;
        private boolean confirmed;
        private Team team;
        private User user;
        private Role assignedRole;
        private boolean isWinner;
        private int score;
        private int mmr;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCaptain(boolean captain) {
            this.captain = captain;
            return this;
        }

        public Builder setConfirmed(boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setAssignedRole(Role assignedRole) {
            this.assignedRole = assignedRole;
            return this;
        }

        public Builder setTeam(Team team) {
            this.team = team;
            return this;
        }

        public ScrimParticipant build() {
            return new ScrimParticipant(this);
        }
    }

    public boolean equalIds(Long userId) {
        return this.user.getId().equals(userId);
    }
}





