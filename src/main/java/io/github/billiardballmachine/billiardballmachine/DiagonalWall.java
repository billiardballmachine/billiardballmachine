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
}
