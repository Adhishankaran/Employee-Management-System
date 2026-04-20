package EmployeeCRUD;

import DBConnection.DBConnection;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCRUD {

    public void addEmployee(String id, String name, String role, String dept, int salary) throws Exception {
        runUpdate("INSERT INTO employee_details (id,name,role,department,salary) VALUES (?,?,?,?,?)",
                id, name, role, dept, salary);
    }

    public void updateEmployee(String id, String name, String role, String dept, int salary) throws Exception {
        runUpdate("UPDATE employee_details SET name=?,role=?,department=?,salary=? WHERE id=?",
                name, role, dept, salary, id);
    }

    public void deleteEmployee(String id) throws Exception {
        runUpdate("DELETE FROM employee_details WHERE id=?", id);
    }

    public void assignTask(String taskId, String employeeId, String taskName, String dueDate) throws Exception {
        runUpdate("INSERT INTO task_details (task_id,employee_id,task_name,due_date,task_status) VALUES (?,?,?,?,?)",
                taskId, employeeId, taskName, dueDate, "Pending");
    }

    public void submitTask(String taskId) throws Exception {
        runUpdate("UPDATE task_details SET task_status=? WHERE task_id=?", "Submitted", taskId);
    }

    public List<Object[]> getEmployees() throws Exception {
        return loadRows("SELECT * FROM employee_details", "id", "name", "role", "department", "salary");
    }

    public List<Object[]> getTasks() throws Exception {
        return loadRows("SELECT * FROM task_details", "task_id", "employee_id", "task_name", "due_date", "task_status");
    }

    public void downloadCsv(String fileName) throws Exception {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("ID,Name,Role,Department,Salary");
            for (Object[] row : getEmployees()) {
                out.println(row[0] + "," + row[1] + "," + row[2] + "," + row[3] + "," + row[4]);
            }
        }
    }

    private void runUpdate(String sql, Object... values) throws Exception {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            ps.executeUpdate();
        }
    }

    private List<Object[]> loadRows(String sql, String... cols) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[cols.length];
                for (int i = 0; i < cols.length; i++) {
                    row[i] = rs.getObject(cols[i]);
                }
                rows.add(row);
            }
        }
        return rows;
    }
}
