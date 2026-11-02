package object.oriented.design;

import java.util.Scanner;

/*
Write a Java program that can “make change.” Your program should take two
numbers as input, one that is a monetary amount charged and the other that is
a monetary amount given. It should then return the number of each kind of bill
and coin to give back as change for the difference between the amount given and
the amount charged. The values assigned to the bills and coins can be based on
the monetary system of any current or former government. Try to design your
program so that it returns the fewest number of bills and coins as possible.
*/

public class MakeChange {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter amount charged: ");
        double charged = input.nextDouble();

        System.out.print("Enter amount given: ");
        double given = input.nextDouble();

        int chargedCents = (int) Math.round(charged * 100);
        int givenCents = (int) Math.round(given * 100);

        int change = givenCents - chargedCents;

        if (change < 0) {
            System.out.println("Not enough money was given.");
            return;
        }

        int[] bills = {10000, 5000, 2000, 1000, 500, 100}; 
        int[] coins = {2500, 1000, 500, 250, 100, 25, 10, 5, 1}; 

        System.out.println("Change due: $" + (change / 100.0));
        System.out.println();

        int[] billCounts = new int[bills.length];
        int[] coinCounts = new int[coins.length];

        for (int i = 0; i < bills.length; i++) {
            billCounts[i] = change / bills[i];
            change = change % bills[i];
        }

        for (int i = 0; i < coins.length; i++) {
            coinCounts[i] = change / coins[i];
            change = change % coins[i];
        }

        if (change != 0) {
            System.out.println("Exact change could not be made with this denomination set.");
        }

        String[] billNames = {"$100 bills", "$50 bills", "$20 bills", "$10 bills", "$5 bills", "$1 bills"};
        for (int i = 0; i < bills.length; i++) {
            if (billCounts[i] > 0) {
                System.out.println(billCounts[i] + " " + billNames[i]);
            }
        }

        String[] coinNames = {"$1 coins", "50¢ coins", "25¢ coins", "10¢ coins", "5¢ coins", "1¢ coins"};
        String[] exactCoinNames = {
                "quarters", "dimes", "nickels", "pennies"
        };

        int[] exactCoinValues = {25, 10, 5, 1};

        int remaining = (int) Math.round((givenCents - chargedCents) % 100);

        int totalChange = givenCents - chargedCents;
        int quarters = totalChange / 25;
        totalChange %= 25;

        int dimes = totalChange / 10;
        totalChange %= 10;

        int nickels = totalChange / 5;
        totalChange %= 5;

        int pennies = totalChange;

        if (quarters > 0) System.out.println(quarters + " quarters");
        if (dimes > 0) System.out.println(dimes + " dimes");
        if (nickels > 0) System.out.println(nickels + " nickels");
        if (pennies > 0) System.out.println(pennies + " pennies");

        int totalBills = givenCents - chargedCents;
        int hundreds = totalBills / 10000;
        totalBills %= 10000;

        int fifties = totalBills / 5000;
        totalBills %= 5000;

        int twenties = totalBills / 2000;
        totalBills %= 2000;

        int tens = totalBills / 1000;
        totalBills %= 1000;

        int fives = totalBills / 500;
        totalBills %= 500;

        int ones = totalBills / 100;
        totalBills %= 100;

        if (hundreds > 0) System.out.println(hundreds + " $100 bills");
        if (fifties > 0) System.out.println(fifties + " $50 bills");
        if (twenties > 0) System.out.println(twenties + " $20 bills");
        if (tens > 0) System.out.println(tens + " $10 bills");
        if (fives > 0) System.out.println(fives + " $5 bills");
        if (ones > 0) System.out.println(ones + " $1 bills");

        input.close();
    }
}
