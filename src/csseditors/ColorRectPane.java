/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 *
 * @author Siddhesh
 */
class ColorRectPane extends VBox {

    private Pane colorRect;
    private final Pane colorBar;
    private final Pane colorRectOverlayOne;
    private final Pane colorRectOverlayTwo;
    private Region colorRectIndicator;
    private final Region colorBarIndicator;

    private boolean changeIsLocal = false;
    private DoubleProperty hue = new SimpleDoubleProperty(0) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateHSBColor();
                changeIsLocal = false;
            }
        }
    };
    private DoubleProperty sat = new SimpleDoubleProperty(0) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateHSBColor();
                changeIsLocal = false;
            }
        }
    };
    private DoubleProperty bright = new SimpleDoubleProperty(0) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateHSBColor();
                changeIsLocal = false;
            }
        }
    };
    private IntegerProperty red = new SimpleIntegerProperty(0) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateRGBColor();
                changeIsLocal = false;
            }
        }
    };

    private IntegerProperty green = new SimpleIntegerProperty(0) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateRGBColor();
                changeIsLocal = false;
            }
        }
    };

    private IntegerProperty blue = new SimpleIntegerProperty(0) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                updateRGBColor();
                changeIsLocal = false;
            }
        }
    };

    private DoubleProperty alpha = new SimpleDoubleProperty(100) {
        @Override
        protected void invalidated() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                setCustomColor(new Color(
                        getCustomColor().getRed(),
                        getCustomColor().getGreen(),
                        getCustomColor().getBlue(),
                        clamp(alpha.get() / 100)));
                changeIsLocal = false;
            }
        }
    };

    private void updateRGBColor() {
        Color newColor = Color.rgb(red.get(), green.get(), blue.get(), clamp(alpha.get() / 100));
        hue.set(newColor.getHue());
        sat.set(newColor.getSaturation() * 100);
        bright.set(newColor.getBrightness() * 100);
        setCustomColor(newColor);
    }

    private void updateHSBColor() {
        Color newColor = Color.hsb(hue.get(), clamp(sat.get() / 100),
                clamp(bright.get() / 100), clamp(alpha.get() / 100));
        red.set(doubleToInt(newColor.getRed()));
        green.set(doubleToInt(newColor.getGreen()));
        blue.set(doubleToInt(newColor.getBlue()));
        setCustomColor(newColor);
    }

    private void colorChanged() {
        if (!changeIsLocal) {
            changeIsLocal = true;
            hue.set(getCustomColor().getHue());
            sat.set(getCustomColor().getSaturation() * 100);
            bright.set(getCustomColor().getBrightness() * 100);
            red.set(doubleToInt(getCustomColor().getRed()));
            green.set(doubleToInt(getCustomColor().getGreen()));
            blue.set(doubleToInt(getCustomColor().getBlue()));
            changeIsLocal = false;
        }
    }

    public ColorRectPane() {

        getStylesheets().add("/csseditors/colorRectPane.css");
        getStyleClass().add("color-rect-pane");

        customColorProperty().addListener((ObservableValue<? extends Color> ov, Color t, Color t1) -> {
            colorChanged();
        });

        colorRectIndicator = new Region();
        colorRectIndicator.setId("color-rect-indicator");
        colorRectIndicator.setManaged(false);
        colorRectIndicator.setMouseTransparent(true);
        colorRectIndicator.setCache(true);

        final Pane colorRectOpacityContainer = new StackPane();

        colorRect = new StackPane() {
            // This is an implementation of square control that chooses its
            // size to fill the available height

            @Override
            public Orientation getContentBias() {
                return Orientation.HORIZONTAL;
            }

            @Override
            protected double computePrefHeight(double width) {
                return width;
            }
        };
        colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

        Pane colorRectHue = new Pane();
        final ObjectBinding<Background> hueBackground = new ObjectBinding<Background>() {

            {
                bind(hue);
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(
                        Color.hsb(hue.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));
            }
        };
        colorRectHue.backgroundProperty().bind(hueBackground);
        colorRectOverlayOne = new Pane();
        colorRectOverlayOne.getStyleClass().add("color-rect");
        colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(255, 255, 255, 1)),
                        new Stop(1, Color.rgb(255, 255, 255, 0))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        EventHandler<MouseEvent> rectMouseHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final double x = event.getX();
                final double y = event.getY();
                sat.set(clamp(x / colorRect.getWidth()) * 100);
                bright.set(100 - (clamp(y / colorRect.getHeight()) * 100));
            }
        };

        colorRectOverlayTwo = new Pane();
        colorRectOverlayTwo.getStyleClass().addAll("color-rect");
        colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
        colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

        Pane colorRectBlackBorder = new Pane();
        colorRectBlackBorder.setMouseTransparent(true);
        colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

        colorBar = new Pane();
        colorBar.getStyleClass().add("color-bar");
        colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(),
                CornerRadii.EMPTY, Insets.EMPTY)));

        colorBar.setPrefHeight(10);
        colorBar.setMinHeight(10);
        colorBarIndicator = new Region();
        colorBarIndicator.setId("color-bar-indicator");
        colorBarIndicator.setMouseTransparent(true);
        colorBarIndicator.setCache(true);

        colorRectIndicator.layoutXProperty().bind(sat.divide(100).multiply(colorRect.widthProperty()));
        colorRectIndicator.layoutYProperty().bind(Bindings.subtract(1, bright.divide(100)).multiply(colorRect.heightProperty()));
        colorBarIndicator.layoutXProperty().bind(hue.divide(360).multiply(colorBar.widthProperty()));

        colorRectOpacityContainer.opacityProperty().bind(alpha.divide(100));

        EventHandler<MouseEvent> barMouseHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final double x = event.getX();
                hue.set(clamp(x / colorRect.getWidth()) * 360);
                event.consume();
            }
        };

        colorBar.setOnMouseDragged(barMouseHandler);
        colorBar.setOnMousePressed(barMouseHandler);
        colorBar.getChildren().setAll(colorBarIndicator);
        
        colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
        colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
        VBox.setVgrow(colorRect, Priority.SOMETIMES);
        setSpacing(7);
        setAlignment(Pos.CENTER);
        DropShadow dropShadow = new DropShadow();
        dropShadow.colorProperty().bind(customColorProperty);
        colorRect.setEffect(dropShadow);
        colorRect.setPrefSize(150, 150);
        colorBar.setEffect(new Reflection());
        colorBarIndicator.setFocusTraversable(true);
        colorBarIndicator.setOnKeyPressed(ke -> {
            double change = 0;
            switch (ke.getCode()) {
                case RIGHT:
                case KP_RIGHT:
                    change = 5;
                    break;
                case LEFT:
                case KP_LEFT:
                    change = -5;
                    break;
            }
            if (change != 0) {
                double newHue = hue.get() + change;
                newHue = newHue > 360 ? 0 : newHue < 0 ? 360 : newHue;
                hue.set(newHue);
            }
        });
        Slider alphaSlider = new Slider(0, 100, 100);
        alphaSlider.setTooltip(new Tooltip("Color Opacity"));
        alphaSlider.valueProperty().bindBidirectional(alpha);
        getChildren().addAll(colorRect, colorBar, alphaSlider);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        // to maintain default size
        colorRectIndicator.autosize();

        // to maintain square size
        double size = Math.min(colorRect.getWidth(), colorRect.getHeight());
        colorRect.resize(size, size);
        colorRect.setLayoutX((getWidth() - size) / 2);
        colorBar.setLayoutX((getWidth() - size) / 2);
        colorBar.resize(size, colorBar.getHeight());
    }

    private ObjectProperty<Color> customColorProperty = new SimpleObjectProperty<>(Color.BLACK);

    ObjectProperty<Color> customColorProperty() {
        return customColorProperty;
    }

    void setCustomColor(Color color) {
        customColorProperty.set(color);
    }

    Color getCustomColor() {
        return customColorProperty.get();
    }

    static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int y = 0; y < 255; y++) {
            offset = (double) (1 - (1.0 / 255) * y);
            int h = (int) ((y / 255.0) * 360);
            stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(1f, 0f, 0f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }

    private static int doubleToInt(double value) {
        return (int) (value * 255 + 0.5); // Adding 0.5 for rounding only
    }
}
