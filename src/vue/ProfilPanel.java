package vue;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modele.Etudiant;
import modele.Utilisateur;

public class ProfilPanel extends JPanel {

    private final Color BG_COLOR     = new Color(15, 23, 42);
    private final Color PANEL_COLOR  = new Color(30, 41, 59);
    private final Color ACCENT_COLOR = new Color(56, 189, 248);
    private final Color TEXT_COLOR   = new Color(226, 232, 240);
    private final Color SUBTLE_COLOR = new Color(100, 116, 139);

    public ProfilPanel(Utilisateur user) {
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 28, 28, 28));
        add(buildCard(user), BorderLayout.NORTH);
    }

    private JPanel buildCard(Utilisateur user) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_COLOR);
        card.setBorder(new EmptyBorder(32, 32, 32, 32));

        // Avatar
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Dialog", Font.PLAIN, 56));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name
        String displayName = user.getLogin();
        if (user instanceof Etudiant) {
            Etudiant et = (Etudiant) user;
            displayName = (et.getNom() != null ? et.getNom() : "") + " " + (et.getPrenom() != null ? et.getPrenom() : "");
        }

        JLabel lblName = new JLabel(displayName.trim(), SwingConstants.CENTER);
        lblName.setFont(new Font("Georgia", Font.BOLD, 24));
        lblName.setForeground(TEXT_COLOR);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel(user.getRole(), SwingConstants.CENTER);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRole.setForeground(ACCENT_COLOR);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(avatar);
        card.add(Box.createVerticalStrut(12));
        card.add(lblName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblRole);
        card.add(Box.createVerticalStrut(24));
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(20));

        // Info rows
        card.add(infoRow("📧 Email", user.getEmail() != null ? user.getEmail() : "-"));
        card.add(Box.createVerticalStrut(12));
        card.add(infoRow("🔑 Login", user.getLogin()));
        card.add(Box.createVerticalStrut(12));
        card.add(infoRow("✅ Statut", user.isActif() ? "Actif" : "Inactif"));

        if (user instanceof Etudiant) {
            Etudiant et = (Etudiant) user;
            card.add(Box.createVerticalStrut(12));
            card.add(infoRow("🎓 Niveau", et.getNiveau() != null ? et.getNiveau() : "-"));
            card.add(Box.createVerticalStrut(12));
            card.add(infoRow("🆔 ID Étudiant", String.valueOf(et.getIdEtudiant())));
        }

        return card;
    }

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(PANEL_COLOR);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Dialog", Font.BOLD, 13));
        lbl.setForeground(SUBTLE_COLOR);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Dialog", Font.PLAIN, 13));
        val.setForeground(TEXT_COLOR);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel buildDivider() {
        JPanel div = new JPanel();
        div.setBackground(new Color(44, 62, 90));
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setPreferredSize(new Dimension(100, 1));
        return div;
    }
}