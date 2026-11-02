package net.cafe;
import java.util.*;
import java.io.*;

public class NetCafe {
    static Scanner sc = new Scanner(System.in);
    static final String FOLDER = "customers";

    public static void main(String[] args) {
        new File(FOLDER).mkdirs();   // create the folder if it doesn't exist

        boolean running = true;
        while (running) {
            System.out.println("==== Net Cafe ====");
            System.out.println("1. Log In");
            System.out.println("2. Register");
            System.out.println("3. Quit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": logIn(); break;
                case "2": register(); break;
                case "3":
                    System.out.println("Thank you for visiting");
                    running = false;
                    break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    static void logIn() {
        System.out.println("\n==== Net Cafe Login ====");

        System.out.print("Enter ID or Username: ");
        String key = sc.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = sc.nextLine().trim();

        // Built-in accounts
        if (key.equals("admin") && password.equals("admin123")) {
            System.out.println("\nLogin Success!");
            System.out.println("Welcome Back, Admin");
            Admin.adminMenu();
            return;
        }
        if (key.equals("staff") && password.equals("staff123")) {
            System.out.println("\nLogin Success!");
            Staff.staffMenu();
            return;
        }

        // Customer accounts (by ID like 001, or by username)
        String[] data = findCustomer(key);
        if (data != null && data[2].equals(password)) {
            System.out.println("\nLogin Success!");
            System.out.println("Welcome, " + data[1]);
            
            // Always use data[0] (the real ID) for the file system
            Customer.currentId = data[0];   
            Customer.currentName = data[1]; 
            
            Customer.customerMenu();
        } else {
            System.out.println("Invalid ID or password");
        }
    }
    
    static void register() {
        System.out.println("\n==== Register ====");

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        if (findCustomer(username) != null || username.equalsIgnoreCase("admin")
                || username.equalsIgnoreCase("staff")) {
            System.out.println("Username already taken.");
            return;
        }

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        System.out.print("Confirm password: ");
        String confirm = sc.nextLine().trim();

        if (password.isEmpty() || !password.equals(confirm)) {
            System.out.println("Passwords do not match (or are empty). Registration cancelled.");
            return;
        }

        String id = nextCustomerId();
        if (saveCustomer(id, username, password)) {
            System.out.println("\nRegistration successful!");
            System.out.println("Your Customer ID is: " + id);
            System.out.println("Please log in again.");
            logIn();
        }
    }

    static void logOut() {
        System.out.println("\n=== Logging Out ===");
        System.out.println("You have successfully logged out!");
        System.out.println("Returning to Main Menu...");
    }
    
    // ---------------- FILE HELPERS ----------------
    // Generates 001, 002, 003, ...
    static String nextCustomerId() {
        File[] files = new File(FOLDER).listFiles((d, name) -> name.endsWith(".txt"));
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

    // File format: line 1 = ID, line 2 = username, line 3 = password
    static boolean saveCustomer(String id, String username, String password) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FOLDER + "/" + id + ".txt"))) {
            pw.println(id);
            pw.println(username);
            pw.println(password);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while saving the customer.");
            return false;
        }
    }

    // Finds a customer by ID or username. Returns {id, username, password} or null.
    static String[] findCustomer(String key) {
        File[] files = new File(FOLDER).listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) return null;

        for (File f : files) {
            try (Scanner fr = new Scanner(f)) {
                String id = fr.nextLine().trim();
                String user = fr.nextLine().trim();
                String pass = fr.nextLine().trim();
                
                if (id.equals(key) || user.equalsIgnoreCase(key)) {
                    return new String[]{id, user, pass};
                }
            } catch (Exception ignored) { }
        }
        return null;
    }
}
