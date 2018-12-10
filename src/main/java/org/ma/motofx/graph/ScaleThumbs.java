package org.ma.motofx.graph;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 *
 * @author maria
 */
public class ScaleThumbs {

   private final static int NUMIMAGES = 6;
   private final static int IMG_WIDTH = 50;
   private final static int IMG_HEIGHT = 50;
   private final static int IMG_SPACES = 5; //Spazi tra le marce
    public ScaleThumbs(Scene scene, Group group) {

        Stop[] stops = new Stop[]{
            new Stop(0.0, Color.YELLOW),
            new Stop(0.5, Color.ORANGE),
            new Stop(1.0, Color.PINK)
        };
        LinearGradient lg = new LinearGradient(0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                stops);

      Thumbnail[] thumbnails = new Thumbnail[NUMIMAGES];
      for (int i = 0; i < NUMIMAGES; i++)
      {
         thumbnails[i] = new Thumbnail("pix/thumbs/gear" + (i + 1) + ".png");
         thumbnails[i].setWidth(IMG_WIDTH);
         thumbnails[i].setHeight(IMG_HEIGHT);
         thumbnails[i].setTranslateX(thumbnails[i].getWidth()*i+(i*IMG_SPACES));
//         thumbnails[i].setTranslateX((scene.getWidth() + 10 -
//                                     NUMIMAGES * thumbnails[i].getWidth() -
//                                     10 * (NUMIMAGES - 1)) / 2 +
//                                     i * (thumbnails[i].getWidth() + 10));
//         thumbnails[i].setTranslateY((scene.getHeight() - 
//                                     thumbnails[i].getHeight()) / 2);
         group.getChildren().add(thumbnails[i]);
      }
//
//        scene.widthProperty()
//                .addListener((observable, oldValue, newValue)
//                        -> {
//                    for (int i = 0; i < NUMIMAGES; i++) {
//                        double w = thumbnails[i].getLayoutBounds().getWidth();
//                        thumbnails[i].setTranslateX((newValue.doubleValue()
//                                + 10 - NUMIMAGES * w
//                                - 10 * (NUMIMAGES - 1)) / 2
//                                + i * (w + 10));
//                    }
//                });
//        scene.heightProperty()
//                .addListener((observable, oldValue, newValue)
//                        -> {
//                    for (int i = 0; i < NUMIMAGES; i++) {
//                        double h = thumbnails[i].getLayoutBounds().getHeight();
//                        thumbnails[i].setTranslateY((newValue.doubleValue()
//                                - h) / 2);
//                    }
//                });
    }
}
