package ar.com.uade.pds.final_project.scrim.entity;

import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimState;
import ar.com.uade.pds.final_project.scrim.business.game.state.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.business.game.state.Searching;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "scrim")
@Getter
@Setter
public class Scrim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String game;
    private String format;
    private int players;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scrim_roles", joinColumns = @JoinColumn(name = "scrim_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private List<Role> roles;

    private String region;
    private String latency;
    @Column(name = "est_duration")
    private int estDuration;
    private String mode;

    @Transient
    private ScrimState state;

    @Enumerated(EnumType.STRING)
    private ScrimStateType stateType;

    @ManyToMany
    @JoinTable(
            name = "scrim_participants",
            joinColumns = @JoinColumn(name = "scrim_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    // Constructor privado para Builder
    private Scrim(Builder builder) {
        this.id = builder.id;
        this.game = builder.game;
        this.format = builder.format;
        this.players = builder.players;
        this.roles = builder.roles;
        this.region = builder.region;
        this.latency = builder.latency;
        this.estDuration = builder.estDuration;
        this.mode = builder.mode;
        this.state = builder.state != null ? builder.state : new Searching();
        this.stateType = builder.stateType != null ? builder.stateType : ScrimStateType.SEARCHING;
        this.participants = builder.participants != null ? builder.participants : new HashSet<>();
    }

    // Builder manual
    public static class Builder {
        private Long id;
        private String game;
        private String format;
        private int players;
        private List<Role> roles;
        private String region;
        private String latency;
        private int estDuration;
        private String mode;
        private ScrimState state;
        private ScrimStateType stateType;
        private Set<User> participants;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder game(String game) { this.game = game; return this; }
        public Builder format(String format) { this.format = format; return this; }
        public Builder players(int players) { this.players = players; return this; }
        public Builder roles(List<Role> roles) { this.roles = roles; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder latency(String latency) { this.latency = latency; return this; }
        public Builder estDuration(int estDuration) { this.estDuration = estDuration; return this; }
        public Builder mode(String modal) { this.mode = modal; return this; }
        public Builder state(ScrimState state) { this.state = state; return this; }
        public Builder stateType(ScrimStateType stateType) { this.stateType = stateType; return this; }
        public Builder participants(Set<User> participants) { this.participants = participants; return this; }

        public Scrim build() {
            return new Scrim(this);
        }
    }

    public void setState(ScrimState newState) {
        this.state = newState;
        this.stateType = ScrimStateType.fromClass(newState.getClass());
    }

    public void start() { this.state.start(this); }
    public void cancel() { this.state.cancel(this); }
    public void confirm() { this.state.confirm(this); }
    public void end() { this.state.end(this); }

    public boolean hasValidStateToJoin() {
        return this.state instanceof Searching;
    }

    public boolean isFull() { return participants.size() >= players; }

    public void addParticipant(User u) { this.participants.add(u); }
}
