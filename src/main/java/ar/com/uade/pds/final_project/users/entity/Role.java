package ar.com.uade.pds.final_project.users.entity;

import java.util.List;

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

    public static List<Role> oneVsOneRoles() {
        return List.of(ASSASSIN, MARKSMAN, SUPPORT);
    }

    public static List<Role> twoVsTwoRoles() {
        return List.of(SNIPER, ASSASSIN, MARKSMAN, SUPPORT);
    }

    public static List<Role> fiveVsFiveRoles() {
        return List.of(SNIPER, SUPPORT, TANK, WARRIOR, ASSASSIN, MAGE, MARKSMAN);
    }
}
