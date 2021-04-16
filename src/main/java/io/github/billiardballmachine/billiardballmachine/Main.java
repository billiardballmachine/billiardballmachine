package io.github.billiardballmachine.billiardballmachine;

import io.github.billiardballmachine.billiardballmachine.ui.MachineGridPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    private static void createAndShowUI(Machine machine) {
        var frame = new JFrame("Billiard Ball Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new MachineGridPanel(machine, 0.2, 0, 30));

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        var machine = Machine.emptyMachine();
        SwingUtilities.invokeLater(() -> {
            createAndShowUI(machine);
        });
    }

}
