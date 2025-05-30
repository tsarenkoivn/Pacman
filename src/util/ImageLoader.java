package util;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.net.URL; // Import URL

public class ImageLoader {
    // Keep this as is. This is the correct absolute path from the classpath root.
    private static final String IMAGE_PATH_PREFIX = "/resources/images/";
    private static Map<String, Image> imageCache = new HashMap<>();

    public static Image loadImage(String fileName) {
        if (imageCache.containsKey(fileName)) {
            return imageCache.get(fileName);
        }

        String fullPath = IMAGE_PATH_PREFIX + fileName;
        System.out.println("Attempting to load image from classpath: " + fullPath); // Add this line for debugging

        try {
            // Get the URL for the resource. This is where 'location' might be null.
            URL imageUrl = ImageLoader.class.getResource(fullPath);

            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                    imageCache.put(fileName, icon.getImage());
                    System.out.println("Successfully loaded image: " + fullPath); // Confirm success
                    return icon.getImage();
                } else {
                    System.err.println("Failed to load image (status incomplete): " + fullPath);
                    return null;
                }
            } else {
                // This is the common case for your error: imageUrl is null
                System.err.println("Image URL is null. Resource not found on classpath: " + fullPath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading image " + fullPath + ": " + e.getMessage());
            return null;
        }
    }
}
