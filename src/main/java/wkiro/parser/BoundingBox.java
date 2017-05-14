package wkiro.parser;

public class BoundingBox {

    private int xMin;
    private int yMin;
    private int xMax;
    private int yMax;

    public BoundingBox(int xMin, int yMin, int xMax, int yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public String toString(double scaleX, double scaleY) {

        return String.format(
                "%d %d %d %d",
                (int) (xMin * scaleX),
                (int) (yMin * scaleY),
                (int) (xMax * scaleX),
                (int) (yMax * scaleY)
        );
    }
}
