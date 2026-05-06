package vue;

import DAO.NoteDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modele.Note;
import modele.Utilisateur;
import util.DBConnection;

public class NotePanel extends JPanel {

    private Utilisateur currentUser;
    private NoteDAO noteDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    private final Color BG_COLOR     = new Color(15, 23, 42);
    private final Color PANEL_COLOR  = new Color(30, 41, 59);
    private final Color ACCENT_COLOR = new Color(56, 189, 248);
    private final Color TEXT_COLOR   = new Color(226, 232, 240);
    private final Color SUBTLE_COLOR = new Color(100, 116, 139);
    private final Color FIELD_BG     = new Color(51, 65, 85);
    private final Color DANGER       = new Color(248, 113, 113);

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public NotePanel(Utilisateur user) {
        this.currentUser = user;
        try {
            noteDAO = new NoteDAO(DBConnection.getConnection());
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
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bar.setBackground(BG_COLOR);
        bar.setBorder(new EmptyBorder(0, 0, 8, 0));

        boolean canEdit = !currentUser.getRole().equalsIgnoreCase("etudiant");
        if (canEdit) {
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
        boolean canEdit = !currentUser.getRole().equalsIgnoreCase("etudiant");
        String[] cols = canEdit
            ? new String[]{"ID", "ID Étudiant", "ID Matière", "Note", "Type Évaluation", "Date", "Actions"}
            : new String[]{"ID", "ID Matière", "Note", "Type Évaluation", "Date"};

        tableModel = new DefaultTableModel(cols, 0) {
            @SuppressWarnings("override")
            public boolean isCellEditable(int r, int c) { return canEdit && c == 6; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        if (canEdit) {
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
        boolean canEdit = !currentUser.getRole().equalsIgnoreCase("etudiant");
        List<Note> list;

        if (currentUser.getRole().equalsIgnoreCase("etudiant")) {
            list = noteDAO.getNotesByEtudiant(currentUser.getId());
        } else {
            list = noteDAO.getAllNotes();
        }

        for (Note n : list) {
            if (canEdit) {
                tableModel.addRow(new Object[]{
                    n.getId(), n.getIdEtudiant(), n.getIdMatiere(),
                    n.getNote(), n.getTypeEvaluation(), n.getDate_evaluation(), "actions"
                });
            } else {
                tableModel.addRow(new Object[]{
                    n.getId(), n.getIdMatiere(),
                    n.getNote(), n.getTypeEvaluation(), n.getDate_evaluation()
                });
            }
        }
    }

    public void showForm(Note existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Ajouter une note" : "Modifier la note", true);
        dialog.setSize(380, 380);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_COLOR);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBackground(PANEL_COLOR);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField fIdEt   = formField(existing != null ? String.valueOf(existing.getIdEtudiant()) : "");
        JTextField fIdMat  = formField(existing != null ? String.valueOf(existing.getIdMatiere()) : "");
        JTextField fNote   = formField(existing != null ? String.valueOf(existing.getNote()) : "");
        JTextField fType   = formField(existing != null ? existing.getTypeEvaluation() : "");

        form.add(formLabel("ID Étudiant"));       form.add(fIdEt);
        form.add(formLabel("ID Matière"));        form.add(fIdMat);
        form.add(formLabel("Note (0-20)"));       form.add(fNote);
        form.add(formLabel("Type d'évaluation")); form.add(fType);

        JButton btnSave   = styledButton(existing == null ? "Ajouter" : "Modifier", ACCENT_COLOR, new Color(15,23,42));
        JButton btnCancel = styledButton("Annuler", FIELD_BG, TEXT_COLOR);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            Note n = existing != null ? existing : new Note();
            try { n.setIdEtudiant(Integer.parseInt(fIdEt.getText().trim())); } catch (NumberFormatException ex) {}
            try { n.setIdMatiere(Integer.parseInt(fIdMat.getText().trim())); } catch (NumberFormatException ex) {}
            try { n.setNote(Double.parseDouble(fNote.getText().trim())); } catch (NumberFormatException ex) {}
            n.setTypeEvaluation(fType.getText().trim());
            n.setDate_evaluation(new Date()); // default to today
            if (existing == null) noteDAO.ajouter(n);
            else noteDAO.modifier(n);
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
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer cette note ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { noteDAO.supprimer(id); loadData(); }
    }

    public void editRow(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        Note n = noteDAO.getNoteById(id);
        if (n != null) showForm(n);
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
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            removeAll(); add(makeBtn("✏", new Color(30,58,90), ACCENT_COLOR)); add(makeBtn("🗑", new Color(60,20,20), DANGER)); return this;
        }
        private JButton makeBtn(String txt, Color bg, Color fg) {
            JButton b = new JButton(txt); b.setBackground(bg); b.setForeground(fg);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13)); b.setBorderPainted(false); b.setFocusPainted(false); return b;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        @SuppressWarnings("FieldMayBeFinal")
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
        @SuppressWarnings("override")
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) { currentRow = row; return container; }
        @SuppressWarnings("override")
        public Object getCellEditorValue() { return ""; }
    }
}