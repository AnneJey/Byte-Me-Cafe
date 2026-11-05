package net.cafe;

import java.util.*;
import java.io.*;

public class CompManage {
    static Scanner sc = NetCafe.sc;
    static final String FOLDER = "computers";   // one .txt file per computer

    // ---------------- START: MANAGE COMPUTER (STAFF AND ADMIN) ----------------
    // isAdmin = true  -> Admin: View And Search, Add, Update Hourly Rate, Update Status, Remove
    // isAdmin = false -> Staff: View And Search, Update Status

    static void manage(boolean isAdmin) {
        new File(FOLDER).mkdirs();

        boolean running = true;
        while (running) {
            // Display menu
            System.out.println("\n===== MANAGE COMPUTERS =====");
            System.out.println("1. View And Search");
            if (isAdmin) {
                System.out.println("2. Add");
                System.out.println("3. Update Hourly Rate");
                System.out.println("4. Update Status");
                System.out.println("5. Remove");
            } else {
                System.out.println("2. Update Status");
            }
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            // Staff only has "Update Status", which is option 2 on their menu
            if (!isAdmin) {
                if (choice.equals("2")) {
                    choice = "4";
                } else if (choice.equals("3") || choice.equals("4") || choice.equals("5")) {
                    choice = "";   // not allowed for staff
                }
            }

            switch (choice) {
                case "1": viewAndSearch(); break;
                case "2": addComputer(); break;
                case "3": updateRate(); break;
                case "4": updateStatus(); break;
                case "5": removeComputer(); break;
                case "0": running = false; break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
        // End -> back to Staff / Admin menu
    }

    // ---------------- 1. VIEW AND SEARCH ----------------
    static void viewAndSearch() {
        listComputers();

        // Enter Computer ID
        System.out.print("\nEnter Computer ID: ");
        String id = sc.nextLine().trim().toUpperCase();

        // Does Computer ID exist?
        String[] c = readComputer(id);
        if (c == null) {
            System.out.println("Computer Does Not Exist");
        } else {
            // Display Computer Details
            showComputer(c);
        }
    }

    // ---------------- 2. ADD (ADMIN) ----------------
    static void addComputer() {
        // Add New Computer -> Does Computer Exist? (Yes -> Display "Computer Already Exist" -> again)
        String id = "";
        boolean valid = false;
        while (!valid) {
            System.out.print("\nAdd New Computer - enter Computer ID (0 to cancel): ");
            id = sc.nextLine().trim().toUpperCase();
            if (id.equals("0")) {
                return;
            }
            if (!id.matches("[A-Z0-9-]+")) {
                System.out.println("Invalid ID. Use letters, numbers and dashes only (example: PC-01).");
            } else if (readComputer(id) != null) {
                System.out.println("Computer Already Exist");
            } else {
                valid = true;
            }
        }

        // Input Details
        int rate = 0;
        while (rate <= 0) {
            System.out.print("Input Hourly Rate (PHP): ");
            try {
                rate = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                rate = 0;
            }
            if (rate <= 0) {
                System.out.println("Invalid hourly rate.");
            }
        }

        // Display New Computer Details -> Save To Computer Files
        String[] c = {id, String.valueOf(rate), "Available"};
        showComputer(c);
        if (saveComputer(c[0], c[1], c[2])) {
            System.out.println("Saved to computer files.");
        }
    }

    // ---------------- 3. UPDATE HOURLY RATE (ADMIN) ----------------
    static void updateRate() {
        // Input Computer ID -> Does Computer Exist? (No -> Display "Computer Does Not Exist" -> again)
        String[] c = null;
        while (c == null) {
            System.out.print("\nInput Computer ID (0 to cancel): ");
            String id = sc.nextLine().trim().toUpperCase();
            if (id.equals("0")) {
                return;
            }
            c = readComputer(id);
            if (c == null) {
                System.out.println("Computer Does Not Exist");
            }
        }

        // Input New Computer Hourly Rate
        int rate = 0;
        while (rate <= 0) {
            System.out.print("Input New Computer Hourly Rate (PHP): ");
            try {
                rate = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                rate = 0;
            }
            if (rate <= 0) {
                System.out.println("Invalid hourly rate.");
            }
        }
        c[1] = String.valueOf(rate);

        // Display Updated Computer Details -> Save To Computer Files
        showComputer(c);
        if (saveComputer(c[0], c[1], c[2])) {
            System.out.println("Saved to computer files.");
        }
    }

    // ---------------- 4. UPDATE STATUS (STAFF AND ADMIN) ----------------
    static void updateStatus() {
        // Input Computer ID -> Does Computer Exist? (No -> Display "Computer Does Not Exist" -> again)
        String[] c = null;
        while (c == null) {
            System.out.print("\nInput Computer ID (0 to cancel): ");
            String id = sc.nextLine().trim().toUpperCase();
            if (id.equals("0")) {
                return;
            }
            c = readComputer(id);
            if (c == null) {
                System.out.println("Computer Does Not Exist");
            }
        }

        // Status: 1. Available  2. In Use  3. Under Maintenance -> Enter Choice
        String status = "";
        while (status.isEmpty()) {
            System.out.println("\nStatus");
            System.out.println("1. Available");
            System.out.println("2. In Use");
            System.out.println("3. Under Maintenance");
            System.out.print("Enter Choice: ");
            String choice = sc.nextLine().trim();
            if (choice.equals("1")) {
                status = "Available";
            } else if (choice.equals("2")) {
                status = "In Use";
            } else if (choice.equals("3")) {
                status = "Under Maintenance";
            } else {
                System.out.println("Invalid choice.");
            }
        }
        c[2] = status;

        // Display Updated Computer Details -> Save To Computer Files
        showComputer(c);
        if (saveComputer(c[0], c[1], c[2])) {
            System.out.println("Saved to computer files.");
        }
    }

    // ---------------- 5. REMOVE (ADMIN) ----------------
    static void removeComputer() {
        // Input Computer ID -> Does Computer Exist? (No -> Display "Computer Does Not Exist" -> again)
        String[] c = null;
        while (c == null) {
            System.out.print("\nInput Computer ID (0 to cancel): ");
            String id = sc.nextLine().trim().toUpperCase();
            if (id.equals("0")) {
                return;
            }
            c = readComputer(id);
            if (c == null) {
                System.out.println("Computer Does Not Exist");
            }
        }

        // Display Computer Details
        showComputer(c);

        // Are You Sure You Want To Delete?
        String confirm = "";
        while (!confirm.equalsIgnoreCase("Y") && !confirm.equalsIgnoreCase("N")) {
            System.out.print("Are you sure you want to delete? (Y/N): ");
            confirm = sc.nextLine().trim();
        }

        if (confirm.equalsIgnoreCase("Y")) {
            // Removed Computer From File
            File f = new File(FOLDER + "/" + c[0] + ".txt");
            if (f.delete()) {
                System.out.println("Removed Computer Successful");
            } else {
                System.out.println("An error occurred while removing the computer.");
            }
        } else {
            System.out.println("Removed Computer Cancelled");
        }
    }

    // ---------------- DISPLAY ----------------
    static void showComputer(String[] c) {
        System.out.println("\n--- Computer Details ---");
        System.out.println("Computer ID : " + c[0]);
        System.out.println("Hourly Rate : PHP " + c[1]);
        System.out.println("Status      : " + c[2]);
    }

    static void listComputers() {
        File[] files = new File(FOLDER).listFiles((d, name) -> name.endsWith(".txt"));
        System.out.println("\n===== COMPUTER LIST =====");
        if (files == null || files.length == 0) {
            System.out.println("No computers found.");
            return;
        }
        Arrays.sort(files);
        System.out.println(String.format("%-10s %-12s %s", "ID", "Rate (PHP)", "Status"));
        for (File f : files) {
            String[] c = readComputer(f.getName().replace(".txt", ""));
            if (c != null) {
                System.out.println(String.format("%-10s %-12s %s", c[0], c[1], c[2]));
            }
        }
    }

    // ---------------- FILE HELPERS ----------------
    // Each computer has its own file: computers/PC-01.txt
    // File format:
    // PC-01
    // Hourly Rate: 20
    // Status: Available
    static boolean saveComputer(String id, String rate, String status) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FOLDER + "/" + id + ".txt"))) {
            pw.println(id);
            pw.println("Hourly Rate: " + rate);
            pw.println("Status: " + status);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while saving the computer.");
            return false;
        }
    }

    // Reads one computer by ID. Returns {id, hourlyRate, status} or null if it does not exist.
    static String[] readComputer(String id) {
        if (!id.matches("[A-Z0-9-]+")) {
            return null;
        }
        File f = new File(FOLDER + "/" + id + ".txt");
        try (Scanner fr = new Scanner(f)) {
            String cid = fr.nextLine().trim();
            String rate = fr.nextLine().replace("Hourly Rate: ", "").trim();
            String status = fr.nextLine().replace("Status: ", "").trim();
            return new String[]{cid, rate, status};
        } catch (Exception ignored) {
            return null;
        }
    }
}
