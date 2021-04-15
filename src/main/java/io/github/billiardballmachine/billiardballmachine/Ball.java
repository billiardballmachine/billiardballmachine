package io.github.billiardballmachine.billiardballmachine;

class Ball {
    private CardinalDirection directionOfMovement;

    Ball(CardinalDirection directionOfMovement) {
        this.directionOfMovement = directionOfMovement;
    }

    CardinalDirection getDirectionOfMovement() {
        return directionOfMovement;
    }

    void setDirectionOfMovement(CardinalDirection directionOfMovement) {
        this.directionOfMovement = directionOfMovement;
    }
}
