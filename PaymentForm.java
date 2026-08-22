import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentForm extends JFrame {

    private JComboBox<String> cbStudents, cbMethod;
    private JTextField txtAmount;
    private JSpinner dateSpinner;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Integer> studentIds = new ArrayList<>();

    private final Color COLOR_BG = new Color(15, 12, 10);
    private final Color COLOR_CARD_BG = new Color(34, 26, 22);
    private final Color COLOR_CARD_BORDER = new Color(55, 43, 37);
    private final Color COLOR_TEXT_PRIMARY = new Color(245, 240, 235);
    private final Color COLOR_TEXT_MUTED = new Color(168, 153, 142);
    private final Color COLOR_ACCENT = new Color(217, 119, 6);

    public PaymentForm() {
        setTitle("Fee & Payment Portal");
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

        JLabel lblTitle = new JLabel("Fee & Payment Portal");
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        lblTitle.setForeground(COLOR_TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Collect and record student tuition fees");
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
        cbStudents.setBackground(new Color(28, 22, 18));
        cbStudents.setForeground(Color.WHITE);
        cbStudents.setPreferredSize(new Dimension(160, 38));
        inputCard.add(cbStudents, gbc);

        // Amount Paid
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblAmount = new JLabel("Amount ($)");
        lblAmount.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblAmount.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblAmount, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtAmount = createStyledTextField();
        inputCard.add(txtAmount, gbc);

        // Method
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.25;
        JLabel lblMethod = new JLabel("Payment Mode");
        lblMethod.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblMethod.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblMethod, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        cbMethod = new JComboBox<>(new String[]{"Cash", "bKash", "Card", "Bank"});
        cbMethod.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cbMethod.setBackground(new Color(28, 22, 18));
        cbMethod.setForeground(Color.WHITE);
        cbMethod.setPreferredSize(new Dimension(130, 38));
        inputCard.add(cbMethod, gbc);

        // Date Picker
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel lblDate = new JLabel("Pay Date");
        lblDate.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblDate.setForeground(COLOR_TEXT_MUTED);
        inputCard.add(lblDate, gbc);

        gbc.gridx = 3; gbc.gridy = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setPreferredSize(new Dimension(120, 38));
        inputCard.add(dateSpinner, gbc);

        // Save Button
        gbc.gridx = 4; gbc.gridy = 1; gbc.weightx = 0.2;
        JButton btnPay = createModernButton("+ Pay", COLOR_ACCENT, new Color(180, 95, 4));
        inputCard.add(btnPay, gbc);

        centerPanel.add(inputCard, BorderLayout.NORTH);

        // Table
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

        JLabel lblTableHeading = new JLabel("Payment Transaction Log");
        lblTableHeading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblTableHeading.setForeground(COLOR_TEXT_PRIMARY);
        tableCard.add(lblTableHeading, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"TRANSACTION ID", "STUDENT NAME", "PAID ($)", "MODE", "PAY DATE"}, 0);
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

        loadDropdownData();
        loadPayments();

        btnPay.addActionListener(e -> savePayment());
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(100, 38));
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
        btn.setPreferredSize(new Dimension(100, 38));
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
        studentIds.clear();
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT student_id, name FROM students");
            while (rs.next()) {
                studentIds.add(rs.getInt("student_id"));
                cbStudents.addItem(rs.getString("name"));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void savePayment() {
        int idx = cbStudents.getSelectedIndex();
        String amtStr = txtAmount.getText().trim();
        if (idx == -1 || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all details.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            double amount = Double.parseDouble(amtStr);
            int studentId = studentIds.get(idx);
            String method = cbMethod.getSelectedItem().toString();
            Date d = (Date) dateSpinner.getValue();
            java.sql.Date sqlDate = new java.sql.Date(d.getTime());

            PreparedStatement ps = con.prepareStatement("INSERT INTO payments (student_id, amount_paid, payment_date, payment_method) VALUES (?, ?, ?, ?)");
            ps.setInt(1, studentId);
            ps.setDouble(2, amount);
            ps.setDate(3, sqlDate);
            ps.setString(4, method);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Payment Recorded Successfully!");
            txtAmount.setText("");
            loadPayments();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadPayments() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT p.payment_id, s.name, p.amount_paid, p.payment_method, p.payment_date " +
                         "FROM payments p JOIN students s ON p.student_id = s.student_id ORDER BY p.payment_id DESC";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                Date d = rs.getDate("payment_date");
                tableModel.addRow(new Object[]{
                    rs.getInt("payment_id"),
                    rs.getString("name"),
                    String.format("%.2f", rs.getDouble("amount_paid")),
                    rs.getString("payment_method"),
                    (d != null) ? sdf.format(d) : ""
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}