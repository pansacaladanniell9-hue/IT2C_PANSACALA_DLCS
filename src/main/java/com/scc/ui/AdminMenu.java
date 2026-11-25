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
                case "5" -> updateUserRoleOrStatus(admin);
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
        printUserTable(users);
    }

    private void viewPendingApprovals() {
        System.out.println("\n--- Pending User Approvals ---");
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("✅ No pending accounts.");
            return;
        }
        printUserTable(pending);
    }

    private void approveUserRegistration() {
        System.out.println("\n--- Approve User Registration ---");
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("✅ No pending accounts.");
            return;
        }
        printUserTable(pending);

        int userId = readIntInput("Enter User ID (or 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Approval cancelled.");
            return;
        }

        boolean inPending = pending.stream().anyMatch(u -> u.id() == userId);
        if (!inPending) {
            System.out.println("❌ User ID not found in pending list.");
            return;
        }

        if (userDao.approveUser(userId)) {
            System.out.println("✅ User account updated to: APPROVED");
        } else {
            System.out.println("❌ Update failed. User not found.");
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

    private void updateUserRoleOrStatus(User currentAdmin) {
        System.out.println("\n--- Update User Role / Status ---");
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        printUserTable(users);

        int userId = readIntInput("Enter User ID (or 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userId == currentAdmin.id()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        User targetUser = users.stream().filter(u -> u.id() == userId).findFirst().orElse(null);
        if (targetUser == null) {
            System.out.println("❌ User not found.");
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(userId, newRole, newStatus)) {
            System.out.println("✅ User updated successfully! Role: " + newRole + ", Status: " + newStatus);
        } else {
            System.out.println("❌ Update failed.");
        }
    }

    private void deleteUserAccount() {
        System.out.println("\n--- Delete User Account ---");
        int userId = readIntInput("Enter User ID to DELETE (or 0 to cancel): ");
        if (userId == 0) {
            System.out.println("Delete cancelled.");
            return;
        }
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
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role (ADMIN / CASHIER / CUSTOMER, or 0 to cancel): ");
            String role = scanner.nextLine().trim().toUpperCase();
            if (role.equals("0")) {
                return null;
            }
            if (role.equals("ADMIN") || role.equals("CASHIER") || role.equals("CUSTOMER")) {
                return role;
            }
            System.out.println("❌ Invalid role. Please enter ADMIN, CASHIER, or CUSTOMER.");
        }
    }

    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status (APPROVED / PENDING / DEACTIVATED, or 0 to cancel): ");
            String status = scanner.nextLine().trim().toUpperCase();
            if (status.equals("0")) {
                return null;
            }
            if (status.equals("APPROVED") || status.equals("PENDING") || status.equals("DEACTIVATED")) {
                return status;
            }
            System.out.println("❌ Invalid status. Please enter APPROVED, PENDING, or DEACTIVATED.");
        }
    }

    private void printUserTable(List<User> users) {
        printTableHeader();
        for (User u : users) {
            printUserRow(u);
        }
    }

    private void printTableHeader() {
        System.out.println("--------------------------------------------------");
        System.out.printf("%-5s | %-15s | %-12s | %-10s%n",
                "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("--------------------------------------------------");
    }

    private void printUserRow(User user) {
        System.out.printf("%-5d | %-15s | %-12s | %-10s%n",
                user.id(),
                user.username(),
                user.role(),
                user.status());
    }
}