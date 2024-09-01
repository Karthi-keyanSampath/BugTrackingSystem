package bugtracking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AdminService {
    private DatabaseConnection dbConnection;

    public AdminService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Method to handle admin login
    public void adminLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Admin Login");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "SELECT * FROM admin_login WHERE admin_name = ? AND admin_password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Login successful!");
                boolean exit = true;
                manageAdminTasks();
            } else {
                System.out.println("Invalid username or password.");

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void manageAdminTasks() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("1. Add User\n2. Logout");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addUser();
                    break;
                case 2:
                    System.out.println("Logged Out Successfully");
                    return;
                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    // Method to add a new user (either tester or developer)
    public void addUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Add User");
        System.out.println("1. Add Tester");
        System.out.println("2. Add Developer");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            System.out.print("Enter Tester Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Tester Password: ");
            String password = scanner.nextLine();
            addUserToTable(username, password, "TESTER");
        } else if (choice == 2) {
            System.out.print("Enter Developer Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Developer Password: ");
            String password = scanner.nextLine();
            addUserToTable(username, password, "DEVELOPER");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void addUserToTable(String username, String password, String userType) {
        String tableName = userType.equals("TESTER") ? "tester" : "developer";
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "INSERT INTO " + tableName + " (username, password) VALUES (?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            System.out.println(userType + " added successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
