package ar.com.uade.pds.final_project.domain.dto.response;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class ScrimDTO {
    private String game;
    private String format;
    private int players;
    private java.util.List<String> roles;
    private String region;
    private String latency;
    private int estDuration;
    private String modal;
    private String state;
}
