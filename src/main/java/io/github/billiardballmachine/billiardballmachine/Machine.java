package io.github.billiardballmachine.billiardballmachine;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.common.collect.HashBiMap;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * A billiard-ball machine.
 * Balls are assumed to be perfect spheres √2/2 unit of distance in radius
 * so they collide when at a diagonal with each other.
 * They are also assumed to have a constant velocity of 1 unit of distance per step.
 */
class Machine {
    private BiMap<Ball, Position> ballPositions;
    private final BiMap<DiagonalWall, Position> wallPositions;

    Machine(BiMap<Ball, Position> ballPositions, BiMap<DiagonalWall, Position> wallPositions) {
        this.ballPositions = ballPositions;
        this.wallPositions = wallPositions;
    }

    static Machine emptyMachine() {
        return new Machine(HashBiMap.create(), EnumHashBiMap.create(DiagonalWall.class));
    }

    void addBall(Ball ball, Position position) {
        // TODO: validate. Ball can't be added inside wall or other ball.
        ballPositions.put(ball, position);
    }

    record Position(int x, int y) {
        Position oneSpaceToward(CardinalDirection direction) {
            return inDirection(direction, 1);
        }

        Position inDirection(CardinalDirection direction, int n) {
            var newX = x + direction.horizontalCoefficient() * n;
            var newY = y + direction.verticalCoefficient() * n;
            return new Position(newX, newY);
        }
    }

    void update() {
        BiMap<Ball, Position> nextBallPositions = HashBiMap.create();
        for (Map.Entry<Ball, Position> entry : ballPositions.entrySet()) {
            var ball = entry.getKey();
            var position = entry.getValue();
            var nextDirection = calculateNextDirection(ball, position); // TODO: handle null, or use Exception?
            var nextPosition = position.oneSpaceToward(nextDirection);
            nextBallPositions.put(new Ball(nextDirection), nextPosition); // TODO: avoid creating new balls each update?
        }
        this.ballPositions = nextBallPositions;
    }

    /**
     * Returns the next direction the ball should move in based on surrounding objects,
     * or null if the ball is surrounded and stuck.
     * @param ball a ball in the machine
     * @param ballPosition the position of the ball in the machine
     * @return the next direction the given ball should move given the current state of the machine
     */
    private CardinalDirection calculateNextDirection(Ball ball, Position ballPosition) {
        var ballDirection = ball.directionOfMovement();
        // Using ship directions relative to ball's direction of movement.
        // Check if ball should be deflected to port or starboard.
        var straightAhead = ballPosition.oneSpaceToward(ballDirection);
        var offThePortBow = straightAhead
                .oneSpaceToward(ballDirection.toPort());
        var offTheStarboardBow = straightAhead
                .oneSpaceToward(ballDirection.toStarboard());
        var willCollideWithWallOnPortBow =
                (ball.isMovingHorizontally() && DiagonalWall.NORTHWEST_TO_SOUTHEAST.equals(getWallAt(straightAhead))) ||
                (ball.isMovingVertically()   && DiagonalWall.SOUTHWEST_TO_NORTHEAST.equals(getWallAt(straightAhead)));
        var willCollideWithWallOnStarboardBow =
                (ball.isMovingHorizontally() && DiagonalWall.SOUTHWEST_TO_NORTHEAST.equals(getWallAt(offTheStarboardBow))) ||
                (ball.isMovingVertically()   && DiagonalWall.NORTHWEST_TO_SOUTHEAST.equals(getWallAt(offTheStarboardBow)));
        var ballOffThePortBow = getBallAt(offThePortBow);
        var willCollideWithBallOnPortBow =
                ballIsAt(offThePortBow)
                        && (ballOffThePortBow.isMovingInDirection(ballDirection.opposite())
                        || ballOffThePortBow.isMovingInDirection(ballDirection.toStarboard()));
        var ballOffTheStarboardBow = getBallAt(offTheStarboardBow);
        var willCollideWithBallOnStarboardBow =
                ballIsAt(offTheStarboardBow)
                        && (ballOffTheStarboardBow.isMovingInDirection(ballDirection.opposite())
                        || ballOffTheStarboardBow.isMovingInDirection(ballDirection.toPort()));
        var willCollideOnPortBow      = willCollideWithWallOnPortBow      || willCollideWithBallOnPortBow;
        var willCollideOnStarboardBow = willCollideWithWallOnStarboardBow || willCollideWithBallOnStarboardBow;

        if (willCollideOnPortBow && willCollideOnStarboardBow) {
            // TODO: check behind. If would collide, stop ball or halt machine. Should try and make general function for checks from port-bow/starboard-bow calculations above.
            return ballDirection.opposite();
        }
        if (willCollideOnPortBow) {
            return ballDirection.toStarboard();
        }
        if (willCollideOnStarboardBow) {
            return ballDirection.toPort();
        }
        // TODO: check if ball would collide with ball or corner of wall straight ahead.
        return ballDirection;
    }

    private DiagonalWall getWallAt(Position position) {
        return wallPositions.inverse().get(position);
    }

    Ball getBallAt(Position position) {
        return ballPositions.inverse().get(position);
    }

    private boolean wallIsAt(Position position) {
        return wallPositions.containsValue(position);
    }

    private boolean ballIsAt(Position position) {
        return ballPositions.containsValue(position);
    }

}
