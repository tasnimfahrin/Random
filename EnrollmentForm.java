import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EnrollmentForm extends JFrame {

    private JComboBox<String> cbStudents, cbCourses;
    private JSpinner dateSpinner;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private List<Integer> studentIds = new ArrayList<>();
    private List<Integer> courseIds = new ArrayList<>();

    // Dark Coffee & Cream Palette
    private final Color COLOR_BG = new Color(20, 14, 11);
    private final Color COLOR_GLASS_BG = new Color(45, 32, 25, 220);
    private final Color COLOR_GLASS_BORDER = new Color(240, 225, 210, 45);
    private final Color COLOR_TEXT_CREAM = new Color(248, 241, 233);
    private final Color COLOR_TEXT_MUTED = new Color(201, 182, 166);
    private final Color COLOR_ACCENT = new Color(224, 138, 38);

    public EnrollmentForm() {
        setTitle("Enrollment Management Portal");
        setSize(860, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Enrollment Management");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_CREAM);

        JLabel lblSub = new JLabel("Assign students to academic courses and record enrollments");
        lblSub.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 18));
        centerPanel.setOpaque(false);

        JPanel inputCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_GLASS_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(COLOR_GLASS_BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputCard.setOpaque(false);
        inputCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Select Student
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblStudent = new JLabel("Select Student");
        lblStudent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblStudent.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblStudent, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        cbStudents = new JComboBox<>();
        cbStudents.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cbStudents.setBackground(new Color(55, 39, 31));
        cbStudents.setForeground(COLOR_TEXT_CREAM);
        cbStudents.setPreferredSize(new Dimension(160, 38));
        inputCard.add(cbStudents, gbc);

        // Select Course
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblCourse = new JLabel("Select Course");
        lblCourse.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblCourse.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblCourse, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        cbCourses = new JComboBox<>();
        cbCourses.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cbCourses.setBackground(new Color(55, 39, 31));
        cbCourses.setForeground(COLOR_TEXT_CREAM);
        cbCourses.setPreferredSize(new Dimension(160, 38));
        inputCard.add(cbCourses, gbc);

        // Date Picker
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel lblDate = new JLabel("Enroll Date");
        lblDate.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblDate.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblDate, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(130, 38));
        inputCard.add(dateSpinner, gbc);

        // Enroll Button
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.2;
        JButton btnEnroll = createModernButton("+ Enroll", COLOR_ACCENT, new Color(195, 115, 25));
        inputCard.add(btnEnroll, gbc);

        centerPanel.add(inputCard, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_GLASS_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(COLOR_GLASS_BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTableHeading = new JLabel("Active Student Enrollments");
        lblTableHeading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblTableHeading.setForeground(COLOR_TEXT_CREAM);
        tableCard.add(lblTableHeading, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ENROLL ID", "STUDENT NAME", "COURSE NAME", "DATE ENROLLED"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setBackground(new Color(45, 32, 25));
        table.setForeground(COLOR_TEXT_CREAM);
        table.setGridColor(COLOR_GLASS_BORDER);
        table.setShowVerticalLines(false);

        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(28, 20, 15));
        table.getTableHeader().setForeground(COLOR_TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_GLASS_BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(45, 32, 25));
        tableCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        loadDropdownData();
        loadEnrollments();

        btnEnroll.addActionListener(e -> saveEnrollment());
    }

    private JButton createModernButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hoverColor : baseColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDropdownData() {
        cbStudents.removeAllItems();
        cbCourses.removeAllItems();
        studentIds.clear();
        courseIds.clear();

        try (Connection con = DBConnection.getConnection()) {
            Statement st = con.createStatement();
            
            ResultSet rs1 = st.executeQuery("SELECT student_id, name FROM students");
            while (rs1.next()) {
                studentIds.add(rs1.getInt("student_id"));
                cbStudents.addItem(rs1.getString("name"));
            }

            ResultSet rs2 = st.executeQuery("SELECT course_id, course_name FROM courses");
            while (rs2.next()) {
                courseIds.add(rs2.getInt("course_id"));
                cbCourses.addItem(rs2.getString("course_name"));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void saveEnrollment() {
        int selectedStudentIdx = cbStudents.getSelectedIndex();
        int selectedCourseIdx = cbCourses.getSelectedIndex();

        if (selectedStudentIdx == -1 || selectedCourseIdx == -1) {
            JOptionPane.showMessageDialog(this, "Please select both Student and Course.");
            return;
        }

        int studentId = studentIds.get(selectedStudentIdx);
        int courseId = courseIds.get(selectedCourseIdx);
        
        Date selectedDate = (Date) dateSpinner.getValue();
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO enrollments (student_id, course_id, enroll_date) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setDate(3, sqlDate);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Enrolled successfully!");
            loadEnrollments();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadEnrollments() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT e.enrollment_id, s.name, c.course_name, e.enroll_date " +
                         "FROM enrollments e " +
                         "JOIN students s ON e.student_id = s.student_id " +
                         "JOIN courses c ON e.course_id = c.course_id " +
                         "ORDER BY e.enrollment_id DESC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Date d = rs.getDate("enroll_date");
                tableModel.addRow(new Object[]{
                    rs.getInt("enrollment_id"),
                    rs.getString("name"),
                    rs.getString("course_name"),
                    (d != null) ? sdf.format(d) : ""
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}