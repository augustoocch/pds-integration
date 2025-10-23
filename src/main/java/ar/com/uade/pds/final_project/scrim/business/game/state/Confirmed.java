package ar.com.uade.pds.final_project.scrim.business.game.state;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Confirmed implements ScrimState {

    public Confirmed(Scrim scrim) {
        for (int i = 5; i >= 0; i--) {
            log.info("Scrim starting in: " + i + " seconds");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Countdown interrupted", e);
            }
        }
        scrim.setState(new InGame());
        log.info("Scrim started!");
    }

    @Override
    public void start(Scrim scrim) {
        throw new IllegalStateException("Scrim already confirmed and starting");
    }

    @Override
    public void cancel(Scrim scrim) {
        scrim.setState(new Cancelled());
    }

    @Override
    public void confirm(Scrim scrim) {
        throw new IllegalStateException("Scrim already confirmed");
    }

    @Override
    public void end(Scrim scrim) {
        throw new IllegalStateException("Scrim didn't start yet");
    }
}
