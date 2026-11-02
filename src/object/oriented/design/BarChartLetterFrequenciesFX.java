package object.oriented.design;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/*

Write a Java program that inputs a document and then outputs a bar-chart plot of
the frequencies of each alphabet character that appears within that document.

*/

public class BarChartLetterFrequenciesFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int[] counts = new int[26];

        try {
            if (getParameters().getRaw().size() > 0) {
                String filePath = getParameters().getRaw().get(0);
                try (InputStream in = new FileInputStream(filePath)) {
                    readAndCount(in, counts);
                }
            } else {
                readAndCount(System.in, counts);
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            System.exit(1);
        }

        BarChart<String, Number> chart = createChart(counts);

        primaryStage.setTitle("Letter Frequency");
        primaryStage.setScene(new Scene(chart, 1000, 650));
        primaryStage.show();
    }

    private static void readAndCount(InputStream in, int[] counts) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            int ch;
            while ((ch = reader.read()) != -1) {
                char c = (char) ch;
                if (Character.isLetter(c)) {
                    char upper = Character.toUpperCase(c);
                    if (upper >= 'A' && upper <= 'Z') {
                        counts[upper - 'A']++;
                    }
                }
            }
        }
    }

    private static BarChart<String, Number> createChart(int[] counts) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Letter");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequency");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Alphabet Character Frequency");
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            series.getData().add(new XYChart.Data<>(String.valueOf(letter), counts[i]));
        }

        chart.getData().add(series);
        return chart;
    }
}
