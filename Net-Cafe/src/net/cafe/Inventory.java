package net.cafe;
import java.util.*;
import java.io.*;

public class Inventory {
    static final String FILE_NAME = "foodInventory.txt";
    static final int MAX = 100;
    static Scanner sc = NetCafe.sc;

    static String[] names = new String[MAX];
    static String[] categories = new String[MAX];
    static double[] prices = new double[MAX];
    static int[] stocks = new int[MAX];
    static int count = 0;

    public static void main(String[] args) {
    }

    // isAdmin = true  -> Admin: View, Add, Remove, Set Price, Update Stock
    // isAdmin = false -> Staff: View, Add, Set Price, Update Stock (no Remove)
    static void InventoryMenu(boolean isAdmin) {
        loadFromFile();

        boolean running = true;
        while (running) {
            System.out.println("\n==================================");
            System.out.println("   Food & Drinks Inventory Menu   ");
            System.out.println("==================================");
            System.out.println("1. View All Items");
            System.out.println("2. Add Item");
            if (isAdmin) {
                System.out.println("3. Remove Item");
            }
            System.out.println("4. Set Item Price");
            System.out.println("5. Update Stock Quantity");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            // Staff cannot remove items
            if (!isAdmin && choice.equals("3")) {
                choice = "";
            }

            switch (choice) {
                case "1": displayAllItems(); break;
                case "2": addItem(); break;
                case "3": removeItem(); break;
                case "4": setPrice(); break;
                case "5": updateStock(); break;
                case "0":
                    saveToFile();
                    System.out.println("\nReturning to menu...\n");
                    running = false;
                    break;
                default: System.out.println("\nInvalid choice.");
            }
        }
    }

    // ===== VIEW =====
    static void displayAllItems() {
        System.out.println("\n====================");
        System.out.println("   Inventory List   ");
        System.out.println("====================");
        System.out.printf("%-5s %-20s %-10s %-10s %-10s%n",
                          "No.", "Item Name", "Category", "Price", "Stock");
        System.out.println("------------------------------------------------------------");

        if (count == 0) {
            System.out.println("No items in inventory.");
        } else {
            for (int i = 0; i < count; i++) {
                System.out.printf("%-5d %-20s %-10s %-10.2f %-10d%n",
                                  (i + 1), names[i], categories[i], prices[i], stocks[i]);
            }
        }
        System.out.println();
    }

    // ===== ADD =====
    static void addItem() {
        if (count >= MAX) {
            System.out.println("Inventory is full.");
            return;
        }

        System.out.println("\n=== Add New Item ===");
        System.out.print("Enter item name: ");
        String name = sc.nextLine();
        System.out.print("Enter category (Food/Drink): ");
        String category = sc.nextLine();
        System.out.print("Enter price: ");
        double price = sc.nextDouble();
        System.out.print("Enter stock quantity: ");
        int stock = sc.nextInt();
        sc.nextLine();

        names[count] = name;
        categories[count] = category;
        prices[count] = price;
        stocks[count] = stock;
        count++;

        saveToFile();
        System.out.println("Item added: " + name);
    }

    // ===== REMOVE (ADMIN ONLY) =====
    static void removeItem() {
        displayAllItems();
        if (count == 0) return;

        System.out.print("Enter item number to remove: ");
        int target = sc.nextInt() - 1;
        sc.nextLine();

        if (target < 0 || target >= count) {
            System.out.println("Invalid item number.");
            return;
        }

        String removed = names[target];

        for (int i = target; i < count - 1; i++) {
            names[i] = names[i + 1];
            categories[i] = categories[i + 1];
            prices[i] = prices[i + 1];
            stocks[i] = stocks[i + 1];
        }
        count--;

        saveToFile();
        System.out.println("Item removed: " + removed);
    }

    // ===== SET PRICE =====
    static void setPrice() {
        displayAllItems();
        if (count == 0) return;

        System.out.print("Enter item number to update price: ");
        int target = sc.nextInt() - 1;
        sc.nextLine();

        if (target < 0 || target >= count) {
            System.out.println("Invalid item number.");
            return;
        }

        System.out.print("Enter new price for " + names[target] + ": ");
        prices[target] = sc.nextDouble();
        sc.nextLine();

        saveToFile();
        System.out.println("Price updated.");
    }

    // ===== UPDATE STOCK =====
    static void updateStock() {
        displayAllItems();
        if (count == 0) return;

        System.out.print("Enter item number to update stock: ");
        int target = sc.nextInt() - 1;
        sc.nextLine();

        if (target < 0 || target >= count) {
            System.out.println("Invalid item number.");
            return;
        }

        System.out.println("1. Add Stock");
        System.out.println("2. Set Stock");
        System.out.print("Enter choice: ");
        int mode = sc.nextInt();
        System.out.print("Enter quantity: ");
        int qty = sc.nextInt();
        sc.nextLine();

        switch (mode) {
            case 1: stocks[target] += qty; break;
            case 2: stocks[target] = qty; break;
            default: System.out.println("Invalid choice."); return;
        }

        saveToFile();
        System.out.println("Stock updated. New stock: " + stocks[target]);
    }

    // ===== LOAD from file into arrays =====
    static void loadFromFile() {
        count = 0;
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null && count < MAX) {
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
            System.out.println("Error reading file.");
        }
    }

    // ===== SAVE arrays back to file =====
    static void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < count; i++) {
                bw.write(names[i] + "," + categories[i] + "," + prices[i] + "," + stocks[i]);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }
}
