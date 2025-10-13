package ar.com.uade.pds.final_project.scrim.entity;

import ar.com.uade.pds.final_project.scrim.business.ScrimState;
import ar.com.uade.pds.final_project.scrim.business.ScrimStateType;
import ar.com.uade.pds.final_project.scrim.business.Searching;
import ar.com.uade.pds.final_project.users.entity.Role;
import ar.com.uade.pds.final_project.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "scrim")
@Getter
@Setter
@Builder
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
    private String modal;
    @Transient
    private ScrimState state = new Searching();

    @Enumerated(EnumType.STRING)
    private ScrimStateType stateType = ScrimStateType.SEARCHING;

    @ManyToMany
    @JoinTable(
            name = "scrim_participants",
            joinColumns = @JoinColumn(name = "scrim_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    public void setState(ScrimState newState) {
        this.state = newState;
        this.stateType = ScrimStateType.fromClass(newState.getClass());
    }

    public void start() {
        this.state.start(this);
    }

    public void cancel() {
        this.state.cancel(this);
    }

    public void confirm() {
        this.state.confirm(this);
    }

    public void end() {
        this.state.end(this);
    }

    public boolean hasValidStateToJoin() {
        if(this.state == null) return false;
        return this.state instanceof Searching;
    }

    public boolean isFull() {
        return participants.size() >= players;
    }

    public void addParticipant(User u) {
        this.participants.add(u);
    }
}


