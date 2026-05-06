package vue;

import DAO.EtudiantDAO;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modele.Etudiant;
import modele.Utilisateur;
import util.DBConnection;

public class EtudiantPanel extends JPanel {

    private Utilisateur currentUser;
    private EtudiantDAO etudiantDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    private final Color BG_COLOR     = new Color(15, 23, 42);
    private final Color PANEL_COLOR  = new Color(30, 41, 59);
    private final Color ACCENT_COLOR = new Color(56, 189, 248);
    private final Color TEXT_COLOR   = new Color(226, 232, 240);
    private final Color SUBTLE_COLOR = new Color(100, 116, 139);
    private final Color FIELD_BG     = new Color(51, 65, 85);
    private final Color DANGER       = new Color(248, 113, 113);

    public EtudiantPanel(Utilisateur user) {
        this.currentUser = user;
        try {
            etudiantDAO = new EtudiantDAO(DBConnection.getConnection());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur DB: " + e.getMessage());
        }
        setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(0, 28, 28, 28));
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(BG_COLOR);
        bar.setBorder(new EmptyBorder(0, 0, 8, 0));

        txtSearch = new JTextField();
        txtSearch.setBackground(FIELD_BG);
        txtSearch.setForeground(TEXT_COLOR);
        txtSearch.setCaretColor(TEXT_COLOR);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105)),
            new EmptyBorder(7, 12, 7, 12)));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filterTable(txtSearch.getText()); }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setBackground(BG_COLOR);

        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");
        if (isAdmin) {
            JButton btnAdd = styledButton("+ Ajouter", ACCENT_COLOR, new Color(15, 23, 42));
            btnAdd.addActionListener(e -> showForm(null));
            buttons.add(btnAdd);
        }

        JButton btnRefresh = styledButton("↻ Actualiser", PANEL_COLOR, TEXT_COLOR);
        btnRefresh.addActionListener(e -> loadData());
        buttons.add(btnRefresh);

        bar.add(txtSearch, BorderLayout.CENTER);
        bar.add(buttons, BorderLayout.EAST);
        return bar;
    }

    private JScrollPane buildTable() {
        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");
        String[] cols = isAdmin
            ? new String[]{"ID", "ID Étudiant", "Nom", "Prénom", "Email", "Niveau", "Statut", "Actions"}
            : new String[]{"ID", "ID Étudiant", "Nom", "Prénom", "Email", "Niveau", "Statut"};

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return isAdmin && c == 7;
            }
        };
        table = new JTable(tableModel);
        styleTable(table);

        if (isAdmin) {
            table.getColumn("Actions").setCellRenderer(new ActionRenderer());
            table.getColumn("Actions").setCellEditor(new ActionEditor(new JCheckBox()));
            table.getColumn("Actions").setMinWidth(120);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(PANEL_COLOR);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(44, 62, 90)));
        return scroll;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Etudiant> list = etudiantDAO.getAllEtudiants();
            boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");
            for (Etudiant e : list) {
                if (isAdmin) {
                    tableModel.addRow(new Object[]{
                        e.getId(), e.getIdEtudiant(), e.getNom(), e.getPrenom(),
                        e.getEmail(), e.getNiveau(), e.getStatus(), "actions"
                    });
                } else {
                    tableModel.addRow(new Object[]{
                        e.getId(), e.getIdEtudiant(), e.getNom(), e.getPrenom(),
                        e.getEmail(), e.getNiveau(), e.getStatus()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur: " + ex.getMessage());
        }
    }

    private void filterTable(String query) {
        tableModel.setRowCount(0);
        try {
            List<Etudiant> list = etudiantDAO.getAllEtudiants();
            boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");
            for (Etudiant e : list) {
                boolean match = e.getNom().toLowerCase().contains(query.toLowerCase())
                    || e.getPrenom().toLowerCase().contains(query.toLowerCase())
                    || (e.getIdEtudiant() != null && e.getIdEtudiant().toLowerCase().contains(query.toLowerCase()));
                if (match) {
                    if (isAdmin) {
                        tableModel.addRow(new Object[]{
                            e.getId(), e.getIdEtudiant(), e.getNom(), e.getPrenom(),
                            e.getEmail(), e.getNiveau(), e.getStatus(), "actions"
                        });
                    } else {
                        tableModel.addRow(new Object[]{
                            e.getId(), e.getIdEtudiant(), e.getNom(), e.getPrenom(),
                            e.getEmail(), e.getNiveau(), e.getStatus()
                        });
                    }
                }
            }
        } catch (SQLException ex) { /* ignore */ }
    }

    public void showForm(Etudiant existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Ajouter un étudiant" : "Modifier l'étudiant", true);
        dialog.setSize(420, 520);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_COLOR);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBackground(PANEL_COLOR);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField fLogin   = formField(existing != null ? existing.getLogin() : "");
        JTextField fNom     = formField(existing != null ? existing.getNom() : "");
        JTextField fPrenom  = formField(existing != null ? existing.getPrenom() : "");
        JTextField fEmail   = formField(existing != null ? existing.getEmail() : "");
        JTextField fNiveau  = formField(existing != null ? existing.getNiveau() : "");
        JTextField fIdEt    = formField(existing != null ? existing.getIdEtudiant() : "");
        JPasswordField fPwd = new JPasswordField(existing != null ? existing.getMotDePasse() : "");
        styleField(fPwd);

        form.add(formLabel("Login"));         form.add(fLogin);
        form.add(formLabel("Nom"));           form.add(fNom);
        form.add(formLabel("Prénom"));        form.add(fPrenom);
        form.add(formLabel("Email"));         form.add(fEmail);
        form.add(formLabel("Niveau"));        form.add(fNiveau);
        form.add(formLabel("ID Étudiant"));   form.add(fIdEt);
        form.add(formLabel("Mot de passe"));  form.add(fPwd);

        JButton btnSave = styledButton(existing == null ? "Ajouter" : "Modifier", ACCENT_COLOR, new Color(15, 23, 42));
        JButton btnCancel = styledButton("Annuler", FIELD_BG, TEXT_COLOR);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            Etudiant et = existing != null ? existing : new Etudiant();
            et.setLogin(fLogin.getText().trim());
            et.setNom(fNom.getText().trim());
            et.setPrenom(fPrenom.getText().trim());
            et.setEmail(fEmail.getText().trim());
            et.setNiveau(fNiveau.getText().trim());
            et.setIdEtudiant(fIdEt.getText().trim());
            et.setMotDePasse(new String(fPwd.getPassword()));
            try {
                if (existing == null) etudiantDAO.ajouter(et);
                else etudiantDAO.modifier(et);
                loadData();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erreur: " + ex.getMessage());
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottom.setBackground(PANEL_COLOR);
        bottom.add(btnCancel);
        bottom.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void deleteRow(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Supprimer cet étudiant ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            etudiantDAO.supprimer(id);
            loadData();
        }
    }

    public void editRow(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        Etudiant et = etudiantDAO.getEtudiantById(id);
        if (et != null) showForm(et);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void styleTable(JTable t) {
        t.setBackground(PANEL_COLOR);
        t.setForeground(TEXT_COLOR);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(38);
        t.setGridColor(new Color(44, 62, 90));
        t.setSelectionBackground(new Color(30, 58, 90));
        t.setSelectionForeground(TEXT_COLOR);
        t.getTableHeader().setBackground(new Color(22, 33, 55));
        t.getTableHeader().setForeground(ACCENT_COLOR);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setShowHorizontalLines(true);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private JTextField formField(String val) {
        JTextField f = new JTextField(val); styleField(f); return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(FIELD_BG); f.setForeground(TEXT_COLOR);
        f.setCaretColor(TEXT_COLOR); f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(6, 10, 6, 10)));
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(SUBTLE_COLOR); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l;
    }

    // ── Action Renderer/Editor ─────────────────────────────────────────────────

    class ActionRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        ActionRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4)); setBackground(PANEL_COLOR); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            removeAll();
            JButton e = makeBtn("✏", new Color(30,58,90), ACCENT_COLOR);
            JButton d = makeBtn("🗑", new Color(60,20,20), DANGER);
            add(e); add(d); return this;
        }
        private JButton makeBtn(String txt, Color bg, Color fg) {
            JButton b = new JButton(txt); b.setBackground(bg); b.setForeground(fg);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); b.setBorderPainted(false); b.setFocusPainted(false); return b;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        private final JPanel container; private int currentRow;
        ActionEditor(JCheckBox cb) {
            super(cb);
            container = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            container.setBackground(PANEL_COLOR);
            JButton btnEdit = makeBtn("✏", new Color(30,58,90), ACCENT_COLOR);
            JButton btnDel  = makeBtn("🗑", new Color(60,20,20), DANGER);
            btnEdit.addActionListener(e -> { fireEditingStopped(); editRow(currentRow); });
            btnDel.addActionListener(e  -> { fireEditingStopped(); deleteRow(currentRow); });
            container.add(btnEdit); container.add(btnDel);
        }
        private JButton makeBtn(String txt, Color bg, Color fg) {
            JButton b = new JButton(txt); b.setBackground(bg); b.setForeground(fg);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); b.setBorderPainted(false); b.setFocusPainted(false); return b;
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
            currentRow = row; return container;
        }
        public Object getCellEditorValue() { return ""; }
    }
}