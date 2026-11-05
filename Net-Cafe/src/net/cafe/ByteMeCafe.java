package net.cafe;
import java.util.*;
import java.io.*;

public class ByteMeCafe {
    static Scanner sc = new Scanner(System.in);
    static final String FOLDER = "customers";   // folder that only holds the customer .txt files

    // ---------------- START / MAIN MENU ----------------
    public static void main(String[] args) {
        new File(FOLDER).mkdirs();   // create the folder if it doesn't exist

        boolean running = true;
        while (running) {
            // Display: 1. Log In  2. Register  3. Quit
            System.out.println("Welcome To Byte Me Cafe!\n");
            System.out.println("==== Byte Me Cafe ====");
            System.out.println("1. Log In");
            System.out.println("2. Register");
            System.out.println("3. Quit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": logIn(); break;
                case "2": register(); break;
                case "3":
                    System.out.println("Thank You For Visiting!");
                    running = false;   // End
                    break;
                default: System.out.println("Invalid choice. Try again.\n");
            }
        }
    }

    // ---------------- LOG IN BRANCH ----------------
    static void logIn() {
        System.out.println("\n==== Byte Me Cafe Login ====");

        // Enter ID or Username
        System.out.print("Enter ID or Username: ");
        String key = sc.nextLine().trim();

        // Enter Password
        System.out.print("Enter Password: ");
        String password = sc.nextLine().trim();

        // Check Credentials -> Display "Login Success..." -> Open Role Based Interface
        if (key.equals("admin") && password.equals("admin123")) {
            System.out.println("\nLogin Success...");
            System.out.println("Welcome Back, Admin");
            Admin.adminMenu();
            return;
        }
        if (key.equals("staff") && password.equals("staff123")) {
            System.out.println("\nLogin Success...");
            Staff.staffMenu();
            return;
        }

        // Customer accounts (by ID like 001, or by username)
        // data = {id, name, username, email, password}
        String[] data = findCustomer(key);
        if (data != null && data[4].equals(password)) {
            System.out.println("\nLogin Success...");
            System.out.println("Welcome, " + data[1]);
            
            Customer.currentId = data[0];
            Customer.currentName = data[1];

            Customer.customerMenu();
        } else {
            System.out.println("Invalid ID or password\n");
        }
    }

    // ---------------- REGISTER BRANCH ----------------
    static void register() {
        System.out.println("\n==== Register ====");

        boolean done = false;
        while (!done) {
            // Enter Name, Username, Email, Password and Confirm Password
            // Each input is checked right away: it asks again until it is not empty
            String name = "";
            while (name.isEmpty()) {
                System.out.print("Enter Name: ");
                name = sc.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("All Fields Must Be Filled\n");
                }
            }

            String username = "";
            while (username.isEmpty()) {
                System.out.print("Enter Username: ");
                username = sc.nextLine().trim();
                if (username.isEmpty()) {
                    System.out.println("All Fields Must Be Filled\n");
                }
            }

            String email = "";
            while (email.isEmpty()) {
                System.out.print("Enter Email: ");
                email = sc.nextLine().trim();
                if (email.isEmpty()) {
                    System.out.println("All Fields Must Be Filled");
                } else if (!email.endsWith("@email.com") || email.equals("@email.com")) {
                    System.out.println("Email must end with @email.com (example: juan@email.com)\n");
                    email = "";   // ask for the email again
                }
            }

            String password = "";
            while (password.isEmpty()) {
                System.out.print("Enter Password: ");
                password = sc.nextLine().trim();
                if (password.isEmpty()) {
                    System.out.println("All Fields Must Be Filled\n");
                }
            }

            String confirm = "";
            while (confirm.isEmpty()) {
                System.out.print("Confirm Password: ");
                confirm = sc.nextLine().trim();
                if (confirm.isEmpty()) {
                    System.out.println("All Fields Must Be Filled\n");
                } else if (!confirm.equals(password)) {
                    System.out.println("Password Must Match\n");
                    confirm = "";   // back to Enter details
                }
            }

            // Are details valid and unused?
            if (isTaken(username, email)) {
                System.out.println("\nAccount already exist. Please Create Another One\n");
                continue;   // back to Enter details
            }

            // Generate ID and Save Account
            String id = nextCustomerId();
            if (saveCustomer(id, name, username, email, password)) {
                // Display "Registration Success"
                System.out.println("\nRegistration Success");
                System.out.println("Your Customer ID is: " + id);
                done = true;
            } else {
                return;   // save failed, go back to main menu
            }
        }

        // Connector "Log In" -> goes to Enter ID or Username (Log In)
        logIn();
    }

    static void logOut() {
        System.out.println("\n=== Logging Out ===");
        System.out.println("You have successfully logged out!");
        System.out.println("Returning to Main Menu...\n");
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

    // Each customer gets their own file: customers/001.txt
    // File format:
    // 001
    // Name: ...
    // Username: ...
    // Email: ...
    // Password: ...
    static boolean saveCustomer(String id, String name, String username, String email, String password) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FOLDER + "/" + id + ".txt"))) {
            pw.println(id);
            pw.println("Name: " + name);
            pw.println("Username: " + username);
            pw.println("Email: " + email);
            pw.println("Password: " + password);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while saving the customer.");
            return false;
        }
    }

    // Reads one customer file. Returns {id, name, username, email, password} or null.
    static String[] readCustomer(File f) {
        try (Scanner fr = new Scanner(f)) {
            String id = fr.nextLine().trim();
            String name = fr.nextLine().replace("Name: ", "").trim();
            String user = fr.nextLine().replace("Username: ", "").trim();
            String email = fr.nextLine().replace("Email: ", "").trim();
            String pass = fr.nextLine().replace("Password: ", "").trim();
            return new String[]{id, name, user, email, pass};
        } catch (Exception ignored) {
            return null;
        }
    }

    // Finds a customer by ID or username. Returns {id, name, username, email, password} or null.
    static String[] findCustomer(String key) {
        File[] files = new File(FOLDER).listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) return null;

        for (File f : files) {
            String[] c = readCustomer(f);
            if (c != null && (c[0].equals(key) || c[2].equalsIgnoreCase(key))) {
                return c;
            }
        }
        return null;
    }

    // True if the username or email is already used (or the username is admin/staff)
    static boolean isTaken(String username, String email) {
        if (username.equalsIgnoreCase("admin") || username.equalsIgnoreCase("staff")) {
            return true;
        }

        File[] files = new File(FOLDER).listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null) return false;

        for (File f : files) {
            String[] c = readCustomer(f);
            // c[0] = id: a username must not equal another customer's ID (login accepts either)
            if (c != null && (c[0].equals(username) || c[2].equalsIgnoreCase(username) || c[3].equalsIgnoreCase(email))) {
                return true;
            }
        }
        return false;
    }
}
