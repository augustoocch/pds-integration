package ar.com.uade.pds.final_project.scrim.business;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;

public class Cancelled implements ScrimState {

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim cancelled");
    }

    @Override
    public void cancel(Scrim scrim) {
        throw new IllegalStateException("Scrim already cancelled");
    }

    @Override
    public void confirm(Scrim scrim) {
        throw new IllegalStateException("Scrim cancelled");
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim cancelled");
    }
}
