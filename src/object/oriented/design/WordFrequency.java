package object.oriented.design;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
Write a Java program that inputs a list of words, separated by whitespace, and
outputs how many times each word appears in the list.
*/

public class WordFrequency {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a list of words separated by spaces:");
        String input = scanner.nextLine();

        String[] words = input.split("\\s+");

        Map<String, Integer> frequency = new HashMap<>();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            String normalized = word.toLowerCase();

            frequency.put(normalized, frequency.getOrDefault(normalized, 0) + 1);
        }

        System.out.println("Word frequencies:");
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        scanner.close();
    }
}
