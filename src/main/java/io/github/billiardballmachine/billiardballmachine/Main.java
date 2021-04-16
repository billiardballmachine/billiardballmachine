package io.github.billiardballmachine.billiardballmachine;

import io.github.billiardballmachine.billiardballmachine.ui.MachineGridPanel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    private static void createAndShowUI(Machine machine, BufferedImage ballImage) {
        var frame = new JFrame("Billiard Ball Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new MachineGridPanel(machine, 0.2, 0, 30, ballImage));

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        var machine = Machine.emptyMachine();
        var ballImage = ImageIO.read(Main.class.getResource("billiard_ball.png"));
        machine.addBall(new Ball(CardinalDirection.NORTH), new Machine.Position(0, 0));
        SwingUtilities.invokeLater(() -> {
            createAndShowUI(machine, ballImage);
        });
    }

}
