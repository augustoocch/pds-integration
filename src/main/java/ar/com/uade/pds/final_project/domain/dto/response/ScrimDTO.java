package ar.com.uade.pds.final_project.domain.dto.response;


import lombok.Getter;

import java.util.List;

@Getter
public class ScrimDTO {
    private String game;
    private String format;
    private int players;
    private List<String> roles;
    private String region;
    private String latency;
    private int estDuration;
    private String mode;
    private String state;

    // Constructor privado para que solo se pueda construir mediante el Builder
    private ScrimDTO(Builder builder) {
        this.game = builder.game;
        this.format = builder.format;
        this.players = builder.players;
        this.roles = builder.roles;
        this.region = builder.region;
        this.latency = builder.latency;
        this.estDuration = builder.estDuration;
        this.mode = builder.mode;
        this.state = builder.state;
    }

    // Builder manual
    public static class Builder {
        private String game;
        private String format;
        private int players;
        private List<String> roles;
        private String region;
        private String latency;
        private int estDuration;
        private String mode;
        private String state;

        public Builder game(String game) {
            this.game = game;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder players(int players) {
            this.players = players;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder latency(String latency) {
            this.latency = latency;
            return this;
        }

        public Builder estDuration(int estDuration) {
            this.estDuration = estDuration;
            return this;
        }

        public Builder mode(String modal) {
            this.mode = modal;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public ScrimDTO build() {
            return new ScrimDTO(this);
        }
    }
}
