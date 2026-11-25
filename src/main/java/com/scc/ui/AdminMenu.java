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
            System.out.println("(1). View All System Users");
            System.out.println("(2). View Pending Registrations");
            System.out.println("(3). Approve User Account");
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
        System.out.println("\n--- All System Users ---");
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        printUserTable(users);
    }

    private void viewPendingApprovals() {
        System.out.println("\n--- Pending Registrations ---");
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("✅ No pending accounts.");
            return;
        }
        printUserTable(pending);
    }

    private void approveUserRegistration() {
        System.out.println("\n--- Approve User Account ---");
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("✅ No pending accounts to approve.");
            return;
        }
        printUserTable(pending);

        System.out.print("Enter Target User ID to approve (or enter 0 to cancel): ");
        try {
            int userId = Integer.parseInt(scanner.nextLine().trim());
            if (userId == 0) {
                System.out.println("↩️ Approval action cancelled.");
                return;
            }

            boolean existsInPending = pending.stream().anyMatch(u -> u.id() == userId);
            if (!existsInPending) {
                System.out.println("❌ User ID " + userId + " is not in the pending registrations list.");
                return;
            }

            if (userDao.approveUser(userId)) {
                System.out.println("✅ User ID " + userId + " has been APPROVED.");
            } else {
                System.out.println("❌ Approval failed.");
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

    private void printUserTable(List<User> users) {
        String border = "--------------------------------------------------";
        System.out.println(border);
        System.out.printf("%-5s %-15s %-12s %-10s%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);
        for (User user : users) {
            System.out.printf("%-5d %-15s %-12s %-10s%n",
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status());
        }
        System.out.println(border);
    }
}