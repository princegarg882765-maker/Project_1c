
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class User {

    static Connection con;

    public User(Connection con) {
        this.con = con;

    }

    public User() {
    }
    User u;
    int hospital_id;
    String roleName;
    int role;
    int user_id;

    Scanner sc = new Scanner(System.in);

    String checkEmail(String message) {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print(message + " (back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("back")) {
                System.out.println(" back...");
                return null;
            } else {
                if (!input.isEmpty()) {
                    try {
                        String sql = "SELECT * FROM USER WHERE email=?";
                        PreparedStatement pst = con.prepareStatement(sql);
                        pst.setString(1, input);
                        ResultSet rs = pst.executeQuery();
                        if (rs.next()) {
                            System.out.println("THIS EMAIL ALREADY ACCESS ");
                            attempts++;
                        } else {
                            if (isValidEmail(input)) {
                                return input;
                            } else {
                                System.out.println("Email format is wrong");
                                attempts++;
                            }
                        }
                    } catch (SQLException sql) {
                        System.out.println(sql);
                    }
                } else {
                    System.out.println(" Input can be empty try again.");
                    attempts++;
                }
            }
        }
        System.out.println(" Too many invalid attempts returning to previous menu...");
        return null;
    }

    boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    void register() {
        try {
            System.out.println("Enter your name :");
            String name = sc.nextLine();

            String email = checkEmail("Enter your email(Example @gmail.com)):");
            if (email == null) {
                return;
            }
            String password = getSafeString("Enter your password:");
            if (password == null) {
                return;
            }

            // java.sql.Date donationDate = java.sql.Date.valueOf(signup_date);
            String sql1 = "INSERT INTO USER(name,email,password,signup_date,role) VALUES (? , ?, ?, current_date(), ?)";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setString(1, name);
            pst1.setString(2, email);
            pst1.setString(3, password);
            pst1.setString(4, roleName);

            int rows = pst1.executeUpdate();

            if (rows > 0) {
                System.out.println(" Registration successful!");
                PreparedStatement p = con.prepareStatement("Select user_id from USER where email=?");
                p.setString(1, email);
                ResultSet r = p.executeQuery();
                if (r.next()) {
                    user_id = r.getInt("user_id");
                    System.out.println("Registeration id :" + user_id);
                } else {
                    System.out.println("Not generate the Registration id :");
                }
                this.connect();
            } else {
                System.out.println(" Registration failed!");

            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void connect() {
        try {
            switch (role) {
                case 1:

                    DonorMenu dm = new DonorMenu(con, u);
                    dm.Menu(user_id);
                    break;

                case 2:
                    Recepeints r = new Recepeints(con, u);
                    r.option(user_id);
                    break;
                case 3:
                    Main ma = new Main(con, u);
                    ma.main(hospital_id);
                    break;
                case 4:
                    AdminDashboard a = new AdminDashboard(con, u);
                    a.adminMenu();

                    break;
                default:
                    System.out.println("Invalid choice! Please select 1-4.");
            }
        } catch (Exception sql) {
            System.out.println(sql);
        }
    }

    void login() {
        try {
            String email = getSafeString("Enter your email:");
            if (email == null) {
                return;
            }
            String password = getSafeString("Enter your password:");
            if (password == null) {
                return;
            }

            String sql = "SELECT * FROM USER WHERE email=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                user_id = rs.getInt("user_id");
                System.out.println(" Login Successfully!");
                this.connect();
            } else {
                System.out.println(" Invalid email or password!");

            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void loginStaff() {
        try {
            System.out.println("--------LOGGIN-------------");
            hospital_id = getSafeInt("Enter your hospital Id");
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
            String email = getSafeString("Enter your email for staff:");
            if (email == null) {
                return;
            }
            String password = getSafeString("Enter your password:");
            if (password == null) {
                return;
            }
            String sql = "SELECT * FROM LOGINADMIN WHERE email=? AND password=? AND hospital_id =? AND rolename = 'staff'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);
            pst.setInt(3, hospital_id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println(" Login Successfully!");
                this.connect();
            } else {
                System.out.println(" Invalid hospital email or password!");
                this.loginStaff();
            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void loginAdmin() {
        try {
            System.out.println("--------LOGGIN-------------");

            String email = getSafeString("Enter your email for Admin:");
            if (email == null) {
                return;
            }

            String password = getSafeString("Enter your password:");
            if (password == null) {
                return;
            }
            String sql = "SELECT * FROM LOGINADMIN WHERE email=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println(" Login Successfully!");
                this.connect();
            } else {
                System.out.println(" Invalid email or password!");
                this.loginAdmin();
            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void loginRegister() {
        try {
            u = new User();
            boolean cond = true;
            while (cond) {
                System.out.println("SELECT LOGIN OR REGISTER");
                System.out.println("1. LOGIN");
                System.out.println("2. REGISTER");
                System.out.println("3. EXIT");

                int choice1 = getSafeInt("ENTER CHOICE NUM");
                if (choice1 == -1) {
                    return;
                }
                switch (choice1) {
                    case 1:
                        this.userMenu();
                        break;

                    case 2:
                        this.usermenu();
                        break;
                    default:
                        cond = false;
                        break;
                }
            }
        } catch (SQLException sql) {
            System.out.println(sql);
        }
    }

    void usermenu() throws SQLException {
        boolean condition = true;
        while (condition) {
            System.out.println("===BLOOD DONATION MANAGEMENT SYSTEM ===");
            System.out.println("1. Donor ");
            System.out.println("2. Recipient");
            role = getSafeInt("Enter choice num");
            if (role == -1) {
                return;
            }
            if (role == 1) {
                roleName = "Donor";
                this.register();

            } else {
                roleName = "Recipient";
                this.register();
                
            }
        }
    }

    void userMenu() {
        try {
            boolean condition = true;
            while (condition) {
                System.out.println("=== BLOOD DONATION MANAGEMENT SYSTEM ===");

                System.out.println("Select your role:");
                System.out.println("1. Donor");
                System.out.println("2. Recipient");
                System.out.println("3. Staff");
                System.out.println("4. Admin");
                role = getSafeInt("Enter choice num");
                if (role == -1) {
                    return;
                }

                switch (role) {
                    case 1:
                        this.login();
                        break;
                    case 2:
                        this.login();
                        break;
                    case 3:
                        this.loginStaff();
                        break;
                    case 4:
                        this.loginAdmin();
                        break;
                    case 5:
                        condition = false;
                        break;

                }
            }
        } catch (Exception sql) {
            System.out.println(sql);
        }
    }

    int getSafeInt(String message) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                System.out.print(message + " (back): ");
                String input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("back")) {
                    System.out.println(" back...");
                    return -1;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" Invalid number please enter again.");
                attempts++;
            }
        }
        System.out.println(" Too many invalid attempts returning to previous menu...");
        return -1;
    }

    String getSafeString(String message) {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print(message + " (back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("back")) {
                System.out.println(" back...");
                return null;
            }
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println(" Input can be empty try again.");
                attempts++;
            }
        }
        System.out.println(" Too many invalid attempts returning to previous menu...");
        return null;
    }

    Date getSafeDate(String message) {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print(message + " (yyyy-mm-dd or type back): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("back")) {
                System.out.println(" back...");
                return null;
            }
            try {
                return Date.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println(" Invalid date format please use YYYY-MM-DD.");
                attempts++;
            }
        }
        System.out.println(" Too many invalid attempts returning to previous menu...");
        return null;
    }

    public static void main(String[] args) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        User u = new User(con);
        u.loginRegister();
    }
}
