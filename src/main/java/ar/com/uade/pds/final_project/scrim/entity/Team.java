package ar.com.uade.pds.final_project.scrim.entity;

import ar.com.uade.pds.final_project.scrim.constants.TeamName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Table(name = "team")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TeamName name;

    private boolean winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrim_id", nullable = false)
    private Scrim scrim;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScrimParticipant> participants = new ArrayList<>();

    public boolean isFull() {
        return participants.size() >= scrim.getPlayers() / 2;
    }

    public void addParticipant(ScrimParticipant p) {
        this.participants.add(p);
        p.setTeam(this);
    }

    public static class Builder {
        private Long id;
        private TeamName name;
        private boolean winner;
        private Scrim scrim;
        private List<ScrimParticipant> participants = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(TeamName name) {
            this.name = name;
            return this;
        }

        public Builder winner(boolean winner) {
            this.winner = winner;
            return this;
        }

        public Builder scrim(Scrim scrim) {
            this.scrim = scrim;
            return this;
        }


        public Team build() {
            Team team = new Team();
            team.setId(this.id);
            team.setName(this.name);
            team.setWinner(this.winner);
            team.setScrim(this.scrim);
            return team;
        }

    }
}

