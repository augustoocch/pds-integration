package ar.com.uade.pds.final_project.scrim.repository;

import ar.com.uade.pds.final_project.scrim.entity.ScrimParticipant;
import ar.com.uade.pds.final_project.scrim.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrimParticipantRepo extends JpaRepository<ScrimParticipant, Long> {
}
