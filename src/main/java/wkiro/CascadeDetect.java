import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class CascadeDetect {
	private CascadeClassifier carCascade;

	private int TP, TN, FP, FN;

	private double tolerance = 0.15;

	public void detectAndCalculateEfficiency() {
		// File folder = new File("resources/cascades");
		// File[] listOfFiles = folder.listFiles((d, name) ->
		// name.endsWith(".xml"));

		int numberOfCascades = 1;// listOfFiles.length;

		for (int i = numberOfCascades; i < numberOfCascades + 1; i++) {
			System.out.println(System.getProperty("user.dir") + " Cascade " + i);
			carCascade = new CascadeClassifier("resources/cascades/" + i + "/cascade.xml");

			try {
				List<String> positiveImgFilelist = Files
						.readAllLines(Paths.get("resources/cascades/" + i + "/positive.txt"));
				System.out.println("Testowanie pozytywnymi obrazami");
				for (String posFile : positiveImgFilelist) {
					String[] arguments = posFile.split(" ");
					Mat img = Imgcodecs.imread("resources/" + arguments[0], Imgcodecs.IMREAD_GRAYSCALE);
					MatOfRect cars = new MatOfRect();
					carCascade.detectMultiScale(img, cars, 1.05, 3, 0, new Size(50, 50), new Size(150, 150));

					Rect expectedRect = new Rect(
							new Point(Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3])),
							new Point(Integer.parseInt(arguments[2]) + Integer.parseInt(arguments[4]),
									Integer.parseInt(arguments[3]) + Integer.parseInt(arguments[5])));

					boolean expectedFound = false;
					System.out.println(arguments[0]);

					for (Rect carRect : cars.toArray()) {
						boolean found = false;
						if (expectedFound)
							continue;

						if (areSameEnoughSurface(expectedRect, carRect)) {
							expectedFound = true;
							found = true;
						}

						if (!found)
							FP++;
					}

					for (Rect r : cars.toList()) {
						Imgproc.rectangle(img, r.tl(), r.br(), new Scalar(255, 0, 0), 1);
					}
					Imgproc.rectangle(img, expectedRect.tl(), expectedRect.br(), new Scalar(0, 0, 255), 1);
					
					Imgcodecs.imwrite(arguments[0], img);

					if (expectedFound)
						TP++;
					else
						FN++;

				}
				System.out.println("Testowanie negatywnymi obrazami");
				List<String> negativeImgFilelist = Files
						.readAllLines(Paths.get("resources/cascades/" + i + "/negative.txt"));
				for (String negFile : negativeImgFilelist) {
					String[] arguments = negFile.split(" ");
					Mat img = Imgcodecs.imread("resources/" + arguments[0], Imgcodecs.IMREAD_GRAYSCALE);
					MatOfRect cars = new MatOfRect();
					carCascade.detectMultiScale(img, cars, 1.05, 5, 0, new Size(70, 70), new Size(150, 150));

					if (cars.toArray().length > 0) {
						for (Rect r : cars.toList()) {
							Imgproc.rectangle(img, r.tl(), r.br(), new Scalar(255, 0, 0), 1);
						}
						Imgcodecs.imwrite(arguments[0], img);
						System.out.println("Wykryto b³êdnie " + cars.toArray().length);
						FP += cars.toArray().length;
					} else {
						TN++;
					}

				}

			} catch (Exception e) {
				System.out.println("Dupa " + e.toString());
			}
			System.out.println("KONIEC");
		}
		System.out.println("TP " + TP);
		System.out.println("FP " + FP);
		System.out.println("TN " + TN);
		System.out.println("FN " + FN);
		System.out.println("Wra¿liwoœæ " + (double) TP / (double) (TP + FN));
		System.out.println("Specyficznoœæ " + (double) TN / (double) (FP + TN));
	}

	private boolean areSameEnough(Rect rectExpected, Rect rectCalculated) {
		/*
		 * System.out.println("Expected "+ rectExpected.toString());
		 * System.out.println("Calculated "+ rectCalculated.toString());
		 */

		int Xtolerance = (int) (tolerance * (double) rectExpected.width + 1.0);
		int Ytolerance = (int) (tolerance * (double) rectExpected.height + 1.0);

		/*
		 * System.out.println("Xtolerance "+ Xtolerance);
		 * System.out.println("Ytolerance "+ Ytolerance);
		 */

		boolean b = Math.abs(rectCalculated.x - rectExpected.x) < Xtolerance
				&& Math.abs(rectCalculated.y - rectExpected.y) < Ytolerance
				&& Math.abs(
						(rectCalculated.x + rectCalculated.width) - (rectExpected.x + rectExpected.width)) < Xtolerance
				&& Math.abs((rectCalculated.y + rectCalculated.height)
						- (rectExpected.y + rectExpected.height)) < Ytolerance;
		// System.out.println(b+"");
		return b;
	}

	private boolean areSameEnoughSurface(Rect rectExpected, Rect rectCalculated) {

		int rectCalculatedArea = rectCalculated.width * rectCalculated.height;

		int outerWidth = Math.max(rectExpected.x - rectCalculated.x, 0)
				+ Math.max((rectCalculated.x + rectCalculated.width) - (rectExpected.x + rectExpected.width), 0);
		int outerheight = Math.max(rectExpected.y - rectCalculated.y, 0)
				+ Math.max((rectCalculated.y + rectCalculated.height) - (rectExpected.y + rectExpected.height), 0);

		/*
		 * System.out.println(rectCalculated); System.out.println(rectExpected);
		 * System.out.println(Math.max(rectExpected.x-rectCalculated.x, 0));
		 * System.out.println(Math.max((rectCalculated.x+rectCalculated.width)-(
		 * rectExpected.x+rectExpected.width), 0));
		 * System.out.println(Math.max(rectExpected.y-rectCalculated.y, 0));
		 * System.out.println(Math.max((rectCalculated.y+rectCalculated.height)-
		 * (rectExpected.y+rectExpected.height),0));
		 * System.out.println(outerWidth); System.out.println(outerheight);
		 */

		int rectCalculatedInArea = (rectCalculated.width - outerWidth) * (rectCalculated.height - outerheight);

		/*
		 * System.out.println("rectCalculatedArea "+ rectCalculatedArea);
		 * System.out.println("rectCalculatedInArea "+ rectCalculatedInArea);
		 */

		boolean b = (double) (rectCalculatedInArea) / (double) (rectCalculatedArea) > 1.0 - tolerance;
		// System.out.println(b+"");
		return b;

	}
}
