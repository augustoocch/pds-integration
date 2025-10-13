package ar.com.uade.pds.final_project.scrim.entity;

public class InGame implements ScrimState {

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim already in game");
    }

    @Override
    public void cancel(Scrim scrim) {
        scrim.setState(new Cancelled());
    }

    @Override
    public void confirm(Scrim scrim) {
        throw new IllegalStateException("Scrim already in game");
    }

    @Override
    public void end(Scrim scrim) {
        scrim.setState(new Ended());
    }
}
