package io.github.billiardballmachine.billiardballmachine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A billiard-ball machine.
 * Balls are assumed to be perfect spheres √2/2 unit of distance in radius
 * so they collide when at a diagonal with each other.
 * They are also assumed to have a constant velocity of 1 unit of distance per step.
 */
public class Machine {
    private Map<Position, Ball> ballPositions;
    private final Map<Position, DiagonalWall> wallPositions;

    Machine(Map<Position, Ball> ballPositions, Map<Position, DiagonalWall> wallPositions) {
        this.ballPositions = ballPositions;
        this.wallPositions = wallPositions;
    }

    static Machine emptyMachine() {
        return new Machine(new HashMap<>(), new HashMap<>());
    }

    void clear() {
        ballPositions.clear();
        wallPositions.clear();
    }

    public void loadFromConfiguration(List<String> configuration) {
        clear();
        for (String configString : configuration) {
            loadObjectFromConfigurationString(configString);
        }
    }

    private void loadObjectFromConfigurationString(String configString) {
        try (
                var s = new Scanner(configString)
        ) {
            var objectType = s.next();
            switch (objectType) {
                case "ball" -> {
                    var direction = CardinalDirection.fromConfigurationString(s.next());
                    var position = readPosition(s);
                    addBall(new Ball(direction), position);
                }
                case "wall" -> {
                    var wall = DiagonalWall.fromConfigurationString(s.next());
                    var position = readPosition(s);
                    addWall(wall, position);
                }
                default -> throw new IllegalArgumentException("Unexpected type in configuration string: " + objectType);
            }
        }
    }

    private static Position readPosition(Scanner s) {
        var x = s.nextInt();
        var y = s.nextInt();
        return new Position(x, y);
    }

    public List<String> getConfigurationAsStrings() {
        var configuration = new ArrayList<String>();
        for (Map.Entry<Position, Ball> e : ballPositions.entrySet()) {
            configuration.add(toConfigurationString(e.getValue(), e.getKey()));
        }
        for (Map.Entry<Position, DiagonalWall> e : wallPositions.entrySet()) {
            configuration.add(toConfigurationString(e.getValue(), e.getKey()));
        }
        return configuration;
    }

    private static String toConfigurationString(DiagonalWall w, Position p) {
        return String.format("wall %s %s",
                w.toConfigurationString(),
                p.toConfigurationString());
    }

    private static String toConfigurationString(Ball b, Position p) {
        return String.format("ball %s %s",
                b.directionOfMovement().toConfigurationString(),
                p.toConfigurationString());
    }

    void addBall(Ball ball, Position position) {
        if (ballIntersects(position) || wallTouches(position)) {
            return;
        }
        ballPositions.put(position, ball);
    }

    private boolean wallTouches(Position position) {
        var n  = position.oneSpaceToward(CardinalDirection.NORTH);
        var w  = position.oneSpaceToward(CardinalDirection.WEST);
        var nw = position.oneSpaceToward(CardinalDirection.NORTH).oneSpaceToward(CardinalDirection.WEST);
        return (wallIsAt(position) && DiagonalWall.NORTHWEST_TO_SOUTHEAST.equals(getWallAt(position)))
            || (wallIsAt(n)        && DiagonalWall.SOUTHWEST_TO_NORTHEAST.equals(getWallAt(n)))
            || (wallIsAt(w)        && DiagonalWall.SOUTHWEST_TO_NORTHEAST.equals(getWallAt(w)))
            || (wallIsAt(nw)       && DiagonalWall.NORTHWEST_TO_SOUTHEAST.equals(getWallAt(nw)));
    }

    private boolean ballIntersects(Position position) {
        return Arrays.asList(
                position,
                position.oneSpaceToward(CardinalDirection.NORTH),
                position.oneSpaceToward(CardinalDirection.EAST),
                position.oneSpaceToward(CardinalDirection.SOUTH),
                position.oneSpaceToward(CardinalDirection.WEST)).stream()
                .anyMatch(this::ballIsAt);
    }

    void addWall(DiagonalWall wall, Position position) {
        boolean intersectsBall = switch (wall) {
            case NORTHWEST_TO_SOUTHEAST -> {
                var se = position.oneSpaceToward(CardinalDirection.SOUTH).oneSpaceToward(CardinalDirection.EAST);
                yield ballIsAt(position) || ballIsAt(se);
            }
            case SOUTHWEST_TO_NORTHEAST -> {
                var s  = position.oneSpaceToward(CardinalDirection.SOUTH);
                var e  = position.oneSpaceToward(CardinalDirection.EAST);
                yield ballIsAt(s) || ballIsAt(e);
            }
        };
        if (wallIsAt(position) || intersectsBall) {
            return;
        }
        wallPositions.put(position, wall);
    }

