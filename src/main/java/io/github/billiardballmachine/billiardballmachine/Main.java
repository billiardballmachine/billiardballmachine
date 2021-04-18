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
        rootPanel.add(machinePanel, BorderLayout.CENTER);
        rootPanel.add(createMachineControlToolBar(machinePanel), BorderLayout.NORTH);

        var machineEditorToolBar = new JToolBar();
        machineEditorToolBar.add(new JLabel("Add: "));
        var ballButton     = createEditorButton(machinePanel,
                "ball_icon.png",
                MachineGridPanel.GridSnap.WHOLE,
                (machine_, position, __) -> {
                    machine_.addBall(new Ball(CardinalDirection.EAST), position);
                });
        machineEditorToolBar.add(ballButton);
        var nwseWallButton = createEditorButton(machinePanel,
                "nwse_wall_icon.png",
                MachineGridPanel.GridSnap.ONLY_HALF,
                (machine_, position, __) -> {
                    machine_.addWall(DiagonalWall.NORTHWEST_TO_SOUTHEAST, position);
                });
        machineEditorToolBar.add(nwseWallButton);
        var swneWallButton = createEditorButton(machinePanel,
                "swne_wall_icon.png",
                MachineGridPanel.GridSnap.ONLY_HALF,
                (machine_, position, __) -> {
                    machine_.addWall(DiagonalWall.SOUTHWEST_TO_NORTHEAST, position);
                });
        machineEditorToolBar.add(swneWallButton);
        machineEditorToolBar.add(new JLabel("Edit: "));
        var turnButton     = createEditorButton(machinePanel,
                "turn_clockwise_icon.png",
                MachineGridPanel.GridSnap.HALF,
                (machine_, position, editingBall) -> {
                    if (editingBall) {
                        machine_.rotateBall(position);
                    } else {
                        machine_.rotateWall(position);
                    }
                }
        );
        machineEditorToolBar.add(turnButton);
        var deleteButton   = createEditorButton(machinePanel,
                "delete_icon.png",
                MachineGridPanel.GridSnap.HALF,
                (machine_, position, editingBall) -> {
                    if (editingBall) {
                        machine_.removeBall(position);
                    } else {
                        machine_.removeWall(position);
                    }
                }
        );
        machineEditorToolBar.add(deleteButton);
        machineEditorToolBar.add(new JLabel("Other: "));
        var clearButton = new JToggleButton("None");
        clearButton.addActionListener(e -> {
            machinePanel.setCursor(null);
            machinePanel.setHoverIcon(null, null);
            machinePanel.setEditMachineCommand(null);
            machinePanel.repaint();
        });
        machineEditorToolBar.add(clearButton);
        var machineEditorButtonGroup = createButtonGroup(ballButton, nwseWallButton, swneWallButton, turnButton, deleteButton, clearButton);
        machineEditorToolBar.setOrientation(SwingConstants.VERTICAL);
        rootPanel.add(machineEditorToolBar, BorderLayout.WEST);

        frame.add(rootPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private static JToggleButton createEditorButton(MachineGridPanel machinePanel, String imagePath, MachineGridPanel.GridSnap snap, EditMachineCommand command) throws IOException {
        var image = ImageIO.read(Main.class.getResource(imagePath));
        var button = new JToggleButton(new ImageIcon(image));
        var cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(image.getWidth() / 2, image.getHeight() / 2), imagePath);
        button.addActionListener(e -> {
            machinePanel.setCursor(cursor);
            machinePanel.setHoverIcon(image, snap);
            machinePanel.setEditMachineCommand(command);
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
