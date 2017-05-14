package wkiro.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Parser {

    private final static String POSITIVE_IMAGES_ROOT_DIR = "images";
    private final static String ANNOTATIONS_ROOT_DIR = "annotations";
    private final static String CARS_SUBDIR = "cars";
    private final static String CARS_BG_SUBDIR = "cars_bg";
    private final static String BACKGROUND_SUBDIR = "background";

    public void run() {

        try {
            List<Path> positiveSamples = getAnnotationPaths(CARS_SUBDIR);
            List<Path> negativeSamples = joinNegativeSamples(getAnnotationPaths(CARS_BG_SUBDIR),
                    getAnnotationPaths(BACKGROUND_SUBDIR), positiveSamples.size() / 2);

            List<ImageAnnotationMetadata> imageList = positiveSamples.stream()
                    .map(ImageAnnotationMetadata::fromPath).collect(Collectors.toList());
            imageList.addAll(negativeSamples.stream().map(ImageAnnotationMetadata::ofNegative)
                    .collect(Collectors.toList()));

            Collections.shuffle(imageList);

            createOutput(imageList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Path> getAnnotationPaths(final String subdir) throws IOException {
        Path positiveImagesRoot = Paths.get(POSITIVE_IMAGES_ROOT_DIR).resolve(subdir);
        Path annotationsRoot = Paths.get(ANNOTATIONS_ROOT_DIR);

        List<Path> result = new ArrayList<>();

        try (DirectoryStream<Path> directory = Files.newDirectoryStream(positiveImagesRoot)) {
            for (Path file: directory) {
                Path annotationFileDir = file.subpath(1, file.getNameCount() - 1);
                String annotationFileName = file.getFileName().toString().replace(".jpg", ".txt");
                Path newP = annotationsRoot.resolve(annotationFileDir).resolve(annotationFileName);
                Files.exists(file);
                if (Files.exists(newP)) {
                    result.add(newP);
                }
            }
        }
        return result;
    }

    private List<Path> joinNegativeSamples(final List<Path> first, final List<Path> second, final int limit) throws Exception {
        List<Path> joinedList = new ArrayList<>(first);
        joinedList.addAll(second);
        Collections.shuffle(joinedList);

        return joinedList.size() > limit ? joinedList.subList(0, limit) : joinedList;
    }

    private void createOutput(List<ImageAnnotationMetadata> images) throws IOException {
        int testSetSize = images.size() / 10;   //90% training set, 10% test set

        Path resultPath = Paths.get("results_" + System.currentTimeMillis());
        Files.createDirectory(resultPath);

        for (int i = 0; i < 10; i++) {

            Path currentIterationResultPath = resultPath.resolve(String.valueOf(i + 1));
            Path testResultPath = currentIterationResultPath.resolve("test");
            Path positiveTestResultPath = testResultPath.resolve("positive.txt");
            Path negativeTestResultPath = testResultPath.resolve("negative.txt");
            Path trainResultPath = currentIterationResultPath.resolve("train");
            Path positiveTrainResultPath = trainResultPath.resolve("positive.txt");
            Path negativeTrainResultPath = trainResultPath.resolve("negative.txt");

            Files.createDirectory(currentIterationResultPath);
            Files.createDirectory(testResultPath);
            Files.createFile(positiveTestResultPath);
            Files.createFile(negativeTestResultPath);
            Files.createDirectory(trainResultPath);
            Files.createFile(positiveTrainResultPath);
            Files.createFile(negativeTrainResultPath);

            try (
                    PrintWriter positiveTestResultOS = new PrintWriter(new FileWriter(positiveTestResultPath.toFile()));
                    PrintWriter negativeTestResultOS = new PrintWriter(new FileWriter(negativeTestResultPath.toFile()));
                    PrintWriter positiveTrainResultOS = new PrintWriter(new FileWriter(positiveTrainResultPath.toFile()));
                    PrintWriter negativeTrainResultOS = new PrintWriter(new FileWriter(negativeTrainResultPath.toFile()))
            ) {
                int firstTestSetElement = i * testSetSize;
                int lastTestSetElement = (i + 1) * testSetSize;

                Consumer<ImageAnnotationMetadata> printToTrainingFile = image -> {
                    if (image.isNegative()) {
                        negativeTrainResultOS.println(image.toString());
                    } else {
                        positiveTrainResultOS.println(image.toString());
                    }
                };

                images.stream().limit(firstTestSetElement).forEach(printToTrainingFile);
                images.stream().skip(lastTestSetElement).forEach(printToTrainingFile);

                images.stream().skip(firstTestSetElement).limit(testSetSize).filter(ImageAnnotationMetadata::isNegative)
                        .forEach(image -> negativeTestResultOS.println(image.toString()));
                images.stream().skip(firstTestSetElement).limit(testSetSize).filter(ImageAnnotationMetadata::isPositive)
                        .forEach(image -> positiveTestResultOS.println(image.toString()));
            }
        }

    }
}
