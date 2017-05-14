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

        int xMinFinal = (int) (xMin * scaleX);
        int yMinFinal = (int) (yMin * scaleY);
        int xMaxFinal = (int) (xMax * scaleX);
        int yMaxFinal = (int) (yMax * scaleY);

        return String.format(
                "%d %d %d %d",
                xMinFinal,
                yMinFinal,
                xMaxFinal - xMinFinal,
                yMaxFinal - yMinFinal
        );
    }
}
