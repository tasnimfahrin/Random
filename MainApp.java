import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainApp extends JFrame {

    private JLabel lblTotalStudents, lblTotalCourses, lblTopCourse, lblMonthlyEnroll;
    private DefaultTableModel tableModel;
    private String currentUser;
    private JTable table;
    private JPanel sidebar;
    private boolean isSidebarVisible = true;

    public MainApp() {
        this("Admin User", "Male");
    }

    public MainApp(String username, String gender) {
        this.currentUser = (username == null || username.trim().isEmpty()) ? "Admin User" : username;

        setTitle("SMS Admin - Student Management System");
        setSize(1350, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

//-----------LEFT DARK SIDEBAR-----------//
sidebar = new JPanel();
sidebar.setPreferredSize(new Dimension(240, 0));
sidebar.setBackground(new Color(24, 28, 36));
sidebar.setLayout(new BorderLayout());

//-----------Sidebar Brand-----------//
JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 28));
brandPanel.setOpaque(false);

JLabel lblBrandIcon = new JLabel(createVectorIcon("cap", 20, Color.WHITE));
JLabel lblBrand = new JLabel("SMS Admin");
lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
lblBrand.setForeground(Color.WHITE);

brandPanel.add(lblBrandIcon);
brandPanel.add(lblBrand);
sidebar.add(brandPanel, BorderLayout.NORTH);

//-----------Sidebar Menu Buttons-----------//
    JPanel menuPanel = new JPanel(new GridLayout(8, 1, 0, 8));
            menuPanel.setOpaque(false);
            menuPanel.setBorder(new EmptyBorder(10, 16, 0, 16));

    JButton btnNavDash = createCustomNavButton("Dashboard", "dash", true);
    JButton btnNavCourses = createCustomNavButton("Courses", "book", false);
    JButton btnNavStudents = createCustomNavButton("Students", "users", false);
    JButton btnNavEnroll = createCustomNavButton("Enroll to Course", "enroll", false);
    JButton btnNavRefresh = createCustomNavButton("Refresh Data", "refresh", false);
    JButton btnLogout = createCustomNavButton("Logout", "logout", false);

    menuPanel.add(btnNavDash);
    menuPanel.add(btnNavCourses);
    menuPanel.add(btnNavStudents);
    menuPanel.add(btnNavEnroll);
    menuPanel.add(btnNavRefresh);
    menuPanel.add(btnLogout);
    sidebar.add(menuPanel, BorderLayout.CENTER);

    add(sidebar, BorderLayout.WEST);

//-----------MAIN AREA (TOP NAVBAR + DASHBOARD BODY)-----------//
    JPanel mainArea = new JPanel(new BorderLayout());
    mainArea.setBackground(new Color(243, 244, 247));

//-----------TOP WHITE NAVBAR-----------//
    JPanel topNavbar = new JPanel(new BorderLayout());
    topNavbar.setPreferredSize(new Dimension(0, 65));
    topNavbar.setBackground(Color.WHITE);
    topNavbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

//-----------Hamburger Menu Button (Sidebar Toggle Active)-----------//
    JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 14));
    navLeft.setOpaque(false);
    JButton btnToggle = new JButton(createVectorIcon("menu", 18, new Color(107, 114, 128)));
    btnToggle.setPreferredSize(new Dimension(40, 36));
    btnToggle.setFocusPainted(false);
    btnToggle.setBackground(Color.WHITE);
    btnToggle.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
    btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

//-----------Click Event to Show-----------//
    btnToggle.addActionListener(e -> {
        isSidebarVisible = !isSidebarVisible;
            sidebar.setVisible(isSidebarVisible);
            revalidate();
            repaint();
        });

    navLeft.add(btnToggle);
    topNavbar.add(navLeft, BorderLayout.WEST);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 32, 18));
        navRight.setOpaque(false);
        JLabel lblUserIcon = new JLabel(createVectorIcon("user", 16, new Color(75, 85, 99)));
        JLabel lblUser = new JLabel(currentUser);
        lblUser.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        lblUser.setForeground(new Color(55, 65, 81));
        lblUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navRight.add(lblUserIcon);
        navRight.add(lblUser);
        topNavbar.add(navRight, BorderLayout.EAST);

        mainArea.add(topNavbar, BorderLayout.NORTH);

