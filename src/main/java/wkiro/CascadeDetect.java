import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
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

	private double tolerance = 0.1;

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
					//Imgcodecs.imwrite("tstdbg.jpg", img);
					MatOfRect cars = new MatOfRect();
					carCascade.detectMultiScale(img, cars);

					List<Rect> expectedRects = new ArrayList<Rect>();
					for (int j = 2; j < 2 + 4 * (Integer.parseInt(arguments[1])); j += 4) {
						expectedRects.add(new Rect(
								new Point(Integer.parseInt(arguments[j]), Integer.parseInt(arguments[j + 1])),
								new Point(Integer.parseInt(arguments[j]) + Integer.parseInt(arguments[j + 2]),
										Integer.parseInt(arguments[j + 1]) + Integer.parseInt(arguments[j + 3]))));
					}
					boolean[] expectedFound = new boolean[expectedRects.size()];
					//if(arguments[0].equals("cars/0104.jpg"))
					//{
						System.out.println(arguments[0]);
					//}
					for (Rect carRect : cars.toArray()) {
						boolean found = false;
						for (int j = 0; j < expectedRects.size(); j++) {
							if (expectedFound[j])
								continue;

							if (areSameEnough(expectedRects.get(j), carRect))
							{
								expectedFound[j]=true;
								found = true;
							}
						}
						if (!found)
							FP++;
					}
					
					for(Rect r : cars.toList())
					{
						Imgproc.rectangle(img, r.tl(), r.br(), new Scalar(255, 0, 0), 1);
					}
					for(Rect r : expectedRects)
					{
						Imgproc.rectangle(img, r.tl(), r.br(), new Scalar(0, 0, 255), 1);
					}
					Imgcodecs.imwrite(arguments[0], img);

					for (int j = 0; j < expectedFound.length; j++) {
						if (expectedFound[j])
							TP++;
						else
							FN++;
					}
				}
				System.out.println("Testowanie negatywnymi obrazami");
				List<String> negativeImgFilelist = Files
						.readAllLines(Paths.get("resources/cascades/" + i + "/negative.txt"));
				for (String negFile : negativeImgFilelist) {
					String[] arguments = negFile.split(" ");
					Mat img = Imgcodecs.imread("resources/" + arguments[0], Imgcodecs.IMREAD_GRAYSCALE);
					MatOfRect cars = new MatOfRect();
					carCascade.detectMultiScale(img, cars, 1.05, 3, 0, new Size(10, 10), new Size(150, 150));
					
					if (cars.toArray().length > 0) {
						for(Rect r : cars.toList())
						{
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
				System.out.println("Dupa" + e.toString());
			}
			System.out.println("KONIEC");
		}
		System.out.println("TP " + TP);
		System.out.println("FP " + FP);
		System.out.println("TN " + TN);
		System.out.println("FN " + FN);
	}

	private boolean areSameEnough(Rect rectExpected, Rect rectCalculated) {
		System.out.println("Expected "+ rectExpected.toString());
		System.out.println("Calculated "+ rectCalculated.toString());
		
		int Xtolerance = (int) (tolerance * (double) rectExpected.width + 1.0);
		int Ytolerance = (int) (tolerance * (double) rectExpected.height + 1.0);
		
		System.out.println("Xtolerance "+ Xtolerance);
		System.out.println("Ytolerance "+ Ytolerance);
		
		boolean b = Math.abs(rectCalculated.x - rectExpected.x) < Xtolerance
				&& Math.abs(rectCalculated.y - rectExpected.y) < Ytolerance
				&& Math.abs((rectCalculated.x + rectCalculated.width) - (rectExpected.x + rectExpected.width)) < Xtolerance
				&& Math.abs(
						(rectCalculated.y + rectCalculated.height) - (rectExpected.y + rectExpected.height)) < Ytolerance;
		System.out.println(b+"");
		return b;
	}

}
