package net.cafe;
import java.util.*;
import java.io.*;

public class TransactionHistory {
    static Scanner sc = ByteMeCafe.sc;
    static final String FILE_NAME = "transactions.txt";

    // ---------------- START: TRANSACTION HISTORY (STAFF AND ADMIN) ----------------
    static void show() {
        System.out.println("\n===== TRANSACTION HISTORY =====");

        // Select Date Range
        String from = askDate("From date");
        if (from == null) {
            return;
        }
        String to = "";
        boolean ok = false;
        while (!ok) {
            to = askDate("To date");
            if (to == null) {
                return;
            }
            if (to.compareTo(from) < 0) {
                System.out.println("To date must not be earlier than the from date.");
            } else {
                ok = true;
            }
        }

        // Are There Any Transactions?
        int found = countMatches(from, to);
        if (found == 0) {
            System.out.println("No Transactions Found");
            return;
        }

        // Display Transaction History
        System.out.println("\n--- Transactions from " + from + " to " + to + " ---");
        try (Scanner fr = new Scanner(new File(FILE_NAME))) {
            while (fr.hasNextLine()) {
                String line = fr.nextLine();
                if (inRange(line, from, to)) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while reading the transactions.");
        }
        System.out.println("Total: " + found + " transaction(s)");
        // End -> back to Staff / Admin menu
    }

    // ---------------- ASK FOR A DATE ----------------
    // Returns the date as yyyy-MM-dd, or null if the user types 0 to cancel.
    static String askDate(String label) {
        while (true) {
            System.out.print(label + " (yyyy-MM-dd, 0 to cancel): ");
            String d = sc.nextLine().trim();
            if (d.equals("0")) {
                return null;
            }
            if (validDate(d)) {
                return d;
            }
            System.out.println("Invalid date. Use yyyy-MM-dd (example: 2026-11-05).");
        }
    }

    static boolean validDate(String d) {
        if (!d.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return false;
        }
        int month = Integer.parseInt(d.substring(5, 7));
        int day = Integer.parseInt(d.substring(8, 10));
        return month >= 1 && month <= 12 && day >= 1 && day <= 31;
    }

    // ---------------- FILE HELPERS ----------------
    // Each line in transactions.txt looks like:
    // TYPE | yyyy-MM-dd HH:mm | details...
    // Example: TOP UP | 2026-11-05 14:30 | Customer 001 | PC-01 | Cash | PHP 100
    static boolean inRange(String line, String from, String to) {
        String[] p = line.split("\\|");
        if (p.length < 2) {
            return false;
        }
        String d = p[1].trim();
        if (d.length() < 10) {
            return false;
        }
        String day = d.substring(0, 10);
        if (!validDate(day)) {
            return false;
        }
        return day.compareTo(from) >= 0 && day.compareTo(to) <= 0;
    }

    static int countMatches(String from, String to) {
        int n = 0;
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            return 0;
        }
        try (Scanner fr = new Scanner(f)) {
            while (fr.hasNextLine()) {
                if (inRange(fr.nextLine(), from, to)) {
                    n++;
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return n;
    }

    // Current date and time as "yyyy-MM-dd HH:mm". Use this when saving any transaction.
    static String now() {
        Calendar c = Calendar.getInstance();
        return String.format("%04d-%02d-%02d %02d:%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }
}
