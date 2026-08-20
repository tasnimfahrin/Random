import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbGender;
    private float cardOpacity = 0.0f;

    public LoginForm() {
        setTitle("SMS Portal - Administrator Login");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        // 1. Premium Background
        JPanel mainBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                File imgFile = new File("Images/login_bg.jpg");
                if (imgFile.exists()) {
                    ImageIcon bgIcon = new ImageIcon("Images/login_bg.jpg");
                    g2d.drawImage(bgIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                    g2d.setColor(new Color(15, 23, 42, 180));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42), getWidth(), getHeight(), new Color(30, 41, 59));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.dispose();
            }
        };
        mainBackground.setLayout(new GridBagLayout());

        // 2. Centered Card with Fade-in Animation
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cardOpacity));
                
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new RoundRectangle2D.Double(3, 3, getWidth() - 6, getHeight() - 6, 24, 24));
                
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 6, getHeight() - 6, 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(390, 560));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Header Elements
        JLabel lblIcon = new JLabel("🎓", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(17, 24, 39));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sign in to your dashboard portal", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(107, 114, 128));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblUser.setForeground(new Color(55, 65, 81));
        txtUsername = createAnimatedTextField();

        // Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblPass.setForeground(new Color(55, 65, 81));
        txtPassword = createAnimatedPasswordField();

        // Gender Field with Clean White Dropdown
        JLabel lblGen = new JLabel("Gender (For Avatar)");
        lblGen.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblGen.setForeground(new Color(55, 65, 81));
        
        cmbGender = new JComboBox<>(new String[]{"Select", "Male", "Female"});
        cmbGender.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbGender.setBackground(Color.WHITE);
        cmbGender.setForeground(new Color(55, 65, 81));
        cmbGender.setFocusable(false); // Prevents blue/gray selection box highlight
        
        // Custom Renderer to keep dropdown purely white
        cmbGender.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(5, 8, 5, 8));
                if (isSelected) {
                    lbl.setBackground(new Color(239, 246, 255)); // Very soft light blue hover
                    lbl.setForeground(new Color(37, 99, 235));
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(new Color(55, 65, 81));
                }
                return lbl;
            }
        });

        // Interactive Buttons
        JButton btnLogin = createAnimatedButton("Sign In", new Color(37, 99, 235), new Color(29, 78, 216));
        JButton btnRegister = createAnimatedButton("Create New Account", new Color(16, 185, 129), new Color(5, 150, 105));

        // Assemble Layout
        card.add(lblIcon);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblTitle);
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel userPanel = new JPanel(new BorderLayout(0, 4));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(330, 52));
        userPanel.add(lblUser, BorderLayout.NORTH);
        userPanel.add(txtUsername, BorderLayout.CENTER);
        card.add(userPanel);

        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel passPanel = new JPanel(new BorderLayout(0, 4));
        passPanel.setOpaque(false);
        passPanel.setMaximumSize(new Dimension(330, 52));
        passPanel.add(lblPass, BorderLayout.NORTH);
        passPanel.add(txtPassword, BorderLayout.CENTER);
        card.add(passPanel);

        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel genPanel = new JPanel(new BorderLayout(0, 4));
        genPanel.setOpaque(false);
        genPanel.setMaximumSize(new Dimension(330, 52));
        genPanel.add(lblGen, BorderLayout.NORTH);
        genPanel.add(cmbGender, BorderLayout.CENTER);
        card.add(genPanel);

        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(btnLogin);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(btnRegister);

        mainBackground.add(card);
        setContentPane(mainBackground);

        // Enter Key Action
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        btnLogin.addActionListener(e -> performLogin());
        btnRegister.addActionListener(e -> performRegister());

        startFadeInAnimation(card);
    }

    private void startFadeInAnimation(JPanel panel) {
        Timer fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(e -> {
            cardOpacity += 0.05f;
            if (cardOpacity >= 1.0f) {
                cardOpacity = 1.0f;
                fadeTimer.stop();
            }
            panel.repaint();
        });
        fadeTimer.start();
    }

    private JButton createAnimatedButton(String text, Color baseColor, Color hoverColor) {
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
        btn.setMaximumSize(new Dimension(330, 38));
        btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private JTextField createAnimatedTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Color normalBorder = new Color(209, 213, 219);
        Color focusedBorder = new Color(59, 130, 246);

        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(normalBorder, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(focusedBorder, 2),
                        BorderFactory.createEmptyBorder(5, 9, 5, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalBorder, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });
        return tf;
    }

    private JPasswordField createAnimatedPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Color normalBorder = new Color(209, 213, 219);
        Color focusedBorder = new Color(59, 130, 246);

        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(normalBorder, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        pf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(focusedBorder, 2),
                        BorderFactory.createEmptyBorder(5, 9, 5, 9)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                pf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalBorder, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });
        return pf;
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Username and Password.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT gender FROM admins WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String gender = rs.getString("gender");
                if (gender == null || gender.isEmpty() || gender.equals("Select")) {
                    gender = "Male";
                }

                dispose();
                new MainApp(username, gender).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + ex.getMessage());
        }
    }

    private void performRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String gender = cmbGender.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Username and Password to Register.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gender.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select Gender (Male or Female) to Register.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO admins (username, password, gender) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, gender);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Account Created Successfully! Now you can Login.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Username already exists or Database Error!", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}