package EmployeeGUI;

import EmployeeCRUD.EmployeeCRUD;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class EmployeeGUI extends JFrame {
    JTextField id = new JTextField(), name = new JTextField(), role = new JTextField(), dept = new JTextField(), salary = new JTextField();
    JTextField taskId = new JTextField(), taskEmpId = new JTextField(), taskName = new JTextField(), taskDate = new JTextField();
    DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Role", "Department", "Salary"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    DefaultTableModel taskModel = new DefaultTableModel(new String[]{"Task ID", "Employee ID", "Task Name", "Due Date", "Status"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable table = new JTable(model), taskTable = new JTable(taskModel);
    CardLayout sideCards = new CardLayout();
    JPanel sidePanel = new JPanel(sideCards);
    EmployeeCRUD crud = new EmployeeCRUD();

    public EmployeeGUI() {
        setTitle("Employee Management System");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(238, 245, 255));
        add(header(), BorderLayout.NORTH);
        add(form(), BorderLayout.WEST);
        add(sideArea(), BorderLayout.CENTER);
        loadEmployees();
        loadTasks();
        sideCards.show(sidePanel, "blank");
    }

    JPanel header() {
        JPanel p = new JPanel();
        p.setBackground(new Color(13, 71, 161));
        JLabel l = new JLabel("Employee Details");
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        p.add(l);
        return p;
    }

    JPanel form() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setPreferredSize(new Dimension(320, 0));
        p.setBackground(new Color(227, 242, 253));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.fill = 2;
        g.weightx = 1;
        field(p, g, 0, "Employee ID", id); field(p, g, 2, "Name", name); field(p, g, 4, "Role", role);
        field(p, g, 6, "Department", dept); field(p, g, 8, "Salary", salary);
        btnRow(p, g, 10, button("Add", new Color(46, 125, 50), e -> addEmployee()), button("Update", new Color(21, 101, 192), e -> updateEmployee()));
        btnRow(p, g, 11, button("Delete", new Color(198, 40, 40), e -> deleteEmployee()), button("View Employees", new Color(251, 140, 0), e -> { loadEmployees(); sideCards.show(sidePanel, "employees"); }));
        btnRow(p, g, 12, button("Tasks", new Color(142, 36, 170), e -> { loadTasks(); sideCards.show(sidePanel, "tasks"); }), button("Clear", new Color(69, 90, 100), e -> clearEmployee()));
        g.gridx = 0; g.gridy = 13; g.gridwidth = 2;
        p.add(button("Download CSV", new Color(0, 121, 107), e -> downloadCsv()), g);
        return p;
    }

    JPanel sideArea() {
        sidePanel.setOpaque(false);
        JPanel blank = new JPanel(new BorderLayout());
        blank.setOpaque(false);
        JLabel msg = new JLabel("Click View Employees or Tasks", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.BOLD, 22));
        msg.setForeground(new Color(90, 90, 90));
        blank.add(msg, BorderLayout.CENTER);
        sidePanel.add(blank, "blank");
        sidePanel.add(records(), "employees");
        sidePanel.add(taskPanel(), "tasks");
        return sidePanel;
    }

    JPanel records() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", 0, 13));
        table.setSelectionBackground(new Color(144, 202, 249));
        table.setSelectionForeground(Color.BLACK);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer();
        hr.setHorizontalAlignment(SwingConstants.CENTER);
        hr.setBackground(new Color(13, 71, 161));
        hr.setForeground(Color.WHITE);
        hr.setOpaque(true);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) table.getColumnModel().getColumn(i).setHeaderRenderer(hr);
        table.getSelectionModel().addListSelectionListener(e -> fillEmployee());
        JLabel head = new JLabel("Employee Records");
        head.setOpaque(true);
        head.setBackground(Color.BLACK);
        head.setForeground(Color.WHITE);
        head.setFont(new Font("Segoe UI", Font.BOLD, 18));
        head.setHorizontalAlignment(SwingConstants.CENTER);
        head.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        p.add(head, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 15));
        return p;
    }

    JPanel taskPanel() {
        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setOpaque(false);
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(255, 243, 224));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.fill = 2;
        g.weightx = 1;
        field(p, g, 0, "Task ID", taskId); field(p, g, 2, "Employee ID", taskEmpId);
        field(p, g, 4, "Task Name", taskName); field(p, g, 6, "Due Date", taskDate);
        btnRow(p, g, 8, button("Assign Task", new Color(142, 36, 170), e -> assignTask()), button("View Tasks", new Color(255, 112, 67), e -> loadTasks()));
        btnRow(p, g, 9, button("Submit Task", new Color(46, 125, 50), e -> submitTask()), button("Back", new Color(96, 125, 139), e -> sideCards.show(sidePanel, "blank")));
        wrap.add(p, BorderLayout.NORTH);
        taskTable.setRowHeight(28);
        taskTable.setFont(new Font("Segoe UI", 0, 13));
        taskTable.setSelectionBackground(new Color(255, 204, 188));
        taskTable.setSelectionForeground(Color.BLACK);
        taskTable.getSelectionModel().addListSelectionListener(e -> fillTask());
        JTableHeader taskHeader = taskTable.getTableHeader();
        taskHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        taskHeader.setPreferredSize(new Dimension(taskHeader.getPreferredSize().width, 34));
        DefaultTableCellRenderer taskHr = new DefaultTableCellRenderer();
        taskHr.setHorizontalAlignment(SwingConstants.CENTER);
        taskHr.setBackground(new Color(115, 38, 0));
        taskHr.setForeground(Color.WHITE);
        taskHr.setOpaque(true);
        for (int i = 0; i < taskTable.getColumnModel().getColumnCount(); i++) taskTable.getColumnModel().getColumn(i).setHeaderRenderer(taskHr);
        JPanel list = new JPanel(new BorderLayout());
        list.setOpaque(false);
        JLabel head = new JLabel("Assigned Tasks");
        head.setOpaque(true);
        head.setBackground(new Color(60, 25, 0));
        head.setForeground(Color.WHITE);
        head.setFont(new Font("Segoe UI", Font.BOLD, 18));
        head.setHorizontalAlignment(SwingConstants.CENTER);
        head.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        list.add(head, BorderLayout.NORTH);
        list.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        wrap.add(list, BorderLayout.CENTER);
        return wrap;
    }

    void field(JPanel p, GridBagConstraints g, int y, String text, JTextField f) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(13, 71, 161));
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        f.setBackground(Color.WHITE);
        f.setForeground(Color.BLACK);
        f.setCaretColor(Color.BLACK);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(100, 181, 246), 2), BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        g.gridx = 0; g.gridy = y; p.add(l, g);
        g.gridx = 1; p.add(f, g);
        JSeparator s = new JSeparator();
        s.setForeground(new Color(144, 202, 249));
        s.setBackground(new Color(144, 202, 249));
        g.gridx = 0; g.gridy = y + 1; g.gridwidth = 2; p.add(s, g); g.gridwidth = 1;
    }

    void btnRow(JPanel p, GridBagConstraints g, int y, JButton a, JButton b) {
        g.gridx = 0; g.gridy = y; p.add(a, g);
        g.gridx = 1; p.add(b, g);
    }

    JButton button(String text, Color color, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.addActionListener(action);
        return b;
    }

    void addEmployee() {
        if (employeeBlank()) {
            msg("Fill all employee details.");
            return;
        }
        try {
            crud.addEmployee(id.getText().trim(), name.getText().trim(), role.getText().trim(), dept.getText().trim(), Integer.parseInt(salary.getText().trim()));
            msg("Employee added.");
            clearEmployee();
            loadEmployees();
        } catch (NumberFormatException e) {
            msg("Salary must be a number.");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void updateEmployee() {
        if (employeeBlank()) {
            msg("Fill all employee details.");
            return;
        }
        try {
            crud.updateEmployee(id.getText().trim(), name.getText().trim(), role.getText().trim(), dept.getText().trim(), Integer.parseInt(salary.getText().trim()));
            msg("Employee updated.");
            clearEmployee();
            loadEmployees();
        } catch (NumberFormatException e) {
            msg("Salary must be a number.");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void deleteEmployee() {
        if (id.getText().trim().isEmpty()) {
            msg("Enter employee ID.");
            return;
        }
        try {
            crud.deleteEmployee(id.getText().trim());
            msg("Employee deleted.");
            clearEmployee();
            loadEmployees();
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void assignTask() {
        if (taskId.getText().trim().isEmpty() || taskEmpId.getText().trim().isEmpty() || taskName.getText().trim().isEmpty() || taskDate.getText().trim().isEmpty()) {
            msg("Fill all task details.");
            return;
        }
        try {
            crud.assignTask(taskId.getText().trim(), taskEmpId.getText().trim(), taskName.getText().trim(), taskDate.getText().trim());
            msg("Task assigned.");
            clearTask();
            loadTasks();
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void submitTask() {
        if (taskId.getText().trim().isEmpty()) {
            msg("Select a task first.");
            return;
        }
        try {
            crud.submitTask(taskId.getText().trim());
            msg("Task submitted.");
            loadTasks();
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void loadEmployees() {
        model.setRowCount(0);
        try {
            for (Object[] row : crud.getEmployees()) model.addRow(row);
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void loadTasks() {
        taskModel.setRowCount(0);
        try {
            for (Object[] row : crud.getTasks()) taskModel.addRow(row);
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void fillEmployee() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        id.setText(model.getValueAt(r, 0).toString());
        name.setText(model.getValueAt(r, 1).toString());
        role.setText(model.getValueAt(r, 2).toString());
        dept.setText(model.getValueAt(r, 3).toString());
        salary.setText(model.getValueAt(r, 4).toString());
    }

    void fillTask() {
        int r = taskTable.getSelectedRow();
        if (r < 0) return;
        taskId.setText(taskModel.getValueAt(r, 0).toString());
        taskEmpId.setText(taskModel.getValueAt(r, 1).toString());
        taskName.setText(taskModel.getValueAt(r, 2).toString());
        taskDate.setText(taskModel.getValueAt(r, 3).toString());
    }

    boolean employeeBlank() {
        return id.getText().trim().isEmpty() || name.getText().trim().isEmpty() || role.getText().trim().isEmpty()
                || dept.getText().trim().isEmpty() || salary.getText().trim().isEmpty();
    }

    void clearEmployee() {
        id.setText(""); name.setText(""); role.setText(""); dept.setText(""); salary.setText(""); table.clearSelection();
    }

    void clearTask() {
        taskId.setText(""); taskEmpId.setText(""); taskName.setText(""); taskDate.setText(""); taskTable.clearSelection();
    }

    void downloadCsv() {
        try {
            crud.downloadCsv("employees.csv");
            msg("CSV downloaded as employees.csv");
        } catch (Exception e) {
            msg(e.getMessage());
        }
    }

    void msg(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new EmployeeGUI().setVisible(true);
        });
    }
}
