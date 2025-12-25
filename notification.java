
import java.lang.classfile.CodeBuilder;
import java.sql.*;
import java.util.Scanner;

public class notification {

    Connection con;
    int choice;
    Scanner sc = new Scanner(System.in);
    User u;

    public notification(int sender, Connection con, User u) {
        this.con = con;
        this.choice = sender;
        this.u = u;

    }
    int senderId;
    int targetId;

    public void sendNotificationFromInput(int id) {
        try {

            String targetType = "";
            String message = "";
            senderId = id;
            switch (choice) {
                case 1:
                    targetType = "Donor";
                    try {
                        PreparedStatement pt = con.prepareStatement("Select * from user where role =?");
                        pt.setString(1, targetType);
                        ResultSet rs = pt.executeQuery();
                        while (rs.next()) {
                            System.out.println(rs.getInt("user_id") + " ||" + rs.getString("role"));
                        }
                        
                    } catch (SQLException sql) {
                        System.out.println(sql);
                    }
                    message = u.getSafeString("Enter your message: ");
                    if (message == null) {
                        return;
                    }
                    targetId = u.getSafeInt("Enter Donor (User ID): ");
                    if (targetId == -1) {
                        return;
                    }

                    break;
                case 2:
                    targetType = "Admin";
                    message = u.getSafeString("Enter your message: ");
                    if (message == null) {
                        return;
                    }
                    break;
                case 3:
                    targetType = "Recipient";
                    try {
                        PreparedStatement pt = con.prepareStatement("Select * from user where role =?");
                        pt.setString(1, targetType);
                        ResultSet rs = pt.executeQuery();
                        while (rs.next()) {
                            System.out.println(rs.getInt("user_id") + " ||" + rs.getString("role"));
                        }
                       
                    } catch (SQLException sql) {
                        System.out.println(sql);
                    }
                    message = u.getSafeString("Enter your message: ");
                    if (message == null) {
                        return;
                    }
                    targetId = u.getSafeInt("Enter Recipient (User ID): ");
                    if (targetId == -1) {
                        return;
                    }

                    break;
                case 4:
                    targetType = "Staff";
                    try {
                        PreparedStatement pt = con.prepareStatement("Select * from hospital");
                        ResultSet rs = pt.executeQuery();
                        while (rs.next()) {
                            System.out.println(rs.getInt("hospital_id") + " ||" + rs.getString("hospital_name"));
                        }
                       
                    } catch (SQLException sql) {
                        System.out.println(sql);
                    }
                    message = u.getSafeString("Enter your message: ");
                    if (message == null) {
                        return;
                    }
                    targetId = u.getSafeInt("Enter Hospital ID (for Staff): ");
                    if (targetId == -1) {
                        return;
                    }

                    break;

                case 5:
                    targetType = "Staff";
                    try {
                        PreparedStatement pt = con.prepareStatement("Select * from hospital");
                        ResultSet rs = pt.executeQuery();
                        while (rs.next()) {
                            System.out.println(rs.getInt("hospital_id") + " ||" + rs.getString("hospital_name"));
                        }
                       
                    } catch (SQLException sql) {
                        System.out.println(sql);
                    }
                    message = u.getSafeString("Enter your message: ");
                    if (message == null) {
                        return;
                    }
                    targetId = u.getSafeInt("Enter Hospital ID (for Staff): ");
                    if (targetId == -1) {
                        return;
                    }

                    break;
                case 6:
                    targetType = "Staff";
                    try {
                        PreparedStatement pt = con.prepareStatement("Select * from hospital");
                        ResultSet rs = pt.executeQuery();
                        while (rs.next()) {
                            System.out.println(rs.getInt("hospital_id") + " ||" + rs.getString("hospital_name"));
                        }
                        
                    } catch (SQLException sql) {
                        System.out.println(sql);
                    }
                    message = u.getSafeString("Enter your message: ");
                    if (message == null) {
                        return;
                    }
                    targetId = u.getSafeInt("Enter Hospital ID (for Staff): ");
                    if (targetId == -1) {
                        return;
                    }

                    break;
                default:
                    System.out.println(" Invalid choice!");
                    return;
            }

            String sql = "INSERT INTO notification (user_id, message, date_sent, target_type, target_id) VALUES (?, ?, now(), ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, senderId);
            pst.setString(2, message);

            pst.setString(3, targetType);
            pst.setInt(4, targetId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println(" Notification sent successfully to " + targetType + "!");
            } else {
                System.out.println(" Failed to send notification.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewAllNotifications() {
        try {
            String sql = "SELECT notification_id, user_id, message, target_type, target_id, date_sent FROM notification ORDER BY date_sent DESC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.println("\n=== All Notifications ===");
            while (rs.next()) {
                System.out.println("Notification ID: " + rs.getInt("notification_id"));
                System.out.println("Sender ID: " + rs.getInt("user_id"));
                System.out.println("Message: " + rs.getString("message"));
                System.out.println("To: " + rs.getString("target_type") + " (ID: " + rs.getInt("target_id") + ")");
                System.out.println("Date: " + rs.getDate("date_sent"));
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    int viewnotification(int user_id,String role) {
        try {
            PreparedStatement p = con.prepareStatement("SELECT * FROM notification where target_id=? AND target_type=?");
            p.setInt(1, user_id);
            p.setString(2, role);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                System.out.println("Notification ID: " + r.getInt("notification_id"));
                System.out.println("Sender ID: " + r.getInt("user_id"));
                System.out.println("Message: " + r.getString("message"));
                System.out.println("To: " + r.getString("target_type") + " (ID: " + r.getInt("target_id") + ")");
                System.out.println("Date: " + r.getDate("date_sent"));
                System.out.println("-----------------------------");
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
        return 0;
    }

}
