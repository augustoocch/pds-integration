package ar.com.uade.pds.final_project.users.entity;

import lombok.Getter;

import java.util.List;

@Getter
public enum Role {
    SNIPER("sniper"),
    SUPPORT("support"),
    TANK("tank"),
    WARRIOR("warrior"),
    ASSASSIN("assassin"),
    MAGE("mage"),
    MARKSMAN("marksman"),
    UNASSIGNED("unassigned");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public static Role fromString(String roleStr) {
        for (Role role : Role.values()) {
            if (role.description.equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        return UNASSIGNED;
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
