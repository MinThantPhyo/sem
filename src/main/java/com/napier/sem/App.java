package com.napier.sem;
import com.mysql.cj.x.protobuf.MysqlxExpect;

import java.sql.*;

public class App {
    private Connection con = null;

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                Thread.sleep(30000);
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID) {
        try {
            // Get Employee
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String emp_data = "SELECT emp_no, first_name, last_name " + "FROM employees " + "WHERE emp_no = " + ID;
            // Execute SQL statement
            ResultSet r_emp_data = stmt.executeQuery(emp_data);

            // Check if there are any results before accessing data
            if (r_emp_data.next()) {
                // Title
                Statement stmt2 = con.createStatement();
                // Create string for SQL statement
                String title_data = "SELECT title " + "FROM titles " + "WHERE emp_no = " + ID;
                // Execute SQL statement
                ResultSet r_title_data = stmt2.executeQuery(title_data);

                // Salary
                // Create an SQL statement
                Statement stmt3 = con.createStatement();
                // Create string for SQL statement
                String salary_data = "SELECT salary " + "FROM salaries " + "WHERE emp_no = " + ID;
                // Execute SQL statement
                ResultSet r_salary_data = stmt3.executeQuery(salary_data);

                // Dept Name
                // Create an SQL statement
                Statement stmt4 = con.createStatement();
                // Create string for SQL statement
                String dep_no = "SELECT dept_no " + "FROM dept_emp " + "WHERE emp_no = " + ID;
                // Execute SQL statement
                ResultSet r_dep_no = stmt4.executeQuery(dep_no);

                String departmentNumber = "";
                if (r_dep_no.next()) {
                    departmentNumber = r_dep_no.getString("dept_no");
                }

                Statement stmt5 = con.createStatement();
                String dep_name = "SELECT dept_name " + "FROM departments " + "WHERE dept_no = '" + departmentNumber + "'";
                // Execute SQL statement
                ResultSet r_dep_name = stmt5.executeQuery(dep_name);

                // Manager
                String mg_name = "";
                Statement stmt6 = con.createStatement();
                String mg_query = "SELECT dm.emp_no, e.first_name, e.last_name " +
                        "FROM dept_manager dm " +
                        "JOIN employees e ON dm.emp_no = e.emp_no " +
                        "WHERE dm.dept_no = '" + departmentNumber + "'";
                ResultSet r_mg_name = stmt6.executeQuery(mg_query);

                if (r_mg_name.next()) {
                    mg_name = r_mg_name.getString("first_name") + " " + r_mg_name.getString("last_name");
                }

                Employee emp = new Employee();
                emp.emp_no = r_emp_data.getInt("emp_no");
                emp.first_name = r_emp_data.getString("first_name");
                emp.last_name = r_emp_data.getString("last_name");
                emp.title = (r_title_data.next()) ? r_title_data.getString("title") : null;
                emp.salary = (r_salary_data.next()) ? r_salary_data.getInt("salary") : 0;
                emp.dept_name = (r_dep_name.next()) ? r_dep_name.getString("dept_name") : null;
                emp.manager = mg_name;

                return emp;
            } else {
                System.out.println("No employee found with ID: " + ID);
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }



    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }

    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();
        // Get Employee
        Employee emp = a.getEmployee(255530);
        // Display results
        a.displayEmployee(emp);

        // Disconnect from database
        a.disconnect();
    }
}
