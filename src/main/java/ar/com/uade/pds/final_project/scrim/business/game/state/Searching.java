package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;

public class Searching implements ScrimState {

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim not in lobby");
    }

    @Override
    public void cancel(Scrim scrim) {
        scrim.setState(new Cancelled());
    }

    @Override
    public void confirm(Scrim scrim) {
        throw new IllegalStateException("Scrim not in lobby");
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim didn't start yet");
    }
}