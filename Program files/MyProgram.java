// Imports!
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class MyProgram
{
    public static void main(String[] args)
    {
        // Scanner setup
        Scanner sc = new Scanner(System.in);
        BufferedImage image = null;
        String filename = "";
        
        // Loop for valid filename
        while (image == null) {
            System.out.print("Enter image filename: ");
            filename = sc.nextLine();
            try {
                File file = new File(filename);
                if (file.exists()) {
                    image = ImageIO.read(file);
                } else {
                    System.out.println("File not found, please retry.");
                }
            } catch (IOException e) {
                System.out.println("File read error, please retry.");
            }
        }
        
        // Read filter name
        System.out.print("Enter filter name: [sepia] ");
        String filter = sc.nextLine().toLowerCase();
        
        // Apply filter
        if (filter.equals("sepia")) {
            image = sepia(image);
        } else {
            System.out.println("Filter not found, saving original image.");
        }
        
        // Save image if possible
        try {
            String outputName = "filtered_" + filter + "_" + filename;
            File output = new File(outputName);
            ImageIO.write(image, "png", output);
            System.out.println("Image saved!");
        } catch (IOException e) {
            System.out.println("Error saving the image.");
        }

        sc.close();
    }
    
    // Sepia filter
    // Params: img (type BufferedImage)
    // Precondition: Img format input
    // Postcondition: Altered image, img format output
    public static BufferedImage sepia(BufferedImage img) {
        
        // 2D array setup from image (pixels)
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] pixelArr = new int[width][height];
        
            // 2D array traversal
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelArr[x][y] = img.getRGB(x, y);
            }
        }
        
        // Image alteration
            // 2D array traversal
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get color from array integers
                Color color = new Color(pixelArr[x][y]);
                int R = color.getRed();
                int G = color.getGreen();
                int B = color.getBlue();
                
                // New values for sepia filter
                    // Got them from here! https://cs50.harvard.edu/college/2019/fall/psets/4/filter/less/
                int newR = (int)(R*0.393 + G*0.769 + B*0.189);
                int newG = (int)(R*0.349 + G*0.686 + B*0.168);
                int newB = (int)(R*0.272 + G*0.534 + B*0.131);
                
                    // Limiting values to 255 max
                newR = Math.min(newR, 255);
                newG = Math.min(newG, 255);
                newB = Math.min(newB, 255);
                
                // Turn RGB back into single integer for 2D array storage!
                Color newColor = new Color(newR, newG, newB);
                pixelArr[x][y] = newColor.getRGB();
            }
        }
        
        // Convert 2D array back to image
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, pixelArr[x][y]);
            }
        }
        
        System.out.println("Filtered!");
        return img;
    }
    
}