
import java.sql.*;
import java.sql.Date;
import java.util.*;
public class AdminDashboard {

    Connection con;
    Scanner sc = new Scanner(System.in);
    User u;

    public AdminDashboard(Connection con, User u) {
        this.con = con;
        this.u = u;
    }

    void logout() {
        System.out.println(" Logged out successfully!");
    }

    void manageHospitals() {
        try {
            while (true) {
                System.out.println("\n=== HOSPITAL MANAGEMENT ===");
                System.out.println("1. Add Hospital");
                System.out.println("2. View Hospitals");
                System.out.println("3. Remove Hospital");
                System.out.println("4. Add staff");
                System.out.println("5. view staff ");
                System.out.println("6. Remove staff");
                System.out.println("7. Back to Admin Dashboard");

                int ch = u.getSafeInt("Enter your choice");
                if (ch == -1 || ch == 7) {
                    return;
                }

                switch (ch) {
                    case 1:
                        addHospital();
                        break;
                    case 2:
                        viewHospitals();
                        break;
                    case 3:
                        removeHospital();
                        break;
                    case 4:
                        addStaff();
                        break;
                        case 5:
                        int hospitalid=u.getSafeInt("Enter hospital Id");
                        viewStaff(hospitalid);
                        break;
                    case 6:
                        removeStaff();
                        break;
                    default:
                        System.out.println("Invalid choice!");
                        break;
                }
            }
        } catch (Exception sql) {
            System.out.println(sql);
        }
    }

    void addHospital() {
        try {
            System.out.println("\n=== ADD NEW HOSPITAL ===");

            int id = u.getSafeInt("Enter hospital id");
            if (id == -1) {
                return;
            }

            String name = u.getSafeString("Enter Hospital Name");
            if (name == null) {
                return;
            }

            String address = u.getSafeString("Enter Address");
            if (address == null) {
                return;
            }

            String contact = u.getSafeString("Enter Contact Number");
            if (contact == null) {
                return;
            } else if (contact.length() < 10 || contact.length() > 10) {
                System.out.println("invalid number ");
                return;
            }

            String city = u.getSafeString("Enter City");
            if (city == null) {
                return;
            }

            String sql = "INSERT INTO hospital (hospital_id, hospital_name, address, contact_number, city) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, address);
            pst.setString(4, contact);
            pst.setString(5, city);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                int i = u.getSafeInt("Enter number of available blood groups (1-6)");
                if (i == -1) {
                    return;
                }

                int row = 0;
                while (i > 0) {
                    String bloodgroup = u.getSafeString("Enter blood group name");
                    if (bloodgroup == null) {
                        return;
                    }
                    try{
                    PreparedStatement pt = con.prepareStatement("INSERT INTO inventory(hospital_id, blood_group) VALUES(?,?)");
                    pt.setInt(1, id);
                    pt.setString(2, bloodgroup);
                    row = pt.executeUpdate();
                    i--;
                    }catch(SQLException s){
                        PreparedStatement p=con.prepareStatement("delete from hospital where hospital_id=?");
                        p.setInt(1, id);
                        int ro=p.executeUpdate();
                       System.out.println("Invalid blood group");
                       return;
                    }
                }
                if (row > 0) {
                    System.out.println(" Hospital added successfully!");
                }
            } else {
                System.out.println(" Failed to add hospital!");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(" Duplicate ID entered! Try again.");
            addHospital();
        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }
    }

