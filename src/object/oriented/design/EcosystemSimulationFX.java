package object.oriented.design;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static javafx.application.Application.launch;

/*

Write a Java program to simulate an ecosystem containing two types of creatures,
bears and fish. The ecosystemconsists of a river, which is modeled as a relatively
large array. Each cell of the array should contain an Animal object, which can
be a Bear object, a Fish object, or null. In each time step, based on a random
process, each animal either attempts to move into an adjacent array cell or stay
where it is. If two animals of the same type are about to collide in the same
cell, then they stay where they are, but they create a new instance of that type
of animal, which is placed in a random empty (i.e., previously null) cell in the
array. If a bear and a fish collide, however, then the fish dies (i.e., it disappears).
Use actual object creation, via the new operator, to model the creation of new
objects, and provide a visualization of the array after each time step.

*/

public class EcosystemSimulationFX extends Application {

    private static final int ROWS = 24;
    private static final int COLS = 36;
    private static final int CELL_SIZE = 16;

    private final Random random = new Random();

    private Animal[][] river;
    private Rectangle[][] cells;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initializeRiver();

        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setPadding(new Insets(10));

        cells = new Rectangle[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
                rect.setFill(Color.web("#1f3b5c"));
                rect.setStroke(Color.web("#2d4d72"));
                cells[r][c] = rect;
                grid.add(rect, c, r);
            }
        }

        renderGrid();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(220), e -> {
                    river = nextStep(river);
                    renderGrid();
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        BorderPane root = new BorderPane(grid);
        root.setPadding(new Insets(10));
        primaryStage.setTitle("River Ecosystem");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initializeRiver() {
        river = new Animal[ROWS][COLS];

        int fishCount = 140;
        while (fishCount > 0) {
            int r = random.nextInt(ROWS);
            int c = random.nextInt(COLS);
            if (river[r][c] == null) {
                river[r][c] = new Fish();
                fishCount--;
            }
        }

        int bearCount = 25;
        while (bearCount > 0) {
            int r = random.nextInt(ROWS);
            int c = random.nextInt(COLS);
            if (river[r][c] == null) {
                river[r][c] = new Bear();
                bearCount--;
            }
        }
    }

    private Animal[][] nextStep(Animal[][] current) {
        Animal[][] next = new Animal[ROWS][COLS];

        Map<Cell, List<Move>> targetMap = new HashMap<>();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Animal animal = current[r][c];
                if (animal == null) continue;

                Cell source = new Cell(r, c);
                Cell target = chooseTarget(source);

                targetMap.computeIfAbsent(target, k -> new ArrayList<>()).add(
                        new Move(source, target, animal)
                );
            }
        }

        for (Map.Entry<Cell, List<Move>> entry : targetMap.entrySet()) {
            Cell target = entry.getKey();
            List<Move> group = entry.getValue();

            if (group.size() > 1) {
                if (allSameType(group)) {
                    for (Move move : group) {
                        next[move.source.row][move.source.col] = move.animal;
                    }
                    Animal child = makeNewAnimal(group.get(0).animal.getClass());
                    if (child != null) {
                        Cell birth = randomEmptyCell(next);
                        if (birth != null) {
                            next[birth.row][birth.col] = child;
                        }
                    }
                } else {
                    for (Move move : group) {
                        if (move.animal instanceof Fish) {
                        } else if (move.animal instanceof Bear) {
                            next[move.source.row][move.source.col] = move.animal;
                        }
                    }
                }
                continue;
            }

            Move move = group.get(0);

            if (move.source.equals(move.target)) {
                next[move.source.row][move.source.col] = move.animal;
                continue;
            }

            if (current[move.target.row][move.target.col] == null) {
                next[move.target.row][move.target.col] = move.animal;
            } else {
                next[move.source.row][move.source.col] = move.animal;
            }
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (current[r][c] != null && next[r][c] == null) {
                    next[r][c] = current[r][c];
                }
            }
        }

        return next;
    }

    private Cell randomEmptyCell(Animal[][] grid) {
        List<Cell> empties = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] == null) {
                    empties.add(new Cell(r, c));
                }
            }
        }

        if (empties.isEmpty()) {
            return null;
        }

        return empties.get(random.nextInt(empties.size()));
    }

    private boolean allSameType(List<Move> group) {
        if (group.isEmpty()) return true;
        Class<?> first = group.get(0).animal.getClass();
        for (Move move : group) {
            if (move.animal.getClass() != first) {
                return false;
            }
        }
        return true;
    }

    private Animal makeNewAnimal(Class<?> type) {
        if (type == Bear.class) {
            return new Bear();
        } else if (type == Fish.class) {
            return new Fish();
        }
        return null;
    }

    private Cell chooseTarget(Cell source) {
        List<Cell> options = new ArrayList<>();

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int r = source.row + dr;
                int c = source.col + dc;
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    options.add(new Cell(r, c));
                }
            }
        }

        return options.get(random.nextInt(options.size()));
    }

    private void renderGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Animal a = river[r][c];
                if (a == null) {
                    cells[r][c].setFill(Color.web("#1f3b5c"));
                } else if (a instanceof Bear) {
                    cells[r][c].setFill(Color.web("#8b5a2b"));
                } else if (a instanceof Fish) {
                    cells[r][c].setFill(Color.web("#3ec5ff"));
                }
            }
        }
    }

    private static class Cell {
        final int row;
        final int col;

        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Cell)) return false;
            Cell other = (Cell) obj;
            return row == other.row && col == other.col;
        }

        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }

    private static class Move {
        final Cell source;
        final Cell target;
        final Animal animal;

        Move(Cell source, Cell target, Animal animal) {
            this.source = source;
            this.target = target;
            this.animal = animal;
        }
    }

    private abstract static class Animal {
    }

    private static class Bear extends Animal {
    }

    private static class Fish extends Animal {
    }
}

