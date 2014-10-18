package radial;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class Background extends JPanel {
    public static final int BORDER = 2;
    protected int rings;
    protected int sectionsPerRing;

    public Background(int rings, int sectionsPerRing) {
        super();
        this.rings = rings;
        this.sectionsPerRing = sectionsPerRing;
    }

    public void paintComponent(Graphics g) {
        // Turn on antialiasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        // Make some quick calculations
        int size = getSize().width;
        double center = size / 2.0;
        double radius = center - BORDER;
        double ringWidth = radius / (rings + 1);
        double sectionRadians = Math.PI * 2 / sectionsPerRing;

        // Set up a few colors
        Color gray1 = new Color(217, 217, 217);
        Color gray2 = new Color(229, 229, 229);

        // Paint background
        g2.setPaint(new GradientPaint(0, 0, Color.GRAY, 0, size, Color.WHITE));
        g2.fill(new Ellipse2D.Double(0, 0, size, size));
        g2.setPaint(new GradientPaint(0, BORDER, gray2, 0, size - 2 * BORDER,
                gray1));
        g2.fill(new Ellipse2D.Double(BORDER, BORDER, size - 2 * BORDER, size
                - 2 * BORDER));

        // Draw sections
        g2.setPaint(new Color(200, 200, 200));
        for (int i = 0; i < sectionsPerRing; ++i)
            g2.draw(new Line2D.Double(center, center, Math.sin(sectionRadians
                    * i)
                    * radius + center, -Math.cos(sectionRadians * i) * radius
                    + center));

        // Draw rings
        for (int j = 2; j <= rings; ++j)
            g2.draw(new Ellipse2D.Double(center - j * ringWidth, center - j
                    * ringWidth, 2 * ringWidth * j, 2 * ringWidth * j));
    }
}
