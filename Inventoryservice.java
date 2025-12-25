
import java.sql.*;
import java.util.Scanner;

    
public class Inventoryservice {

    Connection con;
    int hospitalId;
    User u;
    Scanner sc = new Scanner(System.in);

    public Inventoryservice(Connection con, int hospitalId, User u) {
        this.con = con;
        this.hospitalId = hospitalId;
        this.u = u;
    }

    public void showMenu() {
        while (true) {
            System.out.println("/---- Inventory Management ----/");
            System.out.println("1. View Inventory");
            System.out.println("2. Add New Blood group");
            System.out.println("3. Delete Blood Stock Record");
            System.out.println("4. Check Urgent Need Report");
            System.out.println("5. Back");

            int ch = u.getSafeInt("Enter choice: ");
            if (ch == -1) {
                return;
            }

            switch (ch) {
                case 1:
                    viewInventory();
                    break;
                case 2:
                    addblood();
                    break;

                case 3:
                    deleteStock();
                    break;
                case 4:
                    checkUrgentNeed();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    void viewInventory() {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM inventory WHERE hospital_id=?"
            );
            pst.setInt(1, hospitalId);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n------ Inventory List ------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                
                        "stock_id"+rs.getInt("id")
                        +", ID: " + rs.getInt("hospital_id")
                        + ", Group: " + rs.getString("blood_group")
                        + ", Qty: " + rs.getInt("quantity")
                        + ", Updated: " + rs.getString("last_update")
                        + ", Expiry: " + rs.getString("expiry_date")
                );
            }
            if (!found) {
                System.out.println("No stock available!");
                return;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void addblood() {
try {
PreparedStatement pt=con.prepareStatement("Select * from inventory where hospital_id=?");
pt.setInt(1, hospitalId);
ResultSet rs=pt.executeQuery();
while(rs.next()){
    System.out.println(rs.getInt("hospital_id")+" "+rs.getString("blood_group"));
}
        String bg = u.getSafeString("Enter Blood Group: ");
        if (bg == null) {
            return;
        }
        
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO inventory(hospital_id, blood_group) VALUES(?,?,?,NOW())"
            );
            pst.setInt(1, hospitalId);
            pst.setString(2, bg);
            
            int row = pst.executeUpdate();
            if (row > 0) {
                System.out.println(" Blood group Added Successfully !");
            } else {
                System.out.println(" Failed to Add Blood group !");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /* void updateStock() {
        
        System.out.println("Enter stock id ");
        int id= sc.nextInt();
        System.out.println("1. Increase Quantity");
        System.out.println("2. Decrease Quantity");
        System.out.print("Enter Choice: ");
        int option = sc.nextInt();
        String blood_group=null;
        int quantity = 0;
        
        String qty =null;

        String query = null;
        if (option == 1){
            qty="SELECT donation_quantity,donor_blood_group from appointments where hospital_id=? AND id=?";
            query = "UPDATE inventory SET quantity = quantity + ?, last_update =NOW() WHERE  hospital_id=? AND blood_group=?";
    }
        else if (option == 2){
            qty="SELECT donation_quantity,donor_blood_group from blood_requests where hospital_id=? AND id=?";
              query = "UPDATE inventory SET quantity = quantity - ?, last_update =NOW() WHERE hospital_id=? AND blood_group=?";
        }else {
            System.out.println("Invalid Option!");
            return;
        }

        try {
            PreparedStatement pt=con.prepareStatement(qty);
            pt.setInt(1,hospitalId);
            pt.setInt(2, id);
            ResultSet r=pt.executeQuery();
            if(r.next()){
                 quantity=r.getInt("donation_quantity");
                 blood_group=r.getString("donor_blood_group");
            }
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, quantity);
            pst.setInt(2, hospitalId);
            pst.setString(3,blood_group);

            int row = pst.executeUpdate();
            if (row > 0)
                System.out.println(" Quantity Updated!");
            else
                System.out.println(" Invalid ID or Not Authorized");

        } catch (Exception e) {
            System.out.println(e);
        }
    }*/
    void deleteStock() {
        try {
            PreparedStatement pt = con.prepareStatement("Select * from inventory where expiry_date= current_date() AND hospital_id=?");
            pt.setInt(1, hospitalId);
            ResultSet r = pt.executeQuery();
           
            while (r.next()) {
                
                System.out.println("Stcok id "+r.getInt("id") + " "+"blood group name :" + r.getString("blood_group") + " "+"expiry date :" + r.getDate("expiry_date"));
            }
            
            int id = u.getSafeInt("Enter Stock ID to Delete: ");
            if (id == -1) {
                return;
            }

            PreparedStatement pst = con.prepareStatement(
                    "DELETE FROM inventory WHERE id=? AND hospital_id=?"
            );
            pst.setInt(1, id);
            pst.setInt(2, hospitalId);

            int row = pst.executeUpdate();
            if (row > 0) {
                System.out.println(" Record Deleted!");
            } else {
                System.out.println(" Invalid ID or Not Authorized!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void checkUrgentNeed() {
        try {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT blood_group, quantity FROM inventory WHERE hospital_id=? AND quantity < 5"
            );
            pst.setInt(1, hospitalId);

            ResultSet rs = pst.executeQuery();
            boolean urgent = false;

            System.out.println("\n---- Urgent Low Stock Alerts ----");
            while (rs.next()) {
                urgent = true;
                System.out.println(" Low Stock: " + rs.getString("blood_group")
                        + " | Units Left: " + rs.getInt("quantity")
                        + " | Notify Admin Immediately!");
            }

            if (!urgent) {
                System.out.println(" All stock levels are sufficient!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
