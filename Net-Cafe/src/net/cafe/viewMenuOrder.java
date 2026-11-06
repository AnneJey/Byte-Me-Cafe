package net.cafe;
import java.util.*;
import java.io.*;

public class viewMenuOrder {
    static Scanner sc = ByteMeCafe.sc;
    static final String MENU_FOLDER = "menu";      // one .txt file per menu item
    static final String ORDER_FOLDER = "orders";   // one .txt file per saved order

    // Menu data loaded from the files in the menu folder
    static String[] itemId = new String[100];
    static String[] itemName = new String[100];
    static int[] itemPrice = new int[100];
    static int[] itemStock = new int[100];
    static int itemCount = 0;

    // ---------------- START: MENU AND ORDER (CUSTOMER) ----------------
    static void orderMenu(String customerId, String customerName) {
        new File(MENU_FOLDER).mkdirs();
        new File(ORDER_FOLDER).mkdirs();

        loadMenu();
        if (itemCount == 0) {
            createDefaultMenu();   // first run: create a starting menu
            loadMenu();
        }

        // Cart: cartItem = position in the menu arrays, cartQty = quantity
        int[] cartItem = new int[100];
        int[] cartQty = new int[100];
        int cartCount = 0;

        // Display Menu
        displayMenu();

        boolean adding = true;
        while (adding) {
            // Select Item and Quantity
            System.out.print("\nEnter item number (0 to cancel): ");
            String itemInput = sc.nextLine().trim();
            if (itemInput.equals("0")) {
                System.out.println("Order cancelled");
                return;
            }

            int index = findItem(itemInput);
            if (index == -1) {
                System.out.println("Invalid item number.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int qty;
            try {
                qty = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                qty = 0;
            }
            if (qty <= 0) {
                System.out.println("Invalid quantity.");
                continue;
            }

            // How many of this item are already in the cart?
            int cartPos = -1;
            int inCart = 0;
            for (int i = 0; i < cartCount; i++) {
                if (cartItem[i] == index) {
                    cartPos = i;
                    inCart = cartQty[i];
                }
            }

            // Item In Stock?
            if (itemStock[index] < inCart + qty) {
                // No -> Display Out Of Stock Message -> back to Select Item and Quantity
                System.out.println("Out of stock: " + itemName[index]
                        + " (available: " + (itemStock[index] - inCart) + ")");
                continue;
            }

            // Yes -> Add Item to Cart
            if (cartPos == -1) {
                cartItem[cartCount] = index;
                cartQty[cartCount] = qty;
                cartCount++;
            } else {
                cartQty[cartPos] += qty;
            }
            System.out.println("Added to cart: " + itemName[index] + " x" + qty);

            // Add More Item?
            String more = "";
            while (!more.equalsIgnoreCase("Y") && !more.equalsIgnoreCase("N")) {
                System.out.print("Add more item? (Y/N): ");
                more = sc.nextLine().trim();
            }
            if (more.equalsIgnoreCase("N")) {
                adding = false;   // No -> go to receipt
            }
        }

        // Display Receipt
        displayReceipt(customerId, customerName, cartItem, cartQty, cartCount);

        // Confirm Order?
        String confirm = "";
        while (!confirm.equalsIgnoreCase("Y") && !confirm.equalsIgnoreCase("N")) {
            System.out.print("Confirm order? (Y/N): ");
            confirm = sc.nextLine().trim();
        }

        if (confirm.equalsIgnoreCase("Y")) {
            // Save Transaction and Reduce Stock
            if (saveOrder(customerId, customerName, cartItem, cartQty, cartCount)) {
                for (int i = 0; i < cartCount; i++) {
                    itemStock[cartItem[i]] -= cartQty[i];
                    saveItem(cartItem[i]);
                }
                System.out.println("\nOrder Save Successful");
            }
        } else {
            System.out.println("\nOrder cancelled");
        }
        // End -> back to Customer Menu
    }

    // ---------------- DISPLAY ----------------
    static void displayMenu() {
        System.out.println("\n========== MENU ==========");
        System.out.println(String.format("%-5s %-20s %-8s %s", "No.", "Item", "Price", "Stock"));
        for (int i = 0; i < itemCount; i++) {
            System.out.println(String.format("%-5s %-20s PHP %-4d %d",
                    itemId[i], itemName[i], itemPrice[i], itemStock[i]));
        }
        System.out.println("==========================");
    }

    static void displayReceipt(String customerId, String customerName, int[] cartItem, int[] cartQty, int cartCount) {
        int total = 0;
        System.out.println("\n========== RECEIPT ==========");
        System.out.println("Customer: " + customerName + " (ID " + customerId + ")");
        System.out.println("-----------------------------");
        for (int i = 0; i < cartCount; i++) {
            int subtotal = itemPrice[cartItem[i]] * cartQty[i];
            total += subtotal;
            System.out.println(String.format("%-18s x%-3d PHP %d",
                    itemName[cartItem[i]], cartQty[i], subtotal));
        }
        System.out.println("-----------------------------");
        System.out.println("TOTAL: PHP " + total);
        System.out.println("=============================");
    }

    // ---------------- ITEM HELPERS ----------------
    // Accepts "1" or "001". Returns the position in the menu arrays, or -1 if not found.
    static int findItem(String input) {
        try {
            String id = String.format("%03d", Integer.parseInt(input));
            for (int i = 0; i < itemCount; i++) {
                if (itemId[i].equals(id)) return i;
            }
        } catch (NumberFormatException ignored) { }
        return -1;
    }

    // ---------------- FILE HELPERS ----------------
    // Each menu item has its own file: menu/001.txt
    // File format:
    // 001
    // Name: ...
    // Price: ...
    // Stock: ...
    static void loadMenu() {
        itemCount = 0;
        for (int n = 1; n < 100; n++) {
            String id = String.format("%03d", n);
            File f = new File(MENU_FOLDER + "/" + id + ".txt");
            try (Scanner fr = new Scanner(f)) {
                fr.nextLine();   // line 1 = ID
                String name = fr.nextLine().replace("Name: ", "").trim();
                int price = Integer.parseInt(fr.nextLine().replace("Price: ", "").trim());
                int stock = Integer.parseInt(fr.nextLine().replace("Stock: ", "").trim());

                itemId[itemCount] = id;
                itemName[itemCount] = name;
                itemPrice[itemCount] = price;
                itemStock[itemCount] = stock;
                itemCount++;
            } catch (Exception ignored) { }
        }
    }

    static void saveItem(int i) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MENU_FOLDER + "/" + itemId[i] + ".txt"))) {
            pw.println(itemId[i]);
            pw.println("Name: " + itemName[i]);
            pw.println("Price: " + itemPrice[i]);
            pw.println("Stock: " + itemStock[i]);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the menu item.");
        }
    }

    // Starting menu, created only when the menu folder has no items
    static void createDefaultMenu() {
        String[] names = {"Coke", "Mineral Water", "Chips", "Instant Noodles", "Sandwich", "Coffee"};
        int[] prices = {25, 15, 20, 35, 45, 30};
        int[] stocks = {50, 50, 30, 20, 15, 40};

        for (int i = 0; i < names.length; i++) {
            itemId[i] = String.format("%03d", i + 1);
            itemName[i] = names[i];
            itemPrice[i] = prices[i];
            itemStock[i] = stocks[i];
            saveItem(i);
        }
    }

    // Generates 001, 002, 003, ... for orders
    static String nextOrderId() {
        File[] files = new File(ORDER_FOLDER).listFiles((d, name) -> name.endsWith(".txt"));
        int max = 0;
        if (files != null) {
            for (File f : files) {
                try {
                    int n = Integer.parseInt(f.getName().replace(".txt", ""));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) { }
            }
        }
        return String.format("%03d", max + 1);
    }

    // Each order has its own file: orders/001.txt
    // File format:
    // 001
    // Date: 2026-11-05
    // Customer ID: ...
    // Customer Name: ...
    // Item: Coke x2 = 50
    // Total: ...
    static boolean saveOrder(String customerId, String customerName, int[] cartItem, int[] cartQty, int cartCount) {
        String orderId = nextOrderId();
        int total = 0;
        try (PrintWriter pw = new PrintWriter(new FileWriter(ORDER_FOLDER + "/" + orderId + ".txt"))) {
            pw.println(orderId);
            pw.println("Date: " + java.time.LocalDate.now());   // yyyy-MM-dd, used by the Sales Report
            pw.println("Customer ID: " + customerId);
            pw.println("Customer Name: " + customerName);
            for (int i = 0; i < cartCount; i++) {
                int subtotal = itemPrice[cartItem[i]] * cartQty[i];
                total += subtotal;
                pw.println("Item: " + itemName[cartItem[i]] + " x" + cartQty[i] + " = " + subtotal);
            }
            pw.println("Total: " + total);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while saving the order.");
            return false;
        }
    }
}