//-----------DASHBOARD CONTENT-----------//
    JPanel contentBody = new JPanel(new BorderLayout(0, 24));
    contentBody.setOpaque(false);
    contentBody.setBorder(new EmptyBorder(28, 36, 28, 36));

        JLabel lblDashboard = new JLabel("Dashboard");
        lblDashboard.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblDashboard.setForeground(new Color(17, 24, 39));
        contentBody.add(lblDashboard, BorderLayout.NORTH);

        JPanel centerSection = new JPanel(new BorderLayout(0, 24));
        centerSection.setOpaque(false);

//-----------Modern Dashboard Color Cards-----------//
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setPreferredSize(new Dimension(0, 140));

        lblTotalStudents = new JLabel("0");
        lblTotalCourses = new JLabel("0");
        lblTopCourse = new JLabel("CS101");
        lblMonthlyEnroll = new JLabel("0");

        cardsGrid.add(createDashboardCard("Total Students", lblTotalStudents, "users_lg", new Color(67, 97, 238)));
        cardsGrid.add(createDashboardCard("Total Courses", lblTotalCourses, "book_lg", new Color(32, 201, 151)));
        cardsGrid.add(createDashboardCard("Top Performing Courses", lblTopCourse, "cup_lg", new Color(0, 180, 216)));
        cardsGrid.add(createDashboardCard("Students Enrolled This Month", lblMonthlyEnroll, "enroll_lg", new Color(108, 117, 125)));

        centerSection.add(cardsGrid, BorderLayout.NORTH);

