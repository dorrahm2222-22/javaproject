package vue;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modele.*;

public class MainFrame extends JFrame {

    private final Utilisateur currentUser;

    // Colors
    private final Color BG_COLOR       = new Color(15, 23, 42);
    private final Color SIDEBAR_COLOR  = new Color(22, 33, 55);
    private final Color ACCENT_COLOR   = new Color(56, 189, 248);
    private final Color TEXT_COLOR     = new Color(226, 232, 240);
    private final Color SUBTLE_COLOR   = new Color(100, 116, 139);
    private final Color HOVER_COLOR    = new Color(30, 41, 59);
    private final Color ACTIVE_COLOR   = new Color(30, 58, 90);

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel lblPageTitle;

    public MainFrame(Utilisateur user) {
        this.currentUser = user;
        setTitle("Système de Gestion de l'École");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        // Show first panel by default
        showDefaultPanel();
    }

    // ─── SIDEBAR ────────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(SIDEBAR_COLOR);
        header.setBorder(new EmptyBorder(28, 20, 20, 20));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(220, 120));

        JLabel logo = new JLabel("🎓");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("EcoleManager");
        appName.setFont(new Font("Georgia", Font.BOLD, 16));
        appName.setForeground(ACCENT_COLOR);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        String roleDisplay = currentUser.getRole().substring(0, 1).toUpperCase()
                + currentUser.getRole().substring(1).toLowerCase();
        JLabel roleLabel = new JLabel(roleDisplay + " • " + currentUser.getLogin());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(SUBTLE_COLOR);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(6));
        header.add(appName);
        header.add(Box.createVerticalStrut(2));
        header.add(roleLabel);

        sidebar.add(header);

        // Divider
        sidebar.add(buildDivider());
        sidebar.add(Box.createVerticalStrut(10));

        // Nav items based on role
        String role = currentUser.getRole().toLowerCase();

        switch (role) {
            case "admin":
                sidebar.add(buildNavItem("👥", "Étudiants", "etudiants"));
                sidebar.add(buildNavItem("👨‍🏫", "Enseignants", "enseignants"));
                sidebar.add(buildNavItem("📚", "Matières", "matieres"));
                sidebar.add(buildNavItem("📝", "Notes", "notes"));
                sidebar.add(buildNavItem("📊", "Moyennes", "moyennes"));
                break;
            case "enseignant":
                sidebar.add(buildNavItem("📚", "Ma Matière", "matieres"));
                sidebar.add(buildNavItem("📝", "Notes", "notes"));
                sidebar.add(buildNavItem("📊", "Moyennes", "moyennes"));
                break;
            default:
                sidebar.add(buildNavItem("👤", "Mon Profil", "profil"));
                sidebar.add(buildNavItem("📝", "Mes Notes", "notes"));
                sidebar.add(buildNavItem("📊", "Ma Moyenne", "moyennes"));
                break;
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildDivider());

        // Logout button
        JButton btnLogout = new JButton("⬅  Se déconnecter");
        btnLogout.setBackground(SIDEBAR_COLOR);
        btnLogout.setForeground(ERROR_COLOR());
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(220, 48));
        btnLogout.setBorder(new EmptyBorder(12, 20, 12, 20));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setBackground(HOVER_COLOR); }
            public void mouseExited(MouseEvent e)  { btnLogout.setBackground(SIDEBAR_COLOR); }
        });

        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    private Color ERROR_COLOR() { return new Color(248, 113, 113); }

    private JPanel buildDivider() {
        JPanel div = new JPanel();
        div.setBackground(new Color(44, 62, 90));
        div.setMaximumSize(new Dimension(220, 1));
        div.setPreferredSize(new Dimension(220, 1));
        return div;
    }

    private JButton buildNavItem(String icon, String label, String cardName) {
        JButton btn = new JButton(icon + "  " + label);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(TEXT_COLOR);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 46));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_COLOR); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(SIDEBAR_COLOR); }
        });

        btn.addActionListener(e -> {
            lblPageTitle.setText(label);
            cardLayout.show(contentPanel, cardName);
            btn.setBackground(ACTIVE_COLOR);
        });

        return btn;
    }

    // ─── CONTENT AREA ───────────────────────────────────────────────────────────

    private JPanel buildContent() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_COLOR);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_COLOR);
        topBar.setBorder(new EmptyBorder(20, 28, 16, 28));

        lblPageTitle = new JLabel("Tableau de bord");
        lblPageTitle.setFont(new Font("Georgia", Font.BOLD, 22));
        lblPageTitle.setForeground(TEXT_COLOR);
        topBar.add(lblPageTitle, BorderLayout.WEST);

        // User badge
        JLabel badge = new JLabel(currentUser.getLogin() + "  👤");
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        badge.setForeground(SUBTLE_COLOR);
        topBar.add(badge, BorderLayout.EAST);

        wrapper.add(topBar, BorderLayout.NORTH);

        // Card panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR);

        // Add panels based on role
        String role = currentUser.getRole().toLowerCase();

        if (role.equals("admin")) {
            contentPanel.add(new EtudiantPanel(currentUser), "etudiants");
            contentPanel.add(new EnseignantPanel(currentUser), "enseignants");
            contentPanel.add(new MatierePanel(currentUser), "matieres");
            contentPanel.add(new NotePanel(currentUser), "notes");
            contentPanel.add(new MoyennePanel(currentUser), "moyennes");
        } else if (role.equals("enseignant")) {
            contentPanel.add(new MatierePanel(currentUser), "matieres");
            contentPanel.add(new NotePanel(currentUser), "notes");
            contentPanel.add(new MoyennePanel(currentUser), "moyennes");
        } else {
            contentPanel.add(new ProfilPanel(currentUser), "profil");
            contentPanel.add(new NotePanel(currentUser), "notes");
            contentPanel.add(new MoyennePanel(currentUser), "moyennes");
        }

        wrapper.add(contentPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private void showDefaultPanel() {
        String role = currentUser.getRole().toLowerCase();
        if (role.equals("admin")) {
            cardLayout.show(contentPanel, "etudiants");
            lblPageTitle.setText("Étudiants");
        } else if (role.equals("enseignant")) {
            cardLayout.show(contentPanel, "matieres");
            lblPageTitle.setText("Ma Matière");
        } else {
            cardLayout.show(contentPanel, "profil");
            lblPageTitle.setText("Mon Profil");
        }
    }
}