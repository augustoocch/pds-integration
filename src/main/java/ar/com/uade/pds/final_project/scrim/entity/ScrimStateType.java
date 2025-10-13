package ar.com.uade.pds.final_project.scrim.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ScrimStateType {
    SEARCHING(Searching.class),
    CONFIRMED(Confirmed.class),
    CANCELLED(Cancelled.class),
    LOBBY(Lobby.class),
    IN_GAME(InGame.class);

    private final Class<? extends ScrimState> stateClass;

    ScrimStateType(Class<? extends ScrimState> stateClass) {
        this.stateClass = stateClass;
    }

    private static final Map<Class<? extends ScrimState>, ScrimStateType> map =
            Arrays.stream(values()).collect(Collectors.toMap(s -> s.stateClass, s -> s));

    public static ScrimStateType fromClass(Class<? extends ScrimState> clazz) {
        return Optional.ofNullable(map.get(clazz))
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + clazz.getSimpleName()));
    }
}

