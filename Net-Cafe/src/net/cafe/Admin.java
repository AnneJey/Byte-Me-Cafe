package net.cafe;
import java.util.*;
import java.io.*;

public class Admin {
    static Scanner sc = ByteMeCafe.sc;
    public static void main(String[] args) {
    }
    
    // Admin Menu
static void adminMenu() {
    while (true) {
        System.out.println("\n===== ADMIN MENU =====");
        System.out.println("1. Manage Computers");
        System.out.println("2. Manage Customers");
        System.out.println("3. Manage Inventory");
        System.out.println("4. Transaction History");
        System.out.println("5. Sales Report");
        System.out.println("0. Logout");
        System.out.println("---");
        System.out.print("Enter choice: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1": CompManage.manage(true); break;
            case "2": CustomerManage.manage(); break;
            case "3": Inventory.InventoryMenu(true); break;
            case "4": TransactionHistory.show(); break;
            case "5":
                System.out.println("Opening Sales Report... \n");
                SalesReport.salesReport();
                break;
            case "0":
                System.out.println("Opening Log Out Menu... \n");
                ByteMeCafe.logOut();
                return;
            default:
                System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    }
}
