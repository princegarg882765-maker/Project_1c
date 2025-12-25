
import java.sql.*;
import java.util.Scanner;

public class bloodrequestservice {

    Connection con;
    Scanner sc = new Scanner(System.in);
    int hospitalId;
    User u;

    public bloodrequestservice(Connection con, int hospitalId, User u) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.u = u;
    }

    public void showMenu() {
        while (true) {

            int ch = u.getSafeInt("\n===== Blood Request Management =====" + "\n" + "1. View Pending Requests" + "\n" + "2. View Approved / Rejected History" + "\n" + "3. Back" + "\n" + "Enter Choice: ");
            if (ch == -1) {
                return;
            }
            switch (ch) {
                case 1:
                    pendingRequests();
                    break;
                case 2:
                    history();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid Choice!");
                    break;
            }
        }
    }

    void pendingRequests() {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM blood_requests WHERE status='Pending' AND hospital_id=?"
            );
            pst.setInt(1, hospitalId);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n---- Pending Blood Requests ----");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Req ID: " + rs.getInt("id")
                        + ", Patient: " + rs.getString("patient_name")
                        + ", Blood Group: " + rs.getString("blood_group")
                        + ", Units: " + rs.getInt("units_needed"));
            }
            if (!found) {
                System.out.println("No Pending Requests.");
                return;
            }
            Inventoryservice in = new Inventoryservice(con, hospitalId, u);
            System.out.println("VIEW INVENTORY ");
            in.viewInventory();

            int reqId = u.getSafeInt("Enter Request ID to Manage: ");
            if (reqId == -1) {
                return;
            }

            int op = u.getSafeInt("1. Approve & Allocate Units" + "\n" + "2. Reject" + "\n" + "Enter Option: ");
            if (op == -1) {
                return;
            }
            if (op == 1) {
                approveAndAllocate(reqId);
            } else if (op == 2) {
                reject(reqId);
            } else {
                System.out.println("Invalid Option!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void approveAndAllocate(int reqId) {
        try {

            PreparedStatement get = con.prepareStatement(
                    "SELECT blood_group, units_needed, patient_name FROM blood_requests WHERE id=?"
            );
            get.setInt(1, reqId);
            ResultSet rs = get.executeQuery();

            if (!rs.next()) {
                System.out.println("Invalid Request ID!");
                return;
            }

            String bg = rs.getString("blood_group");
            int requestedUnits = rs.getInt("units_needed");
            String patient = rs.getString("patient_name");

            PreparedStatement checkStock = con.prepareStatement(
                    "SELECT quantity FROM inventory WHERE blood_group=? AND hospital_id=?"
            );
            checkStock.setString(1, bg);
            checkStock.setInt(2, hospitalId);
            ResultSet stockRS = checkStock.executeQuery();

            if (!stockRS.next()) {
                System.out.println(" Stock Not Available!");
                return;
            }

            int currentStock = stockRS.getInt("quantity");

            if (currentStock >= requestedUnits) {
                allocateBlood(reqId, bg, requestedUnits, patient);
            } else if (currentStock > 0) {
                System.out.println(" Requested: " + requestedUnits + " units");
                System.out.println(" Available: " + currentStock + " units only");

                int choice = u.getSafeInt("Do you want to accept available units? (1=Yes, 2=No): ");
                if (choice == -1) {
                    return;
                }
                if (choice == 1) {
                    allocateBlood(reqId, bg, currentStock, patient);

                    PreparedStatement updateReq = con.prepareStatement(
                            "UPDATE blood_requests SET units_needed=?, status='Approved', updated_time=NOW() WHERE id=?"
                    );
                    updateReq.setInt(1, requestedUnits - currentStock);
                    updateReq.setInt(2, reqId);
                    updateReq.executeUpdate();

                    System.out.println(" Partial Approval Completed!");
                    System.out.println(" Notification sent to " + patient);
                } else {
                    reject(reqId);
                }
            } else {
                System.out.println(" No Stock Available!");
                reject(reqId);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void allocateBlood(int reqId, String bg, int units, String patient) {
        try {
            PreparedStatement updateInv = con.prepareStatement(
                    "UPDATE inventory SET quantity = quantity - ?, last_updated=NOW() WHERE blood_group=? AND hospital_id=?"
            );
            updateInv.setInt(1, units);
            updateInv.setString(2, bg);
            updateInv.setInt(3, hospitalId);
            updateInv.executeUpdate();

            PreparedStatement updateReq = con.prepareStatement(
                    "UPDATE blood_requests SET status='Approved', updated_time=NOW() WHERE id=?"
            );
            updateReq.setInt(1, reqId);
            updateReq.executeUpdate();

            System.out.println("Blood Allocated Successfully!");
            System.out.println(" Notification sent to Patient: " + patient);
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void reject(int reqId) {
        try {
            PreparedStatement get = con.prepareStatement(
                    "SELECT patient_name FROM blood_requests WHERE id=?"
            );
            get.setInt(1, reqId);
            ResultSet rs = get.executeQuery();
            String patient = (rs.next()) ? rs.getString("patient_name") : "Unknown";

            PreparedStatement pst = con.prepareStatement(
                    "UPDATE blood_requests SET status='Rejected', updated_time=NOW() WHERE id=?"
            );
            pst.setInt(1, reqId);

            int row = pst.executeUpdate();
            if (row > 0) {
                System.out.println(" Request Rejected!");
                System.out.println(" Notification sent to " + patient);
            } else {
                System.out.println("Invalid Request ID");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void history() {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM blood_requests WHERE status!='Pending' AND hospital_id=?"
            );
            pst.setInt(1, hospitalId);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n---- Request History ----");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Req ID: " + rs.getInt("id")
                        + ", Patient: " + rs.getString("patient_name")
                        + ", Blood: " + rs.getString("blood_group")
                        + ", Units: " + rs.getInt("units_needed")
                        + ", Status: " + rs.getString("status"));
            }
            if (!found) {
                System.out.println("No Records Found!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
