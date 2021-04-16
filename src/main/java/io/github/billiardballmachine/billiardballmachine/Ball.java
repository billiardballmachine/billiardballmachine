package io.github.billiardballmachine.billiardballmachine;

public record Ball(CardinalDirection directionOfMovement) {

    Ball movingInOppositeDirection() {
        return new Ball(directionOfMovement.opposite());
    }

    boolean isMovingInDirection(CardinalDirection direction) {
        return directionOfMovement.equals(direction);
    }

    boolean isMovingHorizontally() {
        return directionOfMovement.isHorizontal();
    }

    boolean isMovingVertically() {
        return directionOfMovement.isVertical();
    }
}
