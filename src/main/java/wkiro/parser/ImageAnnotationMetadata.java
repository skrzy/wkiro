package wkiro.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageAnnotationMetadata {

    private Path path;

    private int width;

    private int height;

    private List<BoundingBox> objects;

    private static int PROCESSED_IMAGE_WIDTH = 150;
    private static int PROCESSED_IMAGE_HEIGHT = 150;

    private ImageAnnotationMetadata(Path path, int width, int height, List<BoundingBox> objects) throws Exception {
        if (width == 0 || height == 0) throw new Exception("Width and height can not be zero.");
        this.path = path;
        this.width = width;
        this.height = height;
        this.objects = objects;
    }

    private ImageAnnotationMetadata(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<BoundingBox> getObjects() {
        return objects;
    }

    public boolean isPositive() {
        return !isNegative();
    }

    public boolean isNegative() {
        return Objects.isNull(objects) || objects.isEmpty();
    }

    public static ImageAnnotationMetadata ofNegative(Path annotationPath) {
        return new ImageAnnotationMetadata(getRelativePath(annotationPath));
    }

    public static ImageAnnotationMetadata fromPath(Path annotationPath) throws RuntimeException {
        final String IMAGE_SIZE_REGEX = "Image size";
        final String BOUNDING_BOX_REGEX = "Bounding box for object";
        final String NUMBER_REGEX = "\\d+";

        try (BufferedReader inputStream = new BufferedReader(new FileReader(annotationPath.toFile()))) {

            Pattern imageSizePattern = Pattern.compile(IMAGE_SIZE_REGEX);
            Pattern boundingBoxPattern = Pattern.compile(BOUNDING_BOX_REGEX);
            Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

            int width = 0, height = 0;
            List<BoundingBox> boundingBoxes = new ArrayList<>();

            String line;
            while ((line = inputStream.readLine()) != null) {

                if (imageSizePattern.matcher(line).find()) {
                    Matcher numberMatcher = numberPattern.matcher(line);
                    width = getNextNumberInLine(numberMatcher);
                    height = getNextNumberInLine(numberMatcher);
                } else if (boundingBoxPattern.matcher(line).find()) {
                    Matcher numberMatcher = numberPattern.matcher(line);
                    getNextNumberInLine(numberMatcher);     //object number
                    int xMin = getNextNumberInLine(numberMatcher);
                    int yMin = getNextNumberInLine(numberMatcher);
                    int xMax = getNextNumberInLine(numberMatcher);
                    int yMax = getNextNumberInLine(numberMatcher);
                    boundingBoxes.add(new BoundingBox(xMin, yMin, xMax, yMax));
                }

            }

            return new ImageAnnotationMetadata(getRelativePath(annotationPath), width, height, boundingBoxes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getRelativePath(Path originalPath) {
        Path fileDir = originalPath.subpath(1, originalPath.getNameCount() - 1);
        String fileName = originalPath.getFileName().toString().replace(".txt", ".jpg");
        return fileDir.resolve(fileName);
    }

    private static int getNextNumberInLine(Matcher matcher) throws Exception {
        if (matcher.find()) {
            return Integer.valueOf(matcher.group());
        } else {
            throw new Exception("Expected number not found");
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.path.toString().replace("\\", "/"));

        if (this.isPositive()) {
            stringBuilder.append(" ");
            stringBuilder.append(this.objects.size());
            for (BoundingBox object : objects) {
                stringBuilder.append(" ");
                stringBuilder.append(object.toString(
                        PROCESSED_IMAGE_WIDTH / (double) this.width,
                        PROCESSED_IMAGE_HEIGHT / (double) this.height
                ));
            }
        }

        return stringBuilder.toString();
    }
}
