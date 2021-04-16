package io.github.billiardballmachine.billiardballmachine;

public enum CardinalDirection {
    NORTH(Direction1D.NONE, Direction1D.TOWARD_NEGATIVE),
    SOUTH(Direction1D.NONE, Direction1D.TOWARD_POSITIVE),
    EAST (Direction1D.TOWARD_POSITIVE, Direction1D.NONE),
    WEST (Direction1D.TOWARD_NEGATIVE, Direction1D.NONE);

    private final Direction1D horizontalDirection;
    private final Direction1D verticalDirection;

    CardinalDirection(Direction1D horizontalDirection, Direction1D verticalDirection) {
        this.horizontalDirection = horizontalDirection;
        this.verticalDirection = verticalDirection;
    }

    CardinalDirection toPort() {
        return switch (this) {
            case NORTH -> WEST;
            case EAST  -> NORTH;
            case SOUTH -> EAST;
            case WEST  -> SOUTH;
        };
    }

    CardinalDirection toStarboard() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST  -> SOUTH;
            case SOUTH -> WEST;
            case WEST  -> NORTH;
        };
    }

    CardinalDirection opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST  -> WEST;
            case SOUTH -> NORTH;
            case WEST  -> EAST;
        };
    }

    boolean isHorizontal() {
        return !horizontalDirection.equals(Direction1D.NONE);
    }

    boolean isVertical() {
        return !verticalDirection.equals(Direction1D.NONE);
    }

    public int horizontalCoefficient() {
        return horizontalDirection.getCoefficient();
    }

    public int verticalCoefficient() {
        return verticalDirection.getCoefficient();
    }
}
