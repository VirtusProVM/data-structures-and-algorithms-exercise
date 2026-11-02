package object.oriented.design;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

/*
Define a Polygon interface that has methods area( ) and perimeter( ). Then implement
classes for Triangle, Quadrilateral, Pentagon, Hexagon, and Octagon,
which implement this interface, with the obvious meanings for the area( ) and
perimeter( ) methods. Also implement classes, IsoscelesTriangle, Equilateral-
Triangle, Rectangle, and Square, which have the appropriate inheritance relationships.
Finally, write a simple user interface, which allows users to create
polygons of the various types, input their geometric dimensions, and then output
their area and perimeter. For extra effort, allow users to input polygons by
specifying their vertex coordinates and be able to test if two such polygons are
similar.
*/

public class PolygonApp extends Application {

    private final ComboBox<String> shapeCombo = new ComboBox<>(
            FXCollections.observableArrayList(
                    "Triangle",
                    "Isosceles Triangle",
                    "Equilateral Triangle",
                    "Quadrilateral",
                    "Rectangle",
                    "Square",
                    "Pentagon",
                    "Hexagon",
                    "Octagon"
            )
    );

    private final VBox dynamicFields = new VBox(10);
    private final Label resultLabel = new Label("Select a polygon to calculate area and perimeter.");
    private final TextArea vertexResultArea = new TextArea();

    private final TextField firstVertexCount = new TextField("3");
    private final TextField secondVertexCount = new TextField("3");
    private final VBox firstVertexPane = new VBox(8);
    private final VBox secondVertexPane = new VBox(8);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Polygon Calculator");

        shapeCombo.setValue("Triangle");
        shapeCombo.setOnAction(e -> refreshDimensionFields());

        Button computeButton = new Button("Compute");
        computeButton.setOnAction(e -> computeSelectedPolygon());

        VBox leftPane = new VBox(15);
        leftPane.setPadding(new Insets(20));
        leftPane.setPrefWidth(420);

        Label shapeLabel = new Label("Choose polygon type:");
        leftPane.getChildren().addAll(shapeLabel, shapeCombo, dynamicFields, computeButton, resultLabel);

        refreshDimensionFields();

        TabPane tabPane = new TabPane();
        Tab shapesTab = new Tab("Dimensions");
        shapesTab.setContent(leftPane);

        VBox vertexTabContent = buildVertexTab();
        Tab verticesTab = new Tab("Vertices + Similarity");
        verticesTab.setContent(vertexTabContent);

        tabPane.getTabs().addAll(shapesTab, verticesTab);

        Scene scene = new Scene(tabPane, 900, 650);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildVertexTab() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        HBox countRow = new HBox(15);
        countRow.setAlignment(Pos.CENTER_LEFT);

        Label firstCountLabel = new Label("First polygon vertices:");
        firstVertexCount.setPrefWidth(80);
        Label secondCountLabel = new Label("Second polygon vertices:");
        secondVertexCount.setPrefWidth(80);

        countRow.getChildren().addAll(firstCountLabel, firstVertexCount, secondCountLabel, secondVertexCount);
        root.getChildren().add(countRow);

        Button buildFirstBtn = new Button("Generate first polygon points");
        Button buildSecondBtn = new Button("Generate second polygon points");
        Button compareBtn = new Button("Check similarity");

        buildFirstBtn.setOnAction(e -> generateVertexInputs(firstVertexCount, firstVertexPane));
        buildSecondBtn.setOnAction(e -> generateVertexInputs(secondVertexCount, secondVertexPane));
        compareBtn.setOnAction(e -> compareVertexPolygons());

        HBox btnRow = new HBox(12);
        btnRow.getChildren().addAll(buildFirstBtn, buildSecondBtn, compareBtn);

        SplitPane split = new SplitPane();
        split.setPrefHeight(350);

