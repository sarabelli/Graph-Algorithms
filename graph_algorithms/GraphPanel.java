package grafoV2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphPanel extends JPanel {
    private Graph graph;

    private double zoom      = 1.0;
    private double panX      = 0;
    private double panY      = 0;
    private int    dragStartX, dragStartY;
    private double panStartX, panStartY;

    private static final Color BG_COLOR   = new Color(245, 248, 252);
    private static final Color GRID_COLOR = new Color(200, 215, 235, 50);

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(BG_COLOR);

        // ── Zoom with mouse wheel ──
        addMouseWheelListener(e -> {
            double factor = (e.getWheelRotation() < 0) ? 1.1 : (1.0 / 1.1);
            // Zoom centered on cursor
            double mx = e.getX();
            double my = e.getY();
            panX = mx - factor * (mx - panX);
            panY = my - factor * (my - panY);
            zoom *= factor;
            zoom = Math.max(0.2, Math.min(zoom, 8.0));
            repaint();
        });

        // ── Pan with drag (left button) ──
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();
                panStartX  = panX;
                panStartY  = panY;
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            @Override public void mouseReleased(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
            @Override public void mouseDragged(MouseEvent e) {
                panX = panStartX + (e.getX() - dragStartX);
                panY = panStartY + (e.getY() - dragStartY);
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public void setGraph(Graph g) {
        this.graph = g;
        repaint();
    }

    /** Resets zoom and pan to center */
    public void resetView() {
        zoom = 1.0;
        panX = 0;
        panY = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Grid
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(0.5f));
        int step = 40;
        for (int x = 0; x < getWidth(); x += step) g2.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += step) g2.drawLine(0, y, getWidth(), y);

        // Apply pan and zoom transform
        Graphics2D g2t = (Graphics2D) g2.create();
        g2t.translate(panX, panY);
        g2t.scale(zoom, zoom);

        for (Edge edge : graph.getEdges()) edge.draw(g2t);
        for (Node node : graph.getNodes())  node.draw(g2t);

        g2t.dispose();

        // Zoom indicator
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(100, 130, 170, 180));
        String zoomStr = String.format("zoom %.0f%%", zoom * 100);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(zoomStr, getWidth() - fm.stringWidth(zoomStr) - 10, getHeight() - 8);
    }
}
