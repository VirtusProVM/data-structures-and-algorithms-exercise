package object.oriented.design;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/*
Write a set of Java classes that can simulate an Internet application in which one
party, Alice, is periodically creating a set of packets that she wants to send to
Bob. An Internet process is continually checking if Alice has any packets to
send, and if so, it delivers them to Bob’s computer; Bob is periodically checking
if his computer has a packet from Alice, and if so, he reads and deletes it.
*/

public class InternetSimulationFX extends Application {

    private final BlockingQueue<Packet> aliceQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Packet> bobQueue = new LinkedBlockingQueue<>();

    private final ObservableList<String> aliceItems = FXCollections.observableArrayList();
    private final ObservableList<String> internetItems = FXCollections.observableArrayList();
    private final ObservableList<String> bobItems = FXCollections.observableArrayList();

    private final TextArea logArea = new TextArea();

    private AliceThread aliceThread;
    private InternetThread internetThread;
    private BobThread bobThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ListView<String> aliceView = new ListView<>(aliceItems);
        ListView<String> internetView = new ListView<>(internetItems);
        ListView<String> bobView = new ListView<>(bobItems);

        aliceView.setPrefHeight(220);
        internetView.setPrefHeight(220);
        bobView.setPrefHeight(220);

        logArea.setPrefHeight(140);
        logArea.setEditable(false);

        VBox left = new VBox(8, new Label("Alice: packets created"), aliceView);
        VBox mid = new VBox(8, new Label("Internet: currently delivering"), internetView);
        VBox right = new VBox(8, new Label("Bob: packets received"), bobView);

        HBox hbox = new HBox(20, left, mid, right);
        hbox.setPadding(new Insets(15));

        VBox root = new VBox(15, hbox, new Label("Event log"), logArea);
        root.setPadding(new Insets(15));

        primaryStage.setTitle("Internet Packet Simulation");
        primaryStage.setScene(new Scene(root, 900, 550));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            if (aliceThread != null) aliceThread.stopThread();
            if (internetThread != null) internetThread.stopThread();
            if (bobThread != null) bobThread.stopThread();
        });

        aliceThread = new AliceThread();
        internetThread = new InternetThread();
        bobThread = new BobThread();

        aliceThread.start();
        internetThread.start();
        bobThread.start();
    }

    private void appendLog(String s) {
        Platform.runLater(() -> {
            logArea.appendText(s + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void addAliceItem(String s) {
        Platform.runLater(() -> {
            aliceItems.add(s);
        });
    }

    private void removeAliceItem(String s) {
        Platform.runLater(() -> {
            aliceItems.remove(s);
        });
    }

    private void addInternetItem(String s) {
        Platform.runLater(() -> {
            internetItems.add(s);
        });
    }

    private void removeInternetItem(String s) {
        Platform.runLater(() -> {
            internetItems.remove(s);
        });
    }

    private void addBobItem(String s) {
        Platform.runLater(() -> {
            bobItems.add(s);
        });
    }

    private static class Packet {
        private final int id;
        private final String data;

        Packet(int id, String data) {
            this.id = id;
            this.data = data;
        }

        public int getId() { return id; }
        public String getData() { return data; }

        @Override
        public String toString() {
            return "Packet #" + id + " (" + data + ")";
        }
    }

    class AliceThread extends Thread {
        private volatile boolean running = true;
        private final Random random = new Random();
        private int nextId = 1;

        public void stopThread() {
            running = false;
            interrupt();
        }

        @Override
        public void run() {
            while (running) {
                int batchSize = 1 + random.nextInt(3);  // create 1..3 packets
                List<Packet> batch = new ArrayList<>();

                for (int i = 0; i < batchSize; i++) {
                    Packet p = new Packet(nextId++, "message");
                    batch.add(p);
                    aliceQueue.add(p);
                    addAliceItem(p.toString());
                    appendLog("Alice created: " + p);
                }

                try {
                    Thread.sleep(700 + random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    class InternetThread extends Thread {
        private volatile boolean running = true;

        public void stopThread() {
            running = false;
            interrupt();
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Packet p = aliceQueue.poll(200, TimeUnit.MILLISECONDS);
                    if (p != null) {
                        // remove from Alice queue display
                        removeAliceItem(p.toString());

                        addInternetItem(p.toString());
                        appendLog("Internet picked up: " + p);

                        Thread.sleep(250); // brief "delivery in transit"

                        bobQueue.put(p);
                        removeInternetItem(p.toString());

                        appendLog("Internet delivered to Bob: " + p);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    class BobThread extends Thread {
        private volatile boolean running = true;

        public void stopThread() {
            running = false;
            interrupt();
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Packet p = bobQueue.poll(400, TimeUnit.MILLISECONDS);
                    if (p != null) {
                        addBobItem("Read " + p);
                        appendLog("Bob read and deleted: " + p);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