//-----------Recent Student Registrations Table with Trash Icon Delete-----------//
        JPanel tableContainer = new JPanel(new BorderLayout(0, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(233, 236, 239));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel tableTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tableTitlePanel.setOpaque(false);
        JLabel lblCalIcon = new JLabel(createVectorIcon("calendar", 16, new Color(55, 65, 81)));
        JLabel lblTableTitle = new JLabel("Recent Student Registrations");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(new Color(33, 37, 41));
        tableTitlePanel.add(lblCalIcon);
        tableTitlePanel.add(lblTableTitle);
        tableContainer.add(tableTitlePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "NAME", "COURSE", "ENROLL DATE", "DURATION", "ACTION"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(new Color(73, 80, 87));
        table.setGridColor(new Color(241, 243, 245));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(237, 242, 255));
        table.setSelectionForeground(new Color(33, 37, 41));

        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(new Color(108, 117, 125));
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new DeleteIconRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new DeleteIconEditor(new JCheckBox()));
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scroll, BorderLayout.CENTER);

        centerSection.add(tableContainer, BorderLayout.CENTER);
        contentBody.add(centerSection, BorderLayout.CENTER);

        mainArea.add(contentBody, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

//-----------Sidebar Navigation Actions-----------//
btnNavStudents.addActionListener(e -> {
    new StudentForm().setVisible(true);
            loadDashboardData();
    });
        btnNavCourses.addActionListener(e -> {
            new CourseForm().setVisible(true);
            loadDashboardData();
        });
        btnNavEnroll.addActionListener(e -> {
            new EnrollmentForm().setVisible(true);
            loadDashboardData();
        });
        btnNavRefresh.addActionListener(e -> {
            loadDashboardData();
            JOptionPane.showMessageDialog(this, "Dashboard data refreshed successfully!");
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        loadDashboardData();
    }

    class DeleteIconRenderer extends JPanel implements TableCellRenderer {
        private final JLabel iconLabel;

        public DeleteIconRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(false);
            iconLabel = new JLabel(createVectorIcon("trash", 16, new Color(239, 68, 68)));
            add(iconLabel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class DeleteIconEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton btn;
        private boolean isPushed;

        public DeleteIconEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new GridBagLayout());
            panel.setOpaque(false);

            btn = new JButton(createVectorIcon("trash", 16, new Color(239, 68, 68))) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(254, 226, 226));
                        g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setPreferredSize(new Dimension(32, 32));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> fireEditingStopped());
            panel.add(btn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            isPushed = true;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int enrollId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                    int confirm = JOptionPane.showConfirmDialog(MainApp.this,
                            "Are you sure you want to delete Enrollment #" + enrollId + "?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteEnrollment(enrollId);
                    }
                }
            }
            isPushed = false;
            return "Delete";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void deleteEnrollment(int enrollId) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, enrollId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Enrollment record deleted successfully!");
            loadDashboardData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting record: " + ex.getMessage());
        }
    }

    private JButton createCustomNavButton(String text, String iconType, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (active) {
                    g2.setColor(new Color(67, 97, 238));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(36, 42, 54));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setIcon(createVectorIcon(iconType, 16, active ? Color.WHITE : new Color(156, 163, 175)));
        btn.setIconTextGap(14);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        btn.setForeground(active ? Color.WHITE : new Color(156, 163, 175));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    btn.setForeground(Color.WHITE);
                    btn.setIcon(createVectorIcon(iconType, 16, Color.WHITE));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    btn.setForeground(new Color(156, 163, 175));
                    btn.setIcon(createVectorIcon(iconType, 16, new Color(156, 163, 175)));
                }
            }
        });

        return btn;
    }

    private JPanel createDashboardCard(String title, JLabel valueLabel, String iconType, Color cardColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            private int hoverYOffset = 0;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hoverYOffset = -4;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoverYOffset = 0;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardColor);
                g2.fill(new RoundRectangle2D.Double(0, hoverYOffset, getWidth(), getHeight() - Math.abs(hoverYOffset), 18, 18));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        lbl.setForeground(new Color(255, 255, 255, 235));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);

        textPanel.add(lbl);
        textPanel.add(valueLabel);
        card.add(textPanel, BorderLayout.WEST);

        JLabel lblIcon = new JLabel(createVectorIcon(iconType, 36, new Color(255, 255, 255, 175)));
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    private Icon createVectorIcon(String type, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f));

                switch (type) {
                    case "cap":
                        g2.fillPolygon(new int[]{x + 2, x + size / 2, x + size - 2}, new int[]{y + size / 2, y + 2, y + size / 2}, 3);
                        g2.fillRect(x + size / 4, y + size / 2, size / 2, size / 3);
                        break;
                    case "dash":
                        g2.drawRoundRect(x, y, size, size, 4, 4);
                        g2.drawLine(x + 3, y + size / 2, x + size - 3, y + size / 2);
                        g2.drawLine(x + size / 2, y + 3, x + size / 2, y + size - 3);
                        break;
                    case "book":
                    case "book_lg":
                        g2.drawRoundRect(x, y + 1, size / 2 - 1, size - 2, 2, 2);
                        g2.drawRoundRect(x + size / 2 + 1, y + 1, size / 2 - 1, size - 2, 2, 2);
                        break;
                    case "users":
                    case "users_lg":
                        g2.drawOval(x + size / 4, y + 1, size / 2, size / 2);
                        g2.drawArc(x + 1, y + size / 2, size - 2, size / 2, 0, 180);
                        break;
                    case "enroll":
                    case "enroll_lg":
                        g2.drawRoundRect(x + 2, y + 1, size - 4, size - 2, 3, 3);
                        g2.drawLine(x + 5, y + 5, x + size - 5, y + 5);
                        g2.drawLine(x + 5, y + 9, x + size - 8, y + 9);
                        break;
                    case "refresh":
                        g2.drawArc(x + 1, y + 1, size - 2, size - 2, 45, 270);
                        g2.drawLine(x + size - 2, y + 2, x + size - 5, y + 6);
                        break;
                    case "logout":
                        g2.drawArc(x + 1, y + 1, size - 2, size - 2, 90, 270);
                        g2.drawLine(x + size / 2, y + 1, x + size / 2, y + size / 2);
                        break;
                    case "menu":
                        g2.drawLine(x + 2, y + 3, x + size - 2, y + 3);
                        g2.drawLine(x + 2, y + size / 2, x + size - 2, y + size / 2);
                        g2.drawLine(x + 2, y + size - 3, x + size - 2, y + size - 3);
                        break;
                    case "user":
                        g2.drawOval(x + 2, y + 1, size - 4, size / 2);
                        g2.drawArc(x, y + size / 2, size, size / 2, 0, 180);
                        break;
                    case "cup_lg":
                        g2.drawArc(x + 2, y + 2, size - 4, size / 2, 0, -180);
                        g2.drawLine(x + size / 2, y + size / 2 + 2, x + size / 2, y + size - 3);
                        g2.drawLine(x + 3, y + size - 2, x + size - 3, y + size - 2);
                        break;
                    case "calendar":
                        g2.drawRoundRect(x, y + 2, size, size - 3, 2, 2);
                        g2.drawLine(x, y + 6, x + size, y + 6);
                        break;
                    case "trash":
                        g2.drawLine(x + 2, y + 4, x + size - 2, y + 4);
                        g2.drawLine(x + size / 3, y + 2, x + size - size / 3, y + 2);
                        g2.drawRoundRect(x + 3, y + 4, size - 6, size - 5, 3, 3);
                        g2.drawLine(x + size / 3 + 1, y + 7, x + size / 3 + 1, y + size - 4);
                        g2.drawLine(x + size - size / 3 - 1, y + 7, x + size - size / 3 - 1, y + size - 4);
                        break;
                }
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    private void animateNumber(JLabel label, int target) {
        if (target <= 0) {
            label.setText("0");
            return;
        }

        Timer timer = new Timer(25, null);
        timer.addActionListener(e -> {
            int current = Integer.parseInt(label.getText().replaceAll("[^0-9]", ""));
            if (current < target) {
                int increment = Math.max(1, (target - current) / 4);
                label.setText(String.valueOf(Math.min(current + increment, target)));
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private void loadDashboardData() {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) return;
            Statement stmt = con.createStatement();

            // Total Students
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM students");
            int totalStud = rs1.next() ? rs1.getInt(1) : 0;
            animateNumber(lblTotalStudents, totalStud);

            // Total Courses
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM courses");
            int totalCour = rs2.next() ? rs2.getInt(1) : 0;
            animateNumber(lblTotalCourses, totalCour);

            // Total Monthly Enrollments
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM enrollments");
            int totalEnr = rs3.next() ? rs3.getInt(1) : 0;
            animateNumber(lblMonthlyEnroll, totalEnr);

            // Top Performing Course Name
            ResultSet rsTop = stmt.executeQuery("SELECT course_name FROM courses LIMIT 1");
            if (rsTop.next()) {
                lblTopCourse.setText(rsTop.getString("course_name"));
            } else {
                lblTopCourse.setText("CS101");
            }

            // Table Data Loading
            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String query = "SELECT e.enrollment_id, s.name, c.course_name, e.enroll_date " +
                        "FROM enrollments e " +
                          "JOIN students s ON e.student_id = s.student_id " +
                          "JOIN courses c ON e.course_id = c.course_id " +
                          "ORDER BY e.enrollment_id DESC LIMIT 10";
            ResultSet rsTable = stmt.executeQuery(query);
            while (rsTable.next()) {
                Date d = rsTable.getDate("enroll_date");
                String formattedDate = (d != null) ? sdf.format(d) : "";

                tableModel.addRow(new Object[]{
                        rsTable.getInt("enrollment_id"),
                        rsTable.getString("name"),
                        rsTable.getString("course_name"),
                        formattedDate,
                        "6 Months",
                        "Delete"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}