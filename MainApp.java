import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainApp extends JFrame {

    private JLabel lblTotalStudents, lblTotalCourses, lblTopCourse, lblMonthlyEnroll;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private String currentUser;
    private JTable table;
    private JPanel sidebar;
    private boolean isSidebarVisible = true;
    private int hoveredTableRow = -1;

    // Dark Coffee & Warm Cream Palette
    private final Color COLOR_BG = new Color(20, 14, 11);
    private final Color COLOR_SIDEBAR = new Color(28, 20, 15);
    private final Color COLOR_GLASS_BG = new Color(45, 32, 25, 230);
    private final Color COLOR_GLASS_BORDER = new Color(240, 225, 210, 45);
    private final Color COLOR_TEXT_CREAM = new Color(248, 241, 233);
    private final Color COLOR_TEXT_MUTED = new Color(201, 182, 166);
    private final Color COLOR_ACCENT = new Color(224, 138, 38);

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

        // 1. SIDEBAR
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setLayout(new BorderLayout());

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 24));
        brandPanel.setOpaque(false);

        JLabel lblBrandIcon = new JLabel(createVectorIcon("cap", 22, COLOR_ACCENT));
        JLabel lblBrand = new JLabel("SMS Admin");
        lblBrand.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        lblBrand.setForeground(COLOR_TEXT_CREAM);

        brandPanel.add(lblBrandIcon);
        brandPanel.add(lblBrand);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Sidebar Menu - Compact Box Layout
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 16, 16, 16));

        JButton btnNavDash = createCustomNavButton("Dashboard", "dash", true);
        JButton btnNavCourses = createCustomNavButton("Courses", "book", false);
        JButton btnNavStudents = createCustomNavButton("Students", "users", false);
        JButton btnNavEnroll = createCustomNavButton("Enrollments", "enroll", false);
        JButton btnNavPayment = createCustomNavButton("Fee Payments", "pay", false);
        JButton btnNavTeachers = createCustomNavButton("Faculty", "user", false);
        JButton btnNavReports = createCustomNavButton("Student Report", "report", false);
        JButton btnLogout = createCustomNavButton("Logout", "logout", false);

        menuPanel.add(btnNavDash);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(btnNavCourses);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(btnNavStudents);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(btnNavEnroll);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(btnNavPayment);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(btnNavTeachers);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        menuPanel.add(btnNavReports);
        
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnLogout);

        sidebar.add(menuPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);

        // 2. MAIN AREA
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(COLOR_BG);

        // Top Navbar
        JPanel topNavbar = new JPanel(new BorderLayout());
        topNavbar.setPreferredSize(new Dimension(0, 70));
        topNavbar.setBackground(COLOR_SIDEBAR);
        topNavbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_GLASS_BORDER));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 16));
        navLeft.setOpaque(false);
        JButton btnToggle = new JButton(createVectorIcon("menu", 18, COLOR_TEXT_MUTED));
        btnToggle.setPreferredSize(new Dimension(42, 38));
        btnToggle.setFocusPainted(false);
        btnToggle.setBackground(new Color(45, 32, 25));
        btnToggle.setBorder(BorderFactory.createLineBorder(COLOR_GLASS_BORDER, 1));
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnToggle.addActionListener(e -> {
            isSidebarVisible = !isSidebarVisible;
            sidebar.setVisible(isSidebarVisible);
            revalidate();
            repaint();
        });
        navLeft.add(btnToggle);
        topNavbar.add(navLeft, BorderLayout.WEST);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 16));
        searchBox.setOpaque(false);
        JTextField txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(260, 36));
        txtSearch.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        txtSearch.setForeground(COLOR_TEXT_CREAM);
        txtSearch.setBackground(new Color(45, 32, 25));
        txtSearch.setCaretColor(COLOR_ACCENT);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_GLASS_BORDER, 1),
                new EmptyBorder(4, 12, 4, 12)
        ));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        searchBox.add(new JLabel(createVectorIcon("search", 16, COLOR_TEXT_MUTED)));
        searchBox.add(txtSearch);
        topNavbar.add(searchBox, BorderLayout.CENTER);

        // Compact Profile Badge (No gap, Sleek Glass Capsule)
        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 15));
        navRight.setOpaque(false);

        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 32, 25, 180));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.setColor(COLOR_GLASS_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        userBadge.setOpaque(false);
        userBadge.setBorder(new EmptyBorder(2, 6, 2, 12));

        // Circular Avatar with First Letter
        String initial = currentUser.isEmpty() ? "A" : currentUser.substring(0, 1).toUpperCase();
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_ACCENT);
                g2.fillOval(0, 0, 26, 26);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (26 - fm.stringWidth(initial)) / 2;
                int ty = ((26 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, tx, ty);
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(26, 26));
        avatarCircle.setOpaque(false);

        JLabel lblUser = new JLabel(currentUser);
        lblUser.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        lblUser.setForeground(COLOR_TEXT_CREAM);

        userBadge.add(avatarCircle);
        userBadge.add(lblUser);
        navRight.add(userBadge);

        topNavbar.add(navRight, BorderLayout.EAST);
        mainArea.add(topNavbar, BorderLayout.NORTH);

        // Dashboard Content Body
        JPanel contentBody = new JPanel(new BorderLayout(0, 24));
        contentBody.setOpaque(false);
        contentBody.setBorder(new EmptyBorder(26, 34, 26, 34));

        JLabel lblDashboard = new JLabel("System Overview");
        lblDashboard.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        lblDashboard.setForeground(COLOR_TEXT_CREAM);
        contentBody.add(lblDashboard, BorderLayout.NORTH);

        JPanel centerSection = new JPanel(new BorderLayout(0, 24));
        centerSection.setOpaque(false);

        // 4 Uniform Glass Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setPreferredSize(new Dimension(0, 130));

        lblTotalStudents = new JLabel("0");
        lblTotalCourses = new JLabel("0");
        lblTopCourse = new JLabel("CS101");
        lblMonthlyEnroll = new JLabel("0");

        cardsGrid.add(createGlassMetricCard("Total Students", lblTotalStudents, "users_lg", COLOR_ACCENT));
        cardsGrid.add(createGlassMetricCard("Active Courses", lblTotalCourses, "book_lg", new Color(16, 185, 129)));
        cardsGrid.add(createGlassMetricCard("Top Performance", lblTopCourse, "cup_lg", new Color(59, 130, 246)));
        cardsGrid.add(createGlassMetricCard("Monthly Enrollments", lblMonthlyEnroll, "enroll_lg", new Color(245, 158, 11)));

        centerSection.add(cardsGrid, BorderLayout.NORTH);

        // Frosted Glass Table Container
        JPanel tableContainer = new JPanel(new BorderLayout(0, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(0, 0, 0, 95));
                g2.fillRoundRect(3, 5, w - 6, h - 7, 20, 20);

                g2.setColor(COLOR_GLASS_BG);
                g2.fillRoundRect(1, 1, w - 3, h - 3, 18, 18);

                g2.setColor(COLOR_GLASS_BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel tableTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tableTitlePanel.setOpaque(false);
        JLabel lblCalIcon = new JLabel(createVectorIcon("calendar", 16, COLOR_ACCENT));
        JLabel lblTableTitle = new JLabel("Recent Student Registrations");
        lblTableTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        lblTableTitle.setForeground(COLOR_TEXT_CREAM);
        tableTitlePanel.add(lblCalIcon);
        tableTitlePanel.add(lblTableTitle);
        tableContainer.add(tableTitlePanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ENROLL ID", "STUDENT NAME", "COURSE", "ENROLL DATE", "DURATION", "ACTION"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 5; }
        };

        table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        table.setRowHeight(48);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.setOpaque(false);
        table.setGridColor(new Color(240, 225, 210, 25));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(75, 52, 40));
        table.setSelectionForeground(COLOR_TEXT_CREAM);

        // Glass Table Row Hover Listener
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoveredTableRow) {
                    hoveredTableRow = row;
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredTableRow = -1;
                table.repaint();
            }
        });

        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(36, 26, 20));
        table.getTableHeader().setForeground(COLOR_TEXT_MUTED);
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_GLASS_BORDER));

        // Animated Glass Row Renderer
        DefaultTableCellRenderer glassRowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setForeground(COLOR_TEXT_CREAM);

                if (isSelected) {
                    setBackground(new Color(85, 60, 48));
                } else if (row == hoveredTableRow) {
                    setBackground(new Color(68, 48, 38, 220));
                } else {
                    setBackground(row % 2 == 0 ? new Color(42, 30, 24, 180) : new Color(38, 27, 21, 180));
                }
                return c;
            }
        };

        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(glassRowRenderer);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new DeleteIconRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new DeleteIconEditor(new JCheckBox()));
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        tableContainer.add(scroll, BorderLayout.CENTER);

        centerSection.add(tableContainer, BorderLayout.CENTER);
        contentBody.add(centerSection, BorderLayout.CENTER);

        mainArea.add(contentBody, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Sidebar Navigation Actions
        btnNavStudents.addActionListener(e -> { new StudentForm().setVisible(true); loadDashboardData(); });
        btnNavCourses.addActionListener(e -> { new CourseForm().setVisible(true); loadDashboardData(); });
        btnNavEnroll.addActionListener(e -> { new EnrollmentForm().setVisible(true); loadDashboardData(); });
        btnNavPayment.addActionListener(e -> new PaymentForm().setVisible(true));
        btnNavTeachers.addActionListener(e -> new TeacherForm().setVisible(true));
        btnNavReports.addActionListener(e -> new StudentReportForm().setVisible(true));
        btnLogout.addActionListener(e -> { dispose(); new LoginForm().setVisible(true); });

        loadDashboardData();
    }

    // Perfectly Symmetrical, Anti-Clipped Frosted Glass Metric Cards
    private JPanel createGlassMetricCard(String title, JLabel valueLabel, String iconType, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(0, 0, 0, 75));
                g2.fillRoundRect(3, 5, w - 6, h - 7, 18, 18);

                if (isHovered) {
                    g2.setColor(new Color(58, 42, 33, 245));
                } else {
                    g2.setColor(new Color(46, 33, 26, 235));
                }
                g2.fillRoundRect(1, 1, w - 3, h - 3, 16, 16);

                if (isHovered) {
                    g2.setColor(new Color(240, 225, 210, 140));
                    g2.setStroke(new BasicStroke(1.2f));
                } else {
                    g2.setColor(COLOR_GLASS_BORDER);
                    g2.setStroke(new BasicStroke(1.0f));
                }
                g2.drawRoundRect(1, 1, w - 3, h - 3, 16, 16);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXT_MUTED);

        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        valueLabel.setForeground(COLOR_TEXT_CREAM);

        textPanel.add(lbl);
        textPanel.add(valueLabel);
        card.add(textPanel, BorderLayout.CENTER);

        JLabel lblIcon = new JLabel(createVectorIcon(iconType, 34, accentColor));
        card.add(lblIcon, BorderLayout.EAST);
        return card;
    }

    class DeleteIconRenderer extends JPanel implements TableCellRenderer {
        public DeleteIconRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(false);
            add(new JLabel(createVectorIcon("trash", 16, new Color(239, 68, 68))));
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) setBackground(new Color(85, 60, 48));
            else if (row == hoveredTableRow) setBackground(new Color(68, 48, 38, 220));
            else setBackground(row % 2 == 0 ? new Color(42, 30, 24, 180) : new Color(38, 27, 21, 180));
            return this;
        }
    }

    class DeleteIconEditor extends DefaultCellEditor {
        private final JPanel panel;
        private boolean isPushed;

        public DeleteIconEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new GridBagLayout());
            panel.setOpaque(false);

            JButton btn = new JButton(createVectorIcon("trash", 16, new Color(239, 68, 68))) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(239, 68, 68, 40));
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

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            isPushed = true;
            return panel;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    int enrollId = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
                    int confirm = JOptionPane.showConfirmDialog(MainApp.this, "Delete Enrollment ID: " + enrollId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) deleteEnrollment(enrollId);
                }
            }
            isPushed = false;
            return "Delete";
        }
        public boolean stopCellEditing() { isPushed = false; return super.stopCellEditing(); }
    }

    private void deleteEnrollment(int enrollId) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM enrollments WHERE enrollment_id = ?");
            ps.setInt(1, enrollId);
            ps.executeUpdate();
            loadDashboardData();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private JButton createCustomNavButton(String text, String iconType, boolean active) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    g2.setColor(COLOR_ACCENT);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(55, 39, 31));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setIcon(createVectorIcon(iconType, 16, active ? Color.WHITE : COLOR_TEXT_MUTED));
        btn.setIconTextGap(14);
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        btn.setForeground(active ? Color.WHITE : COLOR_TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(218, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
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
                    case "pay":
                        g2.drawRoundRect(x + 1, y + 3, size - 2, size - 6, 2, 2);
                        g2.drawLine(x + 1, y + 6, x + size - 1, y + 6);
                        break;
                    case "report":
                        g2.drawRoundRect(x + 2, y + 1, size - 4, size - 2, 2, 2);
                        g2.drawLine(x + 4, y + 4, x + size - 4, y + 4);
                        g2.drawLine(x + 4, y + 8, x + size - 4, y + 8);
                        g2.drawLine(x + 4, y + 12, x + size - 8, y + 12);
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
                    case "search":
                        g2.drawOval(x + 2, y + 2, size - 6, size - 6);
                        g2.drawLine(x + size - 5, y + size - 5, x + size - 1, y + size - 1);
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
            public int getIconWidth() { return size; }
            public int getIconHeight() { return size; }
        };
    }

    private void animateNumber(JLabel label, int target) {
        if (target <= 0) { label.setText("0"); return; }
        Timer timer = new Timer(25, null);
        timer.addActionListener(e -> {
            int current = Integer.parseInt(label.getText().replaceAll("[^0-9]", ""));
            if (current < target) {
                label.setText(String.valueOf(Math.min(current + Math.max(1, (target - current) / 4), target)));
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

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM students");
            animateNumber(lblTotalStudents, rs1.next() ? rs1.getInt(1) : 0);

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM courses");
            animateNumber(lblTotalCourses, rs2.next() ? rs2.getInt(1) : 0);

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM enrollments");
            animateNumber(lblMonthlyEnroll, rs3.next() ? rs3.getInt(1) : 0);

            ResultSet rsTop = stmt.executeQuery("SELECT course_name FROM courses LIMIT 1");
            lblTopCourse.setText(rsTop.next() ? rsTop.getString("course_name") : "CS101");

            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String query = "SELECT e.enrollment_id, s.name, c.course_name, e.enroll_date " +
                          "FROM enrollments e " +
                          "JOIN students s ON e.student_id = s.student_id " +
                          "JOIN courses c ON e.course_id = c.course_id " +
                          "ORDER BY e.enrollment_id DESC";
            ResultSet rsTable = stmt.executeQuery(query);
            while (rsTable.next()) {
                Date d = rsTable.getDate("enroll_date");
                tableModel.addRow(new Object[]{
                        rsTable.getInt("enrollment_id"),
                        rsTable.getString("name"),
                        rsTable.getString("course_name"),
                        (d != null) ? sdf.format(d) : "",
                        "6 Months",
                        "Delete"
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}