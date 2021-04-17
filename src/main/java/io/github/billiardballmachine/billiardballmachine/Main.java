package io.github.billiardballmachine.billiardballmachine;

import io.github.billiardballmachine.billiardballmachine.ui.MachineGridPanel;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main {

    private static void createAndShowUI(Machine machine, BufferedImage ballImage) throws IOException {
        var frame = new JFrame("Billiard Ball Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        var rootPanel = new JPanel(new BorderLayout());
        var machinePanel = new MachineGridPanel(machine, 0.2, 0, 40, ballImage);
        ;rootPanel.add(machinePanel, BorderLayout.CENTER);
        rootPanel.add(createMachineControlToolBar(machinePanel), BorderLayout.NORTH);

        var machineEditorToolBar = new JToolBar();
        machineEditorToolBar.add(new JLabel("Add: "));
        var ballButton     = createEditorButton(machinePanel, "ball_icon.png");
        machineEditorToolBar.add(ballButton);
        var nwseWallButton = createEditorButton(machinePanel, "nwse_wall_icon.png");
        machineEditorToolBar.add(nwseWallButton);
        var swneWallButton = createEditorButton(machinePanel, "swne_wall_icon.png");
        machineEditorToolBar.add(swneWallButton);
        var turnButton     = createEditorButton(machinePanel, "turn_clockwise_icon.png");
        machineEditorToolBar.add(turnButton);
        var deleteButton   = createEditorButton(machinePanel, "delete_icon.png");
        machineEditorToolBar.add(deleteButton);
        var machineEditorButtonGroup = createButtonGroup(ballButton, nwseWallButton, swneWallButton, turnButton, deleteButton);
        machineEditorToolBar.setOrientation(SwingConstants.VERTICAL);
        rootPanel.add(machineEditorToolBar, BorderLayout.WEST);

        frame.add(rootPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private static JToggleButton createEditorButton(MachineGridPanel machinePanel, String imagePath) throws IOException {
        var image = ImageIO.read(Main.class.getResource(imagePath));
        var button = new JToggleButton(new ImageIcon(image));
        var cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(image.getWidth() / 2, image.getHeight() / 2), imagePath);
        button.addActionListener(e -> {
            machinePanel.setCursor(cursor);
        });
        return button;
    }

    private static JToggleButton toggleButtonWithIcon(String path) {
        return new JToggleButton(new ImageIcon(Main.class.getResource(path)));
    }

    private static ButtonGroup createButtonGroup(AbstractButton... buttons) {
        var buttonGroup = new ButtonGroup();
        for (AbstractButton button : buttons) {
            buttonGroup.add(button);
        }
        return buttonGroup;
    }

    private static JToolBar createMachineControlToolBar(MachineGridPanel machinePanel) {
        var toolBar = new JToolBar();
        var updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            machinePanel.updateMachine();
        });
        toolBar.add(updateButton);
        var reverseButton = new JButton("Reverse");
        reverseButton.addActionListener(e -> {
            machinePanel.reverseMachine();
        });
        toolBar.add(reverseButton);
        return toolBar;
    }

    public static void main(String[] args) throws IOException {
        var machine = Machine.emptyMachine();
        var ballImage = ImageIO.read(Main.class.getResource("billiard_ball.png"));
        machine.addBall(new Ball(CardinalDirection.EAST), new Machine.Position(0, 0));
        machine.addWall(DiagonalWall.NORTHWEST_TO_SOUTHEAST, new Machine.Position(1, 1));
        machine.addWall(DiagonalWall.SOUTHWEST_TO_NORTHEAST, new Machine.Position(0, 0));
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowUI(machine, ballImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
