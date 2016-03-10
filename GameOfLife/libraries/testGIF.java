import java.awt.Color;

/*
* A demonstration of the GIFWriter class.
* This program makes a GIF image file with a simple animation.
*/

public class TestGIF {

	public static void main(String[] args) throws Exception {
		
		// data related to the GIF image file
		String path = "testgif.gif";
		int width = 100;
		int height = 100;
		int timePerMilliSecond = 1000; // 1 second
		
		// create the GIFWriter object
		lieng.GIFWriter gwriter = new lieng.GIFWriter(width,height,path,timePerMilliSecond);
		
		// fill the upper half of the image with blue
		gwriter.fillRect(0, width-1, 0, height/2, Color.BLUE);
		
		// insert the painted image to the animation sequence 
		// and proceed to the next image
		gwriter.insertAndProceed();
		
		// fill the lower half of the image with blue
		gwriter.fillRect(0, width-1, height/2, height-1, Color.BLUE);
		
		// insert the painted image into the animation sequence
		gwriter.insertCurrentImage();
		
		// close the GIF stream.
		gwriter.close();
		
		System.out.println("done!");
		
	}
}
