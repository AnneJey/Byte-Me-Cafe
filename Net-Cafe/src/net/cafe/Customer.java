/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.cafe;

import java.util.*;
import java.io.*;

public class Customer {
    static Scanner sc = NetCafe.sc;
    
    static final String FOOD_FILE = "foodInventory.txt";
    static final String COMPUTER_FOLDER = "computers";
    static final String SESSION_FOLDER = "sessions";
    static final String ORDER_FOLDER   = "orders";

    static String currentId = null;
    static String currentName = null;
    
    public static void main(String[] args) {
    }
    
    // Customer Menu
    static void customerMenu() {
        boolean loggedIn = true;
 
        while (loggedIn) {
            System.out.println("\n=========================");
            System.out.println("   Customer Menu   ");
            System.out.println("=========================");
            System.out.println("Logged in as: " + currentName + " (ID " + currentId + ")");
            System.out.println("1. View Available Computers");
            System.out.println("2. Start Session");
            System.out.println("3. View Menu and Order");
            System.out.println("4. My Session and Bill");
            System.out.println("0. Logout");
            System.out.println("---");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
 
            switch (choice) {
                case "1":
                    viewAvailableComp();
                    break;
                case "2":
                    startSession();
                    break;
                case "3":
                    viewMenuOrder();
                    break;
                case "4":
                    SessionBill();
                    break;
                case "0":
                    if (readSession() != null) {
                        System.out.println("\nYou still have an active session.");
                        System.out.println("Please end it in 'My Session and Bill' before logging out.");
                        break;
                    }
                    System.out.println("\nOpening Log Out Menu...");
                    currentId = null;
                    currentName = null;
                    NetCafe.logOut();
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
    
    // 1.
    static void viewAvailableComp() {
        System.out.println("\n=========================");
        System.out.println("   Available Computers   ");
        System.out.println("=========================");
        System.out.printf("%-10s %-15s %s%n", "ID", "Rate/Hour", "Status");
        System.out.println("------------------------------------------");

        File[] files = getAllComputerFiles();

        int count = 0;
        for (File f : files) {
            String[] data = readComputer(f);
            if (data == null || data.length < 3) continue;

            String status = data[2].trim();
            if (status.equalsIgnoreCase("Available")) {
                System.out.printf("%-10s %-15s %s%n",
                        data[0].trim(), data[1].trim(), status);
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No available computers right now.");
        } else {
            System.out.println("------------------------------------------");
            System.out.println("Total available: " + count);
        }

        System.out.println("==========================================");
        System.out.println("0. Back");
        System.out.println("---");

        while (true) {
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine().trim();
    
            if (choice.equals("0")) {
                return;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    // 2.
    static void startSession() {
        // 1. One session per customer only
        if (readSession() != null) {
            System.out.println("\nYou already have an active session.");
            System.out.println("Check 'My Session and Bill' for details.");
            return;
        }

        // 2. Show available computers
        System.out.println("===================");
        System.out.println("   Start Session   ");
        System.out.println("===================");
        System.out.printf("%-10s %-15s %s%n", "ID", "Rate/Hour", "Status");
        System.out.println("------------------------------------------");

        File[] files = getAllComputerFiles();

        int count = 0;
        for (File f : files) {
            String[] data = readComputer(f);
            if (data == null || data.length < 3) continue;

            if (data[2].trim().equalsIgnoreCase("Available")) {
                System.out.printf("%-10s %-15s %s%n",
                        data[0].trim(), data[1].trim(), data[2].trim());
                count++;
            }
        }
        
        if (count == 0) {
            System.out.println("No available computers right now.");
            System.out.println("==========================================");
            return;
        }

        System.out.println("==========================================");
        System.out.println("Enter a computer ID to start: ");
        System.out.println("---");

        // 3. Pick a computer
        while (true) {
            System.out.print("Enter computer ID: ");
            String choice = sc.nextLine().trim();

            }

            File chosenFile = null;
            String[] chosen = null;
            for (File f : files) {
                String[] data = readComputer(f);
                if (data == null || data.length < 3) continue;

                if (data[0].trim().equalsIgnoreCase(choice)
                        && data[2].trim().equalsIgnoreCase("Available")) {
                    chosenFile = f;
                    chosen = data;
                    break;
                }
            }

            if (chosenFile == null) {
                System.out.println("That computer is not available. Please try again.");
                continue;
            }

            // 4. Rewrite the computer as "In Use"
            try (PrintWriter pw = new PrintWriter(new FileWriter(chosenFile))) {
                pw.println(chosen[0].trim() + "," + chosen[1].trim() + ",In Use");
            } catch (IOException e) {
                System.out.println("An error occurred while updating the computer.");
                continue;
            }

            // 5. Create the session file in sessions/001.txt
            new File(SESSION_FOLDER).mkdirs();
            File sessionFile = new File(SESSION_FOLDER, currentId + ".txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(sessionFile))) {
                pw.println(chosen[0].trim() + "," + chosen[1].trim() + ",Active");
            } catch (IOException e) {
                System.out.println("An error occurred while creating your session.");
                
                // Undo the computer status change
                try (PrintWriter pw = new PrintWriter(new FileWriter(chosenFile))) {
                    pw.println(chosen[0].trim() + "," + chosen[1].trim() + ",Available");
                } catch (IOException ignored) { }
                continue;
            }

            System.out.println("\nSession started on computer " + chosen[0].trim() + ".");
            System.out.println("Rate: " + chosen[1].trim() + " per hour.");
            return;
        }
    }
    
    // 3.
    static void viewMenuOrder() {
        String[] names = new String[100];
        String[] categories = new String[100];
        double[] prices = new double[100];
        int[] stocks = new int[100];
        int count = 0;

        String[] cartNames = new String[100];
        double[] cartPrices = new double[100];
        int[] cartQtys = new int[100];
        int cartCount = 0;

        File file = new File(FOOD_FILE);
        if (!file.exists()) {
            System.out.println("Menu is not available right now.");
            return;
        }

        // Load Menu
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null && count < 100) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 4) continue;
                names[count] = p[0].trim();
                categories[count] = p[1].trim();
                prices[count] = Double.parseDouble(p[2].trim());
                stocks[count] = Integer.parseInt(p[3].trim());
                count++;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the menu.");
            return;
        }

        boolean ordering = true;
        while (ordering) {
            System.out.println("\n=== Cafe Menu ===");
            System.out.printf("%-5s %-20s %-10s %-10s %-10s%n",
                              "No.", "Item Name", "Category", "Price", "In Stock");
            System.out.println("------------------------------------------------------------");
            
            for (int i = 0; i < count; i++) {
                String stockDisplay = (stocks[i] > 0) ? String.valueOf(stocks[i]) : "Out";
                System.out.printf("%-5d %-20s %-10s %-10.2f %-10s%n",
                                  (i + 1), names[i], categories[i], prices[i], stockDisplay);
            }

            if (cartCount > 0) {
                System.out.println("\n--- Your Current Order ---");
                double cartTotal = 0;
                for (int i = 0; i < cartCount; i++) {
                    double sub = cartPrices[i] * cartQtys[i];
                    cartTotal += sub;
                    System.out.printf("- %dx %s (%.2f)%n", cartQtys[i], cartNames[i], sub);
                }
                System.out.printf("Total so far: %.2f%n", cartTotal);
            }

            System.out.println("\nOptions:");
            System.out.println("1. Add item to order");
            System.out.println("2. Confirm & Place Order");
            System.out.println("0. Cancel Order");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter item number to add: ");
                    int itemChoice;
                    try {
                        itemChoice = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        break;
                    }

                    if (itemChoice < 1 || itemChoice > count) {
                        System.out.println("Invalid item number.");
                        break;
                    }

                    int index = itemChoice - 1;

                    if (stocks[index] <= 0) {
                        System.out.println("Sorry, that item is out of stock.");
                        break;
                    }

                    System.out.print("Enter quantity: ");
                    int qty;
                    try {
                        qty = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        break;
                    }

                    if (qty <= 0 || qty > stocks[index]) {
                        System.out.println("Invalid quantity or not enough stock.");
                        break;
                    }

                    stocks[index] -= qty;

                    boolean alreadyInCart = false;
                    for (int i = 0; i < cartCount; i++) {
                        if (cartNames[i].equalsIgnoreCase(names[index])) {
                            cartQtys[i] += qty;
                            alreadyInCart = true;
                            break;
                        }
                    }

                    if (!alreadyInCart) {
                        cartNames[cartCount] = names[index];
                        cartPrices[cartCount] = prices[index];
                        cartQtys[cartCount] = qty;
                        cartCount++;
                    }
                    
                    System.out.println("Added to order: " + qty + "x " + names[index]);
                    break;

                case "2":
                    if (cartCount == 0) {
                        System.out.println("Your order is empty. Add something first.");
                        break;
                    }

                    // Save new stock
                    try (PrintWriter pw = new PrintWriter(new FileWriter(FOOD_FILE))) {
                        for (int i = 0; i < count; i++) {
                            pw.println(names[i] + "," + categories[i] + "," + prices[i] + "," + stocks[i]);
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred while updating inventory. Order failed.");
                        break;
                    }

                    // Save order to orders/001.txt
                    new File(ORDER_FOLDER).mkdirs();
                    File orderFile = new File(ORDER_FOLDER, currentId + ".txt");
                    try (PrintWriter pw = new PrintWriter(new FileWriter(orderFile, true))) {
                        for (int i = 0; i < cartCount; i++) {
                            pw.println(cartNames[i] + "," + cartPrices[i] + "," + cartQtys[i]);
                        }
                        System.out.println("\nOrder placed successfully! Thank you.");
                        System.out.println("You can view your bill in 'My Session and Bill'.");
                        ordering = false;
                    } catch (IOException e) {
                        System.out.println("An error occurred while saving your order.");
                    }
                    break;

                case "0":
                    System.out.println("Order cancelled.");
                    ordering = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    // 4.
    static void SessionBill() {
        System.out.println("\n=========================");
        System.out.println("   My Session and Bill   ");
        System.out.println("=========================");

        String[] session = readSession();
        if (session == null) {
            System.out.println("You do not have an active computer session.");
        } else {
            System.out.println("Active Computer: " + session[0]);
            System.out.println("Hourly Rate: " + session[1]);
        }

        System.out.println("\n==============================");
        System.out.println("   My Food and Drink Orders   ");
        System.out.println("==============================");

        File orderFile = new File(ORDER_FOLDER, currentId + ".txt");
        double foodTotal = 0;
        
        if (!orderFile.exists()) {
            System.out.println("No food or drinks ordered yet.");
        } else {
            System.out.printf("%-20s %-10s %-10s%n", "Item Name", "Qty", "Subtotal");
            System.out.println("------------------------------------------");
            try (Scanner fs = new Scanner(orderFile)) {
                while (fs.hasNextLine()) {
                    String line = fs.nextLine();
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    if (p.length < 3) continue;
                    
                    String name = p[0];
                    double price = Double.parseDouble(p[1]);
                    int qty = Integer.parseInt(p[2]);
                    double subtotal = price * qty;
                    foodTotal += subtotal;
                    
                    System.out.printf("%-20s %-10d %-10.2f%n", name, qty, subtotal);
                }
            } catch (Exception e) {
                System.out.println("An error occurred while reading your orders.");
            }
            System.out.println("------------------------------------------");
            System.out.printf("Food & Drinks Total: %.2f%n", foodTotal);
        }
        
        System.out.println("\n==============================");
        System.out.println("   End Session and Pay Bill   ");
        System.out.println("==============================");
        System.out.println("1. End Session & Pay Bill");
        System.out.println("0. Back to Menu");
        System.out.print("Enter choice: ");
        String choice = sc.nextLine().trim();

        if (choice.equals("1")) {
            if (session == null) {
                System.out.println("You don't have an active session to end.");
                System.out.println("\nPress Enter to return...");
                sc.nextLine();
                return;
            }

            String computerId = session[0].trim();
            
            // 1. Delete the session file
            File sessionFile = new File(SESSION_FOLDER, currentId + ".txt");
            if (sessionFile.exists()) {
                sessionFile.delete();
            }

            // 2. Find computer and set to Available
            File[] files = getAllComputerFiles();
            for (File f : files) {
                String[] data = readComputer(f);
                if (data != null && data.length >= 3 && data[0].trim().equalsIgnoreCase(computerId)) {
                    try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                        pw.println(data[0].trim() + "," + data[1].trim() + ",Available");
                    } catch (IOException e) {
                        System.out.println("An error occurred while updating computer status.");
                    }
                    break;
                }
            }

            // 3. Clear food orders so next time is fresh
            if (orderFile.exists()) {
                orderFile.delete();
            }

            System.out.println("\n=================================");
            System.out.println("      Session Ended!             ");
            System.out.println("      Thank you for visiting!    ");
            System.out.println("=================================");
            System.out.println("Your total bill was: " + String.format("%.2f", foodTotal));
            System.out.println("\nPress Enter to return to menu...");
            sc.nextLine();

        }
    }

    //--- Helpers ---
    static File[] getAllComputerFiles() {
        File folder = new File(COMPUTER_FOLDER);
        File[] files = folder.listFiles();
        if (files == null) return new File[0];
        return files;
    }

    static String[] readComputer(File f) {
        try (Scanner fs = new Scanner(f)) {
            if (!fs.hasNextLine()) return null;
            String[] data = fs.nextLine().split(",");
            if (data.length < 3) return null;
            return data;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    static String[] readSession() {
        if (currentId == null) return null;

        File f = new File(SESSION_FOLDER, currentId + ".txt");
        if (!f.exists()) return null;

        try (Scanner fs = new Scanner(f)) {
            if (!fs.hasNextLine()) return null;
            String[] data = fs.nextLine().split(",");
            if (data.length < 3) return null;
            return data;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
