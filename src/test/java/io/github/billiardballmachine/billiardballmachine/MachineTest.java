package io.github.billiardballmachine.billiardballmachine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MachineTest {

    @Test
    public void testUpdateMovesBallHorizontally() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.EAST), new Machine.Position(0, 0));
        machine.update();
        assertEquals(new Ball(CardinalDirection.EAST), machine.getBallAt(new Machine.Position(1, 0)));
    }

    @Test
    public void testUpdateMovesBallVertically() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.NORTH), new Machine.Position(0, 0));
        machine.update();
        assertEquals(new Ball(CardinalDirection.NORTH), machine.getBallAt(new Machine.Position(0, -1)));
    }

    @Test
    public void testUpdateBallsCollideAtPerpendicularAndDeflect() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.EAST ), new Machine.Position(0,  0));
        machine.addBall(new Ball(CardinalDirection.SOUTH), new Machine.Position(1, -1));
        machine.update();
        assertAll(
                () -> assertEquals(new Ball(CardinalDirection.SOUTH), machine.getBallAt(new Machine.Position(0,  1))),
                () -> assertEquals(new Ball(CardinalDirection.EAST),  machine.getBallAt(new Machine.Position(2, -1)))
        );
    }

    @Test
    public void testUpdateBallsCollideApproachingEachOtherHorizontally() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.EAST ), new Machine.Position(0,  0));
        machine.addBall(new Ball(CardinalDirection.WEST), new Machine.Position(1, -1));
        machine.update();
        assertAll(
                () -> assertEquals(new Ball(CardinalDirection.SOUTH), machine.getBallAt(new Machine.Position(0,  1))),
                () -> assertEquals(new Ball(CardinalDirection.NORTH), machine.getBallAt(new Machine.Position(1, -2)))
        );
    }

    @Test
    public void testUpdateBallsCollideApproachingEachOtherVertically() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.NORTH), new Machine.Position(0,  0));
        machine.addBall(new Ball(CardinalDirection.SOUTH), new Machine.Position(1, -1));
        machine.update();
        assertAll(
                () -> assertEquals(new Ball(CardinalDirection.WEST), machine.getBallAt(new Machine.Position(-1, 0))),
                () -> assertEquals(new Ball(CardinalDirection.EAST), machine.getBallAt(new Machine.Position(2, -1)))
        );
    }

    @Test
    public void testUpdateBallCollidesWithSWNEWallAtPerpendicularAndDeflects() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.EAST), new Machine.Position(0, 0));
        machine.addWall(DiagonalWall.SOUTHWEST_TO_NORTHEAST, new Machine.Position(0, 0));
        machine.update();
        assertEquals(new Ball(CardinalDirection.NORTH), machine.getBallAt(new Machine.Position(0, -1)));
    }

    @Test
    public void testUpdateBallCollidesWithNWSEWallAtPerpendicularAndDeflects() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.EAST), new Machine.Position(0, 0));
        machine.addWall(DiagonalWall.NORTHWEST_TO_SOUTHEAST, new Machine.Position(0, -1));
        machine.update();
        assertEquals(new Ball(CardinalDirection.SOUTH), machine.getBallAt(new Machine.Position(0, 1)));
    }

    @Test
    public void testUpdateBallMovingSouthCollidesWithWallAtPerpendicularAndDeflects() {
        Machine machine = Machine.emptyMachine();
        machine.addBall(new Ball(CardinalDirection.SOUTH), new Machine.Position(0, 0));
        machine.addWall(DiagonalWall.NORTHWEST_TO_SOUTHEAST, new Machine.Position(-1, 0));
        machine.update();
        assertEquals(new Ball(CardinalDirection.EAST), machine.getBallAt(new Machine.Position(1, 0)));
    }

}
