
import com.sun.source.doctree.ReturnTree;
import java.awt.Choice;
import java.sql.*;
import java.util.*;

class Recepeints {

    int hospital;
    Connection con;
    Scanner sc = new Scanner(System.in);
    String name;
    int user_id;
    int age;
    String blood_group;
    int quantity;
    String location;
    User u;

    public Recepeints(Connection con, User u) {
        this.con = con;
        this.u = u;
    }

    void option(int user_id) {
        try {
            boolean condition = true;
            user_id = user_id;
            while (condition) {
                System.out.println("CHOOSE AN OPTION:");
                System.out.println("1. REGISTRATION");
                System.out.println("2. TRACK REQUEST");
                System.out.println("3. EXIT");
                System.out.println("4. CHECK NOTIFICATION ");
                int choose = u.getSafeInt("ENTER CHOOSE OPTION NUMBER ");
                if (choose == -1) {
                    return;
                }
                if (choose == 1) {
                    this.details(user_id);
                } else if (choose == 2) {
                    this.track(user_id);
                } else if (choose == 3) {
                    condition = false;
                } else if (choose == 4) {
                    notification notify = new notification(5, con, u);

                    int num = u.getSafeInt("Enter number :" + "\n" + "1. send" + "\n" + "2. view all notification:");
                    if (num == -1) {
                        return;
                    }
                    if (num == 1) {
                        notify.sendNotificationFromInput(user_id);
                    } else if (num == 2) {
                        notify.viewnotification(user_id,"Recipient");
                    } else {
                        System.out.println("Invalid number ");
                    }
                } else {
                    System.out.println(" invalid number ");
                }
            }
        } catch (Exception sql) {
            System.out.println(sql);
        }
    }

    void details(int user_id) {
        try {
            hospital = u.getSafeInt("ENTER HOSPITAL ID :");
            if (hospital == -1) {
                return;
            }

            user_id = user_id;
            age = u.getSafeInt("ENTER RECEPAINTS AGE ");
            if (age == -1) {
                return;
            }

            blood_group = u.getSafeString("ENTER RECEPAINTS BLOOD GROUP");
            if (blood_group == null) {
                return;
            }
            quantity = u.getSafeInt("ENTER QUNTITY OF BLOOD ");
            if (quantity == -1) {
                return;
            }

            location = u.getSafeString("ENTER LOCATION OF NEED BLOOD ");
            if (location == null) {
                return;
            }
            String query = "insert into RECEPAINTS VALUES(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, user_id);
            ps.setInt(2, age);
            ps.setString(3, blood_group);
            ps.setString(4, location);
            int row = ps.executeUpdate();
            if (row > 0) {
                try{
                System.out.println("REGISTRATION SUCCESSFULY ");
                String query1 = "insert into blood_requests(id,patient_name,blood_group,units_needed,hospital_id ) VALUES(?,?,?,?,?)";
                PreparedStatement ps1 = con.prepareStatement(query1);
                ps1.setInt(1, user_id);
                ps1.setString(2, name);
                ps1.setString(3, blood_group);
                ps1.setInt(4, quantity);
                ps1.setInt(5, hospital);

                int row1 = ps1.executeUpdate();
                if (row1 > 0) {
                    System.out.println("REQUEST SUBMITTED ");
                } else {
                    System.out.println("REQUEST NOT SUBMITTED ");

                }
            }catch(SQLException sq){
                System.out.println(sq);
            }
            } else {
                System.out.println("RESTRATION NOT SUCCESS ");

            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void track(int user_id) {
        try {
            System.out.println("TRACK REQUEST: ");
            user_id = user_id;
            PreparedStatement p = con.prepareStatement("Select * from blood_requests WHERE id=? AND status=?");
            p.setInt(1, user_id);
            p.setString(2, "Approved");
            ResultSet r = p.executeQuery();
            if (r.next()) {
                System.out.println("REQUEST ARE ACCEPTED");
            } else {
                System.out.println("WAIT FOR NOTIFICATION AND RETRY");

            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }
}
