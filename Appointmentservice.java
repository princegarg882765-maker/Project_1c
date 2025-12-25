
import java.sql.*;
import java.util.Scanner;

class Appointmentservice {

    Connection con;
    Scanner sc = new Scanner(System.in);
    User u;
    int hospital_id;

    public Appointmentservice(Connection con, int hospital_id, User u) {
        this.con = con;
        this.u = u;
        this.hospital_id = hospital_id;
    }

    public void showMenu() {
        while (true) {

            int ch = u.getSafeInt("\n/---- Staff Appointment Dashboard ----/" + "\n" + "1. View Pending Appointments" + "\n" + "2. View Appointment History" + "\n" + "3. Exit" + "\n" + "Enter choice: ");
            if (ch == -1) {
                return;
            }
            switch (ch) {
                case 1:
                    pendingMenu();
                    break;
                case 2:
                    viewHistory();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid Input!");
                    break;
            }
        }
    }
    int quantity;
    String bloodGroup;

    void pendingMenu() {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM appointments WHERE status='Pending' AND hospital_id=?");
            pst.setInt(1, hospital_id);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n----- Pending Appointments -----");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("Appointement ID: " + rs.getInt("id")
                        + ", Donor: " + rs.getString("donor_name")
                        + ", Blood: " + rs.getString("donor_blood_group")
                        + ", quantity: " + rs.getInt("donation_quantity"));
                quantity = rs.getInt("donation_quantity");
                bloodGroup = rs.getString("donor_blood_group");
            }

            if (!found) {
                System.out.println("No pending appointments.");
                return;
            }
            if (bloodGroup == null || quantity == 0) {
                System.out.println("Invalid appointment data.");
                return;
            }

            Inventoryservice inv = new Inventoryservice(con, quantity, u);
            inv.viewInventory();

            int id = u.getSafeInt("Enter Appointment ID to Manage: ");
            if (id == -1) {
                return;
            }

            int opt = u.getSafeInt("1. Approve" + "\n" + "2. Reject" + "\n" + "Enter choice: ");
            if (opt == -1) {
                return;
            }
            if (opt == 1) {
                approveAppointment(id);
            } else if (opt == 2) {
                rejectAppointment(id);
            } else {
                System.out.println("Invalid option!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void approveAppointment(int id) {
        try {

            PreparedStatement pst = con.prepareStatement(
                    "UPDATE appointments SET appointment_date=current_date(), status='Approved', "
                    + "notification_message='Your blood donation request has been APPROVED!', "
                    + "notification_status='Unread'WHERE hospital_id=? AND id=?"
            );

            pst.setInt(1, hospital_id);
            pst.setInt(2, id);

            int row = pst.executeUpdate();
            if (row > 0) {
                System.out.println(" Appointment Approved. Donor Notified!");
            } else {
                System.out.println(" Invalid Appointment ID");
            }
            PreparedStatement updateInv = con.prepareStatement(
                    "UPDATE inventory SET quantity = quantity + ?, last_updated=NOW() WHERE blood_group=? AND hospital_id=?"
            );
            updateInv.setInt(1, quantity);
            updateInv.setString(2, bloodGroup);
            updateInv.setInt(3, hospital_id);
            updateInv.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void rejectAppointment(int id) {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "UPDATE appointments SET status='Rejected', "
                    + "notification_message='Your blood donation request has been REJECTED!', "
                    + "notification_status='Unread'WHERE hospital_id=? AND id=?"
            );
            pst.setInt(1, hospital_id);
            pst.setInt(2, id);

            int row = pst.executeUpdate();
            if (row > 0) {
                System.out.println(" Appointment Rejected. Donor Notified!");
            } else {
                System.out.println("Invalid Appointment ID");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void viewHistory() {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM appointments WHERE status!='Pending' AND hospital_id=?");
            pst.setInt(1, hospital_id);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n----- Appointment History -----");
            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                        "Appointment ID: " + rs.getInt("id")
                        + ", Donor: " + rs.getString("donor_name")
                        + ", Blood: " + rs.getString("donor_blood_group")
                        + ", Date: " + rs.getString("appointment_date")
                        + ", Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No history records.");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
