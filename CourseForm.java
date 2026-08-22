import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CourseForm extends JFrame {

    private JTextField txtCourseName, txtFee;
    private JTable table;
    private DefaultTableModel tableModel;

    private final Color COLOR_BG = new Color(15, 12, 10);
    private final Color COLOR_CARD_BG = new Color(34, 26, 22);
    private final Color COLOR_CARD_BORDER = new Color(55, 43, 37);
    private final Color COLOR_TEXT_PRIMARY = new Color(245, 240, 235);
    private final Color COLOR_TEXT_MUTED = new Color(168, 153, 142);
    private final Color COLOR_ACCENT = new Color(217, 119, 6);

    public CourseForm() {
        setTitle("Course Management Portal");
        setSize(780, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Course Catalog");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Configure courses and tuition fees");
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
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(COLOR_CARD_BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputCard.setOpaque(false);
        inputCard.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        JLabel lblCourse = new JLabel("Course Title");
        lblCourse.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblCourse.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblCourse, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtCourseName = createStyledTextField();
        inputCard.add(txtCourseName, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblFee = new JLabel("Course Fee ($)");
        lblFee.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblFee.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblFee, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtFee = createStyledTextField();
        inputCard.add(txtFee, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.3;
        JButton btnSave = createModernButton("+ Add Course", COLOR_ACCENT, new Color(180, 95, 4));
        inputCard.add(btnSave, gbc);

        centerPanel.add(inputCard, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout(0, 12)) {
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
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        tableModel = new DefaultTableModel(new String[]{"COURSE ID", "COURSE TITLE", "FEE AMOUNT"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setBackground(COLOR_CARD_BG);
        table.setForeground(COLOR_TEXT_PRIMARY);
        table.setGridColor(COLOR_CARD_BORDER);
        table.setShowVerticalLines(false);

        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(24, 18, 15));
        table.getTableHeader().setForeground(COLOR_TEXT_MUTED);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_CARD_BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_CARD_BG);
        tableCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveCourse());
        loadCourses();
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(140, 38));
        tf.setBackground(new Color(28, 22, 18));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1), new EmptyBorder(6, 12, 6, 12)));
        return tf;
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
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void saveCourse() {
        String name = txtCourseName.getText().trim();
        String feeStr = txtFee.getText().trim();
        if (name.isEmpty() || feeStr.isEmpty()) return;

        try (Connection con = DBConnection.getConnection()) {
            double fee = Double.parseDouble(feeStr);
            PreparedStatement ps = con.prepareStatement("INSERT INTO courses (course_name, fee) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setDouble(2, fee);
            ps.executeUpdate();
            txtCourseName.setText("");
            txtFee.setText("");
            loadCourses();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("course_id"), rs.getString("course_name"), String.format("%.2f", rs.getDouble("fee"))});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}