package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final Scanner scan = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void show() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("[1] View Pending Accounts");
            System.out.println("[2] Approve / Update Account Status");
            System.out.println("[3] Logout");
            System.out.print("Enter choice: ");
            String choice = scan.nextLine();

            switch (choice) {
                case "1" -> showPending();
                case "2" -> updateStatus();
                case "3" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showPending() {
        List<User> pending = userDao.getAllPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending accounts.");
            return;
        }
        System.out.println("\n--- Pending Accounts ---");
        for (User u : pending) {
            System.out.printf("ID: %d | Username: %s | Role: %s | Status: %s%n",
                    u.id(), u.username(), u.role(), u.status());
        }
    }

    private static void updateStatus() {
        showPending();
        System.out.print("\nEnter User ID to update: ");
        try {
            int id = Integer.parseInt(scan.nextLine().trim());
            System.out.print("New Status (APPROVED / DEACTIVATED): ");
            String status = scan.nextLine().trim().toUpperCase();

            if (!status.equals("APPROVED") && !status.equals("DEACTIVATED")) {
                System.out.println("Invalid status! Use APPROVED or DEACTIVATED.");
                return;
            }

            boolean ok = userDao.updateStatus(id, status);
            if (ok) {
                System.out.println("Account updated to: " + status);
            } else {
                System.out.println("Update failed! User not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error! Enter a valid number for ID.");
        }
    }
}
