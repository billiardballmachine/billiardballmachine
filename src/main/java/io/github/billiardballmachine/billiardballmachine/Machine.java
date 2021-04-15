package io.github.billiardballmachine.billiardballmachine;

import java.util.HashMap;
import java.util.Map;

class Machine {
    private final Map<Ball, Position> ballPositions;
    private final Map<DiagonalWall, Position> wallPositions;

    Machine(Map<Ball, Position> ballPositions, Map<DiagonalWall, Position> wallPositions) {
        this.ballPositions = ballPositions;
        this.wallPositions = wallPositions;
    }

    static Machine emptyMachine() {
        return new Machine(new HashMap<>(), new HashMap<>());
    }

    void addBall(Ball ball, Position position) {
        ballPositions.put(ball, position);
    }

    record Position(int x, int y) {
        Position oneStepToward(CardinalDirection direction) {
            var newX = x + direction.horizontalCoefficient();
            var newY = y + direction.verticalCoefficient();
            return new Position(newX, newY);
        }
    }

    void update() {
        for (Map.Entry<Ball, Position> entry : ballPositions.entrySet()) {
            var ball = entry.getKey();
            var position = entry.getValue();
            var newPosition = position.oneStepToward(ball.getDirectionOfMovement());
            entry.setValue(newPosition);
        }
    }

    Position getPosition(Ball ball) {
        return ballPositions.get(ball);
    }

}
