
import java.sql.*;
import java.sql.Date;
import java.util.Scanner;

public class DriveManagement {

    Connection con;
    User u;
    Scanner sc = new Scanner(System.in);

    public DriveManagement(Connection con, User u) {
        this.con = con;
        this.u = u;
    }

    void manageDonationDrives(int hospital_id) {
        while (true) {
            System.out.println("\n=== DONATION DRIVE MANAGEMENT ===");
            System.out.println("1. Create Drive");
            System.out.println("2. View Drives");
            System.out.println("3. Delete Drive");
            System.out.println("4. Back");
            int ch = u.getSafeInt("Enter your choice");

            if (ch == -1 || ch == 4) {
                return;
            }

            switch (ch) {
                case 1: {
                    String name = u.getSafeString("Enter Drive Name");
                    if (name == null) {
                        return;
                    }

                    String location = u.getSafeString("Enter Location");
                    if (location == null) {
                        return;
                    }

                    Date date = u.getSafeDate("Enter Drive Date");
                    if (date == null) {
                        return;
                    }
                    try {
                        String sql = "INSERT INTO donor_drive (drive_id,drive_name, location, drive_date) VALUES (?,?, ?, ?)";
                        PreparedStatement pst = con.prepareStatement(sql);
                        pst.setInt(1, hospital_id);
                        pst.setString(2, name);
                        pst.setString(3, location);
                        pst.setDate(4, date);
                        pst.executeUpdate();
                        System.out.println(" Drive created successfully!");
                    } catch (SQLIntegrityConstraintViolationException e) {
                        System.out.println(" Duplicate entry lease try again.");
                    } catch (SQLException e) {
                        System.out.println(" Database error: " + e.getMessage());
                    }
                }
                break;
                case 2: {
                    try {
                        String sql = "SELECT * FROM donor_drive";
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        System.out.println("\n--- Donation Drives ---");
                        while (rs.next()) {
                            System.out.println("ID: " + rs.getInt("drive_id")
                                    + " | Name: " + rs.getString("drive_name")
                                    + " | Location: " + rs.getString("location")
                                    + " | Date: " + rs.getDate("drive_date"));
                        }
                    } catch (SQLException e) {
                        System.out.println(" Error fetching drives: " + e.getMessage());
                    }
                }
                break;

                case 3: {
                    int id = u.getSafeInt("Enter Drive ID to delete");
                    if (id == -1) {
                        return;
                    }
                    try {
                        String sql = "DELETE FROM donor_drive WHERE drive_id=?";
                        PreparedStatement pst = con.prepareStatement(sql);
                        pst.setInt(1, id);
                        int rows = pst.executeUpdate();
                        if (rows > 0) {
                            System.out.println(" Drive deleted successfully!");
                        } else {
                            System.out.println(" Drive not found!");
                        }
                    } catch (SQLException e) {
                        System.out.println(" Error deleting drive: " + e.getMessage());
                    }
                }
                break;
                case 4:
                 System.out.println("back");
                 break;
                default:
                    System.out.println(" Invalid choice!");
            }
        }
    }
}
