package grafoV2;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class Edge {
    private Node from;
    private Node to;
    private int weight;
    private int curveOffset;
    private boolean directed;
    private boolean weighted;

    private static final int NODE_RADIUS = 20;

    private static final Color EDGE_COLOR        = new Color(71, 120, 180);
    private static final Color ARROW_COLOR       = new Color(30, 58, 100);
    private static final Color WEIGHT_BG_COLOR   = new Color(255, 255, 220, 235);
    private static final Color WEIGHT_TEXT_COLOR = new Color(30, 60, 110);

    public Edge(Node from, Node to, int weight, int curveOffset, boolean directed, boolean weighted) {
        this.from        = from;
        this.to          = to;
        this.weight      = weight;
        this.curveOffset = curveOffset;
        this.directed    = directed;
        this.weighted    = weighted;
    }

    public Node getFrom() { return from; }
    public Node getTo()   { return to; }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x1 = from.getX(), y1 = from.getY();
        int x2 = to.getX(),   y2 = to.getY();

        // Control point for the curve
        int dx = x2 - x1, dy = y2 - y1;
        int cpx = (x1 + x2) / 2 - dy * curveOffset / 100;
        int cpy = (y1 + y2) / 2 + dx * curveOffset / 100;

        // Arrival angle
        double tanArrX = x2 - cpx;
        double tanArrY = y2 - cpy;
        double arrivalAngle = Math.atan2(tanArrY, tanArrX);

        // Endpoint on node border
        double borderX = x2 - NODE_RADIUS * Math.cos(arrivalAngle);
        double borderY = y2 - NODE_RADIUS * Math.sin(arrivalAngle);

        // Departure angle
        double tanDepX = cpx - x1;
        double tanDepY = cpy - y1;
        double departureAngle = Math.atan2(tanDepY, tanDepX);
        double startX = x1 + NODE_RADIUS * Math.cos(departureAngle);
        double startY = y1 + NODE_RADIUS * Math.sin(departureAngle);

        g2.setColor(EDGE_COLOR);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new QuadCurve2D.Double(startX, startY, cpx, cpy, borderX, borderY));

        // Arrow for directed graphs
        if (directed) {
            drawArrow(g2, borderX, borderY, arrivalAngle);
        }

        // Weight label
        if (weighted) {
            double t = 0.5;
            double weightX = (1-t)*(1-t)*startX + 2*(1-t)*t*cpx + t*t*borderX;
            double weightY = (1-t)*(1-t)*startY + 2*(1-t)*t*cpy + t*t*borderY;

            double dxTang = 2*(1-t)*(cpx - startX) + 2*t*(borderX - cpx);
            double dyTang = 2*(1-t)*(cpy - startY) + 2*t*(borderY - cpy);
            double len = Math.hypot(dxTang, dyTang);
            double weightOffset = 12;
            if (len > 0) {
                weightX += -dyTang / len * weightOffset;
                weightY +=  dxTang / len * weightOffset;
            }

            String weightStr = String.valueOf(weight);
            Font font = new Font("Segoe UI", Font.BOLD, 12);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(weightStr);
            int h = fm.getHeight();

            int bx = (int)weightX - w/2 - 4;
            int by = (int)weightY - h/2 - 1;
            int bw = w + 8;
            int bh = h + 2;

            g2.setColor(WEIGHT_BG_COLOR);
            g2.fillRoundRect(bx, by, bw, bh, 6, 6);
            g2.setColor(new Color(160, 140, 0, 140));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(bx, by, bw, bh, 6, 6);
            g2.setColor(WEIGHT_TEXT_COLOR);
            g2.drawString(weightStr, (int)weightX - w/2, (int)weightY + fm.getAscent()/2 - 1);
        }
    }

    /**
     * Draws a filled arrowhead at point (px, py) pointing in direction "angle".
     */
    private void drawArrow(Graphics2D g2, double px, double py, double angle) {
        int length   = 14;
        int halfBase = 7;

        double baseCX = px - length * Math.cos(angle);
        double baseCY = py - length * Math.sin(angle);

        double ax1 = baseCX - halfBase * Math.sin(angle);
        double ay1 = baseCY + halfBase * Math.cos(angle);
        double ax2 = baseCX + halfBase * Math.sin(angle);
        double ay2 = baseCY - halfBase * Math.cos(angle);

        int[] xp = { (int)px,  (int)ax1, (int)ax2 };
        int[] yp = { (int)py,  (int)ay1, (int)ay2 };

        g2.setColor(ARROW_COLOR);
        g2.fillPolygon(xp, yp, 3);
        g2.setColor(EDGE_COLOR);
        g2.setStroke(new BasicStroke(1f));
        g2.drawPolygon(xp, yp, 3);
    }
}
