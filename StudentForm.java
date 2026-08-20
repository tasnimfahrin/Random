import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StudentForm extends JFrame {

    private JTextField txtName;
    private JRadioButton rbMale, rbFemale, rbOther;
    private ButtonGroup bgGender;
    private JTable table;
    private DefaultTableModel tableModel;

    public StudentForm() {
        setTitle("Student Registration Portal");
        setSize(780, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(243, 244, 247));
        mainPanel.setBorder(new EmptyBorder(22, 26, 22, 26));

        // Header Title
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Student Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(17, 24, 39));

        JLabel lblSub = new JLabel("Register new students and manage student records");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(107, 114, 128));

        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 18));
        centerPanel.setOpaque(false);

        // 1. Input Form Card
        JPanel inputCard = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(229, 231, 235));
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

        // Student Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        JLabel lblName = new JLabel("Student Full Name");
        lblName.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblName.setForeground(new Color(55, 65, 81));
        inputCard.add(lblName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        txtName = createStyledTextField();
        inputCard.add(txtName, gbc);

        // Gender (Radio Group)
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35;
        JLabel lblGender = new JLabel("Gender");
        lblGender.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblGender.setForeground(new Color(55, 65, 81));
        inputCard.add(lblGender, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JPanel genderBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        genderBox.setOpaque(false);
        rbMale = new JRadioButton("MALE", true);
        rbFemale = new JRadioButton("FEMALE");
        rbOther = new JRadioButton("OTHER");
        rbMale.setOpaque(false);
        rbFemale.setOpaque(false);
        rbOther.setOpaque(false);
        rbMale.setFocusPainted(false);
        rbFemale.setFocusPainted(false);
        rbOther.setFocusPainted(false);
        rbMale.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rbFemale.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rbOther.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        bgGender = new ButtonGroup();
        bgGender.add(rbMale);
        bgGender.add(rbFemale);
        bgGender.add(rbOther);

        genderBox.add(rbMale);
        genderBox.add(rbFemale);
        genderBox.add(rbOther);
        inputCard.add(genderBox, gbc);

        // Save Button (Royal Blue Theme)
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.25;
        JButton btnSave = createModernButton("+ Save Student", new Color(67, 97, 238), new Color(47, 77, 218));
        inputCard.add(btnSave, gbc);

        centerPanel.add(inputCard, BorderLayout.NORTH);

        // 2. Table Card
        JPanel tableCard = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(229, 231, 235));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel lblTableHeading = new JLabel("Registered Student Roster");
        lblTableHeading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableHeading.setForeground(new Color(31, 41, 55));
        tableCard.add(lblTableHeading, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"STUDENT ID", "STUDENT NAME", "GENDER"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(new Color(55, 65, 81));
        table.setGridColor(new Color(243, 244, 246));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(237, 242, 255));
        table.setSelectionForeground(new Color(17, 24, 39));

        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(new Color(107, 114, 128));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(scroll, BorderLayout.CENTER);

        centerPanel.add(tableCard, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        btnSave.addActionListener(e -> saveStudent());
        loadStudents();
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(140, 38));
        Color normalBorder = new Color(209, 213, 219);
        Color focusBorder = new Color(67, 97, 238);

        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(normalBorder, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(focusBorder, 2),
                        new EmptyBorder(5, 11, 5, 11)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalBorder, 1),
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }
        });
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
        btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void saveStudent() {
        String name = txtName.getText().trim();
        Gender gender = rbMale.isSelected() ? Gender.MALE : (rbFemale.isSelected() ? Gender.FEMALE : Gender.OTHER);

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter student name.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO students (name, gender) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, gender.name());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student added successfully!");
            txtName.setText("");
            loadStudents();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM students");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("gender")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}