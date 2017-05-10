import java.io.*;
import java.net.URL;
import java.nio.file.*;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageDowloader {
	public void getImages() {
		try {
			URL url = new URL("http://image-net.org/api/text/imagenet.synset.geturls?wnid=n00523513");
	        
			BufferedReader in = new BufferedReader(
	                new InputStreamReader(url.openStream()));

	                String inputLine;
	                int counter= 0;
	                while ((inputLine = in.readLine()) != null)
	                {
	                	inputLine = inputLine.trim();
	                	try{
	                		InputStream inImage = new URL(inputLine).openStream();
	                		Files.copy(inImage, Paths.get("resources/"+counter+".jpg"));
	                	}catch (Exception e) {
	                		//System.out.print("Broken: ");
	                		//System.out.println(inputLine);
	                		continue;
						}
                		System.out.print("Correct: ");
                		System.out.println(inputLine);
                		Mat img = Imgcodecs.imread("resources/"+counter+".jpg", Imgcodecs.IMREAD_GRAYSCALE);                		
                		Mat imgResize = new Mat();
                		if(img.width()>0 &&	img.height()>0)
                		{
                    		Imgproc.resize( img, imgResize, new Size(100,100) );
                    		
                    		Imgcodecs.imwrite("resources/"+counter+".jpg", imgResize);
                    		counter++;
    	                	
                		}
                		else
                		{
                			Files.delete(Paths.get("resources/"+counter+".jpg"));
                			System.out.println("B³ad pliku");
                		}              		
	                }
	                in.close();
		} catch (Exception e) {
			System.out.println("Exception" + e.getMessage());
		}
	}
}