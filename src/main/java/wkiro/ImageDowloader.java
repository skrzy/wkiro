import java.io.*;
import java.net.URL;
import java.nio.file.*;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageDowloader {

	public void getLARSImages() {
		for (int i = 1; i <= 1370; i++) {
			String number=""+i;
			int length=number.length();
			for(int j=0; j < 4 - length; j++ )
			{
				number="0"+number;
			}
			String url = "http://lars.mec.ua.pt/public/Media/ResearchDevelopmentProjects/HaarFeatures_RoadFilms/HaarFeaturesTests/CarsRear/Caltech/Annotations/cars_bg/image_"
					+  number + ".txt";
			System.out.println(url);
			try {
				InputStream inImage = new URL(url).openStream();
				Files.copy(inImage, Paths.get("resources/Annotations/cars_bg/"+number+".txt"));
			} catch (Exception e) {
				 //System.out.print(e.getMessage());
				continue;
			}
			/*Mat img = Imgcodecs.imread("resources/cars_bg/"+number+".t", Imgcodecs.IMREAD_GRAYSCALE);
			Imgcodecs.imwrite("resources/cars_bg_grey/"+number+".jpg", img);*/
			
			System.out.print("Correct: ");
			System.out.println(url);
		}
	}

	public void getImages() {
		try {
			URL url = new URL("http://image-net.org/api/text/imagenet.synset.geturls?wnid=n00007846");

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String inputLine;
			int counter = 0;
			while ((inputLine = in.readLine()) != null) {
				String filePathName = "resources/neg/" + counter + ".jpg";
				inputLine = inputLine.trim();
				try {
					InputStream inImage = new URL(inputLine).openStream();
					Files.copy(inImage, Paths.get(filePathName));
				} catch (Exception e) {
					// System.out.print("Broken: ");
					// System.out.println(inputLine);
					continue;
				}
				System.out.print("Correct: ");
				System.out.println(inputLine);
				Mat img = Imgcodecs.imread(filePathName, Imgcodecs.IMREAD_GRAYSCALE);
				Mat imgResize = new Mat();
				if (img.width() > 0 && img.height() > 0) {
					Imgproc.resize(img, imgResize, new Size(100, 100));

					Imgcodecs.imwrite(filePathName, imgResize);
					counter++;

				} else {
					Files.delete(Paths.get(filePathName));
					System.out.println("B³ad pliku");
				}
			}
			in.close();
		} catch (Exception e) {
			System.out.println("Exception" + e.getMessage());
		}
	}

	public void createBGFile() {
		try {

			File bgFile = new File("resources/APictures/info.txt");
			FileOutputStream fileOutputStream = new FileOutputStream(bgFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			Writer w = new BufferedWriter(outputStreamWriter);

			File folder = new File("resources/APictures/cars_grey");
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile()) {
					w.write("cars_grey/" + file.getName() + " 1 0 0 150 150"+System.getProperty("line.separator"));
				}
			}

			w.close();
		} catch (IOException e) {
			System.err.println("Problem writing to the file bg.txt");
		}
	}

	public void toGreyScale() {
		Mat img = Imgcodecs.imread("resources/pos/zegarek.jpg", Imgcodecs.IMREAD_GRAYSCALE);
		Imgcodecs.imwrite("resources/pos/zegarek.jpg", img);
	}
}