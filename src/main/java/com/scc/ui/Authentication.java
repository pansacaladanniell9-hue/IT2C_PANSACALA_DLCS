package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;
    private User currentUser;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n===== DIGITAL LOYALTY CARD SYSTEM =====");
            System.out.println("[1] Register");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("👋 Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register New Account ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.println("Select Role:");
        System.out.println("  [1] Admin");
        System.out.println("  [2] Cashier");
        System.out.println("  [3] Customer");
        System.out.print("Enter choice: ");
        String roleChoice = scanner.nextLine().trim();

        String role;
        switch (roleChoice) {
            case "1" -> role = "ADMIN";
            case "2" -> role = "CASHIER";
            case "3" -> role = "CUSTOMER";
            default -> {
                System.out.println("❌ Invalid role selected.");
                return;
            }
        }

        if (userDao.registerUser(username, password, role)) {
            System.out.println("✅ Registration successful! Status: PENDING — waiting for approval.");
        } else {
            System.out.println("❌ Registration failed. Username may already exist.");
        }
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        currentUser = userDao.login(username, password);

        if (currentUser == null) {
            System.out.println("❌ Login failed. Check your credentials.");
            return;
        }

        if (!"APPROVED".equals(currentUser.status())) {
            System.out.println("⏳ Your account is not approved yet. Please wait for admin approval.");
            return;
        }

        System.out.println("✅ Welcome, " + currentUser.username() + "! Role: " + currentUser.role());
        routeToMenu();
        currentUser = null;
        System.out.println("🔒 Logged out successfully.");
    }

    private void routeToMenu() {
        switch (currentUser.role()) {
            case "ADMIN" -> new AdminMenu(userDao, scanner).display(currentUser);
            case "CASHIER" -> System.out.println("Cashier menu — ready");
            case "CUSTOMER" -> System.out.println("Customer menu — ready");
        }
    }
}
