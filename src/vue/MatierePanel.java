package vue;

import DAO.MatiereDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modele.Matiere;
import modele.Utilisateur;
import util.DBConnection;

public class MatierePanel extends JPanel {

    private Utilisateur currentUser;
    private MatiereDAO matiereDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private final Color BG_COLOR     = new Color(15, 23, 42);
    private final Color PANEL_COLOR  = new Color(30, 41, 59);
    private final Color ACCENT_COLOR = new Color(56, 189, 248);
    private final Color TEXT_COLOR   = new Color(226, 232, 240);
    private final Color SUBTLE_COLOR = new Color(100, 116, 139);
    private final Color FIELD_BG     = new Color(51, 65, 85);
    private final Color DANGER       = new Color(248, 113, 113);

    public MatierePanel(Utilisateur user) {
        this.currentUser = user;
        try {
            matiereDAO = new MatiereDAO(DBConnection.getConnection());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur DB: " + e.getMessage());
        }
        setBackground(BG_COLOR);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(0, 28, 28, 28));
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bar.setBackground(BG_COLOR);
        bar.setBorder(new EmptyBorder(0, 0, 8, 0));

        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");
        if (isAdmin) {
            JButton btnAdd = styledButton("+ Ajouter", ACCENT_COLOR, new Color(15, 23, 42));
            btnAdd.addActionListener(e -> showForm(null));
            bar.add(btnAdd);
        }

        JButton btnRefresh = styledButton("↻ Actualiser", PANEL_COLOR, TEXT_COLOR);
        btnRefresh.addActionListener(e -> loadData());
        bar.add(btnRefresh);
        return bar;
    }

    private JScrollPane buildTable() {
        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");
        String[] cols = isAdmin
            ? new String[]{"ID", "Nom", "Coefficient", "Volume Horaire", "Semestre", "Actions"}
            : new String[]{"ID", "Nom", "Coefficient", "Volume Horaire", "Semestre"};

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return isAdmin && c == 5; }
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
        boolean isAdmin = currentUser.getRole().equalsIgnoreCase("admin");
        List<Matiere> list = matiereDAO.getAllMatieres();
        for (Matiere m : list) {
            if (isAdmin) {
                tableModel.addRow(new Object[]{m.getId(), m.getNom(), m.getCoefficient(), m.getVolumeHoraire(), m.getSemestre(), "actions"});
            } else {
                tableModel.addRow(new Object[]{m.getId(), m.getNom(), m.getCoefficient(), m.getVolumeHoraire(), m.getSemestre()});
            }
        }
    }

    public void showForm(Matiere existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Ajouter une matière" : "Modifier la matière", true);
        dialog.setSize(380, 360);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_COLOR);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBackground(PANEL_COLOR);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField fNom  = formField(existing != null ? existing.getNom() : "");
        JTextField fCoef = formField(existing != null ? String.valueOf(existing.getCoefficient()) : "");
        JTextField fVol  = formField(existing != null ? String.valueOf(existing.getVolumeHoraire()) : "");
        JTextField fSem  = formField(existing != null ? existing.getSemestre() : "");

        form.add(formLabel("Nom"));            form.add(fNom);
        form.add(formLabel("Coefficient"));    form.add(fCoef);
        form.add(formLabel("Volume Horaire")); form.add(fVol);
        form.add(formLabel("Semestre"));       form.add(fSem);

        JButton btnSave   = styledButton(existing == null ? "Ajouter" : "Modifier", ACCENT_COLOR, new Color(15,23,42));
        JButton btnCancel = styledButton("Annuler", FIELD_BG, TEXT_COLOR);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            Matiere m = existing != null ? existing : new Matiere();
            m.setNom(fNom.getText().trim());
            try { m.setCoefficient(Integer.parseInt(fCoef.getText().trim())); } catch (NumberFormatException ex) { m.setCoefficient(1); }
            try { m.setVolumeHoraire(Integer.parseInt(fVol.getText().trim())); } catch (NumberFormatException ex) { m.setVolumeHoraire(0); }
            m.setSemestre(fSem.getText().trim());
            if (existing == null) matiereDAO.ajouter(m);
            else matiereDAO.modifier(m);
            loadData();
            dialog.dispose();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottom.setBackground(PANEL_COLOR);
        bottom.add(btnCancel); bottom.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void deleteRow(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cette matière ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { matiereDAO.supprimer(id); loadData(); }
    }

    public void editRow(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        Matiere m = matiereDAO.getMatiereById(id);
        if (m != null) showForm(m);
    }

    private void styleTable(JTable t) {
        t.setBackground(PANEL_COLOR); t.setForeground(TEXT_COLOR);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13)); t.setRowHeight(38);
        t.setGridColor(new Color(44, 62, 90));
        t.setSelectionBackground(new Color(30, 58, 90)); t.setSelectionForeground(TEXT_COLOR);
        t.getTableHeader().setBackground(new Color(22, 33, 55));
        t.getTableHeader().setForeground(ACCENT_COLOR);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setShowHorizontalLines(true); t.setIntercellSpacing(new Dimension(0, 1));
        t.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text); btn.setBackground(bg); btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private JTextField formField(String val) { JTextField f = new JTextField(val); styleField(f); return f; }

    private void styleField(JTextField f) {
        f.setBackground(FIELD_BG); f.setForeground(TEXT_COLOR); f.setCaretColor(TEXT_COLOR);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(6, 10, 6, 10)));
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text); l.setForeground(SUBTLE_COLOR); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); return l;
    }

    class ActionRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        ActionRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4)); setBackground(PANEL_COLOR); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            removeAll(); add(makeBtn("✏", new Color(30,58,90), ACCENT_COLOR)); add(makeBtn("🗑", new Color(60,20,20), DANGER)); return this;
        }
        private JButton makeBtn(String txt, Color bg, Color fg) {
            JButton b = new JButton(txt); b.setBackground(bg); b.setForeground(fg);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); b.setBorderPainted(false); b.setFocusPainted(false); return b;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        private JPanel container; private int currentRow;
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
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) { currentRow = row; return container; }
        public Object getCellEditorValue() { return ""; }
    }
}