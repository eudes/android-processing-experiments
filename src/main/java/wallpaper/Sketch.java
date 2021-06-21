package wallpaper;


import processing.core.PApplet;
import wallpaper.noise.FastNoiseLite;
import wallpaper.noise.Noise;

public class Sketch extends PApplet {

    public void settings() {
        fullScreen();
    }

    /**/
    Noise n;

    float freq = 0.0007f;
    float amp = 2;
    float pers = 0.5f;
    int octaves = 5;
    float warpStrength = 7000;

    float z = 0;
    float zIncrement = 0.000003f;

    float rough = -2.24f;
    float maxRough = 2.45f;
    int incrementSign = 1;
    float roughIncrement = 0.00028f;

    Gradient gradient;

    public void setup() {
        n = new Noise();
        background(0);
        rough = random(-maxRough, maxRough);
        System.out.println(rough);

        int[] colors = {
                color(0),
                color(75, 43, 179),
                color(161, 133, 255),
                color(140, 163, 39),
                color(252, 239, 106)
        };
        float[] bounds = {
                0.0f,
                0.25f,
                0.5f,
                0.75f,
                1.0f
        };
        gradient = new Gradient(colors, bounds);
    }

    // Likes: 8, 7
    int reductionFactor = 5;

    public void draw() {
        loadPixels();
        //dotsPixelation();
        linePixelation();
        //pixelation();
        updatePixels();
        z += zIncrement;
        changeRough();
    }

    public void changeRough() {
        if (FastNoiseLite.FastAbs(rough) >= maxRough) {
            incrementSign *= -1;
            rough = rough + roughIncrement * incrementSign;
        }
        rough = rough + roughIncrement * incrementSign;
    }

    public void dotsPixelation() {
        // Real nice CRT vibe: reductionFactor-pixel-long horizontal lines, contiguous in hor.,
        // separated reductionFactor px vertically
        for (int y = 0; y < height; y += reductionFactor) {
            for (int x = 0; x < width; x += reductionFactor) {
                float val = domainWarp(x, y, z);
                // get pixel position in pixels array
                int i = x + y * width;
                //float c = map(val, -1, 1, 0, 255);
                // pixels[i] = color(c);
                float c = map(val, -1, 1, 0, 1);
                pixels[i] = gradient.getColor(c);
            }
        }
    }

    float domainWarp(float x, float y, float z) {
        // offset fractal noise using more noise (domain warp)
        float offsetX = n.fractal(x + 0, y + 0, z,
                freq, amp, rough, pers, octaves);
        float offsetY = n.fractal(x + 5.2f, y + 2.4f, z,
                freq, amp, rough, pers, octaves);

        return n.fractal(x + warpStrength * offsetX, y + warpStrength * offsetY, 0,
                freq, amp, rough, pers, octaves);
    }

    FastNoiseLite fnl = new FastNoiseLite(3);

    float fnlDomainWarp(float x, float y, float z) {
        // General
        fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        fnl.SetRotationType3D(FastNoiseLite.RotationType3D.None);
        fnl.SetSeed(1337);
        fnl.SetFrequency(0.03f);

        // Fractal
        fnl.SetFractalType(FastNoiseLite.FractalType.None);
        fnl.SetFractalOctaves(1);
        fnl.SetFractalLacunarity(1.7f);
        fnl.SetFractalGain(0f);
        fnl.SetFractalWeightedStrength(0f);
        fnl.SetFractalPingPongStrength(2);

        // Cellular
        fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
        fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
        fnl.SetCellularJitter(1);

        // Domain Warp
        fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
        fnl.SetRotationType3D(FastNoiseLite.RotationType3D.None);
        fnl.SetDomainWarpAmp(500);
        fnl.SetDomainWarpFrequency(0.003f + map(rough, -maxRough, maxRough, -0.001f, 0.001f) * 2);

        // Domain Warp Fractal
        fnl.SetFractalType(FastNoiseLite.FractalType.DomainWarpProgressive);
        fnl.SetFractalOctaves(5);
        fnl.SetFractalLacunarity(1.8f);
        fnl.SetFractalGain(0.6f);

        FastNoiseLite.Vector3 v = new FastNoiseLite.Vector3(x, y, z);
        fnl.DomainWarp(v);

        return fnl.GetNoise(v.x, v.y, map(rough, -maxRough, maxRough, 0, 5000));
    }

    public void linePixelation() {
        // Real nice CRT vibe: reductionFactor-pixel-long horizontal lines, contiguous in hor.,
        // separated reductionFactor px vertically
        for (int y = 0; y < height; y += reductionFactor) {
            for (int x = 0; x < width; x += reductionFactor) {
                float val = domainWarp(x, y, z);
                // get pixel position in pixels array
                int i = x + y * width;
                for (int blockX = 0; blockX < reductionFactor; blockX++) {
                    float c = map(val, -1, 1, 0, 1);
                    pixels[i + blockX] = gradient.getColor(c);
                }
            }
        }
    }


    public void pixelation() {
        // Ugly squares
        for (int y = 0; y < height; y += reductionFactor) {
            for (int x = 0; x < width; x += reductionFactor) {
                float val = domainWarp(x, y, z);
                for (int xc = 0; xc < reductionFactor; xc++) {
                    for (int yc = 0; yc < reductionFactor; yc++) {
                        int xyc = (x + xc) + (y + yc) * width;
                        if (xyc < pixels.length) {
                            float c = map(val, -1, 1, 0, 1);
                            pixels[xyc] = gradient.getColor(c);
                        }
                    }
                }
            }
        }
    }

    public void vertexPixelation() {
        // not very pretty: -----
        //                  |
        //                  |
        for (int y = 0; y < height; y += reductionFactor) {
            for (int x = 0; x < width; x += reductionFactor) {
                float val = domainWarp(x, y, z);
                // get pixel position in pixels array
                int i = x + y * width;
                for (int c = 0; c < reductionFactor; c++) {
                    int ic = i + c;
                    if (ic < pixels.length) {
                        pixels[ic] = color(map(val, -1, 1, 0, 255));
                    }
                    int xyc = x + (y + c) * width;
                    if (xyc < pixels.length) {
                        pixels[xyc] = color(map(val, -1, 1, 0, 255));
                    }
                }
            }
        }
    }


    float domainWarpSimple(float x, float y, float z) {
        // just noise, smoke

        return n.fractal(x + warpStrength, y + warpStrength, 0,
                freq, amp, rough, pers, octaves);
    }

//  float domainWarpPerlin(float x, float y, float z) {
//    // static patterned noise
//    float offsetX = n.perlin(x + 0, y + 0, z);
//    float offsetY = n.perlin(x + 5.2f, y + 2.4f, z);
//
//    return n.fractal(x + warpStrength * offsetX, y + warpStrength * offsetY, 0,
//            freq, amp, rough, pers, octaves);
//  }


}
