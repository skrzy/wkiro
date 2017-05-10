package wkiro;

import org.opencv.core.Core;

public class App {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new DetectFace().run(); // sample z opencv testowo
		/*ImageDowloader id = new ImageDowloader();
	    id.getImages();*/
    }
}
