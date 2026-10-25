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
        
        // Turn image into 2D array
        int[][] pixels = preProcessor(image);

        String filter = "";
        while (true) {
            // Read filter name
            System.out.print("Enter filter name: [sepia, warm, cool, red, green, blue, posterize, duotone] ");
            filter = sc.nextLine().toLowerCase();
            
            // Apply filter
            if (filter.equals("sepia")) {
                pixels = sepia(pixels);
                break;
            } else if (filter.equals("warm")) {
                pixels = tint(pixels, true);
                break;
            } else if (filter.equals("cool")) {
                pixels = tint(pixels, false);
                break;
            } else if (filter.equals("red")) {
                pixels = channel(pixels, "red");
                break;
            } else if (filter.equals("green")) {
                pixels = channel(pixels, "green");
                break;
            } else if (filter.equals("blue")) {
                pixels = channel(pixels, "blue");
                break;
            } else if (filter.equals("posterize")) {
                pixels = posterize(pixels);
                break;
            } else if (filter.equals("duotone")) {
                pixels = duotone(pixels);
                break;
            } else {
                System.out.println("Filter not found, please input a valid filter.");
            }
        }

        // Turn 2D array back to image
        postProcessor(image, pixels);
        
        // Save image
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
    
    // Image pre-processor (turns image into 2D array)
    // Params: img (type BufferedImage)
    // Precondition: Img format input
    // Postcondition: 2D array output
    public static int[][] preProcessor(BufferedImage img) {
        
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
        return pixelArr;
    }

    // Image post-processor (turns 2D array back to image)
    // Params: img (type BufferedImage), pixelArr (type 2D int array)
    // Precondition: img is the original image, pixelArr is the array with modified RGB values
    // Postcondition: Altered image output
    public static BufferedImage postProcessor(BufferedImage img, int[][] pixelArr) {

        int width = pixelArr.length;
        int height = pixelArr[0].length;

        // 2D array traversal
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                img.setRGB(x, y, pixelArr[x][y]);
            }
        }
        System.out.println("Filtered!");
        return img;
    }

    // Sepia filter
    // Params: pixelArr (type 2D int array)
    // Precondition: Image was converted into a 2D array of RGB values
    // Postcondition: Altered 2D array output
    public static int[][] sepia(int[][] pixelArr) {

        // 2D array traversal
        for (int x = 0; x < pixelArr.length; x++) {
            for (int y = 0; y < pixelArr[0].length; y++) {

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
        // Return 2D array
        return pixelArr;
    }

    // Temperature tint filter (warm and cool)
    // Params: pixelArr (type 2D int array), warm (boolean that determines warm or cool shift)
    // Precondition: Image was converted into a 2D array of RGB values
    // Postcondition: Altered 2D array output
    public static int[][] tint(int[][] pixelArr, boolean warm) {

        // Filter intensity variable
        int intensity = 50;

        // 2D array traversal
        for (int x = 0; x < pixelArr.length; x++) {
            for (int y = 0; y < pixelArr[0].length; y++) {

                // Get color from array integers
                Color color = new Color(pixelArr[x][y]);
                int R = color.getRed();
                int G = color.getGreen();
                int B = color.getBlue();
                
                // New values for temperature filter (warm and cool)
                if (warm) {
                    R = Math.min(R + intensity, 255);
                    B = Math.max(B - intensity, 0);
                } else {
                    R = Math.max(R - intensity, 0);
                    B = Math.min(B + intensity, 255);
                }

                // Turn RGB back into single integer for 2D array storage!
                Color newColor = new Color(R, G, B);
                pixelArr[x][y] = newColor.getRGB();
            }
        }
        // Return 2D array
        return pixelArr;
    }

    // Channel isolation filter (RGB)
    // Params: pixelArr (type 2D int array), channel (String, determines which channel will be kept)
    // Precondition: Image was converted into a 2D array of RGB values
    // Postcondition: Altered 2D array output
    public static int[][] channel(int[][] pixelArr, String channel) {

        // 2D array traversal
        for (int x = 0; x < pixelArr.length; x++) {
            for (int y = 0; y < pixelArr[0].length; y++) {

                // Get color from array integers
                Color color = new Color(pixelArr[x][y]);
                int R = color.getRed();
                int G = color.getGreen();
                int B = color.getBlue();
                
                // New values for each isolated channel (red, green, blue)
                if (channel.equals("red")) {
                    G = 0;
                    B = 0;
                } else if (channel.equals("green")) {
                    R = 0;
                    B = 0;
                } else if (channel.equals("blue")) {
                    R = 0;
                    G = 0;
                }

                // Turn RGB back into single integer for 2D array storage!
                Color newColor = new Color(R, G, B);
                pixelArr[x][y] = newColor.getRGB();
            }
        }
        // Return 2D array
        return pixelArr;
    }

    // Posterization filter
    // Params: pixelArr (type 2D int array)
    // Precondition: Image was converted into a 2D array of RGB values
    // Postcondition: Altered 2D array output
    public static int[][] posterize(int[][] pixelArr) {

    // Got method from here! https://arielortiz.info/s202613/tc2038/image_processing/image_processing.html#_posterization
        
        // Factor variable (higher value -> less colors)
        int factor = 64;

        // 2D array traversal
        for (int x = 0; x < pixelArr.length; x++) {
            for (int y = 0; y < pixelArr[0].length; y++) {

                // Get color from array integers
                Color color = new Color(pixelArr[x][y]);
                int R = color.getRed();
                int G = color.getGreen();
                int B = color.getBlue();
                
                // New values from using integer division to reduce variation of color
                R = (R / factor) * factor;
                G = (G / factor) * factor;
                B = (B / factor) * factor;

                // Turn RGB back into single integer for 2D array storage!
                Color newColor = new Color(R, G, B);
                pixelArr[x][y] = newColor.getRGB();
            }
        }
        // Return 2D array
        return pixelArr;
    }

    // Duotone filter
    // Params: pixelArr (type 2D int array)
    // Precondition: Image was converted into a 2D array of RGB values
    // Postcondition: Altered 2D array output
    public static int[][] duotone(int[][] pixelArr) {

    // Got method from here! https://medium.com/@elonskolnik/processing-image-manipulation-posterization-dcf89472a246
    
        // Define two colors I like for the palette :D
        Color light = new Color(255, 0, 166); // Pink
        Color dark = new Color(27, 74, 255); // Blue

        // Set cutoff for dark vs. light
        int threshold = 128; // Middle of 0-255

        // 2D array traversal
        for (int x = 0; x < pixelArr.length; x++) {
            for (int y = 0; y < pixelArr[0].length; y++) {

                // Get color from array integers
                Color color = new Color(pixelArr[x][y]);
                int R = color.getRed();
                int G = color.getGreen();
                int B = color.getBlue();
                
                // Average brightness 
                int brightness = (R + G + B) / 3;

                // Replace pixel RGB value with palette based on brightness
                if (brightness < threshold) {
                    pixelArr[x][y] = dark.getRGB();
                } else {
                    pixelArr[x][y] = light.getRGB();
                }
            }
        }
        // Return 2D array with replaced pixels
        return pixelArr;
    }
}