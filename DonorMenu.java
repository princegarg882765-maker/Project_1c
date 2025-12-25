
import com.sun.jdi.Locatable;
import java.sql.*;
import java.util.*;

class DonorMenu {

    int count = 0;
    String email;
    int age;
    String bloodGroup;
    float weight;
    int hemoglobin;
    int bloodPressure;
    int timeGap;
    int id;
    int quantity;
    String location;
    Scanner sc = new Scanner(System.in);
    Connection con;
    User u;

    DonorMenu(Connection con, User u) {
        this.con = con;
        this.u = u;
    }

    void Menu(int user_id) {
       
        try {
            boolean condition = true;
            while (condition) {
                int choice = u.getSafeInt("CHOOSE AN OPTION:" + "\n" + "1. ELIGIBILTY CHECK AS DONOR" + "\n" + "2. CHECK HISTORY " + "\n" + "3. EXIT" + "\n" + "4. VIEW NOTIFICATION ");
                if (choice == -1) {
                    return;
                }
                if (choice == 1) {
                    this.information(user_id);
                } else if (choice == 2) {
                    this.history(user_id);
                } else if (choice == 3) {
                    condition = false;
                } else if (choice == 4) {
                    notification notify = new notification(6, con, u);

                    int num = u.getSafeInt("Enter number :" + "\n" + "1. send" + "\n" + "2. view all notification:");
                    if (num == -1) {
                        return;
                    }
                    if (num == 1) {
                        notify.sendNotificationFromInput(user_id);
                    } else if (num == 2) {
                        notify.viewnotification(user_id,"Donor");
                    } else {
                        System.out.println("Invalid number ");
                    }
                } else {
                    System.out.println("Invalid num");
                }

            }
        } catch (Exception sql) {
            System.out.println(sql);
        }
    }

