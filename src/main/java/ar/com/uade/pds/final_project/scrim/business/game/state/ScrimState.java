package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import ar.com.uade.pds.final_project.users.entity.User;

public interface ScrimState {
    void start(Scrim scrim);
    void cancel(Scrim scrim, Long userId);
    void confirm(Scrim scrim, User user);
    void end(Scrim scrim);
}