        VBox firstBox = new VBox(8);
        firstBox.setPadding(new Insets(10));
        firstBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8;");
        firstBox.getChildren().add(new Label("Polygon 1"));
        firstBox.getChildren().add(firstVertexPane);

        VBox secondBox = new VBox(8);
        secondBox.setPadding(new Insets(10));
        secondBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8;");
        secondBox.getChildren().add(new Label("Polygon 2"));
        secondBox.getChildren().add(secondVertexPane);

        split.getItems().addAll(firstBox, secondBox);
        root.getChildren().addAll(btnRow, split, vertexResultArea);

        generateVertexInputs(firstVertexCount, firstVertexPane);
        generateVertexInputs(secondVertexCount, secondVertexPane);

        return root;
    }

    private void refreshDimensionFields() {
        dynamicFields.getChildren().clear();

        String type = shapeCombo.getValue();
        switch (type) {
            case "Triangle" -> addFields("a", "b", "c");
            case "Isosceles Triangle" -> addFields("base", "equal side");
            case "Equilateral Triangle" -> addFields("side");
            case "Quadrilateral" -> addFields("a", "b", "c", "d", "diagonal AC", "diagonal BD", "angle between diagonals (radians)");
            case "Rectangle" -> addFields("width", "height");
            case "Square" -> addFields("side");
            case "Pentagon" -> addFields("side");
            case "Hexagon" -> addFields("side");
            case "Octagon" -> addFields("side");
            default -> addFields("side");
        }
    }

    private void addFields(String... names) {
        for (String name : names) {
            Label label = new Label(name + ":");
            TextField field = new TextField();
            field.setPrefWidth(180);

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().addAll(label, field);

            dynamicFields.getChildren().add(row);
        }
    }

    private void computeSelectedPolygon() {
        String type = shapeCombo.getValue();
        try {
            Polygon p = buildPolygon(type);
            String message = String.format(
                    "%s\nArea: %.6f\nPerimeter: %.6f",
                    type,
                    p.area(),
                    p.perimeter()
            );
            resultLabel.setText(message);
        } catch (Exception ex) {
            resultLabel.setText("Error: " + ex.getMessage());
        }
    }

    private Polygon buildPolygon(String type) {
        switch (type) {
            case "Triangle" -> {
                double a = getDoubleAt(0);
                double b = getDoubleAt(1);
                double c = getDoubleAt(2);
                return new Triangle(a, b, c);
            }
            case "Isosceles Triangle" -> {
                double base = getDoubleAt(0);
                double equalSide = getDoubleAt(1);
                return new IsoscelesTriangle(base, equalSide);
            }
            case "Equilateral Triangle" -> {
                double side = getDoubleAt(0);
                return new EquilateralTriangle(side);
            }
            case "Quadrilateral" -> {
                double a = getDoubleAt(0);
                double b = getDoubleAt(1);
                double c = getDoubleAt(2);
                double d = getDoubleAt(3);
                double diag1 = getDoubleAt(4);
                double diag2 = getDoubleAt(5);
                double angle = getDoubleAt(6);
                return new Quadrilateral(a, b, c, d, diag1, diag2, angle);
            }
            case "Rectangle" -> {
                double w = getDoubleAt(0);
                double h = getDoubleAt(1);
                return new Rectangle(w, h);
            }
            case "Square" -> {
                double side = getDoubleAt(0);
                return new Square(side);
            }
            case "Pentagon" -> {
                double side = getDoubleAt(0);
                return new Pentagon(side);
            }
            case "Hexagon" -> {
                double side = getDoubleAt(0);
                return new Hexagon(side);
            }
            case "Octagon" -> {
                double side = getDoubleAt(0);
                return new Octagon(side);
            }
            default -> throw new IllegalArgumentException("Unsupported polygon type.");
        }
    }

    private double getDoubleAt(int index) {
        HBox row = (HBox) dynamicFields.getChildren().get(index);
        TextField field = (TextField) row.getChildren().get(1);
        return Double.parseDouble(field.getText().trim());
    }

    private void generateVertexInputs(TextField countField, VBox targetPane) {
        try {
            int count = Integer.parseInt(countField.getText().trim());
            if (count < 3) {
                targetPane.getChildren().setAll(new Label("Need at least 3 vertices."));
                return;
            }

            targetPane.getChildren().clear();
            for (int i = 0; i < count; i++) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                Label label = new Label("P" + (i + 1) + ":");
                TextField x = new TextField();
                x.setPromptText("x");
                TextField y = new TextField();
                y.setPromptText("y");

                row.getChildren().addAll(label, x, y);
                targetPane.getChildren().add(row);
            }
        } catch (Exception ex) {
            targetPane.getChildren().setAll(new Label("Invalid vertex count."));
        }
    }

    private void compareVertexPolygons() {
        try {
            VertexPolygon p1 = readVertexPolygon(firstVertexPane);
            VertexPolygon p2 = readVertexPolygon(secondVertexPane);

            boolean similar = p1.isSimilarTo(p2);

            vertexResultArea.setText(
                    "Polygon 1 area: " + p1.area() + "\n" +
                    "Polygon 2 area: " + p2.area() + "\n" +
                    "Similarity: " + (similar ? "YES" : "NO")
            );
        } catch (Exception ex) {
            vertexResultArea.setText("Error: " + ex.getMessage());
        }
    }

    private VertexPolygon readVertexPolygon(VBox pane) {
        List<Vertex> points = new ArrayList<>();

        for (int i = 0; i < pane.getChildren().size(); i++) {
            Node node = pane.getChildren().get(i);
            if (node instanceof HBox row) {
                TextField xField = (TextField) row.getChildren().get(1);
                TextField yField = (TextField) row.getChildren().get(2);

                double x = Double.parseDouble(xField.getText().trim());
                double y = Double.parseDouble(yField.getText().trim());
                points.add(new Vertex(x, y));
            }
        }

        if (points.size() < 3) {
            throw new IllegalArgumentException("Each polygon must have at least 3 points.");
        }

        return new VertexPolygon(points);
    }

    interface Polygon {
        double area();
        double perimeter();
    }

    static class Vertex {
        final double x;
        final double y;

        Vertex(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Triangle implements Polygon {
        protected final double a, b, c;

        public Triangle(double a, double b, double c) {
            if (a <= 0 || b <= 0 || c <= 0) {
                throw new IllegalArgumentException("Triangle sides must be positive.");
            }
            if (a + b <= c || a + c <= b || b + c <= a) {
                throw new IllegalArgumentException("These sides do not form a triangle.");
            }
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public double perimeter() {
            return a + b + c;
        }

        @Override
        public double area() {
            double s = perimeter() / 2.0;
            return Math.sqrt(s * (s - a) * (s - b) * (s - c));
        }
    }

    static class IsoscelesTriangle extends Triangle {
        public IsoscelesTriangle(double base, double equalSide) {
            super(base, equalSide, equalSide);
        }
    }

    static class EquilateralTriangle extends Triangle {
        public EquilateralTriangle(double side) {
            super(side, side, side);
        }

        @Override
        public double area() {
            return (Math.sqrt(3.0) / 4.0) * a * a;
        }
    }

    static class Quadrilateral implements Polygon {
        protected final double a, b, c, d;
        protected final double diagonal1, diagonal2, angle;

        public Quadrilateral(double a, double b, double c, double d,
                            double diagonal1, double diagonal2, double angle) {
            if (a <= 0 || b <= 0 || c <= 0 || d <= 0) {
                throw new IllegalArgumentException("Quadrilateral sides must be positive.");
            }
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.diagonal1 = diagonal1;
            this.diagonal2 = diagonal2;
            this.angle = angle;
        }

        @Override
        public double perimeter() {
            return a + b + c + d;
        }

        @Override
        public double area() {
            return 0.5 * diagonal1 * diagonal2 * Math.sin(angle);
        }
    }

    static class Rectangle extends Quadrilateral {
        protected final double width, height;

        public Rectangle(double width, double height) {
            super(width, height, width, height,
                    Math.hypot(width, height),
                    Math.hypot(width, height),
                    Math.PI / 2.0);
            this.width = width;
            this.height = height;
        }

        @Override
        public double perimeter() {
            return 2 * (width + height);
        }

        @Override
        public double area() {
            return width * height;
        }
    }

    static class Square extends Rectangle {
        public Square(double side) {
            super(side, side);
        }

        @Override
        public double perimeter() {
            return 4 * width;
        }

        @Override
        public double area() {
            return width * width;
        }
    }

    static class RegularPolygon implements Polygon {
        protected final int sides;
        protected final double sideLength;

        public RegularPolygon(int sides, double sideLength) {
            if (sides < 3) {
                throw new IllegalArgumentException("A polygon must have at least 3 sides.");
            }
            if (sideLength <= 0) {
                throw new IllegalArgumentException("Side length must be positive.");
            }
            this.sides = sides;
            this.sideLength = sideLength;
        }

        @Override
        public double perimeter() {
            return sides * sideLength;
        }

        @Override
        public double area() {
            return (sides * sideLength * sideLength) / (4.0 * Math.tan(Math.PI / sides));
        }
    }

    static class Pentagon extends RegularPolygon {
        public Pentagon(double sideLength) {
            super(5, sideLength);
        }
    }

    static class Hexagon extends RegularPolygon {
        public Hexagon(double sideLength) {
            super(6, sideLength);
        }
    }

    static class Octagon extends RegularPolygon {
        public Octagon(double sideLength) {
            super(8, sideLength);
        }
    }

    static class VertexPolygon implements Polygon {
        private final List<Vertex> vertices;

        public VertexPolygon(List<Vertex> vertices) {
            if (vertices == null || vertices.size() < 3) {
                throw new IllegalArgumentException("A vertex-based polygon must have at least 3 points.");
            }
            this.vertices = new ArrayList<>(vertices);
        }

        @Override
        public double perimeter() {
            double total = 0;
            for (int i = 0; i < vertices.size(); i++) {
                Vertex current = vertices.get(i);
                Vertex next = vertices.get((i + 1) % vertices.size());
                total += dist(current, next);
            }
            return total;
        }

        @Override
        public double area() {
            double sum = 0;
            for (int i = 0; i < vertices.size(); i++) {
                Vertex current = vertices.get(i);
                Vertex next = vertices.get((i + 1) % vertices.size());
                sum += current.x * next.y - next.x * current.y;
            }
            return Math.abs(sum) / 2.0;
        }

        public boolean isSimilarTo(VertexPolygon other) {
            if (other == null) return false;
            if (vertices.size() != other.vertices.size()) return false;
            if (vertices.size() < 3) return false;

            double[] thisSides = sideLengths(vertices);
            double[] otherSides = sideLengths(other.vertices);

            for (int start = 0; start < thisSides.length; start++) {
                double ratio = thisSides[0] / otherSides[start];
                boolean ok = true;

                for (int i = 0; i < thisSides.length; i++) {
                    double expected = otherSides[(start + i) % otherSides.length] * ratio;
                    if (Math.abs(thisSides[i] - expected) > 1e-4) {
                        ok = false;
                        break;
                    }
                }

                if (ok) return true;
            }

            return false;
        }

        private static double[] sideLengths(List<Vertex> pts) {
            double[] result = new double[pts.size()];
            for (int i = 0; i < pts.size(); i++) {
                Vertex current = pts.get(i);
                Vertex next = pts.get((i + 1) % pts.size());
                result[i] = dist(current, next);
            }
            return result;
        }

        private static double dist(Vertex a, Vertex b) {
            return Math.hypot(a.x - b.x, a.y - b.y);
        }
    }
}
