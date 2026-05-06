package vue;

import DAO.AdminDAO;
import DAO.EnseignantDAO;
import DAO.EtudiantDAO;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modele.Admin;
import modele.Enseignant;
import modele.Etudiant;
import util.DBConnection;

public class LoginFrame extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    // Colors
    private final Color BG_COLOR       = new Color(15, 23, 42);
    private final Color PANEL_COLOR    = new Color(30, 41, 59);
    private final Color ACCENT_COLOR   = new Color(56, 189, 248);
    private final Color TEXT_COLOR     = new Color(226, 232, 240);
    private final Color SUBTLE_COLOR   = new Color(100, 116, 139);
    private final Color FIELD_BG       = new Color(51, 65, 85);
    private final Color ERROR_COLOR    = new Color(248, 113, 113);

    public LoginFrame() {
        setTitle("Système de Gestion de l'École");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        add(buildMainPanel(), BorderLayout.CENTER);
    }

    private JPanel buildMainPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_COLOR);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_COLOR);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(340, 420));

        // Icon / Logo area
        JLabel icon = new JLabel("🎓", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("Bienvenue", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 26));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitle = new JLabel("Connectez-vous à votre compte", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(SUBTLE_COLOR);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login field
        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLogin.setForeground(SUBTLE_COLOR);
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLogin = new JTextField();
        styleField(txtLogin);

        // Password field
        JLabel lblPwd = new JLabel("Mot de passe");
        lblPwd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPwd.setForeground(SUBTLE_COLOR);
        lblPwd.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        styleField(txtPassword);

        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(ERROR_COLOR);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        btnLogin = new JButton("Se connecter");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setBackground(ACCENT_COLOR);
        btnLogin.setForeground(new Color(15, 23, 42));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(125, 211, 252));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(ACCENT_COLOR);
            }
        });

        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());

        // Assemble
        card.add(icon);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(lblLogin);
        card.add(Box.createVerticalStrut(6));
        card.add(txtLogin);
        card.add(Box.createVerticalStrut(16));
        card.add(lblPwd);
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(8));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(btnLogin);

        outer.add(card);
        return outer;
    }

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
    }

    private void handleLogin() {
        String login = txtLogin.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (login.isEmpty() || password.isEmpty()) {
            lblError.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            // Try Admin
            AdminDAO adminDAO = new AdminDAO(conn);
            Admin admin = adminDAO.getAdminByLogin(login);
            if (admin != null && admin.getMotDePasse().equals(password)) {
                lblError.setText(" ");
                dispose();
                new MainFrame(admin).setVisible(true);
                return;
            }

            // Try Enseignant
            EnseignantDAO enseignantDAO = new EnseignantDAO(conn);
            Enseignant enseignant = enseignantDAO.getEnseignantByLogin(login);
            if (enseignant != null && enseignant.getMotDePasse().equals(password)) {
                lblError.setText(" ");
                dispose();
                new MainFrame(enseignant).setVisible(true);
                return;
            }

            // Try Etudiant
            EtudiantDAO etudiantDAO = new EtudiantDAO(conn);
            Etudiant etudiant = etudiantDAO.getEtudiantByLogin(login);
            if (etudiant != null && etudiant.getMotDePasse().equals(password)) {
                lblError.setText(" ");
                dispose();
                new MainFrame(etudiant).setVisible(true);
                return;
            }

            lblError.setText("Login ou mot de passe incorrect.");

        } catch (SQLException ex) {
            lblError.setText("Erreur de connexion à la base de données.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
