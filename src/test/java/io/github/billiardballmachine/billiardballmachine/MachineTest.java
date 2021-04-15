package io.github.billiardballmachine.billiardballmachine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MachineTest {

    @Test
    public void testUpdateMovesBallHorizontally() {
        Ball ball = new Ball(CardinalDirection.EAST);
        Machine machine = Machine.emptyMachine();
        machine.addBall(ball, new Machine.Position(0, 0));
        machine.update();
        assertEquals(new Machine.Position(1, 0), machine.getPosition(ball));
    }

    @Test
    public void testUpdateMovesBallVertically() {
        Ball ball = new Ball(CardinalDirection.NORTH);
        Machine machine = Machine.emptyMachine();
        machine.addBall(ball, new Machine.Position(0, 0));
        machine.update();
        assertEquals(new Machine.Position(0, -1), machine.getPosition(ball));
    }
}
