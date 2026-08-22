import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbGender;
    private float cardOpacity = 0.0f;

    // Dark Palette
    private final Color COLOR_CARD_BG = new Color(28, 22, 18);
    private final Color COLOR_CARD_BORDER = new Color(55, 43, 37);
    private final Color COLOR_ACCENT = new Color(217, 119, 6);

    public LoginForm() {
        setTitle("SMS Portal - Administrator Login");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        JPanel mainBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(13, 10, 8), getWidth(), getHeight(), new Color(24, 18, 15));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainBackground.setLayout(new GridBagLayout());

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, cardOpacity));

                // 3D Drop Shadow
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fill(new RoundRectangle2D.Double(4, 6, getWidth() - 8, getHeight() - 8, 24, 24));

                // Dark Coffee Body
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 6, getHeight() - 6, 24, 24));
                g2.setColor(COLOR_CARD_BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 7, getHeight() - 7, 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(390, 560));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 34, 32, 34));

        JLabel lblIcon = new JLabel("☕", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(245, 240, 235));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Administrator Control Portal", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(168, 153, 142));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblUser.setForeground(new Color(210, 195, 185));
        txtUsername = createDarkTextField();

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblPass.setForeground(new Color(210, 195, 185));
        txtPassword = createDarkPasswordField();

        JLabel lblGen = new JLabel("Gender");
        lblGen.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        lblGen.setForeground(new Color(210, 195, 185));
        
        cmbGender = new JComboBox<>(new String[]{"Select", "Male", "Female"});
        cmbGender.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbGender.setBackground(new Color(38, 30, 25));
        cmbGender.setForeground(Color.WHITE);
        cmbGender.setFocusable(false);

        JButton btnLogin = createModernButton("Sign In", COLOR_ACCENT, new Color(180, 95, 4));
        JButton btnRegister = createModernButton("Create New Account", new Color(16, 185, 129), new Color(5, 150, 105));

        card.add(lblIcon);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblTitle);
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

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

        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(btnLogin);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(btnRegister);

        mainBackground.add(card);
        setContentPane(mainBackground);

        KeyAdapter enterKey = new KeyAdapter() {
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

    private JTextField createDarkTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(38, 30, 25));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private JPasswordField createDarkPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setBackground(new Color(38, 30, 25));
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(COLOR_ACCENT);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Username and Password.");
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
                dispose();
                new MainApp(username, gender).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void performRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String gender = cmbGender.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || gender.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO admins (username, password, gender) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, gender);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account Created Successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}