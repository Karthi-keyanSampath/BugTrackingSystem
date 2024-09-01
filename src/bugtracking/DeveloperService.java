package bugtracking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class DeveloperService {
    private DatabaseConnection dbConnection;


    public DeveloperService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    public void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the Username:");
        String username = scanner.nextLine();
        System.out.println("Enter the Password:");
        String password = scanner.nextLine();

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "SELECT id FROM developer WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int developerId = rs.getInt(1);
                manageDeveloperTasks(developerId);
            } else {
                System.out.println("Incorrect username or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageDeveloperTasks(int developerId) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("1. View Assigned Bugs\n2. Change Bug Status\n3. Logout");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAssignedBugs(developerId);
                    break;
                case 2:
                    changeBugStatus(developerId);
                    break;
                case 3:
                    System.out.println("Logged Out Successfully");
                    return;
                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    public void viewAssignedBugs(int developerId) {
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "SELECT bugid, filename, bugdescription, bugpriority, bugstatus FROM bug_entry WHERE developer_id=?")) {
            ps.setInt(1, developerId);
            ResultSet rs = ps.executeQuery();

            System.out.println("bugid\tfilename\tbugdescription\tbugpriority\tbugstatus");
            while (rs.next()) {
                System.out.printf("%d\t%s\t%s\t%s\t%s\n", rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeBugStatus(int developerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the BugID:");
        int bugId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Enter the new Bug Status:");
        String bugStatus = scanner.nextLine();

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "UPDATE bug_entry SET bugstatus=? WHERE bugid=? AND developer_id=?")) {
            ps.setString(1, bugStatus);
            ps.setInt(2, bugId);
            ps.setInt(3, developerId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Bug Status Updated Successfully");
            } else {
                System.out.println("You are not authorized to change the status of this bug.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
