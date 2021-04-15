package io.github.billiardballmachine.billiardballmachine;

enum CardinalDirection {
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

    int horizontalCoefficient() {
        return horizontalDirection.getCoefficient();
    }

    int verticalCoefficient() {
        return verticalDirection.getCoefficient();
    }
}
