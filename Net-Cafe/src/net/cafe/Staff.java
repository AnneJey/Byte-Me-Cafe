package net.cafe;

import java.util.*;
import java.io.*;

public class Staff {

    static Scanner sc = new Scanner(System.in);

    static String FOLDER = "computers";
    static String SESSION_FOLDER = "sessions";
    static String PAYMENTS = "transactions.txt";
    static String FOOD_FILE = "foodInventory.txt";
    static String ORDER_FOLDER = "orders";

    public static void main(String[] args) {
    }

    // ===== STAFF MENU =====
    static void staffMenu() {
        while (true) {
            System.out.println("\n===== STAFF MENU =====");
            System.out.println("1. Manage Computers");
            System.out.println("2. Start Session");
            System.out.println("3. End Session & Process Payment");
            System.out.println("4. Process Food Orders");
            System.out.println("5. Transaction History");
            System.out.println("0. Logout");

            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": StaffcompManage(); break;
                case "2": startSession(); break;
                case "3": endSessionAndPay(); break;
                case "4": processOrders(); break;
                case "5": transactionHistory(); break;
                case "0": 
                    System.out.println("Logging out...");
                    NetCafe.logOut(); 
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ===== COMPUTER MANAGEMENT =====
    static void StaffcompManage() {
        while (true) {
            System.out.println("\n===== MANAGE COMPUTERS =====");
            System.out.println("1. View All Computers");
            System.out.println("2. Search Computer by ID");
            System.out.println("3. Update Computer Status");
            System.out.println("0. Back");

            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": CompManage.viewAll(); break;
                case "2": CompManage.searchComp(); break;
                case "3": CompManage.status(); break;
                case "0": return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ===== START SESSION  =====
    static void startSession() {
        System.out.println("\n===== START CUSTOMER SESSION =====");
        System.out.print("Enter Customer ID or Username: ");
        String key = sc.nextLine().trim();

        String customerId = getCustomerIdFromKey(key);
        if (customerId == null) {
            System.out.println("Customer not found in the system.");
            return;
        }

        File sessionFile = new File(SESSION_FOLDER, customerId + ".txt");
        if (sessionFile.exists()) {
            System.out.println("Customer " + customerId + " already has an active session.");
            return;
        }

        // Show available computers
        File folder = new File(FOLDER);
        File[] files = folder.listFiles();
        int count = 0;

        System.out.println("\n===== AVAILABLE COMPUTERS =====");
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    String[] data = readComputer(f);
                    if (data != null && data[2].equalsIgnoreCase("Available")) {
                        System.out.println("ID: " + data[0] + " | Rate: " + data[1]);
                        count++;
                    }
                }
            }
        }

        if (count == 0) {
            System.out.println("No computers are currently available.");
            return;
        }

        System.out.print("\nEnter Computer ID to assign: ");
        String compId = sc.nextLine().trim();

        File chosenFile = null;
        String[] chosen = null;

        if (files != null) {
            for (File f : files) {
                String[] data = readComputer(f);
                if (data != null && data[0].equalsIgnoreCase(compId) && data[2].equalsIgnoreCase("Available")) {
                    chosenFile = f;
                    chosen = data;
                    break;
                }
            }
        }

        if (chosenFile == null) {
            System.out.println("That computer is not available.");
            return;
        }

        // 1. Update computer to "In Use"
        try (PrintWriter writer = new PrintWriter(new FileWriter(chosenFile))) {
            writer.println(chosen[0] + "," + chosen[1] + ",In Use");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the computer.");
            return;
        }

        // 2. Create the session file
        new File(SESSION_FOLDER).mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(sessionFile))) {
            writer.println(chosen[0] + "," + chosen[1] + ",Active");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the session.");
            return;
        }

