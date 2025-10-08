package ar.com.uade.pds.final_project.users.entity;

public enum Role {
    SNIPER("sniper"),
    SUPPORT("support"),
    TANK("tank"),
    WARRIOR("warrior"),
    ASSASSIN("assassin"),
    MAGE("mage"),
    MARKSMAN("marksman");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
