package io.github.billiardballmachine.billiardballmachine.ui;

import io.github.billiardballmachine.billiardballmachine.Ball;
import io.github.billiardballmachine.billiardballmachine.DiagonalWall;
import io.github.billiardballmachine.billiardballmachine.Machine;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MachineGridPanel extends JPanel {

    private final Machine machine;
    private final BufferedImage ballImage;

    // The coordinates of the machine that should be displayed at the center of the window.
    // They don't have to be valid integer positions. This is to facilitate smooth panning.
    private double centerX;
    private double centerY;

    // The amount of pixels that should separate gridlines.
    // It is a double to try and facilitate smooth zooming.
    // TODO: consider if this should be an int
    private double gridUnitLength;

    public MachineGridPanel(
            Machine machine,
            double centerX,
            double centerY,
            double gridUnitLength,
            BufferedImage ballImage
    ) {
        this.machine = machine;
        this.centerX = centerX;
        this.centerY = centerY;
        this.gridUnitLength = gridUnitLength;
        this.ballImage = ballImage;
    }

    public void updateMachine() {
        machine.update();
        repaint();
        revalidate();
    }

    public void reverseMachine() {
        machine.updateReverse();
        repaint();
        revalidate();
    }

    public Dimension getPreferredSize() {
        return new Dimension(250,200);
    }

    public void paintComponent(Graphics g) {
        var g2 = (Graphics2D) g;
        super.paintComponent(g2);

        var height = getHeight();
        var width = getWidth();

        var gridData = calculateGridData();
        var xCoords = gridData.xData().gridCoords();
        var yCoords = gridData.yData().gridCoords();

        // Draw gridlines
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.LIGHT_GRAY);
        for (double x : xCoords) {
            var lineX = (int) x;
            g2.drawLine(lineX, 0, lineX, height);
        }

        for (double y : yCoords) {
            var lineY = (int) y;
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(0, lineY, width, lineY);
        }

        // Draw balls and walls
        int machineGridX = gridData.xData().machineGridStart();
        for (double x : xCoords) {
            int machineGridY = gridData.yData().machineGridStart();
            for (double y : yCoords) {
                var position = new Machine.Position(machineGridX, machineGridY);
                if (machine.wallIsAt(position)) {
                    var wall = machine.getWallAt(position);
                    paintWall(g2, wall, x, y);
                }
                if (machine.ballIsAt(position)) {
                    var ball = machine.getBallAt(position);
                    paintBall(g2, ball, x, y);
                }
                machineGridY++;
            }
            machineGridX++;
        }
    }

    private record DimensionData(int machineGridStart, List<Double> gridCoords) {}
    private record GridData(DimensionData xData, DimensionData yData) {}

    private DimensionData calculateDimensionData(int length, double center) {
        var windowCenter = length / 2;
        var gridOffset = gridUnitLength * (center % 1);
        var start = windowCenter - gridOffset;
        var machineGridStart = (int) center;
        // Starting at center, "back up" `start` to the leftmost or topmost edge without going off.
        // This will be the position of the first gridline (`start`).
        // `machineGridStart` tracks which index in the Machine the first gridline corresponds to.
        while (true) {
            var backOneSpace = start - gridUnitLength;
            if (backOneSpace < 0) {
                break;
            }
            start = backOneSpace;
            machineGridStart--;
        }
        // Calculate the gridline positions from left to right or top to bottom.
        List<Double> coords = new ArrayList<>();
        for (double x = start; x < length; x += gridUnitLength) {
            coords.add(x);
        }
        return new DimensionData(machineGridStart, coords);
    }

    private GridData calculateGridData() {
        return new GridData(
                calculateDimensionData(getWidth(), centerX),
                calculateDimensionData(getHeight(), centerY)
        );
    }

    private void paintBall(Graphics2D g, Ball ball, double x, double y) {
        var diameter = gridUnitLength * 1.4; // roughly sqrt(2), minus a little to account for stroke width
        var radius = diameter / 2;
        var left   = (x - radius);
        var top    = (y - radius);

        var transform = new AffineTransform();
        var rotations = switch (ball.directionOfMovement()) {
            case EAST -> 0;
            case SOUTH -> 1;
            case WEST -> 2;
            case NORTH -> 3;
        };
        var imgW = ballImage.getWidth();
        var imgH = ballImage.getHeight();
        transform.translate(left, top);
        transform.scale(diameter / imgW, diameter / imgH);
        transform.quadrantRotate(rotations, imgW / 2, imgH / 2);
        g.drawImage(ballImage, transform, null);
    }

    private void paintWall(Graphics2D g, DiagonalWall wall, double x, double y) {
        double leftY = 0;
        double rightY = 0;
        switch (wall) {
            case NORTHWEST_TO_SOUTHEAST -> {
                leftY = y;
                rightY = y + gridUnitLength;
            }
            case SOUTHWEST_TO_NORTHEAST -> {
                leftY = y + gridUnitLength;
                rightY = y;
            }
        }
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLACK);
        g.drawLine((int)x, (int)leftY, (int)(x + gridUnitLength), (int)rightY);
    }
}
