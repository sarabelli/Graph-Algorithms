package grafoV2;

import java.awt.*;

public class Node {
    private int id;
    private int x, y;
    private static final int SIZE = 40;

    private static final Color NODE_COLOR   = new Color(135, 206, 235);
    private static final Color BORDER_COLOR = new Color(20, 45, 85);

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() { return id; }
    public int getX()  { return x; }
    public int getY()  { return y; }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(x - SIZE/2 + 3, y - SIZE/2 + 4, SIZE, SIZE);

        // Fill
        g2d.setColor(NODE_COLOR);
        g2d.fillOval(x - SIZE/2, y - SIZE/2, SIZE, SIZE);

        // Border
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawOval(x - SIZE/2, y - SIZE/2, SIZE, SIZE);

        // Label
        g2d.setColor(Color.WHITE);
        Font font = new Font("Segoe UI", Font.BOLD, 14);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        String label = String.valueOf(id);
        int textX = x - fm.stringWidth(label) / 2;
        int textY = y + fm.getAscent() / 2 - 2;
        g2d.drawString(label, textX, textY);
    }
}