    void viewHospitals() {
        System.out.println("\n=== LIST OF HOSPITALS ===");
        try {
            String sql = "SELECT * FROM hospital";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("hospital_id")
                        + " | Name: " + rs.getString("hospital_name")
                        + " | Address: " + rs.getString("address")
                        + " | Contact: " + rs.getString("contact_number")
                        + " | City: " + rs.getString("city"));
            }
        } catch (SQLException e) {
            System.out.println(" Error fetching hospitals: " + e.getMessage());
        }
    }

    void removeHospital() {
        System.out.println("\n=== REMOVE HOSPITAL ===");
        int id = u.getSafeInt("Enter Hospital ID to remove");
        if (id == -1) {
            return;
        }

        try {
            PreparedStatement psInv = con.prepareStatement("DELETE FROM inventory WHERE hospital_id = ?");
            psInv.setInt(1, id);
            psInv.executeUpdate();
            PreparedStatement ps1 = con.prepareStatement("DELETE FROM loginadmin WHERE hospital_id = ?");
            ps1.setInt(1, id);
            ps1.executeUpdate();
            String sql = ("DELETE FROM hospital WHERE hospital_id=?");
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println(" Hospital removed successfully!");
            } else {
                System.out.println(" Hospital not found!");
            }
        } catch (SQLException e) {
            System.out.println(" Error removing hospital: " + e.getMessage());
        }
    }
    int hospital_id;
    String email;
    String password;

    void addStaff() {
        hospital_id = u.getSafeInt("Enter hospital id ");
        if (hospital_id == -1) {
            return;
        }
        try {
            PreparedStatement pt=con.prepareStatement("Select hospital_id from hospital where hospital_id=?");
            pt.setInt(1, hospital_id);
            ResultSet rs=pt.executeQuery();
            if(!rs.next()){
               System.out.println("Hospital id not exists ");
               return;
            }
        } catch (Exception e) {
            System.out.println("Hospital not check ");
        }
        email = u.checkEmail("Enter email id");
        if (email == null) {
            return;
        }
        password = u.getSafeString("Enter password ");
        if (password == null) {
            return;
        }
        try {
            PreparedStatement pt = con.prepareStatement("Insert into loginadmin values (?,?,'staff',?)");
            pt.setString(1, email);
            pt.setString(2, password);
            pt.setInt(3, hospital_id);
            int row = pt.executeUpdate();
            if (row > 0) {
                System.out.println("Staff Add successfuly hospital id:" + hospital_id);
            }
        } catch (SQLException sql) {
            System.out.println("Hospital not exits ");
        }
    }
    void viewStaff(int hospital_id){
        System.out.println("Staff details ");
        try {
            PreparedStatement pt=con.prepareStatement("Select * from loginadmin where hospital_id=?");
            pt.setInt(1, hospital_id);
            ResultSet rs=pt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("email")+" "+rs.getString("password")+" "+rs.getString("rolename"));
            }
        } catch (SQLException sq) {
            System.out.println(sq);
    }
}
    void removeStaff() {
      
        hospital_id = u.getSafeInt("Enter hospital id");
        if (hospital_id == -1) {
            return;
        }
        try {
            PreparedStatement pt=con.prepareStatement("Select hospital_id from loginadmin where hospital_id=?");
            pt.setInt(1, hospital_id);
            ResultSet rs=pt.executeQuery();
            if(!rs.next()){
               System.out.println("Hospital id not exists for this staff");
               return;
            }
        } catch (Exception e) {
            System.out.println("Hospital staff not check  ");
        }
        email = u.getSafeString("Enter Staff email id ");
        if (email == null) {
            return;
        }
        password = u.getSafeString("Enter Staff password ");
        if (password == null) {
            return;
        }
        try {
            PreparedStatement pt = con.prepareStatement("delete from loginadmin where email=? AND password=? AND rolename=? AND hospital_id=?");
            pt.setString(1, email);
            pt.setString(2, password);
            pt.setString(3, "staff");
            pt.setInt(4, hospital_id);
            int row = pt.executeUpdate();
            if (row > 0) {
                System.out.println("Staff remove successfuly hospital id:" + hospital_id);
            }
            else{
                    System.out.println("No matching staff found Please check Email/Password/Hospital ID.");

            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void adminMenu() {
        try {
            while (true) {
                System.out.println("\n=== ADMIN DASHBOARD ===");
                System.out.println("1. View Inventory");
                System.out.println("2. Manage Hospitals");
                System.out.println("3. Check/Send Notifications");
                System.out.println("4. Logout");

                int choice = u.getSafeInt("Enter choice");
                if (choice == -1) {
                    return;
                }

                switch (choice) {
                    case 1:
                        int hospitalId = u.getSafeInt("Enter Hospital ID");
                        if (hospitalId == -1) {
                            return;
                        }
                        Inventoryservice in = new Inventoryservice(con, hospitalId, u);
                        in.viewInventory();
                        break;
                    case 2:
                        manageHospitals();
                        break;

                    case 3:
                        notification notify = new notification(4, con, u);
                        System.out.println("Enter number:\n1. Send\n2. View all notifications");
                        int num = u.getSafeInt("Enter choice");
                        if (num == -1) {
                            return;
                        }
                        if (num == 1) {
                            notify.sendNotificationFromInput(0);
                        } else if (num == 2) {
                            notify.viewAllNotifications();
                        } else {
                            System.out.println(" Invalid number!");
                        }
                        break;
                    case 4:
                        logout();
                        return;
                    default:
                        System.out.println(" Invalid option!");
                        break;
                }
            }
        } catch (Exception sql) {
            System.out.println(sql);
        }
    }
}