    void information(int user_id) {
        try {
            user_id = user_id;

            age = u.getSafeInt("ENTER DONOR AGE: ");
            if (age == -1) {
                return;
            }

            weight = u.getSafeInt("ENTER DONOR WEIGHT: ");
            if (weight == -1) {
                return;
            }

            hemoglobin = u.getSafeInt("ENTER DONOR HEMOGLOBIN: ");
            if (hemoglobin == -1) {
                return;
            }

            bloodPressure = u.getSafeInt("ENTER DONOR BLOOD PRESSURE: ");
            if (bloodPressure == -1) {
                return;
            }

            bloodGroup = u.getSafeString("ENTER DONOR BLOOD GROUP");
            if (bloodGroup == null) {
                return;
            }

            location = u.getSafeString("ENTER DONOR LOCATION NAME :");
            if (location == null) {
                return;
            }

            quantity = 1;
            PreparedStatement p = con.prepareStatement("SELECT notification_message , notification_status from appointments where donor_id=?");
            p.setInt(1, user_id);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                System.out.println(r.getString("notification_message"));
                System.out.println(r.getString("notification_status"));
            }
            String sql = "INSERT INTO DONOR (user_id, last_donation_date, blood_group) VALUES (?, CURRENT_DATE(), ?)";
            String sql1 = "SELECT DATEDIFF(current_date(), last_donation_date) AS day,d.user_id AS user_id FROM DONOR d JOIN USER u ON d.user_id = u.user_id WHERE u.user_id = ?";
            PreparedStatement ps = con.prepareStatement(sql1);
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                timeGap = rs.getInt("day");
            } else {

                PreparedStatement p1 = con.prepareStatement(sql);
                p1.setInt(1, user_id);
                p1.setString(2, bloodGroup);
                p1.executeUpdate();
                timeGap = 0;
            }
            this.eligibility(user_id);
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void eligibility(int user_id) {
        try {
            count = 0;
            if (age < 18 || age > 65) {
                System.out.println(" AGE DOES NOT MATCH DONATION CRITERIA.");
                count++;
            }
            if (weight < 45) {
                System.out.println(" WEIGHT DOES NOT MATCH DONATION CRITERIA.");
                count++;
            }
            if (hemoglobin < 13) {
                System.out.println(" HEMOGLOBIN LEVEL TOO LOW FOR DONATION.");
                count++;
            }
            if (bloodPressure < 100 || bloodPressure > 140) {
                System.out.println(" BLOOD PRESSURE NOT IN DONATION RANGE .");
                count++;
            }
            if (timeGap < 60 && timeGap > 0) {
                System.out.println(" 60 DAYS NOT COMPLETED SINCE LAST DONATION.");
                count++;
            }
            if (count != 0) {
                System.out.println(" YOU ARE NOT ELIGIBLE FOR BLOOD DONATION");
                PreparedStatement stmt = con.prepareStatement(
                        "UPDATE DONOR SET eligible_status = ? WHERE user_id = ?");
                stmt.setString(1, "NOT ELIGIBLE");
                stmt.setInt(2, user_id);
                stmt.executeUpdate();

            } else {
                System.out.println("YOU ARE ELIGIBLE FOR BLOOD DONATION.");
                PreparedStatement stmt = con.prepareStatement(
                        "UPDATE DONOR SET last_donation_date = NOW(), eligible_status = ? WHERE user_id = ?");
                stmt.setString(1, "ELIGIBLE");
                stmt.setInt(2, user_id);
                stmt.executeUpdate();
            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }
    void driveCheck(){
        try{
       PreparedStatement p = con.prepareStatement("Select * from donor_drive");
            ResultSet r = p.executeQuery();
            while (r.next()) {
                System.out.println(r.getInt("drive_id") + "" + r.getString("drive_name") + "" + r.getString("location") + "" + r.getDate("drive_date"));
                 int hospital_id = u.getSafeInt("ENTER DRIVE ID :");
                if (hospital_id == -1) {
                    return;
                }
                 this.Request_apointement(hospital_id);
            }
            if(!r.next()){
                System.out.println("Drive not exists please donat the drive is exists for next time ");
            }
        }catch(SQLException sql){
            System.out.println("Drive is not check ");
        }
    }
    void Request_apointement(int hospital_id) {
        try {
            int select = u.getSafeInt("BOOK APOINTEMENT ENTER NUM" + "\n" + "1. YES " + "\n" + "2. NO ");
            if (select == -1) {
                return;
            }
            if (select == 1) {

                id = u.getSafeInt("ENTER USER ID BOOK APOINTEMENT ");
                if (id == -1) {
                    return;
                }
               
                
                PreparedStatement pr = con.prepareStatement("INSERT INTO appointments (donor_id,donor_blood_group,hospital_id,donation_quantity) VALUES (?, ?,?,?)");
                pr.setInt(1, id);    
                pr.setString(2, bloodGroup);
                pr.setInt(3, hospital_id);
                pr.setInt(4, quantity);
                pr.executeUpdate();
                System.out.println("APOINTEMENT REQUEST IS SUCCESSFULLY ");
            } else {
                System.out.println("APOINTEMENT REQUEST IS NOT SUBMIT  ");
            }
        } catch (SQLException sql) {
            System.out.println("Drive not Exists");
        }
    }

    void history(int user_id) {
        try {
            user_id = user_id;

            String donorHist = "SELECT d.user_id, d.last_donation_date,blood_group,eligible_status FROM DONOR d JOIN USER u ON d.user_id = u.user_id WHERE d.user_id = ?";
            PreparedStatement pst = con.prepareStatement(donorHist);
            pst.setInt(1, user_id);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n------ DONATION HISTORY ------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("USER ID: " + rs.getInt("user_id"));
                System.out.println("LAST DONATION DATE: " + rs.getDate("last_donation_date"));
                System.out.println("BLOOD GROUP: " + rs.getString("blood_group"));
                System.out.println("ELIGIBLITY :" + rs.getString("eligible_status"));
                System.out.println("----------------------------");
            }
            if (!found) {
                System.out.println("NO HISTORY FOUND FOR THIS ID.");
            }
        } catch (Exception sql) {
            System.out.println(sql);
        }
    }
}
