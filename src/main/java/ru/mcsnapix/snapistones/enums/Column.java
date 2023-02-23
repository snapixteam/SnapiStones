package ru.mcsnapix.snapistones.enums;

import lombok.Getter;

@Getter
public enum Column {
    OWNERS(3, "owners_name"),
    MEMBERS(4, "members_name"),
    DATE(5, "creation_date"),
    LOCATION(6, "location"),
    EFFECTS(7, "bought_effects"),
    ACTIVE_EFFECTS(8, "active_effects"),
    MAX_OWNERS(9, "max_owners"),
    MAX_MEMBERS(10, "max_members");

    private final int column;
    private final String columns;

    Column(int column, String columns) {
        this.column = column;
        this.columns = columns;
    }
}
