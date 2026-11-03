package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n==================================================");
            System.out.println("                  ADMIN DASHBOARD                  ");
            System.out.println("==================================================");
            System.out.println("(1). View All Users");
            System.out.println("(2). View Pending User Approvals");
            System.out.println("(3). Approve User Registration");
            System.out.println("(4). Add New User");
            System.out.println("(5). Update User Role / Status");
            System.out.println("(6). Delete User Account");
            System.out.println("(7). Logout");
            System.out.println("==================================================");
            System.out.print("Choose an option (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingApprovals();
                case "3" -> approveUserRegistration();
                case "4" -> addNewUser();
                case "5" -> updateUserRoleOrStatus();
                case "6" -> deleteUserAccount();
                case "7" -> {
                    System.out.println("🔓 Logging out...");
                    loggedIn = false;
                }
                default -> System.out.println("❌ Invalid option! Please choose 1-7.");
            }
        }
    }

    private void viewAllUsers() {
        System.out.println("\n--- All Registered Users ---");
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        printTableHeader();
        for (User u : users) {
            printUserRow(u);
        }
    }

    private void viewPendingApprovals() {
        System.out.println("\n--- Pending User Approvals ---");
        List<User> pending = userDao.getAllPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("✅ No pending accounts.");
            return;
        }
        printTableHeader();
        for (User u : pending) {
            printUserRow(u);
        }
    }

    private void approveUserRegistration() {
        viewPendingApprovals();
        System.out.println("\n--- Approve User Registration ---");
        System.out.print("Enter User ID: ");
        try {
            int userId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter new status (APPROVED / DEACTIVATED): ");
            String newStatus = scanner.nextLine().trim().toUpperCase();

            if (!newStatus.equals("APPROVED") && !newStatus.equals("DEACTIVATED")) {
                System.out.println("❌ Invalid status. Use APPROVED or DEACTIVATED.");
                return;
            }

            if (userDao.updateStatus(userId, newStatus)) {
                System.out.println("✅ User account updated to: " + newStatus);
            } else {
                System.out.println("❌ Update failed. User not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private void addNewUser() {
        System.out.println("\n--- Add New User ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Role (ADMIN / CASHIER / CUSTOMER): ");
        String role = scanner.nextLine().trim().toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("CASHIER") && !role.equals("CUSTOMER")) {
            System.out.println("❌ Invalid role.");
            return;
        }

        if (userDao.registerUser(username, password, role)) {
            System.out.println("✅ User added successfully! Status: PENDING.");
        } else {
            System.out.println("❌ Username already exists.");
        }
    }

    private void updateUserRoleOrStatus() {
        System.out.println("\n--- Update User Role / Status ---");
        System.out.print("Enter User ID: ");
        try {
            int userId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Update role? (ADMIN / CASHIER / CUSTOMER) or press Enter to skip: ");
            String newRole = scanner.nextLine().trim().toUpperCase();

            System.out.print("Update status? (APPROVED / DEACTIVATED) or press Enter to skip: ");
            String newStatus = scanner.nextLine().trim().toUpperCase();

            boolean updated = false;

            if (!newRole.isEmpty()) {
                if (!newRole.equals("ADMIN") && !newRole.equals("CASHIER") && !newRole.equals("CUSTOMER")) {
                    System.out.println("❌ Invalid role — skipped.");
                } else if (userDao.updateUserRole(userId, newRole)) {
                    System.out.println("✅ Role updated to: " + newRole);
                    updated = true;
                }
            }

            if (!newStatus.isEmpty()) {
                if (!newStatus.equals("APPROVED") && !newStatus.equals("DEACTIVATED")) {
                    System.out.println("❌ Invalid status — skipped.");
                } else if (userDao.updateStatus(userId, newStatus)) {
                    System.out.println("✅ Status updated to: " + newStatus);
                    updated = true;
                }
            }

            if (!updated) {
                System.out.println("⚠️ No changes made.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private void deleteUserAccount() {
        System.out.println("\n--- Delete User Account ---");
        System.out.print("Enter User ID to DELETE: ");
        try {
            int userId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Are you sure? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("yes")) {
                if (userDao.deleteUser(userId)) {
                    System.out.println("✅ User account deleted successfully.");
                } else {
                    System.out.println("❌ Delete failed. User not found.");
                }
            } else {
                System.out.println("Delete cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number.");
        }
    }

    private void printTableHeader() {
        System.out.println("--------------------------------------------------");
        System.out.printf(" %-4s | %-15s | %-10s | %-10s%n",
                "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("--------------------------------------------------");
    }

    private void printUserRow(User user) {
        System.out.printf(" %-4d | %-15s | %-10s | %-10s%n",
                user.id(),
                user.username(),
                user.role(),
                user.status());
    }
}
