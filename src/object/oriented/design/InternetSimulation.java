package object.oriented.design;

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

public class InternetSimulation {

    public static class Packet {
        private final String from;
        private final String to;
        private final int id;
        private final String data;

        public Packet(String from, String to, int id, String data) {
            this.from = from;
            this.to = to;
            this.id = id;
            this.data = data;
        }

        public String getFrom() { return from; }
        public String getTo() { return to; }
        public int getId() { return id; }
        public String getData() { return data; }

        @Override
        public String toString() {
            return "Packet[id=" + id + ", from=" + from + ", to=" + to + ", data=" + data + "]";
        }
    }

    public static class Alice extends Thread {
        private final BlockingQueue<Packet> aliceToInternet;
        private final Random random = new Random();
        private int nextPacketId = 1;
        private volatile boolean running = true;

        public Alice(BlockingQueue<Packet> aliceToInternet) {
            this.aliceToInternet = aliceToInternet;
        }

        public void stopRunning() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                int batchSize = 1 + random.nextInt(3); 
                List<Packet> batch = new ArrayList<>();

                for (int i = 0; i < batchSize; i++) {
                    Packet p = new Packet("Alice", "Bob", nextPacketId++, "Message " + nextPacketId);
                    batch.add(p);
                }

                for (Packet p : batch) {
                    try {
                        aliceToInternet.put(p);
                        System.out.println("Alice created: " + p);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                try {
                    Thread.sleep(500 + random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public static class Internet extends Thread {
        private final BlockingQueue<Packet> aliceToInternet;
        private final BlockingQueue<Packet> internetToBob;
        private volatile boolean running = true;

        public Internet(BlockingQueue<Packet> aliceToInternet, BlockingQueue<Packet> internetToBob) {
            this.aliceToInternet = aliceToInternet;
            this.internetToBob = internetToBob;
        }

        public void stopRunning() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Packet p = aliceToInternet.poll(200, TimeUnit.MILLISECONDS); // check if Alice has packets
                    if (p != null) {
                        internetToBob.put(p);  // deliver to Bob
                        System.out.println("Internet delivered: " + p);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public static class Bob extends Thread {
        private final BlockingQueue<Packet> internetToBob;
        private volatile boolean running = true;

        public Bob(BlockingQueue<Packet> internetToBob) {
            this.internetToBob = internetToBob;
        }

        public void stopRunning() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Packet p = internetToBob.poll(300, TimeUnit.MILLISECONDS); // Bob checks periodically
                    if (p != null) {
                        System.out.println("Bob read: " + p);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        BlockingQueue<Packet> aliceToInternet = new LinkedBlockingQueue<>();
        BlockingQueue<Packet> internetToBob = new LinkedBlockingQueue<>();

        Alice alice = new Alice(aliceToInternet);
        Internet internet = new Internet(aliceToInternet, internetToBob);
        Bob bob = new Bob(internetToBob);

        alice.start();
        internet.start();
        bob.start();

        try {
            Thread.sleep(15000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        alice.stopRunning();
        internet.stopRunning();
        bob.stopRunning();

        alice.interrupt();
        internet.interrupt();
        bob.interrupt();
    }
}
