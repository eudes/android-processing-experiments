import processing.core.PApplet;
import wallpaper.Sketch;

public class RunSketch {

    public static void main(String... args){
        String[] pargs = new String[] {"Sketch"};
        //PApplet.main(pargs);
        PApplet.runSketch(pargs, new Sketch());
    }

}
