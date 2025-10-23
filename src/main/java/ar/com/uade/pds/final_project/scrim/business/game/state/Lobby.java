package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lobby implements ScrimState {

    public Lobby() {
        log.info("Scrim in Lobby state!");
    }

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim must be confirmed before starting");
    }

    @Override
    public void cancel(Scrim scrim) {
        scrim.setState(new Cancelled());
    }

    @Override
    public void confirm(Scrim scrim) {
        scrim.setState(new Confirmed(scrim));
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim didn't start yet");
    }
}