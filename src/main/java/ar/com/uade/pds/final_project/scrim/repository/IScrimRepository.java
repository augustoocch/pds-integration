package ar.com.uade.pds.final_project.scrim.repository;

import ar.com.uade.pds.final_project.scrim.entity.Scrim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IScrimRepository extends JpaRepository<Scrim, Long> {

    @Query("SELECT s FROM Scrim s WHERE " +
            "(:game IS NULL OR s.game = :game) AND " +
            "(:region IS NULL OR s.region = :region) AND " +
            "(:format IS NULL OR s.format = :format)")
    List<Scrim> findByFilters(@Param("game") String game,
                              @Param("region") String region,
                              @Param("format") String format);
}

