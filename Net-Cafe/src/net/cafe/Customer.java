
package net.cafe;
import java.util.*;
import java.io.*;

public class Customer {
    static Scanner sc = NetCafe.sc;
    static String currentId;
    static String currentName;
    public static void main(String[] args) {
    }
    
    // Customer Menu
    static void customerMenu() {
        boolean loggedIn = true;
 
        while (loggedIn) {
            System.out.println("\n===================");
            System.out.println("   Customer Menu   ");
            System.out.println("===================");
            System.out.println("Logged in as: " + currentName + " (ID " + currentId + ")");
            System.out.println("1. Payment (Start Session)");
            System.out.println("2. Menu And Order");
            System.out.println("3. Request Assistance");
            System.out.println("0. Logout");
            System.out.println("---");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine().trim();
 
            switch (choice) {
                case "1":
                    Payment.Display();
                    break;
                case "2":
                    viewMenuOrder.orderMenu(currentId, currentName);
                    break;
                case "3":
                    RequestAssistance.requestAssistance(currentId);
                    break;
                case "0":
                    System.out.println("\nOpening Log Out Menu...");
                    currentId = null;
                    currentName = null;
                    NetCafe.logOut();
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
}
