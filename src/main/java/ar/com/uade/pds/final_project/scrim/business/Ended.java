package ar.com.uade.pds.final_project.scrim.business;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;

public class Ended implements ScrimState {

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim ended");
    }

    @Override
    public void cancel(Scrim scrim) {
        throw new IllegalStateException("Scrim ended");
    }

    @Override
    public void confirm(Scrim scrim) {
        throw new IllegalStateException("Scrim ended");
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim ended");
    }
}
