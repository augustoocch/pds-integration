package ar.com.uade.pds.final_project.scrim.business;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;

public interface ScrimState {
    void start(Scrim scrim);
    void cancel(Scrim scrim);
    void confirm(Scrim scrim);
    void end(Scrim scrim);
}
