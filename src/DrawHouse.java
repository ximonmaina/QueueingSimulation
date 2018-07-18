
import java.awt.*;
import java.awt.*;
import java.awt.event.*;

public class DrawHouse {
//flowers
    //flowers
    //base colors

    static int base1 = 0;
    static int base2 = 0;
    static int base3 = 0;

    //
//    roof color
    static int roof1 = 0;
    static int roof2 = 0;
    static int roof3 = 0;

    static int flower1 = 0;
    static int flower2 = 0;
    static int flower3 = 0;
    static Graphics2D g2;
    static Graphics paint;
    public static void main(String args[]) {
            paintComponent(paint);
    }

    public static void paintComponent(Graphics paint) {

       
        Color petal = randomColor("flowers");

        //loop to draw more flowers
        for (int space = 0;
                space
                <= 400;) {

            g2 = (Graphics2D) paint;

            // Draw the stem.
            g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(0, 128, 0));  // green
            g2.drawLine(toScreenX(100 + space), toScreenY(400), toScreenX(100 + space), toScreenY(350));

            // Draw the center.
            g2.setColor(new Color(255, 165, 0));  // orange
            g2.fillOval(toScreenX(85 + space), toScreenY(340), toScreenX(30), toScreenY(30));

            // Draw eight petals at N, NE, E, SE, S, SW, W, NW positions on the center.
            int petalWidth = toScreenX(20);
            int petalHeight = toScreenY(20);
            g2.setColor(petal);  // pink

            g2.fillOval(toScreenX(90 + space), toScreenY(320), petalWidth, petalHeight);
            g2.fillOval(toScreenX(90 + space), toScreenY(370), petalWidth, petalHeight);
            g2.fillOval(toScreenX(64 + space), toScreenY(350), petalWidth, petalHeight);
            g2.fillOval(toScreenX(115 + space), toScreenY(350), petalWidth, petalHeight);
            g2.fillOval(toScreenX(108 + space), toScreenY(368), petalWidth, petalHeight);
            g2.fillOval(toScreenX(108 + space), toScreenY(332), petalWidth, petalHeight);
            g2.fillOval(toScreenX(72 + space), toScreenY(368), petalWidth, petalHeight);
            g2.fillOval(toScreenX(72 + space), toScreenY(332), petalWidth, petalHeight);

            space += 100;

        }
    }
    //method to generate random color

    public static Color randomColor(String type) {

        int R = (int) (Math.random() * 256);
        int G = (int) (Math.random() * 256);
        int B = (int) (Math.random() * 256);

        Color color = new Color(R, G, B);
        if (type.equals("flowers")) {
            flower1 = R;
            flower2 = G;
            flower3 = G;
        }

        if (type.equals("base")) {
            base1 = R;
            base2 = G;
            base3 = B;
        }
        if (type.equals("triangle")) {
            roof1 = R;
            roof2 = G;
            roof3 = B;
        }

        return color;

    }

    /**
     * Converts an x-coordinate from a 200-width screen to the actual width.
     */
    private static int toScreenX(int x) {
//    return Math.round(x * getWidth() / 200f);
        return x;
    }

    /**
     * Converts an y-coordinate from a 200-width screen to the actual width.
     */
    private static int toScreenY(int y) {
//    return Math.round(y * getHeight() / 200f);
        return y;
    }

}
