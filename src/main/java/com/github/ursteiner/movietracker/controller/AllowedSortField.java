package com.github.ursteiner.movietracker.controller;

import java.util.Arrays;

public enum AllowedSortField {
    NAME("name"),
    DATE_WATCHED("dateWatched");

    private final String name;

    AllowedSortField(String property) {
        this.name = property;
    }

    public String property() {
        return name;
    }

    public static AllowedSortField fromString(String value) {
        return Arrays.stream(values())
                .filter(field -> field.name.equals(value))
                .findFirst()
                .orElse(DATE_WATCHED);
    }
}

