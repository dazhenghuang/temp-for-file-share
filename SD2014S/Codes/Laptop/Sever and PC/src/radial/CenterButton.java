package radial;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;

public class CenterButton extends JButton {
    protected double diameter;

    public CenterButton(String label) {
        super(label);
        setContentAreaFilled(false);
        setBorderPainted(false);
        diameter = 0;
    }

    public void setDiameter(double diameter) {
        if (diameter < 0)
            diameter = 0;
        this.diameter = diameter;
    }

    public void paintComponent(Graphics g) {
        // Turn on antialiasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        // Set up some colors
        GradientPaint borderPaint, fillPaint;
        Color gray1 = new Color(229, 229, 229);
        Color gray2 = new Color(235, 235, 235);

        // Center the graphics context
        double translationXY = getBounds().width / 2.0 - diameter / 2;
        g2.translate(translationXY, translationXY);

        // Draw the button
        int pushOffset = 4;
        if (getModel().isArmed()) {
            borderPaint = new GradientPaint(0, 0, Color.GRAY, 0,
                    (int) diameter, Color.WHITE);
            fillPaint = new GradientPaint(0, 1, gray1, 0, (int) diameter - 2,
                    gray2);
            pushOffset = 6;
        }
        else {
            borderPaint = new GradientPaint(0, 0, Color.WHITE, 0,
                    (int) diameter, Color.GRAY);
            fillPaint = new GradientPaint(0, 1, gray2, 0, (int) diameter - 2,
                    gray1);
        }
        g2.setPaint(borderPaint);
        g2.fill(new Ellipse2D.Double(0, 0, diameter, diameter));
        g2.setPaint(fillPaint);
        g2.fill(new Ellipse2D.Double(1, 1, diameter - 2, diameter - 2));

        g2.setPaint(Color.BLACK);
        int strWidth = g.getFontMetrics().stringWidth(getText());
        g2.drawString(getText(), (int) (diameter / 2 - strWidth / 2),
                (int) (diameter / 2 + pushOffset));

    }

    public boolean contains(int x, int y) {
        int center = getBounds().width / 2;
        double x2 = Math.pow(center - x, 2);
        double y2 = Math.pow(center - y, 2);
        return (Math.sqrt(x2 + y2) <= diameter / 2);
    }
}
