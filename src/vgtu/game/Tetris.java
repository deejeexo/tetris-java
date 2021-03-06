package vgtu.game;

import vgtu.controllers.FileController;
import vgtu.controllers.KeyboardController;

import static java.awt.desktop.UserSessionEvent.Reason.LOCK;
import static vgtu.game.Colors.*;
import static vgtu.game.TetrominosIndustry.generateTetromino;

public class Tetris {
    private Tetromino tetromino = null;
    private final KeyboardController keyboardController = new KeyboardController();
    private final Canvas canvas = new Canvas();
    public static Tetris tetris = new Tetris();
    private final FileController fileController = new FileController();
    private boolean isGameOver = false;

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Tetromino getTetromino() {
        return tetromino;
    }

    public void generateNewFigure() {
        tetromino = generateTetromino((int) (Math.random() * (getCanvas().getWidth() / 2)), 0);
    }

    public void startGame() throws Exception {
        keyboardController.getThread().start();
        generateNewFigure();
        do {
            tetromino.setyAxis(tetromino.getyAxis() + 1);
            while (!tetromino.checkCurrentCellAccessibility()) {
                tetromino.setyAxis(tetromino.getyAxis() - 1);
                tetromino.landTetromino();
                isGameOver = tetromino.gameOver();
                getCanvas().removeFilledLines();
                generateNewFigure();
            }
            getCanvas().printCanvas();
            synchronized (LOCK) {
                LOCK.wait(1000);
            }
        } while (!isGameOver);
        if (fileController.readScore() < getCanvas().getScore().getScore())
            fileController.writeRecordToFile(getCanvas().getScore().toString());
        System.out.printf("%sGame Over%s%n", getAnsiRed(), getAnsiReset());
    }

    public static void main(String[] args) throws Exception {
        tetris.startGame();
        System.exit(0);
    }
}