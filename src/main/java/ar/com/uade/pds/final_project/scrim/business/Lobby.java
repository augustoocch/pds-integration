package ar.com.uade.pds.final_project.scrim.business;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;

public class Lobby implements ScrimState {

    @Override
    public void start(Scrim scrim) {
        scrim.setState(new InGame());
    }

    @Override
    public void cancel(Scrim scrim) {
        scrim.setState(new Cancelled());
    }

    @Override
    public void confirm(Scrim scrim) {
        scrim.setState(new Confirmed());
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim didn't start yet");
    }
}