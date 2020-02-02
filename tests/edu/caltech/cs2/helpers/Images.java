package edu.caltech.cs2.helpers;

import org.junit.platform.commons.util.Preconditions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class Images {
  public static BufferedImage getImage(String resource) {
    Preconditions.notBlank(resource, "Test file " + resource + " must not be null or blank");
    try {
      return ImageIO.read(new File(resource));
    } catch (IOException e) {
      fail("Test file " + resource + " does not exist");
    }
    return null;
  }

  private static boolean within(int a, int b, double threshold) {
    System.err.println(a + ", " + b + ", " + Math.abs(a - b));
    return Math.abs(a - b) <= threshold;
  }

  private static boolean rgbEqual(int p1, int p2, double t) {
    int a1 = (p1 >> 24) & 0xff;
    int r1 = (p1 >> 16) & 0xff;
    int g1 = (p1 >> 8) & 0xff;
    int b1 = (p1) & 0xff;
    int a2 = (p2 >> 24) & 0xff;
    int r2 = (p2 >> 16) & 0xff;
    int g2 = (p2 >> 8) & 0xff;
    int b2 = (p2) & 0xff;
    return within(a1, a2, t) && within(r1, r2, t) && within(g1, g2, t) && within(b1, b2, t);
  }
  public static String rgb(int pixel) {
    int alpha = (pixel >> 24) & 0xff;
    int red = (pixel >> 16) & 0xff;
    int green = (pixel >> 8) & 0xff;
    int blue = (pixel) & 0xff;
    return "(" + alpha + ", " + red + ", " + green + ", " + blue + ")";
  }

  public static void assertImagesEqual(BufferedImage expected, BufferedImage image, double threshold) {
    assertEquals(expected.getWidth(), image.getWidth(), "image widths are different");
    assertEquals(expected.getHeight(), image.getHeight(), "image height are different");

    int width  = expected.getWidth();
    int height = expected.getHeight();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (!rgbEqual(expected.getRGB(x, y), image.getRGB(x, y), threshold)){
          fail("image is not the same as expected at (" + x + ", " + y + "): \nexpected: <" + rgb(expected.getRGB(x, y)) + ">\nactual: <" + rgb(image.getRGB(x, y)) + ">");
        }
      }
    }
  }
}
