package io.github.billiardballmachine.billiardballmachine;

public interface EditMachineCommand {
    void execute(Machine machine, Machine.Position position, boolean editingBall);
}
