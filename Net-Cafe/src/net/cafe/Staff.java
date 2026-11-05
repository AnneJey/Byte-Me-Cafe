package net.cafe;

import java.util.*;
import java.io.*;

public class Staff {
    static Scanner sc = ByteMeCafe.sc;
    public static void main(String[] args) {
    }

    // ===== STAFF MENU =====
    static void staffMenu() {
        while (true) {
            System.out.println("\n===== STAFF MENU =====");
            System.out.println("1. Manage Computers");
            System.out.println("2. Manage Inventory (add, update food and drinks)");
            System.out.println("3. Manage Customers");
            System.out.println("4. Transaction History");
            System.out.println("5. Assistance Management");
            System.out.println("0. Logout");

            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": CompManage.manage(false); break;
                case "2": Inventory.InventoryMenu(false); break;
                case "3": CustomerManage.manage(); break;
                case "4": TransactionHistory.show(); break;
                case "5": AssistanceManage.manage(); break;
                case "0": 
                    System.out.println("Logging out...");
                    ByteMeCafe.logOut(); 
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