        System.out.println("\nSession successfully started for Customer " + customerId + " on Computer " + chosen[0] + ".");
    }

    // ===== END SESSION & PAYMENT =====
    static void endSessionAndPay() {
        System.out.println("\n===== END SESSION & PROCESS PAYMENT =====");
        System.out.print("Enter Customer ID or Username: ");
        String key = sc.nextLine().trim();

        String customerId = getCustomerIdFromKey(key);
        if (customerId == null) {
            System.out.println("Customer not found in the system.");
            return;
        }

        File sessionFile = new File(SESSION_FOLDER, customerId + ".txt");
        if (!sessionFile.exists()) {
            System.out.println("No active session found for this customer.");
            return;
        }

        String computerID = "";
        String rate = "";
        
        try (Scanner reader = new Scanner(sessionFile)) {
            if (reader.hasNextLine()) {
                String[] data = reader.nextLine().split(",");
                if (data.length >= 2) {
                    computerID = data[0].trim();
                    rate = data[1].trim();
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while reading the session.");
            return;
        }

        File computerFile = new File(FOLDER, computerID + ".txt");
        String[] computer = readComputer(computerFile);
        
        // 1. Change computer status back to Available
        if (computer != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(computerFile))) {
                writer.println(computer[0] + "," + computer[1] + ",Available");
            } catch (IOException e) {
                System.out.println("An error occurred while updating the computer.");
                return;
            }
        }

        // 2. Delete the session file
        sessionFile.delete();

        // 3. Calculate total bill (Computer + Food)
        double totalBill = Double.parseDouble(rate);
        
        File orderFile = new File(ORDER_FOLDER, customerId + ".txt");
        System.out.println("\n========== BILL ==========");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Computer: " + computerID + " (Rate: " + rate + ")");
        
        if (orderFile.exists()) {
            try (Scanner reader = new Scanner(orderFile)) {
                while (reader.hasNextLine()) {
                    String[] data = reader.nextLine().split(",");
                    if (data.length >= 3) {
                        double price = Double.parseDouble(data[1]);
                        int qty = Integer.parseInt(data[2]);
                        totalBill += (price * qty);
                        System.out.println("- " + qty + "x " + data[0] + " (" + price + ")");
                    }
                }
            } catch (Exception e) {
                System.out.println("An error occurred while reading food orders.");
            }
        }

        System.out.println("==========================");
        System.out.println("Total Due: " + String.format("%.2f", totalBill));

        System.out.print("Confirm payment? (Y/N): ");
        String confirm = sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Payment cancelled. Session is ended, but bill remains unpaid.");
            return;
        }

        // 4. Save payment to transactions.txt
        try (FileWriter writer = new FileWriter(PAYMENTS, true)) {
            writer.write(customerId + ",Session+Food," + totalBill + "\n");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the payment.");
            return;
        }

        // 5. Delete order file (bill cleared)
        if (orderFile.exists()) {
            orderFile.delete();
        }

        System.out.println("Payment successful! Amount paid: " + String.format("%.2f", totalBill));
    }

    // ===== PROCESS ORDERS =====
        // ===== PROCESS ORDERS =====
    static void processOrders() {
        System.out.println("\n===== PROCESS ORDERS =====");
        System.out.print("Enter Customer ID or Username: ");
        String key = sc.nextLine().trim();

        String customerId = getCustomerIdFromKey(key);
        if (customerId == null) {
            System.out.println("Customer not found.");
            return;
        }

        String[] names = new String[100];
        String[] categories = new String[100];
        double[] prices = new double[100];
        int[] stocks = new int[100];
        int count = 0;

        String[] cartNames = new String[100];
        double[] cartPrices = new double[100];
        int[] cartQty = new int[100];
        int cartCount = 0;

        File file = new File(FOOD_FILE);
        if (!file.exists()) {
            System.out.println("Menu not available.");
            return;
        }

        // Read inventory
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine() && count < 100) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 4) {
                    names[count] = data[0].trim();
                    categories[count] = data[1].trim();
                    prices[count] = Double.parseDouble(data[2].trim());
                    stocks[count] = Integer.parseInt(data[3].trim());
                    count++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading inventory.");
            return;
        }

        boolean ordering = true;
        while (ordering) {
            // ===== FIXED CAFE MENU FORMATTING =====
            System.out.println("\n===================== CAFE MENU =====================");
            System.out.printf("%-5s %-22s %-10s %-10s%n", "No.", "Item Name", "Price", "Stock");
            System.out.println("-----------------------------------------------------");
            
            for (int i = 0; i < count; i++) {
                String stockDisplay = (stocks[i] > 0) ? String.valueOf(stocks[i]) : "Out";
                System.out.printf("%-5d %-22s %-10.2f %-10s%n", 
                                  (i + 1), names[i], prices[i], stockDisplay);
            }
            System.out.println("-----------------------------------------------------");

            // Display current cart if not empty
            if (cartCount > 0) {
                System.out.println("\n--- Current Order for Customer " + customerId + " ---");
                double total = 0;
                for (int i = 0; i < cartCount; i++) {
                    double subtotal = cartPrices[i] * cartQty[i];
                    total += subtotal;
                    System.out.printf("- %dx %-20s (%.2f)%n", cartQty[i], cartNames[i], subtotal);
                }
                System.out.printf("Total so far: %.2f%n", total);
            }

            System.out.println("\nOptions:");
            System.out.println("1. Add Item");
            System.out.println("2. Confirm Order");
            System.out.println("0. Cancel Order");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter item number: ");
                    int item;
                    try { 
                        item = Integer.parseInt(sc.nextLine().trim()); 
                    } catch (Exception e) { 
                        System.out.println("Invalid input.");
                        continue; 
                    }
                    
                    if (item < 1 || item > count) { 
                        System.out.println("Invalid item number."); 
                        continue; 
                    }
                    int index = item - 1;
                    
                    if (stocks[index] <= 0) { 
                        System.out.println("Item is out of stock."); 
                        continue; 
                    }

                    System.out.print("Enter quantity: ");
                    int qty;
                    try { 
                        qty = Integer.parseInt(sc.nextLine().trim()); 
                    } catch (Exception e) { 
                        System.out.println("Invalid input.");
                        continue; 
                    }
                    
                    if (qty <= 0 || qty > stocks[index]) { 
                        System.out.println("Invalid quantity."); 
                        continue; 
                    }

                    stocks[index] -= qty;
                    
                    boolean found = false;
                    for (int i = 0; i < cartCount; i++) {
                        if (cartNames[i].equalsIgnoreCase(names[index])) {
                            cartQty[i] += qty;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        cartNames[cartCount] = names[index];
                        cartPrices[cartCount] = prices[index];
                        cartQty[cartCount] = qty;
                        cartCount++;
                    }
                    System.out.println("Added " + qty + "x " + names[index]);
                    break;

                case "2":
                    if (cartCount == 0) { 
                        System.out.println("Cart is empty."); 
                        continue; 
                    }

                    // Save updated inventory to foodInventory.txt
                    try (PrintWriter writer = new PrintWriter(new FileWriter(FOOD_FILE))) {
                        for (int i = 0; i < count; i++) {
                            writer.println(names[i] + "," + categories[i] + "," + prices[i] + "," + stocks[i]);
                        }
                    } catch (IOException e) {
                        System.out.println("Error saving inventory.");
                        continue;
                    }

                    // Save order to orders/002.txt
                    new File(ORDER_FOLDER).mkdirs();
                    File orderFile = new File(ORDER_FOLDER, customerId + ".txt");
                    try (PrintWriter writer = new PrintWriter(new FileWriter(orderFile, true))) {
                        for (int i = 0; i < cartCount; i++) {
                            writer.println(cartNames[i] + "," + cartPrices[i] + "," + cartQty[i]);
                        }
                        System.out.println("Order placed successfully for Customer " + customerId + "!");
                        ordering = false;
                    } catch (IOException e) {
                        System.out.println("Error saving order.");
                    }
                    break;

                case "0":
                    System.out.println("Order cancelled.");
                    ordering = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ===== TRANSACTION HISTORY =====
    static void transactionHistory() {
        System.out.println("\n===== TRANSACTION HISTORY =====");
        File file = new File(PAYMENTS);
        if (!file.exists()) {
            System.out.println("No transactions yet.");
            return;
        }

        int count = 0;
        double total = 0;
        
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String[] data = reader.nextLine().split(",");
                if (data.length >= 3) {
                    System.out.println("Customer ID: " + data[0] + " | Type: " + data[1] + " | Amount: " + data[2]);
                    total += Double.parseDouble(data[2]);
                    count++;
                }
            }
            if (count == 0) System.out.println("No transactions found.");
            else System.out.println("\nGrand Total: " + total);
        } catch (Exception e) {
            System.out.println("Error reading transactions.");
        }
    }
    
    // --- HELPERS ---
    static String getCustomerIdFromKey(String key) {
        File folder = new File("customers");
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    try (Scanner fs = new Scanner(f)) {
                        String id = fs.nextLine().trim();
                        String user = fs.nextLine().trim();
                        if (id.equalsIgnoreCase(key) || user.equalsIgnoreCase(key)) {
                            return id;
                        }
                    } catch (Exception e) { }
                }
            }
        }
        return null;
    }

    static String[] readComputer(File file) {
        try (Scanner reader = new Scanner(file)) {
            if (reader.hasNextLine()) {
                String[] data = reader.nextLine().split(",");
                if (data.length >= 3) return data;
            }
        } catch (Exception e) { }
        return null;
    }
}
