package io.github.billiardballmachine.billiardballmachine;

import io.github.billiardballmachine.billiardballmachine.ui.MachineGridPanel;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    private static void createAndShowUI(Machine machine, BufferedImage ballImage) {
        var frame = new JFrame("Billiard Ball Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var rootPanel = new JPanel(new BorderLayout());
        var machinePanel = new MachineGridPanel(machine, 0.2, 0, 30, ballImage);
        rootPanel.add(machinePanel, BorderLayout.CENTER);
        var toolBar = new JToolBar();
        var button = new JButton("Update");
        button.addActionListener(e -> {
            machinePanel.updateMachine();
        });
        toolBar.add(button);
        rootPanel.add(toolBar, BorderLayout.NORTH);

        frame.add(rootPanel);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        var machine = Machine.emptyMachine();
        var ballImage = ImageIO.read(Main.class.getResource("billiard_ball.png"));
        machine.addBall(new Ball(CardinalDirection.EAST), new Machine.Position(0, 0));
        machine.addWall(DiagonalWall.NORTHWEST_TO_SOUTHEAST, new Machine.Position(1, 1));
        machine.addWall(DiagonalWall.SOUTHWEST_TO_NORTHEAST, new Machine.Position(0, 0));
        SwingUtilities.invokeLater(() -> {
            createAndShowUI(machine, ballImage);
        });
    }

}
