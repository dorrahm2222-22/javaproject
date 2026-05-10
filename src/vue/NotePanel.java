package vue;

import DAO.NoteDAO;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            JButton btnAdd = styledButton("➕ Ajouter", ACCENT_COLOR, new Color(15, 23, 42));
            btnAdd.addActionListener(e -> showForm(null));
            bar.add(btnAdd);
        }

        JButton btnRefresh = styledButton("🔄 Actualiser", PANEL_COLOR, TEXT_COLOR);
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
        } else if(currentUser.getRole().equalsIgnoreCase("enseignant")) {
            int matiereId = ((modele.Enseignant) currentUser).getMatiereId();
            list = noteDAO.getNotesByMatiere(matiereId);
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
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(PANEL_COLOR);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBackground(PANEL_COLOR);
        form.setBorder(new EmptyBorder(24, 24, 16, 24));

        JTextField fIdEt  = formField(existing != null ? String.valueOf(existing.getIdEtudiant()) : "");
        JTextField fIdMat = formField(existing != null ? String.valueOf(existing.getIdMatiere()) : "");
        JTextField fNote  = formField(existing != null ? String.valueOf(existing.getNote()) : "");

        //button group
        String currentType = existing != null ? existing.getTypeEvaluation() : "Examen final";
        final String[] selectedType = { currentType };

        JToggleButton btnExamen    = new JToggleButton("Examen final");
        JToggleButton btnControle  = new JToggleButton("Contrôle continu");
        styleToggle(btnExamen,   selectedType[0].equals("Examen final"));
        styleToggle(btnControle, selectedType[0].equals("Contrôle continu"));

        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(btnExamen);
        typeGroup.add(btnControle);
        if (selectedType[0].equals("Examen final")) btnExamen.setSelected(true);
        else btnControle.setSelected(true);

        btnExamen.addActionListener(e -> {
            selectedType[0] = "Examen final";
            styleToggle(btnExamen, true);
            styleToggle(btnControle, false);
        });
        btnControle.addActionListener(e -> {
            selectedType[0] = "Contrôle continu";
            styleToggle(btnExamen, false);
            styleToggle(btnControle, true);
        });

        JPanel typePanel = new JPanel(new GridLayout(1, 2, 6, 0));
        typePanel.setBackground(PANEL_COLOR);
        typePanel.add(btnExamen);
        typePanel.add(btnControle);

        //calendar
        final Date[] selectedDate = { existing != null && existing.getDate_evaluation() != null
                ? existing.getDate_evaluation() : new Date() };
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        JButton btnDate = new JButton("📅  " + sdf.format(selectedDate[0]));
        btnDate.setBackground(FIELD_BG);
        btnDate.setForeground(TEXT_COLOR);
        btnDate.setFont(new Font("Dialog", Font.PLAIN, 13));
        btnDate.setBorderPainted(true);
        btnDate.setFocusPainted(false);
        btnDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(6, 10, 6, 10)));
        btnDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDate.setHorizontalAlignment(SwingConstants.LEFT);

        btnDate.addActionListener(e -> {
            Date picked = showCalendarPopup(dialog, selectedDate[0]);
            if (picked != null) {
                selectedDate[0] = picked;
                btnDate.setText("📅  " + sdf.format(picked));
            }
        });

        form.add(formLabel("ID Étudiant"));       
        form.add(fIdEt);
        form.add(formLabel("ID Matière"));        
        form.add(fIdMat);
        form.add(formLabel("Note (0-20)"));       
        form.add(fNote);
        form.add(formLabel("Type d'évaluation")); 
        form.add(typePanel);
        form.add(formLabel("Date d'évaluation")); 
        form.add(btnDate);

        JButton btnSave   = styledButton(existing == null ? "Ajouter" : "Modifier", ACCENT_COLOR, new Color(15, 23, 42));
        JButton btnCancel = styledButton("Annuler", FIELD_BG, TEXT_COLOR);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            Note n = existing != null ? existing : new Note();

            try {
                n.setIdEtudiant(Integer.parseInt(fIdEt.getText().trim()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "ID Étudiant invalide.");
                return;
            }
            try {
                n.setIdMatiere(Integer.parseInt(fIdMat.getText().trim()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "ID Matière invalide.");
                return;
            }
            try {
                double val = Double.parseDouble(fNote.getText().trim());
                if (val < 0 || val > 20) {
                    JOptionPane.showMessageDialog(dialog, "La note doit être entre 0 et 20.");
                    return;
                }
                n.setNote(val);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Note invalide (ex: 14.5).");
                return;
            }


            n.setTypeEvaluation(selectedType[0]);
            n.setDate_evaluation(selectedDate[0]);

            int result = existing == null ? noteDAO.ajouter(n) : (noteDAO.modifier(n) ? 1 : -1);
            if (result == -1) {
                try {
                DAO.MoyennegDAO mDAO = new DAO.MoyennegDAO(DBConnection.getConnection());
                mDAO.recalculerEtSauvegarder(n.getIdEtudiant(), "2025-2026");
            }   catch (SQLException ex) {
                System.err.println("Erreur mise à jour moyenne: " + ex.getMessage());
             }
                JOptionPane.showMessageDialog(dialog, "Erreur lors de l'enregistrement. Vérifiez les IDs.");
                return;
            }
            loadData();
            dialog.dispose();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bottom.setBackground(PANEL_COLOR);
        bottom.add(btnCancel);
        bottom.add(btnSave);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private Date showCalendarPopup(JDialog parent, Date initial) {
        JDialog cal = new JDialog(parent, "Choisir une date", true);
        cal.setSize(300, 280);
        cal.setLocationRelativeTo(parent);
        cal.getContentPane().setBackground(PANEL_COLOR);
        cal.setLayout(new BorderLayout(0, 4));

        Calendar c = Calendar.getInstance();
        if (initial != null) c.setTime(initial);
        final int[] year  = { c.get(Calendar.YEAR) };
        final int[] month = { c.get(Calendar.MONTH) };
        final Date[] result = { null };

        // Header: prev / month-year label / next
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(22, 33, 55));
        header.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel lblMonth = new JLabel("", SwingConstants.CENTER);
        lblMonth.setForeground(ACCENT_COLOR);
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JButton prev = navBtn("‹");
        JButton next = navBtn("›");
        header.add(prev, BorderLayout.WEST);
        header.add(lblMonth, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);

    
        JPanel grid = new JPanel(new GridLayout(7, 7, 2, 2));
        grid.setBackground(PANEL_COLOR);
        grid.setBorder(new EmptyBorder(4, 8, 8, 8));

        String[] dayNames = {"Lu","Ma","Me","Je","Ve","Sa","Di"};

        Runnable buildGrid = () -> {
            grid.removeAll();
            String[] months = {"Janvier","Février","Mars","Avril","Mai","Juin",
                               "Juillet","Août","Septembre","Octobre","Novembre","Décembre"};
            lblMonth.setText(months[month[0]] + " " + year[0]);

            for (String d : dayNames) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setForeground(SUBTLE_COLOR);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                grid.add(lbl);
            }

            Calendar tmp = Calendar.getInstance();
            tmp.set(year[0], month[0], 1);
            int firstDow = (tmp.get(Calendar.DAY_OF_WEEK) + 5) % 7; // Mon=0
            int daysInMonth = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 0; i < firstDow; i++) grid.add(new JLabel(""));

            Calendar today = Calendar.getInstance();
            for (int day = 1; day <= daysInMonth; day++) {
                final int d2 = day;
                boolean isToday = (day == today.get(Calendar.DAY_OF_MONTH)
                    && month[0] == today.get(Calendar.MONTH)
                    && year[0] == today.get(Calendar.YEAR));
                JButton btn = new JButton(String.valueOf(day));
                btn.setFont(new Font("Dialog", Font.PLAIN, 12));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBackground(isToday ? ACCENT_COLOR : FIELD_BG);
                btn.setForeground(isToday ? new Color(15, 23, 42) : TEXT_COLOR);
                btn.addActionListener(ev -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(year[0], month[0], d2, 0, 0, 0);
                    result[0] = sel.getTime();
                    cal.dispose();
                });
                grid.add(btn);
            }
            grid.revalidate();
            grid.repaint();
        };

        prev.addActionListener(e -> {
            month[0]--;
            if (month[0] < 0) { month[0] = 11; year[0]--; }
            buildGrid.run();
        });
        next.addActionListener(e -> {
            month[0]++;
            if (month[0] > 11) { month[0] = 0; year[0]++; }
            buildGrid.run();
        });

        buildGrid.run();
        cal.add(header, BorderLayout.NORTH);
        cal.add(grid, BorderLayout.CENTER);
        cal.setVisible(true);
        return result[0];
    }

    private JButton navBtn(String txt) {
        JButton b = new JButton(txt);
        b.setBackground(new Color(22, 33, 55));
        b.setForeground(ACCENT_COLOR);
        b.setFont(new Font("Dialog", Font.BOLD, 16));
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void styleToggle(JToggleButton btn, boolean selected) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (selected) {
            btn.setBackground(ACCENT_COLOR);
            btn.setForeground(new Color(15, 23, 42));
            btn.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
        } else {
            btn.setBackground(FIELD_BG);
            btn.setForeground(SUBTLE_COLOR);
            btn.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void deleteRow(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        int etudiantId = (int) tableModel.getValueAt(row, 1); 
        
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { 
            noteDAO.supprimer(id); 
            try {
                DAO.MoyennegDAO mDAO = new DAO.MoyennegDAO(DBConnection.getConnection());
                mDAO.recalculerEtSauvegarder(etudiantId, "2025-2026");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            loadData(); 
    }
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
        btn.setFont(new Font("Dialog", Font.BOLD, 13));
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
        JLabel l = new JLabel(text);
        l.setForeground(SUBTLE_COLOR);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    class ActionRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        ActionRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4)); setBackground(PANEL_COLOR); }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            removeAll();
            add(makeBtn("✏", new Color(30, 58, 90), ACCENT_COLOR));
            add(makeBtn("🗑", new Color(60, 20, 20), DANGER));
            return this;
        }
        private JButton makeBtn(String txt, Color bg, Color fg) {
            JButton b = new JButton(txt); b.setBackground(bg); b.setForeground(fg);
            b.setFont(new Font("Dialog", Font.PLAIN, 13)); b.setBorderPainted(false); b.setFocusPainted(false); return b;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        @SuppressWarnings("FieldMayBeFinal")
        private JPanel container;
        private int currentRow;

        ActionEditor(JCheckBox cb) {
            super(cb);
            container = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            container.setBackground(PANEL_COLOR);
            JButton btnEdit = makeBtn("✏", new Color(30, 58, 90), ACCENT_COLOR);
            JButton btnDel  = makeBtn("🗑", new Color(60, 20, 20), DANGER);
            btnEdit.addActionListener(e -> { fireEditingStopped(); editRow(currentRow); });
            btnDel.addActionListener(e  -> { fireEditingStopped(); deleteRow(currentRow); });
            container.add(btnEdit);
            container.add(btnDel);
        }

        private JButton makeBtn(String txt, Color bg, Color fg) {
            JButton b = new JButton(txt); b.setBackground(bg); b.setForeground(fg);
            b.setFont(new Font("Dialog", Font.PLAIN, 13)); b.setBorderPainted(false); b.setFocusPainted(false); return b;
        }

        @SuppressWarnings("override")
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
            currentRow = row; return container;
        }

        @SuppressWarnings("override")
        public Object getCellEditorValue() { return ""; }
    }
}