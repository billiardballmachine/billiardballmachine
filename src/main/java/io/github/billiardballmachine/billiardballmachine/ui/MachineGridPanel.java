package io.github.billiardballmachine.billiardballmachine.ui;

import io.github.billiardballmachine.billiardballmachine.Machine;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class MachineGridPanel extends JPanel {

    private final Machine machine;

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
            double gridUnitLength
    ) {
        this.machine = machine;
        this.centerX = centerX;
        this.centerY = centerY;
        this.gridUnitLength = gridUnitLength;
    }

    public Dimension getPreferredSize() {
        return new Dimension(250,200);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var height = getHeight();
        var width = getWidth();

        var gridData = calculateGridData();

        for (double x : gridData.xData().gridCoords()) {
            var lineX = (int) x;
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(lineX, 0, lineX, height);
        }

        for (double y : gridData.yData().gridCoords()) {
            var lineY = (int) y;
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(0, lineY, width, lineY);
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

    private void paintBall(Graphics g, int x, int y) {

    }

    private void paintWall(Graphics g, int x, int y) {
        // TODO
    }
}
