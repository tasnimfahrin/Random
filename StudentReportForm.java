import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentReportForm extends JFrame {

    private JComboBox<String> cbStudents;
    private JLabel lblNameVal, lblGenderVal, lblCoursesVal, lblTotalPaidVal;
    private List<Integer> studentIds = new ArrayList<>();

    private final Color COLOR_BG = new Color(15, 12, 10);
    private final Color COLOR_CARD_BG = new Color(34, 26, 22);
    private final Color COLOR_CARD_BORDER = new Color(55, 43, 37);
    private final Color COLOR_TEXT_PRIMARY = new Color(245, 240, 235);
    private final Color COLOR_TEXT_MUTED = new Color(168, 153, 142);

    public StudentReportForm() {
        setTitle("Student Profile & Academic Report");
        setSize(650, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(22, 26, 22, 26));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Student Academic Report");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Summary of student enrollments and payments");
        lblSub.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblSub.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        // Dropdown selection
        JPanel selPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selPanel.setOpaque(false);
        JLabel lblSel = new JLabel("Select Student:");
        lblSel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        lblSel.setForeground(COLOR_TEXT_MUTED);

        cbStudents = new JComboBox<>();
        cbStudents.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cbStudents.setBackground(new Color(28, 22, 18));
        cbStudents.setForeground(Color.WHITE);
        cbStudents.setPreferredSize(new Dimension(220, 36));

        selPanel.add(lblSel);
        selPanel.add(cbStudents);
        centerPanel.add(selPanel, BorderLayout.NORTH);

        // Summary Card
        JPanel card = new JPanel(new GridLayout(4, 2, 10, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(COLOR_CARD_BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        lblNameVal = createValueLabel("-");
        lblGenderVal = createValueLabel("-");
        lblCoursesVal = createValueLabel("None");
        lblTotalPaidVal = createValueLabel("0.00");
        lblTotalPaidVal.setForeground(new Color(16, 185, 129));

        card.add(createTitleLabel("Student Full Name:"));
        card.add(lblNameVal);
        card.add(createTitleLabel("Gender:"));
        card.add(lblGenderVal);
        card.add(createTitleLabel("Enrolled Courses:"));
        card.add(lblCoursesVal);
        card.add(createTitleLabel("Total Tuition Paid:"));
        card.add(lblTotalPaidVal);

        centerPanel.add(card, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        cbStudents.addActionListener(e -> generateReport());
        loadStudents();
    }

    private JLabel createTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        l.setForeground(COLOR_TEXT_MUTED);
        return l;
    }

    private JLabel createValueLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        l.setForeground(COLOR_TEXT_PRIMARY);
        return l;
    }

    private void loadStudents() {
        cbStudents.removeAllItems();
        studentIds.clear();
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT student_id, name FROM students");
            while (rs.next()) {
                studentIds.add(rs.getInt("student_id"));
                cbStudents.addItem(rs.getString("name"));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void generateReport() {
        int idx = cbStudents.getSelectedIndex();
        if (idx == -1) return;
        int sId = studentIds.get(idx);

        try (Connection con = DBConnection.getConnection()) {
            Statement st = con.createStatement();
            
            // Student details
            ResultSet rs1 = st.executeQuery("SELECT name, gender FROM students WHERE student_id = " + sId);
            if (rs1.next()) {
                lblNameVal.setText(rs1.getString("name"));
                lblGenderVal.setText(rs1.getString("gender"));
            }

            // Enrolled courses count & names
            ResultSet rs2 = st.executeQuery("SELECT c.course_name FROM enrollments e JOIN courses c ON e.course_id = c.course_id WHERE e.student_id = " + sId);
            List<String> enrolled = new ArrayList<>();
            while (rs2.next()) enrolled.add(rs2.getString("course_name"));
            lblCoursesVal.setText(enrolled.isEmpty() ? "None" : String.join(", ", enrolled));

            // Total fee paid (No $ sign)
            ResultSet rs3 = st.executeQuery("SELECT SUM(amount_paid) FROM payments WHERE student_id = " + sId);
            if (rs3.next()) {
                double total = rs3.getDouble(1);
                lblTotalPaidVal.setText(String.format("%.2f", total));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}