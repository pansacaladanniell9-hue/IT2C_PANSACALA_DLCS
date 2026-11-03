package com.scc;

import com.scc.ui.AdminMenu;
import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Main {
    private static final Scanner scan = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();
    public static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Digital Loyalty Card System ===");
        mainMenu();
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n[1] Register");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.print("Choice: ");
            String opt = scan.nextLine();

            switch (opt) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void register() {
        System.out.print("Username: ");
        String u = scan.nextLine().trim();
        System.out.print("Password: ");
        String p = scan.nextLine().trim();
        System.out.print("Role (ADMIN/CASHIER/CUSTOMER): ");
        String r = scan.nextLine().trim().toUpperCase();

        if (userDao.registerUser(u, p, r)) {
            System.out.println("Registered — status: PENDING.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    private static void login() {
        System.out.print("Username: ");
        String u = scan.nextLine().trim();
        System.out.print("Password: ");
        String p = scan.nextLine().trim();

        User user = userDao.login(u, p);
        if (user == null) {
            System.out.println("Login failed.");
            return;
        }
        if (!"APPROVED".equals(user.status())) {
            System.out.println("Not approved yet.");
            return;
        }
        currentUser = user;
        System.out.println("Welcome, " + user.username() + " [" + user.role() + "]");

        if ("ADMIN".equals(user.role())) {
            AdminMenu.show();
        }

        currentUser = null;
        System.out.println("Logged out.");
    }
}
