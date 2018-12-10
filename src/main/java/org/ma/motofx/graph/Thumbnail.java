package org.ma.motofx.graph;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import javafx.scene.Parent;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import javafx.util.Duration;

/**
 *
 * @author maria
 */
class Thumbnail extends Parent {

    public Thumbnail(String image) {
        final ScaleTransition stBig = new ScaleTransition();
        stBig.setNode(this);
        stBig.setFromX(1.0);
        stBig.setFromY(1.0);
        stBig.setToX(2.0);
        stBig.setToY(2.0);
        stBig.setDuration(new Duration(500));

        final ScaleTransition stSmall = new ScaleTransition();
        stSmall.setNode(this);
        stSmall.setFromX(2.0);
        stSmall.setFromY(2.0);
        stSmall.setToX(1.0);
        stSmall.setToY(1.0);
        stSmall.setDuration(new Duration(1000));

        Rectangle rect = new Rectangle();
        rect.widthProperty().bind(widthProperty());
        rect.heightProperty().bind(heightProperty());
        rect.setArcWidth(50);
        rect.setArcHeight(50);
        rect.setStrokeWidth(1);
        rect.setStroke(Color.GREENYELLOW);
        
        ImageView iv = new ImageView(new Image(Thumbnail.class.getClassLoader().getResourceAsStream(image)));
        iv.setX(4);
        iv.setY(4);
        iv.fitWidthProperty().bind(widthProperty().subtract(8));
        iv.fitHeightProperty().bind(heightProperty().subtract(8));

        setOnMouseEntered(me
                -> {
            if (stSmall.getStatus() == Animation.Status.RUNNING) {
                stSmall.stop();
                stBig.setFromX(stSmall.getNode().getScaleX());
                stBig.setFromY(stSmall.getNode().getScaleY());
            } else {
                stBig.setFromX(1.0);
                stBig.setFromY(1.0);
            }

            stBig.setToX(2.0);
            stBig.setToY(2.0);

            stBig.getNode().toFront();
            stBig.playFromStart();
        });

        setOnMouseExited(me
                -> {
            if (stBig.getStatus() == Animation.Status.RUNNING) {
                stBig.stop();
                stSmall.setFromX(stBig.getNode().getScaleX());
                stSmall.setFromY(stBig.getNode().getScaleY());
            } else {
                stSmall.setFromX(2.0);
                stSmall.setFromY(2.0);
            }

            stSmall.setToX(1.0);
            stSmall.setToY(1.0);

            stSmall.playFromStart();
        });

        getChildren().addAll(rect, iv);
    }

    private final DoubleProperty width = new SimpleDoubleProperty(0.0);

    public final void setWidth(double value) {
        width.set(value);
    }

    public final double getWidth() {
        return width.get();
    }

    private DoubleProperty widthProperty() {
        return width;
    }

    private final DoubleProperty height = new SimpleDoubleProperty(0.0);

    public final void setHeight(double value) {
        height.set(value);
    }

    public final double getHeight() {
        return height.get();
    }

    private DoubleProperty heightProperty() {
        return height;
    }
}
