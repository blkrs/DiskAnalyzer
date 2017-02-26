package com.diskscanner;
import javax.swing.ImageIcon;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ImageUtils {
	/** Returns an ImageIcon, or null if the path was invalid. */
	public  static ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = ImageUtils.class.getClassLoader().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
}
