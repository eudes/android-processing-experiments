package wallpaper;

import processing.core.PApplet;
import processing.core.PGraphics;

import static processing.opengl.PGL.RGB;

public class Gradient {

    private int[] colors;
    private float[] bounds;

    public Gradient(int[] c, float[] b) {
      colors = c;
      bounds = b;
    }

    public int getColor(float amt) {
      for (int i = 1; i < bounds.length; i++) {
        if (amt < bounds[i]) {
          float percent = (amt - bounds[i-1]) / (bounds[i] - bounds[i-1]);
          return PGraphics.lerpColor(colors[i-1], colors[i], percent, RGB);
        }
      }

      return colors[colors.length - 1];
    }
  }