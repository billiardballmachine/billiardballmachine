package io.github.billiardballmachine.billiardballmachine;

public enum DiagonalWall {
    NORTHWEST_TO_SOUTHEAST,
    SOUTHWEST_TO_NORTHEAST;

    public DiagonalWall rotateClockwise() {
        return switch (this) {
            case NORTHWEST_TO_SOUTHEAST -> SOUTHWEST_TO_NORTHEAST;
            case SOUTHWEST_TO_NORTHEAST -> NORTHWEST_TO_SOUTHEAST;
        };
    }

    String toConfigurationString() {
        return switch (this) {
            case NORTHWEST_TO_SOUTHEAST -> "NWSE";
            case SOUTHWEST_TO_NORTHEAST -> "SWNE";
        };
    }

    static DiagonalWall fromConfigurationString(String s) {
        return switch (s) {
            case "NWSE" -> NORTHWEST_TO_SOUTHEAST;
            case "SWNE" -> SOUTHWEST_TO_NORTHEAST;
            default -> throw new IllegalArgumentException("Unexpected configuration value for DiagonalWall: " + s);
        };
    }
}
