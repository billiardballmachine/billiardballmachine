package io.github.billiardballmachine.billiardballmachine;

import io.github.billiardballmachine.billiardballmachine.ui.MachineGridPanel;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class Main {

    private static void createAndShowUI(Machine machine, BufferedImage ballImage) throws IOException {
        var frame = new JFrame("Billiard Ball Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var rootPanel = new JPanel(new BorderLayout());
        var machinePanel = new MachineGridPanel(machine, 0, 0, 25, ballImage);
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
        createButtonGroup(ballButton, nwseWallButton, swneWallButton, turnButton, deleteButton, clearButton);
        machineEditorToolBar.setOrientation(SwingConstants.VERTICAL);
        rootPanel.add(machineEditorToolBar, BorderLayout.WEST);

        frame.add(rootPanel);

        var fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        var bbmFileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isFile() || pathname.getName().endsWith(".bbm");
            }

            @Override
            public String getDescription() {
                return "Billiard-Ball Machine configuration files (*.bbm)";
            }
        };
        fileChooser.addChoosableFileFilter(bbmFileFilter);
        fileChooser.setFileFilter(bbmFileFilter);

        var menuBar = new JMenuBar();
        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        var openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(e -> {
            var result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                var file = fileChooser.getSelectedFile();
                try {
                    var configuration = Files.readAllLines(file.toPath());
                    machinePanel.loadMachineFromConfiguration(configuration);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        fileMenu.add(openMenuItem);
        var exportMenuItem = new JMenuItem("Export...");
        exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        exportMenuItem.addActionListener(e -> {
            var result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                var file = fileChooser.getSelectedFile();
                var configuration = machinePanel.getMachineConfiguration();
                try (
                        var pw = new PrintWriter(file)
                ) {
                    configuration.forEach(pw::println);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        fileMenu.add(exportMenuItem);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);

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

    private static ButtonGroup createButtonGroup(AbstractButton... buttons) {
        var buttonGroup = new ButtonGroup();
        for (AbstractButton button : buttons) {
            buttonGroup.add(button);
        }
        return buttonGroup;
    }

    private static JToolBar createMachineControlToolBar(MachineGridPanel machinePanel) {
        var toolBar = new JToolBar();

        toolBar.add(new JLabel("Update: "));
        addToolBarButton(toolBar, "Reverse", e -> { machinePanel.reverseMachine(); });
        addToolBarButton(toolBar, "Forward", e -> { machinePanel.updateMachine(); });

        toolBar.addSeparator();

        toolBar.add(new JLabel("Animate: "));
        addToolBarButton(toolBar, "Reverse", e -> { machinePanel.reverseAnimateMachine(); });
        addToolBarButton(toolBar, "Stop",    e -> { machinePanel.stopMachine(); });
        addToolBarButton(toolBar, "Forward", e -> { machinePanel.animateMachine(); });

        toolBar.add(new JLabel("Speed: "));
        var animationSpeedSlider = new JSlider(0, 4, 0);
        animationSpeedSlider.addChangeListener(ce -> {
            var slider = (JSlider) ce.getSource();
            var newValue = slider.getValue();
            var newPeriod = 500 - 100 * newValue;
            machinePanel.setAnimationPeriod(newPeriod);
        });
        toolBar.add(animationSpeedSlider);

        return toolBar;
    }

    private static void addToolBarButton(JToolBar toolBar, String label, ActionListener action) {
        var button = new JButton(label);
        button.addActionListener(action);
        toolBar.add(button);
    }

    public static void main(String[] args) throws IOException {
        var machine = Machine.emptyMachine();
        var ballImage = ImageIO.read(Main.class.getResource("billiard_ball.png"));
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowUI(machine, ballImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