    Ball removeBall(Position position) {
        return ballPositions.remove(position);
    }

    DiagonalWall removeWall(Position position) {
        return wallPositions.remove(position);
    }

    void rotateBall(Position position) {
        var ball = removeBall(position);
        if (ball == null) {
            return;
        }
        var newDirection = ball.directionOfMovement().toStarboard();
        addBall(new Ball(newDirection), position);
    }

    void rotateWall(Position position) {
        var wall = removeWall(position);
        if (wall == null) {
            return;
        }
        addWall(wall.rotateClockwise(), position);
    }

    public record Position(int x, int y) {
        Position oneSpaceToward(CardinalDirection direction) {
            return inDirection(direction, 1);
        }

        Position inDirection(CardinalDirection direction, int n) {
            var newX = x + direction.horizontalCoefficient() * n;
            var newY = y + direction.verticalCoefficient() * n;
            return new Position(newX, newY);
        }

        Position plus(Position other) {
            return new Position(this.x + other.x, this.y + other.y);
        }

        public String toConfigurationString() {
            return x + " " + y;
        }
    }

    public void update() {
        Map<Position, Ball> nextBallPositions = new HashMap<>();
        for (Map.Entry<Position, Ball> entry : ballPositions.entrySet()) {
            var position = entry.getKey();
            var ball = entry.getValue();
            var nextDirection = calculateNextDirection(ball, position); // TODO: handle null, or use Exception?
            var nextPosition = position.oneSpaceToward(nextDirection);
            nextBallPositions.put(nextPosition, new Ball(nextDirection));
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
        // Wall positions/anchors are at their northwest corner, so the "port-bow" or "starboard-bow" position for the wall
        // depends on the ball's direction. A ball going east will collide on the starboard side with a SW-NE wall anchored at the ball's position,
        // but a ball going west will collide on the starboard side with a SW-NE wall anchored *one space north and one space west* of the ball's position.
        // TODO: consider doubling the scale of the grid, make balls move 2 spaces per step, walls anchored at center. Simplifies this logic, but then balls and walls can only be placed at even and odd gridCoords, respectively.
        var portBowWallOffset = switch (ballDirection) {
            case NORTH -> new Position(-1, -1);
            case EAST  -> new Position( 0, -1);
            case SOUTH -> new Position( 0,  0);
            case WEST  -> new Position(-1,  0);
        };
        var portBowWallPosition = ballPosition.plus(portBowWallOffset);
        var starboardBowWallOffset = switch (ballDirection) {
            case NORTH -> new Position( 0, -1);
            case EAST  -> new Position( 0,  0);
            case SOUTH -> new Position(-1,  0);
            case WEST  -> new Position(-1, -1);
        };
        var starboardBowWallPosition = ballPosition.plus(starboardBowWallOffset);
        var willCollideWithWallOnPortBow =
                (ball.isMovingHorizontally() && DiagonalWall.NORTHWEST_TO_SOUTHEAST.equals(getWallAt(portBowWallPosition))) ||
                (ball.isMovingVertically()   && DiagonalWall.SOUTHWEST_TO_NORTHEAST.equals(getWallAt(portBowWallPosition)));
        var willCollideWithWallOnStarboardBow =
                (ball.isMovingHorizontally() && DiagonalWall.SOUTHWEST_TO_NORTHEAST.equals(getWallAt(starboardBowWallPosition))) ||
                (ball.isMovingVertically()   && DiagonalWall.NORTHWEST_TO_SOUTHEAST.equals(getWallAt(starboardBowWallPosition)));
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

    public void updateReverse() {
        reverseBallDirections();
        update();
        reverseBallDirections();
    }

    private void reverseBallDirections() {
        ballPositions.replaceAll((_p, ball) -> ball.movingInOppositeDirection());
    }

    public DiagonalWall getWallAt(Position position) {
        return wallPositions.get(position);
    }

    public Ball getBallAt(Position position) {
        return ballPositions.get(position);
    }

    public boolean wallIsAt(Position position) {
        return wallPositions.containsKey(position);
    }

    public boolean ballIsAt(Position position) {
        return ballPositions.containsKey(position);
    }

}