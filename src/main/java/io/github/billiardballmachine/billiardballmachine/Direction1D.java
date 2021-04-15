package io.github.billiardballmachine.billiardballmachine;

enum Direction1D {
    TOWARD_POSITIVE(1),
    NONE(0),
    TOWARD_NEGATIVE(-1);

    private final int coefficient;

    Direction1D(int coefficient) {
        this.coefficient = coefficient;
    }

    int getCoefficient() {
        return coefficient;
    }
}
