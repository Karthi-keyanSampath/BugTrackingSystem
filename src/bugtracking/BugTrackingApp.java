package bugtracking;

import java.util.Scanner;

public class BugTrackingApp {
    public static void main(String[] args) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        AdminService adminService = new AdminService(dbConnection);
        BugService bugService = new BugService(dbConnection);
        TesterService testerService = new TesterService(dbConnection, bugService);
        DeveloperService developerService = new DeveloperService(dbConnection);

        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("WELCOME TO BUG TRACKER");
            System.out.println("1. Admin Login\n2. Tester Login\n3. Developer Login\n4. Exit");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    adminService.adminLogin();
                    break;
                case 2:
                    testerService.login();
                    break;
                case 3:
                    developerService.login();
                    break;
                case 4:
                    dbConnection.closeConnection();
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid Choice");
            }
        }
    }
}
