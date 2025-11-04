package ar.com.uade.pds.final_project.scrim.entity;

import ar.com.uade.pds.final_project.notifications.event.DomainEvent;
import ar.com.uade.pds.final_project.scrim.business.game.state.*;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "scrim")
@Getter
@Setter
@NoArgsConstructor
public class Scrim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String game;
    private String format;
    private int players;
    private Long idCreator;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scrim_roles", joinColumns = @JoinColumn(name = "scrim_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private List<Role> roles;

    private String region;
    private int latency;
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
    @ElementCollection
    @CollectionTable(name = "scrim_confirmed_users", joinColumns = @JoinColumn(name = "scrim_id"))
    @Column(name = "user_id")
    private Set<Long> confirmedUsers = new HashSet<>();
    private Integer mmrMin;
    private Integer mmrMax;
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();


    private Scrim(Builder builder) {
        this.id = builder.id;
        this.idCreator = builder.idCreator;
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
        this.mmrMin = builder.mmrMin;
        this.mmrMax = builder.mmrMax;
    }

    // Builder manual
    public static class Builder {
        private Long id;
        private String game;
        private String format;
        private int players;
        private List<Role> roles;
        private String region;
        private Integer latency;
        private int estDuration;
        private String mode;
        private ScrimState state;
        private ScrimStateType stateType;
        private Set<User> participants;
        private Integer mmrMin;
        private Integer mmrMax;
        private Long idCreator;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder game(String game) { this.game = game; return this; }
        public Builder format(String format) { this.format = format; return this; }
        public Builder players(int players) { this.players = players; return this; }
        public Builder roles(List<Role> roles) { this.roles = roles; return this; }
        public Builder region(String region) { this.region = region; return this; }
        public Builder latency(Integer latency) { this.latency = latency; return this; }
        public Builder estDuration(int estDuration) { this.estDuration = estDuration; return this; }
        public Builder mode(String modal) { this.mode = modal; return this; }
        public Builder state(ScrimState state) { this.state = state; return this; }
        public Builder stateType(ScrimStateType stateType) { this.stateType = stateType; return this; }
        public Builder idCreator(Long idCreator) { this.idCreator = idCreator; return this; }
        public Builder mmrMin(Integer mmrMin) { this.mmrMin = mmrMin; return this; }
        public Builder mmrMax(Integer mmrMax) { this.mmrMax = mmrMax; return this; }

        public Scrim build() {
            return new Scrim(this);
        }
    }

    public void setState(ScrimState newState) {
        this.state = newState;
        this.stateType = ScrimStateType.fromClass(newState.getClass());
    }

    public void confirm(User user) {
        this.state.confirm(this, user);
    }

    public void cancel(Long userId) {
        this.state.cancel(this, userId);
    }

    public void end() {
        this.state.end(this);
    }

    public boolean hasValidStateToJoin() {
        return this.stateType == ScrimStateType.SEARCHING;
    }

    public boolean isFull() {
        return participants.size() >= players;
    }

    public void addParticipant(User u) {
        this.participants.add(u);
    }

    public void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    public void setCurrentState() {
        ScrimState state = ScrimStateType.scrimStateFromString(this.stateType);
        this.setState(state);
    }
}
