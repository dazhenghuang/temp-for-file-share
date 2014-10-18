package radial;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

import javax.swing.JButton;

import security.KeyGrouping;

public class InputButton extends JButton {
    private double innerDist;
    private double outerDist;
    private double radians;
    private double radialPosition;
    private KeyGrouping grouping;
    private Shape hitBox;

    // Color Constants -- These four colors conform to WCAG 2.0 guidelines for
    // readability
    private static final Color RED = new Color(179, 0, 21);
    private static final Color GREEN = new Color(0, 98, 15);
    private static final Color BLUE = new Color(0, 67, 190);
    private static final Color MAGENTA = new Color(150, 0, 161);

    public InputButton(KeyGrouping grouping) {
        super(grouping.getKeys());
        setContentAreaFilled(false);
        setBorderPainted(false);
        this.innerDist = this.outerDist = 1;
        this.radians = 2 * Math.PI;
        this.radialPosition = 0.0;
        this.hitBox = null;
        this.grouping = grouping;
    }

    public void setShape(double innerDist, double outerDist, double radians,
            double radialPosition) {
        this.innerDist = innerDist;
        this.outerDist = outerDist;
        this.radians = radians;
        this.radialPosition = radialPosition;
    }

    public void paintComponent(Graphics g) {
        // Turn on antialiasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        // Construct shape
        Arc2D.Double arc = new Arc2D.Double();
        GeneralPath shape = new GeneralPath();
        shape.moveTo(0, 0);
        shape.lineTo(0, outerDist - innerDist);
        arc.setArcByCenter(0, outerDist, innerDist, 90,
                -Math.toDegrees(radians), Arc2D.OPEN);
        shape.append(arc, true);
        shape.lineTo(Math.sin(radians) * outerDist,
                outerDist - Math.cos(radians) * outerDist);
        arc.setArcByCenter(0, outerDist, outerDist,
                90 - Math.toDegrees(radians), Math.toDegrees(radians),
                Arc2D.OPEN);
        shape.append(arc, true);
        shape.closePath();

        Color color = getColorByNumber(grouping.getColor());
        g2.setPaint(color);

        // Rotate/translate it into place
        AffineTransform at = new AffineTransform();
        at.translate(getBounds().width / 2.0, getBounds().height / 2.0
                - outerDist);
        at.rotate(radialPosition, 0, outerDist);
        shape.transform(at);
        hitBox = shape;
        g2.fill(shape);

        if(!getText().equals("") && getModel().isRollover()) {
            // This section controls the coloring when the button is highlighted
            g2.setPaint(new GradientPaint(0, 2, color, 0,
                    getBounds().height - 2 * 2, color.darker().darker()));
            g2.fill(shape);
            g2.setPaint(Color.WHITE);
        }
        else {
            // This is the default coloring.
            g2.setPaint(Color.WHITE);
        }

        // Paint text
        double ringWidth = outerDist - innerDist;
        int center_x = getBounds().width / 2;
        int center_y = getBounds().height / 2;
        int x_coord = (int) (Math.sin(radialPosition + radians / 2)
                * (outerDist - ringWidth / 2) + getBounds().width / 2 - 2);
        int y_coord = (int) (-Math.cos(radialPosition + radians / 2)
                * (outerDist - ringWidth / 2) + getBounds().height / 2);

        if(x_coord > center_x && y_coord > center_y || x_coord < center_x
                && y_coord < center_y) {
            g2.rotate(Math.toRadians(-45), x_coord, y_coord);
        }
        else {
            g2.rotate(Math.toRadians(45), x_coord, y_coord);
        }

        g2.drawString(getText(), x_coord, y_coord);
    }

    public boolean contains(int x, int y) {
        if(hitBox != null)
            return hitBox.contains(x, y);
        else
            return false;
    }

    private Color getColorByNumber(int index) {
        switch(index) {
            case 0:
                return RED;
            case 1:
                return GREEN;
            case 2:
                return BLUE;
            default:
                return MAGENTA;
        }
    }
}
