package bugtracking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class TesterService {
    private DatabaseConnection dbConnection;
    private BugService bugService;

    public TesterService(DatabaseConnection dbConnection, BugService bugService) {
        this.dbConnection = dbConnection;
        this.bugService = bugService;
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the Username:");
        String username = scanner.nextLine();
        System.out.println("Enter the Password:");
        String password = scanner.nextLine();

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "SELECT id FROM tester WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int testerId = rs.getInt(1);
                manageTesterTasks(testerId);
            } else {
                System.out.println("Incorrect username or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageTesterTasks(int testerId) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("1. File a New Bug\n2. Change the Status of a Bug\n3. Edit Bug Details\n4. Get Bug Report\n5. Logout");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    bugService.fileNewBug(testerId);
                    break;
                case 2:
                    bugService.changeBugStatus(testerId);
                    break;
                case 3:
                    bugService.editBugDetails(testerId);
                    break;
                case 4:
                    bugService.getBugReport();
                    break;
                case 5:
                    System.out.println("Logged Out Successfully");
                    return;
                default:
                    System.out.println("Invalid Choice");
            }
        }
    }

    private void fileNewBug(int testerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the FileName:");
        String filename = scanner.nextLine();
        System.out.println("Enter the BugID:");
        int bugId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter the Bug Description:");
        String bugDescription = scanner.nextLine();
        System.out.println("Enter the Bug Priority:");
        String bugPriority = scanner.nextLine();
        System.out.println("Enter the Bug Status:");
        String bugStatus = scanner.nextLine();
        System.out.println("Enter the Developer ID (0 if not assigned):");
        int developerId = scanner.nextInt();

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "INSERT INTO bug_entry (bugid, filename, bugdescription, bugpriority, bugstatus, tester_id, developer_id) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, bugId);
            ps.setString(2, filename);
            ps.setString(3, bugDescription);
            ps.setString(4, bugPriority);
            ps.setString(5, bugStatus);
            ps.setInt(6, testerId);
            ps.setInt(7, developerId);
            ps.executeUpdate();
            System.out.println("Bug Filed Successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
