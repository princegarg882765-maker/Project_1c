
import java.sql.*;
import java.util.Scanner;

class Main {

    Connection con;
    User u;

    public Main(Connection con, User u) {
        this.con = con;
        this.u = u;
    }

    void main(int hospital_id) {
        Scanner sc = new Scanner(System.in);
        if (con == null) {
            System.out.println("Application Stopped Due to DB Connection Issue!");
            return;
        }
        boolean cond = true;
        while (cond) {

            int hospitalId = hospital_id;
            StaffDashboard sd = new StaffDashboard(con, u);
            sd.showDashboard(hospitalId);
            break;

        }
    }
}
