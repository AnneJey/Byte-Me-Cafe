package net.cafe;

import java.util.*;
import java.io.*;

public class CompManage {
    static Scanner sc = new Scanner(System.in);
    static final String FOLDER = "computers";

    public static void main(String[] args) {
    }

    // ===== COMPUTER MANAGEMENT =====
    static void compManage() {
        while (true) {
            System.out.println("\n=== Computer Management ===");
            System.out.println("1. Search / View All Computers");
            System.out.println("2. Add Computer");
            System.out.println("3. Remove Computer");
            System.out.println("4. Set Hourly Rate");
            System.out.println("5. Set Status");
            System.out.println("0. Back");
            
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "1":
                    System.out.println("\nOpening View All Computers...\n");
                    searchViewAll();
                    break;
                case "2":
                    System.out.println("\nOpening Add Computer...");
                    addComp();
                    break;
                case "3":
                    System.out.println("\nOpening Remove Computer...\n");
                    removeComp();
                    break;
                case "4":
                    System.out.println("\nOpening Set Hourly Rate Computer...\n");
                    hourlyRate();
                    break;
                case "5":
                    System.out.println("\nOpening Set Status Computer...\n");
                    status();
                    break;
                case "0":
                    // Just return to the Admin Menu instead of calling it (prevents stacking)
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    } 

    // ===== SEARCH / VIEW ALL MENU =====
    static void searchViewAll() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("   Search / View All Computers");
            System.out.println("=================================");
            System.out.println("1. View All Computers");
            System.out.println("2. Search Computer by ID");
            System.out.println("0. Back");
            System.out.println("---");
            
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine().trim();
                
            switch (choice) {
                case "1": viewAll(); break;
                case "2": searchComp(); break;
                case "0": return;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    // ===== DISPLAY ALL COMPUTERS =====
    static void viewAll() {
        System.out.println("\n==========================================");
        System.out.println("            View All Computers");
        System.out.println("==========================================");
        System.out.printf("%-10s %-15s %s%n", "ID", "Rate/Hour", "Status");
        System.out.println("------------------------------------------");

        File folder = new File(FOLDER);
        if (!folder.exists()) folder.mkdirs();
        File[] files = folder.listFiles((d, n) -> n.toLowerCase().endsWith(".txt"));
        if (files == null) files = new File[0];
        Arrays.sort(files);

        int count = 0;
        for (File f : files) {
            String[] data = readData(f);
            if (data != null) {
                System.out.printf("%-10s %-15s %s%n",
                        data[0].trim(), data[1].trim(), data[2].trim());
                count++;
            }
        }

        if (count == 0) {
            System.out.println("No computers found.");
        }

        System.out.println("==========================================");
        System.out.println("0. Back");
        System.out.println("---");

        while (true) {
            System.out.print("Enter your choice: ");
            if (sc.nextLine().trim().equals("0")) {
                return;
            }
            System.out.println("Invalid choice! Please enter 0.");
        }
    }
    
    // ===== SEARCH COMPUTER BY ID =====
    static void searchComp() {
        while (true) {
            System.out.println("\n===========================");
            System.out.println("   Search Computer by ID   ");
            System.out.println("===========================");
            System.out.print("Enter Computer ID (0 to go back): ");
            String searchID = sc.nextLine().trim();

            if (searchID.equals("0")) return;

            if (searchID.isEmpty()) {
                System.out.println("Computer ID cannot be empty!");
                continue;
            }

            File file = new File(FOLDER + "/" + searchID + ".txt");
            String[] data = (searchID.matches("[A-Za-z0-9_-]+") && file.exists())
                    ? readData(file) : null;

            if (data != null) {
                System.out.println("\nComputer Found!\n");
                System.out.println("===========================");
                System.out.println("ID: " + data[0].trim());
                System.out.println("Hourly Rate: " + data[1].trim());
                System.out.println("Status: " + data[2].trim());
                System.out.println("===========================");
            } else {
                System.out.println("\nComputer ID not found!");
            }
        }
    }
                
    // ===== ADD COMPUTER =====
    static void addComp() {
        while (true) {
            System.out.println("\n========================");
            System.out.println("   Add Computer");
            System.out.println("========================");
            
            System.out.print("Enter Computer ID (0 to cancel): ");
            String id = sc.nextLine().trim();

            if (id.equals("0")) return;

            if (id.isEmpty() || !id.matches("[A-Za-z0-9_-]+")) {
                System.out.println("ID can only contain letters, numbers, - and _ (no spaces or commas).");
                continue;
            }
            
            if (new File(FOLDER + "/" + id + ".txt").exists()) {
                System.out.println("Computer ID already exists.");
                continue;
            }

            double rate = 0;
            while (true) {
                System.out.print("Enter Hourly Rate: ");
                String input = sc.nextLine().trim();
                try {
                    rate = Double.parseDouble(input);
                    if (rate > 0 && rate < Double.MAX_VALUE) break;
                    System.out.println("Rate must be a positive number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid rate. Please enter a number.");
                }
            }

            if (writeData(id, rate, "Available")) {
                System.out.println("\nComputer added successfully.");
            }

            System.out.println("\n1. Add Another Computer");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
            if (!choice.equals("1")) {
                return;
            }
        }
    }
    
    // ===== REMOVE COMPUTER =====
    static void removeComp(){
        while (true) {
            System.out.println("\n========================");
            System.out.println("   Remove Computer");
            System.out.println("========================");
            
            System.out.print("Enter Computer ID (0 to cancel): ");
            String id = sc.nextLine().trim();

            if (id.equals("0")) return;

            File file = new File(FOLDER + "/" + id + ".txt");
            if (id.isEmpty() || !id.matches("[A-Za-z0-9_-]+") || !file.exists()) {
                System.out.println("Computer ID not found!");
                continue;
            }

            String[] data = readData(file);
            if (data == null) {
                System.out.println("Could not read computer record.");
                continue;
            }

            String status = data[2].trim();

            System.out.println("\nComputer Found!");
            System.out.println("---------------------------");
            System.out.println("ID: " + data[0].trim());
            System.out.println("Hourly Rate: " + data[1].trim());
            System.out.println("Status: " + status);
            System.out.println("---------------------------");

            if (status.equalsIgnoreCase("Occupied")) {
                System.out.println("This computer is currently Occupied and cannot be removed.");
                System.out.println("Change its status first, then try again.");
                continue;
            }

            boolean confirmed = false;
            while (true) {
                System.out.print("Are you sure you want to remove this computer? (Y/N): ");
                String answer = sc.nextLine().trim();
                if (answer.equalsIgnoreCase("Y")) { confirmed = true; break; }
                if (answer.equalsIgnoreCase("N")) break;
                System.out.println("Please enter Y or N.");
            }

            if (confirmed) {
                if (file.delete()) {
                    System.out.println("Computer removed successfully.");
                } else {
                    System.out.println("Error removing computer.");
                }
            } else {
                System.out.println("Removal cancelled.");
            }

            System.out.println("\n1. Remove Another Computer");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
            if (!choice.equals("1")) {
                return;
            }
        }
    }
    
    // ===== HOURLY RATE =====
    static void hourlyRate(){
       while (true) {
            System.out.println("\n========================");
            System.out.println("   Set Hourly Rate");
            System.out.println("========================");
            
            System.out.print("Enter Computer ID (0 to cancel): ");
            String id = sc.nextLine().trim();

            if (id.equals("0")) return;

            File file = new File(FOLDER + "/" + id + ".txt");
            if (id.isEmpty() || !id.matches("[A-Za-z0-9_-]+") || !file.exists()) {
                System.out.println("Computer ID not found!");
                continue;
            }

            String[] data = readData(file);
            if (data == null) {
                System.out.println("Could not read computer record.");
                continue;
            }

            System.out.println("\nCurrent Hourly Rate: " + data[1].trim());
            System.out.println("Status: " + data[2].trim());

            double newRate = 0;
            while (true) {
                System.out.print("Enter New Hourly Rate: ");
                String input = sc.nextLine().trim();
                try {
                    newRate = Double.parseDouble(input);
                    if (newRate > 0 && newRate < Double.MAX_VALUE) break;
                    System.out.println("Rate must be a positive number.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid rate. Please enter a number.");
                }
            }

            if (writeData(id, newRate, data[2].trim())) {
                System.out.println("Hourly rate updated successfully.");
                System.out.println("New Hourly Rate: " + String.format("%.2f", newRate));
            }

            System.out.println("\n1. Update Another Rate");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
            if (!choice.equals("1")) {
                return;
            }
        }
    }
    
    // ===== STATUS =====
    static void status(){
        String[] STATUSES = {"Available", "Under Maintenance", "Occupied"};
        
        while (true) {
            System.out.println("\n========================");
            System.out.println("   Set Status");
            System.out.println("========================");
            
            System.out.print("Enter Computer ID (0 to cancel): ");
            String id = sc.nextLine().trim();

            if (id.equals("0")) return;

            File file = new File(FOLDER + "/" + id + ".txt");
            if (id.isEmpty() || !id.matches("[A-Za-z0-9_-]+") || !file.exists()) {
                System.out.println("Computer ID not found!");
                continue;
            }

            String[] data = readData(file);
            if (data == null) {
                System.out.println("Could not read computer record.");
                continue;
            }

            double rate;
            try {
                rate = Double.parseDouble(data[1].trim());
            } catch (NumberFormatException e) {
                System.out.println("Computer record has an invalid rate.");
                continue;
            }

            String currentStatus = data[2].trim();

            System.out.println("\nCurrent Status: " + currentStatus);
            System.out.println("---");
            System.out.println("1. " + STATUSES[0]);
            System.out.println("2. " + STATUSES[1]);
            System.out.println("3. " + STATUSES[2]);
            System.out.println("---");

            String newStatus = null;
            while (newStatus == null) {
                System.out.print("Select New Status: ");
                String input = sc.nextLine().trim();
                switch (input) {
                    case "1": newStatus = STATUSES[0]; break;
                    case "2": newStatus = STATUSES[1]; break;
                    case "3": newStatus = STATUSES[2]; break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            }

            if (newStatus.equalsIgnoreCase(currentStatus)) {
                System.out.println("Computer is already " + newStatus + ".");
            } else {
                if (writeData(id, rate, newStatus)) {
                    System.out.println("Status updated successfully.");
                    System.out.println("New Status: " + newStatus);
                }
            }

            System.out.println("\n1. Update Another Status");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
            if (!choice.equals("1")) {
                return;
            }
        }
    }

    // ===== FILE HELPERS =====
    static String[] readData(File file) {
        try (Scanner reader = new Scanner(file)) {
            if (reader.hasNextLine()) {
                String[] data = reader.nextLine().trim().split(",", -1);
                if (data.length == 3) return data;
            }
        } catch (IOException e) {
            System.out.println("Error reading " + file.getName());
        }
        return null;
    }

    static boolean writeData(String id, double rate, String status) {
        File folder = new File(FOLDER);
        if (!folder.exists()) folder.mkdirs();
        try (FileWriter writer = new FileWriter(new File(folder, id + ".txt"), false)) {
            writer.write(id + "," + String.format("%.2f", rate) + "," + status + System.lineSeparator());
            return true;
        } catch (IOException e) {
            System.out.println("Error saving computer.");
            return false;
        }
    }
}