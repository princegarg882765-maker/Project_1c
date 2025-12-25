
import java.sql.*;
import java.util.Scanner;

class StaffDashboard {

    Connection con;
    int sender;
    Scanner sc = new Scanner(System.in);
    User u;

    public StaffDashboard(Connection con, User u) {
        this.con = con;

        this.u = u;
    }

    public void showDashboard(int hospitalId) {
        Appointmentservice ap = new Appointmentservice(con, hospitalId, u);
        Inventoryservice inv = new Inventoryservice(con, hospitalId, u);
        bloodrequestservice br = new bloodrequestservice(con, hospitalId, u);

        while (true) {
            System.out.println("\n|----- Staff Dashboard -----|");
            System.out.println("1. Manage Blood Inventory");
            System.out.println("2. Manage Donor Appointments");
            System.out.println("3. Manage Blood Requests");
            System.out.println("4. Logout");
            System.out.println("5. check notification ");
            System.out.println("6. Manage donation drive ");

            int ch = u.getSafeInt("Enter Choice: ");
            if (ch == -1) {
                return;
            }

            switch (ch) {
                case 1:
                    inv.showMenu();
                    break;
                case 2:
                    ap.showMenu();
                    break;
                case 3:
                    br.showMenu();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                case 5:
                    System.out.println("Enter number for check notification ");
                    System.out.println("1. donor ");
                    System.out.println("2. admin ");
                    System.out.println("3. recipaints");
                    sender = u.getSafeInt("Enter choice number :");
                    notification notify = new notification(sender, con, u);
                    if (sender == -1) {
                        return;
                    }
                    int num = u.getSafeInt("Enter number :" + "\n" + "1. send" + "\n" + "2. view all notification:");
                    if (num == -1) {
                        return;
                    }
                    if (num == 1) {
                        notify.sendNotificationFromInput(hospitalId);
                    } else if (num == 2) {
                        notify.viewnotification(hospitalId,"staff");
                    } else {
                        System.out.println("Invalid number ");
                    }
                    break;
                case 6:
                    DriveManagement drive = new DriveManagement(con, u);
                    drive.manageDonationDrives(hospitalId);

                default:
                    System.out.println(" Invalid Input");
                    break;
            }
        }
    }
}
