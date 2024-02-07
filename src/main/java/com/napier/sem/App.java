package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {
    private Connection con = null;

    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " +                                  Integer.toString(i));
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


//
//    //Get Salary from ID
//    public Employee getSalary(int ID)
//    {
//        try
//        {
//            // Create an SQL statement
//            Statement stmt = con.createStatement();
//            // Create string for SQL statement
//            String strSelect =
//                    "SELECT salary "
//                            + "FROM salaries "
//                            + "WHERE emp_no = " + ID;
//            // Execute SQL statement
//            ResultSet rset = stmt.executeQuery(strSelect);
//            if (rset.next())
//            {
//                Employee emp = new Employee();
//                emp.salary = rset.getInt("salary");
//                return emp;
//            }
//            else
//                return null;
//
//
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//            System.out.println("Failed to get employee details");
//            return null;
//        }
//    }
//
//    //Get Titles from ID
//    public Employee getTitle(int ID)
//    {
//        try
//        {
//            // Create an SQL statement
//            Statement stmt = con.createStatement();
//            // Create string for SQL statement
//            String strSelect =
//                    "SELECT title "
//                            + "FROM titles "
//                            + "WHERE emp_no = " + ID;
//            // Execute SQL statement
//            ResultSet rset = stmt.executeQuery(strSelect);
//            if (rset.next())
//            {
//                Employee emp = new Employee();
//                emp.title = rset.getString("title");
//                return emp;
//            }
//            else
//                return null;
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//            System.out.println("Failed to get employee details");
//            return null;
//        }
//    }
//
//    //Get Dept Name from ID
//    public Employee getDeptName(int ID)
//    {
//        try
//        {
//            // Create an SQL statement
//            Statement stmt = con.createStatement();
//            // Create string for SQL statement
//            String strSelect1 =
//                    "SELECT dept_no "
//                            + "FROM dept_emp "
//                            + "WHERE emp_no = " + ID;
//            // Execute SQL statement
//            ResultSet rset1 = stmt.executeQuery(strSelect1);
//
//            String strSelect2 =
//                    "SELECT dept_name "
//                            + "FROM departments "
//                            + "WHERE dept_no = " + rset1;
//
//            ResultSet rset2 = stmt.executeQuery(strSelect2);
//            if (rset2.next())
//            {
//                Employee emp = new Employee();
//                emp.dept_name = rset2.getString("dept_name");
//                return emp;
//            }
//            else
//                return null;
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//            System.out.println("Failed to get employee details");
//            return null;
//        }
//    }
//
//    //Get Manager from ID
//    public Employee getManager(int ID)
//    {
//        try
//        {
//            // Create an SQL statement
//            Statement stmt = con.createStatement();
//            // Create string for SQL statement
//            String strSelect1 =
//                    "SELECT dept_no "
//                            + "FROM dept_emp "
//                            + "WHERE emp_no = " + ID;
//            // Execute SQL statement
//            ResultSet rset1 = stmt.executeQuery(strSelect1);
//
//            String strSelect2 =
//                    "SELECT emp_no "
//                            + "FROM dept_manager "
//                            + "WHERE dept_no = " + rset1;
//            // Execute SQL statement
//            ResultSet rset2 = stmt.executeQuery(strSelect2);
//
//            String strSelect3 =
//                    "SELECT first_name, last_name "
//                            + "FROM employees "
//                            + "WHERE emp_no = " + rset2;
//            // Execute SQL statement
//            ResultSet rset3 = stmt.executeQuery(strSelect3);
//
//            // Return new employee if valid.
//            // Check one is returned
//            if (rset3.next())
//            {
//                Employee emp = new Employee();
//                emp.first_name = rset3.getString("first_name");
//                emp.last_name = rset3.getString("last_name");
//                return emp;
//            }
//            else
//                return null;
//        }
//        catch (Exception e)
//        {
//            System.out.println(e.getMessage());
//            System.out.println("Failed to get employee details");
//            return null;
//        }
//    }
//

    /**
     * Gets all the current employees and salaries.
     *
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries() {
        try {
            // Create an SQL statement
            Statement stmt_ = con.createStatement();
            // Create string for SQL statement
            String strSelect_ =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt_.executeQuery(strSelect_);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }


    /**
     * Gets all the current employees and salaries by role
     *
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getSalariesByRole() {
        try {
            // Create an SQL statement
            Statement stmt_ = con.createStatement();
            // Create string for SQL statement
            String strSelect_ =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries, titles " +
                            "WHERE employees.emp_no = salaries.emp_no " +
                            "AND employees.emp_no = titles.emp_no " +
                            "AND salaries.to_date = '9999-01-01' " +
                            "AND titles.to_date = '9999-01-01' " +
                            "AND titles.title = 'Engineer' " +
                            "ORDER BY employees.emp_no ASC ";
            // Execute SQL statement
            ResultSet rset = stmt_.executeQuery(strSelect_);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details by role");
            return null;
        }
    }

    public ArrayList<Employee> getDepartment(String dept_name) {
        try {
            // Create an SQL statement
            Statement stmt_ = con.createStatement();
            // Create string for SQL statement
            String strSelect_ =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, departments, salaries, dept_emp "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = dept_emp.emp_no "
                            + "AND dept_emp.dept_no = departments.dept_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND departments.dept_name = '" + dept_name + "'"
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt_.executeQuery(strSelect_);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public ArrayList<Employee> getSalariesByDepartment(Department dept){
        try {
            // Create an SQL statement
            Statement stmt_ = con.createStatement();
            // Create string for SQL statement
            String strSelect_ =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries, dept_emp, departments " +
                            "WHERE employees.emp_no = salaries.emp_no " +
                            "AND employees.emp_no = dept_emp.emp_no " +
                            "AND dept_emp.dept_no = departments.dept_no " +
                            "AND salaries.to_date = '9999-01-01' " +
                            "AND departments.dept_no = 'Sales' " +
                            "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt_.executeQuery(strSelect_);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details by role");
            return null;
        }
    }

    /**
     * Prints a list of employees.
     *
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    public void displayEmployee(Employee emp) {
        // Check employees is not null
        if (emp == null)
        {
            System.out.println("No employees to display");
            return;
        }
        if (emp != null) {
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

    public void addEmployee(Employee emp)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strUpdate =
                    "INSERT INTO employees (emp_no, first_name, last_name, birth_date, gender, hire_date) " +
                            "VALUES (" + emp.emp_no + ", '" + emp.first_name + "', '" + emp.last_name + "', " +
                            "'9999-01-01', 'M', '9999-01-01')";
            stmt.execute(strUpdate);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to add employee");
        }
    }

    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        if(args.length < 1){
            a.connect("localhost:33060", 30000);
        }else{
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        // Get Employee by ID
//        Employee emp = a.getEmployee(255530);
//        // Display results
//        a.displayEmployee(emp);
//
        // Get all employees' salaries
        ArrayList<Employee> employees = a.getSalariesByRole();
        // Test the size of the returned data - should be 240124
        System.out.println(employees.size());
        a.printSalaries(employees);

//        // Get salaries by department name
//        ArrayList<Employee> allSalary = a.getDepartment("Sales");
//        a.printSalaries(allSalary);

        // Disconnect from database
        a.disconnect();
    }
}