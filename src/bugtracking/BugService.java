package bugtracking;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class BugService {
    private DatabaseConnection dbConnection;

    public BugService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Method to file a new bug, used by testers
    public void fileNewBug(int testerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the FileName:");
        String filename = scanner.nextLine();
        System.out.println("Enter the BugID:");
        int bugId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter the Bug Description (Max 200 characters):");
        String bugDescription = scanner.nextLine();
        System.out.println("Enter the Bug Priority:");
        String bugPriority = scanner.nextLine();

        String bugStatus = selectBugStatus(scanner);

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

    // Method to change bug status, used by both testers and developers
    public void changeBugStatus(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the BugID:");
        int bugId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String bugStatus = selectBugStatus(scanner);

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "UPDATE bug_entry SET bugstatus=? WHERE bugid=? AND (tester_id=? OR developer_id=?)")) {
            ps.setString(1, bugStatus);
            ps.setInt(2, bugId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
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

    // Method to edit bug details
    public void editBugDetails(int userId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the BugID:");
        int bugId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Edit the FileName:");
        String filename = scanner.nextLine();

        System.out.println("Edit the Bug Description (Max 200 characters):");
        String bugDescription = scanner.nextLine();

        System.out.println("Edit the Bug Priority:");
        String bugPriority = scanner.nextLine();

        String bugStatus = selectBugStatus(scanner);

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "UPDATE bug_entry SET filename=?, bugdescription=?, bugpriority=?, bugstatus=? WHERE bugid=? AND (tester_id=? OR developer_id=?)")) {
            ps.setString(1, filename);
            ps.setString(2, bugDescription);
            ps.setString(3, bugPriority);
            ps.setString(4, bugStatus);
            ps.setInt(5, bugId);
            ps.setInt(6, userId);
            ps.setInt(7, userId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Bug Details Updated Successfully");
            } else {
                System.out.println("You are not authorized to edit this bug.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to get bug report
    public void getBugReport() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the BugID:");
        int bugId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "SELECT bugid, filename, bugdescription, bugpriority, bugstatus, tester_id, developer_id FROM bug_entry WHERE bugid=?")) {
            ps.setInt(1, bugId);
            ResultSet rs = ps.executeQuery();

            System.out.println("bugid\tfilename\tbugdescription\tbugpriority\tbugstatus\ttester_id\tdeveloper_id");
            if (rs.next()) {
                System.out.printf("%d\t%s\t%s\t%s\t%s\t%d\t%d\n", rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), rs.getInt(7));
            } else {
                System.out.println("No bug found with the given BugID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to select bug status from predefined options
    private String selectBugStatus(Scanner scanner) {
        System.out.println("Select the Bug Status:");
        System.out.println("1. New");
        System.out.println("2. In Progress");
        System.out.println("3. Resolved");
        System.out.println("4. Closed");
        System.out.print("Enter choice: ");
        int statusChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String bugStatus;
        switch (statusChoice) {
            case 1:
                bugStatus = "New";
                break;
            case 2:
                bugStatus = "In Progress";
                break;
            case 3:
                bugStatus = "Resolved";
                break;
            case 4:
                bugStatus = "Closed";
                break;
            default:
                System.out.println("Invalid choice. Defaulting to 'New'.");
                bugStatus = "New";
        }

        return bugStatus;
    }
}
