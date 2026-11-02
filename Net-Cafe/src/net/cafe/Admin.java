/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.cafe;
import java.util.*;
import java.io.*;

public class Admin {
    static Scanner sc = new Scanner(System.in);
    
    static final String SESSION_FOLDER = "sessions";
    static final String ORDER_FOLDER   = "orders";
    static final String TRANSACTIONS   = "transactions.txt";
    public static void main(String[] args) {
    }
    
    // Admin Menu
    static void adminMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Manage Computers");
            System.out.println("2. Manage Users");
            System.out.println("3. Manage Inventory");
            System.out.println("4. View Sessions");
            System.out.println("5. Sales Report");
            System.out.println("0. Logout");
            System.out.println("---");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            
            switch (choice){
                case 1:
                    System.out.println("Opening Computer Management... \n");
                    CompManage.compManage();
                    break;
                case 2:
                    System.out.println("Opening Users Management...");
                    UserManagement.accManage();
                    break;
                case 3:
                    System.out.println("Opening Inventory Management... \n");
                    Inventory.InventoryMenu();
                    break;
                case 4: System.out.println("Opening View Sessions... \n");
                    viewSessions(); break;
                case 5: System.out.println("Opening Sales Report... \n");
                    salesReport(); break;
                case 0:
                    System.out.println("Opening Log Out Menu... \n");
                    NetCafe.logOut();
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");   
            }
        }
    }
    
    static void viewSessions() {
        System.out.println("\n===============================");
        System.out.println("      ACTIVE CUSTOMER SESSIONS ");
        System.out.println("===============================");
        System.out.printf("%-10s %-15s %-15s%n", "Customer ID", "Computer ID", "Rate/Hour");
        System.out.println("-----------------------------------------------");

        File folder = new File(SESSION_FOLDER);
        File[] files = folder.listFiles();
        int count = 0;

        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    String customerId = f.getName().replace(".txt", "");
                    try (Scanner fs = new Scanner(f)) {
                        if (fs.hasNextLine()) {
                            String[] data = fs.nextLine().split(",");
                            if (data.length >= 2) {
                                System.out.printf("%-10s %-15s %-15s%n", 
                                    customerId, data[0].trim(), data[1].trim());
                                count++;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred while reading a session file.");
                    }
                }
            }
        }

        if (count == 0) {
            System.out.println("No active sessions right now.");
        }
        System.out.println("-----------------------------------------------");
        System.out.println("Total Active Sessions: " + count);
        System.out.println("\nPress Enter to return...");
        sc.nextLine();
    }
    
    static void salesReport() {
        System.out.println("\n===============================");
        System.out.println("          SALES REPORT         ");
        System.out.println("===============================");

        double totalSales = 0;
        int transactionCount = 0;

        // 1. Read Session Payments (transactions.txt)
        File transFile = new File(TRANSACTIONS);
        if (transFile.exists()) {
            System.out.println("--- Computer Session Payments ---");
            try (Scanner fs = new Scanner(transFile)) {
                while (fs.hasNextLine()) {
                    String line = fs.nextLine().trim();
                    if (line.isEmpty()) continue;
                    
                    String[] p = line.split(",");
                    if (p.length >= 3) {
                        // Format: customer,computerId,amount
                        System.out.println("Customer: " + p[0] + " | Computer: " + p[1] + " | Amount: " + p[2]);
                        try {
                            totalSales += Double.parseDouble(p[2].trim());
                            transactionCount++;
                        } catch (NumberFormatException e) { }
                    }
                }
            } catch (Exception e) {
                System.out.println("An error occurred while reading transactions.");
            }
        } else {
            System.out.println("No computer session payments found.");
        }

        // 2. Read Unpaid Food Orders (orders folder)
        // Note: Since customers pay for food when they end their session,
        // we display the total value of food currently ordered.
        double foodSales = 0;
        File orderFolder = new File(ORDER_FOLDER);
        File[] orderFiles = orderFolder.listFiles();
        
        if (orderFiles != null && orderFiles.length > 0) {
            System.out.println("\n--- Food & Drinks Ordered ---");
            for (File f : orderFiles) {
                if (f.isFile() && f.getName().endsWith(".txt")) {
                    String customerId = f.getName().replace(".txt", "");
                    double customerFoodTotal = 0;
                    
                    try (Scanner fs = new Scanner(f)) {
                        while (fs.hasNextLine()) {
                            String line = fs.nextLine().trim();
                            if (line.isEmpty()) continue;
                            
                            String[] p = line.split(",");
                            if (p.length >= 3) {
                                // Format: itemName,price,quantity
                                try {
                                    double price = Double.parseDouble(p[1].trim());
                                    int qty = Integer.parseInt(p[2].trim());
                                    customerFoodTotal += (price * qty);
                                } catch (NumberFormatException e) { }
                            }
                        }
                        if (customerFoodTotal > 0) {
                            System.out.println("Customer ID: " + customerId + " | Food Total: " + String.format("%.2f", customerFoodTotal));
                            foodSales += customerFoodTotal;
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred while reading order file for " + customerId);
                    }
                }
            }
        }

        // 3. Print Totals
        System.out.println("\n===============================");
        System.out.println("           TOTALS              ");
        System.out.println("===============================");
        System.out.println("Total Session Sales:  " + String.format("%.2f", totalSales));
        System.out.println("Total Food Sales:     " + String.format("%.2f", foodSales) + " (Active/Pending)");
        System.out.println("---------------------------------");
        System.out.println("GRAND TOTAL:          " + String.format("%.2f", (totalSales + foodSales)));
        System.out.println("===============================");

        System.out.println("\nPress Enter to return...");
        sc.nextLine();
    }
}
    