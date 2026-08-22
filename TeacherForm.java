import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeacherForm extends JFrame {

    private JTextField txtName, txtEmail, txtDept, txtPhone;
    private JTable table;
    private DefaultTableModel tableModel;

    // Dark Coffee & Cream Palette
    private final Color COLOR_BG = new Color(20, 14, 11);
    private final Color COLOR_GLASS_BG = new Color(45, 32, 25, 220);
    private final Color COLOR_GLASS_BORDER = new Color(240, 225, 210, 45);
    private final Color COLOR_TEXT_CREAM = new Color(248, 241, 233);
    private final Color COLOR_TEXT_MUTED = new Color(201, 182, 166);
    private final Color COLOR_ACCENT = new Color(224, 138, 38);

    public TeacherForm() {
        setTitle("Faculty Management Portal");
        setSize(860, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Faculty Directory");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_CREAM);

        JLabel lblSub = new JLabel("Manage faculty members and instructors");
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

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblName = new JLabel("Faculty Name");
        lblName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblName.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtName = createStyledTextField();
        inputCard.add(txtName, gbc);

        // Department
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblDept = new JLabel("Department");
        lblDept.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblDept.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblDept, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtDept = createStyledTextField();
        inputCard.add(txtDept, gbc);

        // Email
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblEmail = new JLabel("Email");
        lblEmail.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblEmail.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblEmail, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        txtEmail = createStyledTextField();
        inputCard.add(txtEmail, gbc);

        // Phone
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblPhone = new JLabel("Phone");
        lblPhone.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblPhone.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblPhone, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        txtPhone = createStyledTextField();
        inputCard.add(txtPhone, gbc);

        // Button
        gbc.gridx = 4; gbc.gridy = 1; gbc.weightx = 0.2;
        JButton btnSave = createModernButton("+ Save", COLOR_ACCENT, new Color(195, 115, 25));
        inputCard.add(btnSave, gbc);

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

        tableModel = new DefaultTableModel(new String[]{"ID", "FACULTY NAME", "DEPARTMENT", "EMAIL", "PHONE"}, 0);
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
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_GLASS_BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(45, 32, 25));
        tableCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveTeacher());
        loadTeachers();
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(110, 38));
        tf.setBackground(new Color(55, 39, 31));
        tf.setForeground(COLOR_TEXT_CREAM);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_GLASS_BORDER, 1), new EmptyBorder(6, 10, 6, 10)));
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
        btn.setPreferredSize(new Dimension(90, 38));
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void saveTeacher() {
        String name = txtName.getText().trim();
        String dept = txtDept.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Name and Department.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO teachers (name, department, email, phone) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.executeUpdate();

            txtName.setText("");
            txtDept.setText("");
            txtEmail.setText("");
            txtPhone.setText("");
            loadTeachers();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadTeachers() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM teachers");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("teacher_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}