/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.cafe;
import java.util.*;
import java.io.*;

public class UserManagement {
    static final int MAX = 100;
    static String[] ids = new String[MAX];
    static String[] usernames = new String[MAX];
    static String[] passwords = new String[MAX];
    static int count = 0;

    static final String FOLDER_NAME = "customers";
    static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
    }
    
    // Customer Accounts
    static void accManage(){
        loadFromFolder();
        boolean running = true;
        
        // 👇 Removed the duplicate "while (running)" loop here
        while (running) {
            System.out.println("\n===== CUSTOMER ACCOUNTS =====");
            System.out.println("1. View customer accounts");
            System.out.println("2. Search customer account");
            System.out.println("3. Remove customer account");
            System.out.println("0. Exit");
            
            System.out.println("---");
            System.out.print("Enter choice: ");
            if (!sc.hasNextInt()) {
                sc.nextLine();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine();
 
            switch (choice) {
                case 1: viewAcc(); break;
                case 2: searchAcc(); break;
                case 3: removeAcc(); break;
                case 0:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
    
    // View All Accounts
    static void viewAcc(){
        System.out.println("\n====================");
        System.out.println("   View Accounts   ");
        System.out.println("====================");
        if (count == 0) {
            System.out.println("No customer accounts found.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". ID: " + ids[i]
                    + " | Username: " + usernames[i]
                    + " | Password: " + maskPassword(passwords[i]));
        }
    }
    
    // Search Account
    static void searchAcc(){
        System.out.println("\n====================");
        System.out.println("   Search Account   ");
        System.out.println("====================");
        System.out.print("Enter ID or username to search: ");
        String query = sc.nextLine().trim().toLowerCase();
        
        boolean found = false;
        System.out.println("\n--- Search Results ---");
        for (int i = 0; i < count; i++) {
            if (ids[i].toLowerCase().contains(query)
                    || usernames[i].toLowerCase().contains(query)) {
                System.out.println("ID: " + ids[i]
                        + " | Username: " + usernames[i]
                        + " | Password: " + maskPassword(passwords[i]));
                found = true;
            }
        }
        if (!found) System.out.println("No matching accounts found.");
    }
    
    // Remove Account
    static void removeAcc() {
        viewAcc();
        if (count == 0) return;

        System.out.println("\n====================");
        System.out.println("   Remove Account   ");
        System.out.println("====================");
        System.out.print("Enter account ID to remove: ");
        String id = sc.nextLine().trim();

        int idx = findIndex(id);
        if (idx == -1) {
            System.out.println("Account not found.");
            return;
        }
        
        // Confirmation
        System.out.print("Are you sure you want to remove ID '" + id + "'? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        switch (confirm) {
            case "y":
            case "yes":
                // 👇 Added ".txt" so it knows exactly which file to delete
                File fileToDelete = new File(FOLDER_NAME, id + ".txt"); 
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }

                // Shift elements left in memory to remove it
                for (int i = idx; i < count - 1; i++) {
                    ids[i]       = ids[i + 1];
                    usernames[i] = usernames[i + 1];
                    passwords[i] = passwords[i + 1];
                }
                count--;
                System.out.println("Account " + id + " removed successfully.");
                break;
                
            case "n":
            case "no": 
                System.out.println("Removal cancelled."); 
                break;
            default: 
                System.out.println("Invalid input. Removal cancelled."); 
                break;
        }
    }

    // --- Helpers ---
    static int findIndex(String id) {
        for (int i = 0; i < count; i++) {
            if (ids[i].equals(id)) return i;
        }
        return -1;
    }
    // --- Hide password ---
    static String maskPassword(String pwd) {
        if (pwd == null || pwd.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pwd.length(); i++) sb.append('*');
        return sb.toString();
    }
    // --- FILE I/O ---
    static void loadFromFolder() {
        File folder = new File(FOLDER_NAME);
        
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        File[] listOfFiles = folder.listFiles();
        count = 0;

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && count < MAX) {
                    
                    // 👇 THIS IS THE FIX: Removes ".txt" from the ID
                    String fileName = file.getName();
                    if (fileName.endsWith(".txt")) {
                        fileName = fileName.substring(0, fileName.length() - 4);
                    }
                    
                    ids[count] = fileName; // Now it will just be "001", "002", etc.
                    
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String userLine = br.readLine();
                        String passLine = br.readLine();
                        
                        usernames[count] = (userLine != null) ? userLine.trim() : "N/A";
                        passwords[count] = (passLine != null) ? passLine.trim() : "N/A";
                    } catch (IOException e) {
                        usernames[count] = "Error";
                        passwords[count] = "Error";
                    }
                    count++;
                }
            }
        }
    }
}
