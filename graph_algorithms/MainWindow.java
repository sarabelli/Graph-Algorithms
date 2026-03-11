package grafoV2;

/*
  Author: Sara Belli
*/

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {

    private static final Color DARK_BLUE       = new Color(30, 58, 100);
    private static final Color MID_BLUE        = new Color(44, 82, 130);
    private static final Color LIGHT_BLUE      = new Color(71, 120, 180);
    private static final Color BACKGROUND_BLUE = new Color(235, 242, 252);
    private static final Color WHITE            = Color.WHITE;
    private static final Color DARK_TEXT        = new Color(25, 40, 65);
    private static final Color SELECTION_COLOR  = new Color(150, 190, 235);
    private static final Color LOCKED_CELL_BG   = new Color(200, 210, 228);
    private static final Color LOCKED_CELL_TEXT = new Color(80, 100, 135);
    private static final Color GREEN            = new Color(0, 150, 60);

    private JTextField    txtNodes;
    private JButton       btnCreate, btnReset;
    private JTable        table;
    private JScrollPane   scrollPane;
    private GraphPanel    graphPanel;
    private Graph         graph;

    private JToggleButton btnDirected, btnUndirected;
    private JToggleButton btnWeighted, btnUnweighted;

    private boolean directed = true;
    private boolean weighted = true;

    // Flag to prevent recursive mirror update
    private boolean updatingMirror = false;

    public MainWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Graph Visualizer");
        getContentPane().setBackground(BACKGROUND_BLUE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        buildUI();
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.setBackground(DARK_BLUE);
        top.setBorder(new EmptyBorder(2, 4, 2, 4));

        JLabel appLabel = new JLabel("Graph Visualizer");
        appLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appLabel.setForeground(new Color(180, 210, 245));
        top.add(appLabel);
        top.add(verticalSeparator());

        top.add(label("Nodes:"));
        txtNodes = textField(4);
        top.add(txtNodes);

        btnCreate = createButton("Create matrix", MID_BLUE, LIGHT_BLUE, DARK_BLUE);
        btnReset  = createButton("Reset", new Color(160,40,40), new Color(200,60,60), new Color(110,25,25));
        btnReset.setPreferredSize(new Dimension(80, 34));
        top.add(btnCreate);
        top.add(btnReset);
        top.add(verticalSeparator());

        top.add(label("Direction:"));
        ButtonGroup grpDir = new ButtonGroup();
        btnDirected   = createToggle("Directed");
        btnUndirected = createToggle("Undirected");
        grpDir.add(btnDirected); grpDir.add(btnUndirected);
        btnDirected.setSelected(true);
        top.add(btnDirected); top.add(btnUndirected);
        top.add(verticalSeparator());

        top.add(label("Weight:"));
        ButtonGroup grpW = new ButtonGroup();
        btnWeighted   = createToggle("Weighted");
        btnUnweighted = createToggle("Unweighted");
        grpW.add(btnWeighted); grpW.add(btnUnweighted);
        btnWeighted.setSelected(true);
        top.add(btnWeighted); top.add(btnUnweighted);

        add(top, BorderLayout.NORTH);

        graph      = new Graph(directed, weighted);
        graphPanel = new GraphPanel(graph);
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BACKGROUND_BLUE);
        center.setBorder(new EmptyBorder(6, 6, 4, 6));
        center.add(graphPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        btnCreate.addActionListener(e -> createTable());
        btnReset.addActionListener(e -> resetAll());

        // Type change → reset table and rebuild if nodes are already set
        btnDirected.addActionListener(e ->   { if (btnDirected.isSelected())   { directed = true;  onTypeChange(); } });
        btnUndirected.addActionListener(e -> { if (btnUndirected.isSelected()) { directed = false; onTypeChange(); } });
        btnWeighted.addActionListener(e ->   { if (btnWeighted.isSelected())   { weighted = true;  onTypeChange(); } });
        btnUnweighted.addActionListener(e -> { if (btnUnweighted.isSelected()) { weighted = false; onTypeChange(); } });
    }

    // Called on every directed/weighted toggle change: resets table and graph completely
    private void onTypeChange() {
        graph.setDirected(directed);
        graph.setWeighted(weighted);
        if (table != null) {
            int n = table.getRowCount();
            // Remove existing table
            if (scrollPane != null) { remove(scrollPane); scrollPane = null; table = null; }
            // Clear the graph
            graph.getNodes().clear();
            graph.getEdges().clear();
            graphPanel.repaint();
            // Recreate empty table with the same number of nodes
            createTableN(n);
        } else {
            graphPanel.repaint();
        }
    }

    // ─────────────────────────────────────────────────────────────
    private void createTable() {
        int n;
        try {
            n = Integer.parseInt(txtNodes.getText().trim());
            if (n < 1 || n > 20) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number of nodes (1–20).", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        createTableN(n);
    }

    private void createTableN(int n) {
        if (scrollPane != null) { remove(scrollPane); scrollPane = null; table = null; }

        // Model: diagonal is not editable.
        // Undirected: only upper triangle (i < j-1) is editable; lower triangle
        // is updated automatically (mirror) but stays visually locked (read-only).
        DefaultTableModel model = new DefaultTableModel(n, n + 1) {
            @Override
            public boolean isCellEditable(int row, int col) {
                if (col == 0) return false;            // label column
                if (row == col - 1) return false;      // diagonal
                if (!directed && row > col - 1) return false; // lower triangle → mirror, not editable
                return true;
            }
            @Override public String getColumnName(int col) {
                return col == 0 ? "Node" : "N" + (col - 1);
            }
        };

        for (int i = 0; i < n; i++) model.setValueAt("Node " + i, i, 0);
        for (int i = 0; i < n; i++) model.setValueAt(0, i, i + 1);

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(true);
        table.setGridColor(new Color(185, 205, 230));
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionForeground(DARK_TEXT);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(true);

        // ── Renderer ──
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Segoe UI", col == 0 ? Font.BOLD : Font.PLAIN, 13));

                boolean isDiag   = (col > 0 && row == col - 1);
                boolean isMirror = (!directed && col > 0 && row > col - 1);

                if (sel && col > 0 && !isDiag && !isMirror) {
                    setBackground(SELECTION_COLOR); setForeground(DARK_TEXT);
                } else if (col == 0) {
                    setBackground(MID_BLUE); setForeground(WHITE);
                } else if (isDiag) {
                    setBackground(LOCKED_CELL_BG); setForeground(LOCKED_CELL_TEXT);
                } else if (isMirror) {
                    // Lower triangle: mirror, clearly readable
                    setBackground(new Color(220, 230, 245)); setForeground(new Color(60, 80, 120));
                } else {
                    // Normal editable cells
                    setBackground(WHITE);
                    if (!weighted && val != null && !val.toString().isEmpty()) {
                        try {
                            int v = Integer.parseInt(val.toString().trim());
                            if (v == 1) {
                                setForeground(GREEN);
                                setFont(new Font("Segoe UI", Font.BOLD, 13));
                            } else {
                                setForeground(DARK_TEXT);
                            }
                        } catch (NumberFormatException ignored) { setForeground(new Color(180, 0, 0)); }
                    } else {
                        setForeground(DARK_TEXT);
                    }
                }
                setBorder(new EmptyBorder(0, 4, 0, 4));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(72);
        for (int i = 1; i <= n; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(48);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBackground(MID_BLUE);
                setForeground(WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(20, 45, 85)));
                return this;
            }
        });

        // ── Editor with validation ──
        javax.swing.DefaultCellEditor editor = new javax.swing.DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                String val = ((JTextField) getComponent()).getText().trim();
                if (!val.isEmpty()) {
                    try {
                        int v = Integer.parseInt(val);
                        if (!weighted && v != 0 && v != 1) {
                            JOptionPane.showMessageDialog(MainWindow.this,
                                "Unweighted graph: only 0 or 1 allowed.",
                                "Invalid value", JOptionPane.WARNING_MESSAGE);
                            ((JTextField) getComponent()).setText("0");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                            "Please enter an integer.", "Invalid value", JOptionPane.WARNING_MESSAGE);
                        ((JTextField) getComponent()).setText("0");
                    }
                }
                return super.stopCellEditing();
            }
        };
        editor.setClickCountToStart(1);
        table.setDefaultEditor(Object.class, editor);

        // ── Listener: mirror update + graph refresh ──
        model.addTableModelListener(e -> {
            if (updatingMirror) return;
            int row = e.getFirstRow();
            int col = e.getColumn();
            // Mirror update only for undirected graphs, upper triangle
            if (!directed && col > 0 && row < col - 1) {
                int mirrorRow = col - 1;
                int mirrorCol = row + 1;
                Object val = model.getValueAt(row, col);
                updatingMirror = true;
                model.setValueAt(val, mirrorRow, mirrorCol);
                updatingMirror = false;
            }
            updateGraph();
        });

        String title = "  Adjacency matrix"
            + (weighted ? " (values = weights)" : " (0 and 1 only)")
            + (!directed ? " — symmetric" : "")
            + "  ";

        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 220));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(4, 8, 8, 8),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(LIGHT_BLUE, 1, true),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), MID_BLUE
            )
        ));

        add(scrollPane, BorderLayout.SOUTH);
        revalidate(); repaint();
    }

    // ─────────────────────────────────────────────────────────────
    private void updateGraph() {
        if (table == null) return;
        int n = table.getRowCount();
        graph.getNodes().clear();
        graph.getEdges().clear();

        int cx = graphPanel.getWidth()  / 2;
        int cy = graphPanel.getHeight() / 2;
        int r  = Math.min(graphPanel.getWidth(), graphPanel.getHeight()) / 2 - 60;

        for (int i = 0; i < n; i++) {
            int x = cx + (int)(r * Math.cos(2 * Math.PI * i / n - Math.PI / 2));
            int y = cy + (int)(r * Math.sin(2 * Math.PI * i / n - Math.PI / 2));
            graph.addNode(new Node(i, x, y));
        }

        if (!directed) {
            // Only upper triangle; edge exists if cell != 0
            // (for unweighted = 1, for weighted = any non-zero value)
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    int weight = readWeight(i, j + 1);
                    if (weight != 0) {
                        graph.addEdge(graph.getNodes().get(i), graph.getNodes().get(j), weight);
                    }
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) continue;
                    int weight = readWeight(i, j + 1);
                    if (weight != 0) {
                        graph.addEdge(graph.getNodes().get(i), graph.getNodes().get(j), weight);
                    }
                }
            }
        }
        graphPanel.repaint();
    }

    private int readWeight(int row, int col) {
        Object val = table.getValueAt(row, col);
        if (val == null || val.toString().trim().isEmpty()) return 0;
        try { return Integer.parseInt(val.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    // ─────────────────────────────────────────────────────────────
    private void resetAll() {
        if (scrollPane != null) { remove(scrollPane); scrollPane = null; table = null; }
        txtNodes.setText("");
        graph.getNodes().clear();
        graph.getEdges().clear();
        graphPanel.resetView();
        revalidate(); repaint();
    }

    // ── UI Helpers ─────────────────────────────────────────────

    private JSeparator verticalSeparator() {
        JSeparator s = new JSeparator(JSeparator.VERTICAL);
        s.setPreferredSize(new Dimension(1, 28));
        s.setForeground(new Color(80, 110, 150));
        return s;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(190, 215, 245));
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JTextField textField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(55, 90, 140));
        tf.setForeground(WHITE);
        tf.setCaretColor(WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 120, 170), 1, true),
            new EmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private JToggleButton createToggle(String text) {
        JToggleButton btn = new JToggleButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? LIGHT_BLUE
                    : getModel().isRollover() ? new Color(50,80,120) : new Color(40,65,100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (isSelected()) {
                    g2.setColor(new Color(180, 220, 255, 80));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
                g2.setColor(WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(115, 28));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createButton(String text, Color base, Color hover, Color press) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? press : getModel().isRollover() ? hover : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(130, 34));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
