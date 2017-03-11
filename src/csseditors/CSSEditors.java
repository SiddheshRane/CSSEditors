/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.application.Application;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

/**
 *
 * @author Siddhesh
 */
public class CSSEditors extends Application {

    @Override
    public void start(Stage primaryStage) {
        /*
         uncomment any one to play its demo
         */
        backgroundFillEditorTest(primaryStage);
//        regionPropertiesTest(primaryStage);
//        colorRectPaneTest(primaryStage);
//        colorRectPaneTest2(primaryStage);
//        popupControlTest(primaryStage);
//        popoverControlTest(primaryStage);
//        oldLinearGradientEditorTest(primaryStage);
//        stopCellTest(primaryStage);
//        LinearGradientEditorTest(primaryStage);
//        RadialGradientEditorTest(primaryStage);
//        backgroundLayerTest(primaryStage);
//        backgroundLayerTest2(primaryStage);
//        backgroundLayerTestFinal(primaryStage);
//        backgroundLayerTest3(primaryStage);
//        compositeAppTest(primaryStage);
//        fxmlLoaderTest(primaryStage);
//        scene3dTest(primaryStage);
    }

    public void backgroundFillEditorTest(Stage s) {
        VBox box = new VBox();
        Button b = new Button("Stylized Button");
        BackgroundFill bgFill = new BackgroundFill(Color.AQUAMARINE, new CornerRadii(3), Insets.EMPTY);

        b.setBackground(new Background(bgFill));

        BackgroundLayer layer = new BackgroundLayer(bgFill);

        layer.backgroundFillProperty().addListener((ObservableValue<? extends BackgroundFill> observable, BackgroundFill oldValue, BackgroundFill newValue) -> {
            b.setBackground(new Background(newValue));
        });
        layer.layoutXProperty().bind(b.layoutXProperty());
        layer.layoutYProperty().bind(b.layoutYProperty());
        layer.prefWidthProperty().bind(b.widthProperty());
        layer.prefHeightProperty().bind(b.heightProperty());

        BackgroundFillEditor editor = new BackgroundFillEditor(layer.backgroundFillProperty());

        Group g = new Group(layer);
        layer.setScaleX(4);
        layer.setScaleY(4);

        box.getChildren().addAll(b, editor, g);
        box.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(box);
        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        s.setTitle("BackgroundFillEditor Test");
        s.show();
    }

    public void regionPropertiesTest(Stage s) {
        Button but = new Button("But!");
        but.applyCss();
        but.setScaleX(3);
        but.setScaleY(3);

        RegionProperties rp = new RegionProperties();
        rp.registerNode(but);

        Pane box = new VBox(rp, new Group(but));
        Scene scene = new Scene(box);
        s.setTitle("RegionProperties Test");
        s.setScene(scene);
        s.show();
    }

    public void colorRectPaneTest(Stage s) {
        ColorRectPane colorRectPane = new ColorRectPane();
        Scene scene = new Scene(new StackPane(colorRectPane));
        s.setTitle("ColorRectPane test");
        s.setScene(scene);
        s.show();

    }

    public void colorRectPaneTest2(Stage s) {
        Button button = new Button("Choose Color");
        StackPane pane = new StackPane(button);
        Popup pop = new Popup();
        ColorRectPane colorRectPane = new ColorRectPane();
        pop.getContent().add(colorRectPane);
        pop.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT);
        pop.setAutoHide(true);
        button.setOnAction(ae -> {
            Point2D loc = button.localToScreen(0, 0);
            pop.show(button, loc.getX(), loc.getY());
        });
        Scene scene = new Scene(pane);
        s.setTitle("ColorRectPane test");
        s.setScene(scene);
        s.show();
    }

    public void popupControlTest(Stage s) {
        Button b = new Button("Popup Control");
        StackPane pane = new StackPane(b);
        Scene scene = new Scene(pane);
        PopupControl c = new PopupControl();
        c.setStyle("-fx-background-color:tomato");
        b.setOnAction(ae -> c.show(b, 0, 0));

        s.setTitle("Popup Control  test");
        s.setScene(scene);
        s.show();
    }

    public void popoverControlTest(Stage s) {
        Button b = new Button("Popup Control");
        Button b2 = new Button("Popup Control2");
        PopOver pop = new PopOver();
        pop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        pop.setDetachedTitle("Pick Color");
        ColorRectPane colorRectPane = new ColorRectPane();
        StackPane stackPane = new StackPane(colorRectPane);
        stackPane.setPadding(new Insets(7));
        pop.setContentNode(stackPane);
        b.setOnAction(ae -> {
            pop.show(b);
        });
        pop.setAutoHide(true);

        PopOver pop2 = new PopOver();
        pop2.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        pop2.setDetachedTitle("Pick Color");
        ColorRectPane colorRectPane2 = new ColorRectPane();
        StackPane stackPane2 = new StackPane(colorRectPane2);
        stackPane2.setPadding(new Insets(7));
        pop2.setContentNode(stackPane2);
        b2.setOnAction(ae -> {
            pop2.show(b2);
        });
        pop2.setAutoHide(true);

        Scene scene = new Scene(new HBox(b, b2));
        s.setTitle("Popup Control  test");
        s.setScene(scene);
        s.show();
    }

    public void oldLinearGradientEditorTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        LinearGradientEditorDepracated linearGradientEditor = new LinearGradientEditorDepracated();
        pane.getChildren().add(linearGradientEditor);
        AnchorPane.setLeftAnchor(linearGradientEditor, 25d);
        AnchorPane.setRightAnchor(linearGradientEditor, 25d);
        AnchorPane.setTopAnchor(linearGradientEditor, 25d);
        AnchorPane.setBottomAnchor(linearGradientEditor, 25d);

        Scene scene = new Scene(pane);
        s.setScene(scene);
        s.setTitle("LinearGradientEditor Test");
        s.show();
    }

    public void LinearGradientEditorTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        LinearGradientEditor linearGradientEditor = new LinearGradientEditor();
        pane.getChildren().add(linearGradientEditor);
        AnchorPane.setLeftAnchor(linearGradientEditor, 25d);
        AnchorPane.setRightAnchor(linearGradientEditor, 25d);
        AnchorPane.setTopAnchor(linearGradientEditor, 25d);
        AnchorPane.setBottomAnchor(linearGradientEditor, 25d);

        linearGradientEditor.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                super.bind(linearGradientEditor.gradientProperty());
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(linearGradientEditor.getGradient(), CornerRadii.EMPTY, Insets.EMPTY));
            }
        });

        Scene scene = new Scene(pane);
        s.setScene(scene);
        s.setTitle("LGE Test");
        s.show();
    }

    public void RadialGradientEditorTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        RadialGradientEditor radialGradientEditor = new RadialGradientEditor();
        pane.getChildren().add(radialGradientEditor);
        AnchorPane.setLeftAnchor(radialGradientEditor, 25d);
        AnchorPane.setRightAnchor(radialGradientEditor, 25d);
        AnchorPane.setTopAnchor(radialGradientEditor, 25d);
        AnchorPane.setBottomAnchor(radialGradientEditor, 25d);

        ComboBox<CycleMethod> cycleMethodBox = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));
        cycleMethodBox.valueProperty().bindBidirectional(radialGradientEditor.cycleMethodProperty());

        Label focus = new Label();
        focus.textProperty().bind(radialGradientEditor.focusProperty().asString("Focus: %2.2f"));
        Label focusAngle = new Label();
        focusAngle.textProperty().bind(radialGradientEditor.focusAngleProperty().asString("Focus Angle: %2.2f"));
        Label radius = new Label();
        radius.textProperty().bind(radialGradientEditor.radiusProperty().asString("Radius: %2.2f"));

        Slider slider = new Slider(-180, +180, 90);
        slider.valueProperty().bindBidirectional(radialGradientEditor.focusAngleProperty());

        StopList listView = new StopList();
        listView.setItems(radialGradientEditor.getStops());
        listView.setPrefHeight(100);

        VBox vbox = new VBox(pane, cycleMethodBox, focus, focusAngle, slider, radius, listView);
        VBox.setVgrow(pane, Priority.ALWAYS);

        radialGradientEditor.setGradient(RadialGradient.valueOf("radial-gradient(center 50% 50%,radius 50%, springgreen, steelblue)"));
        radialGradientEditor.backgroundProperty().bind(new ObjectBinding<Background>() {
            {
                super.bind(radialGradientEditor.gradientProperty());
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(radialGradientEditor.getGradient(), CornerRadii.EMPTY, Insets.EMPTY));
            }
        });
        Scene scene = new Scene(vbox);
        s.setScene(scene);
        s.setTitle("RGE Test");
        s.show();
    }

    public void stopCellTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        ListView<Stop> stopsList = new ListView<>();
        stopsList.setEditable(true);
        ObservableList<Stop> stops = FXCollections.observableArrayList(new Stop(0, Color.AQUA),
                                                                       new Stop(0.5, Color.ALICEBLUE), new Stop(0.6, Color.CHARTREUSE),
                                                                       new Stop(0.7, Color.BLANCHEDALMOND));

        stopsList.setItems(stops);
        stopsList.setCellFactory((ListView<Stop> param) -> new StopCell());

        pane.getChildren().add(stopsList);
        AnchorPane.setLeftAnchor(stopsList, 25d);
        AnchorPane.setRightAnchor(stopsList, 25d);
        AnchorPane.setTopAnchor(stopsList, 25d);
        AnchorPane.setBottomAnchor(stopsList, 25d);

        pane.getStylesheets().add("/csseditors/lge.css");
        Scene scene = new Scene(pane);
        s.setScene(scene);
        s.setTitle("StopCell Test");
        s.show();
    }

    public void backgroundLayerTest(Stage s) {
        Button test = new Button(" ");
        test.setOnAction(a -> System.out.println("Clicked me!"));
        test.setStyle(
                "-fx-background-color: cornflowerblue,skyblue,teal;"
                + "-fx-background-insets: 0,2,4;"
                + "-fx-background-radius: 30%;"
        );
        test.setMouseTransparent(true);
        test.setFocusTraversable(false);
        //add the button to group to turn its transform into layout
        StackPane stack = new StackPane(new Group(test));
        test.setScaleX(4);
        test.setScaleY(4);
        stack.setOnScroll(se -> {
            if (se.isControlDown()) {
                se.consume();
                System.out.println("delta X,Y = " + se.getDeltaX() + "," + se.getDeltaY());
                test.setScaleX(test.getScaleX() + se.getDeltaY() / 100);
                test.setScaleY(test.getScaleY() + se.getDeltaY() / 100);
            }
        });
        //add everything to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(stack);
        Scene scene = new Scene(scrollPane);
        //apply css to the button
        scrollPane.layout();
        scrollPane.applyCss();
        scrollPane.setPrefSize(400, 400);
        //get all bgfills of the button
        List<BackgroundFill> fills = test.getBackground().getFills();
        List<BackgroundLayer> bglayers = new ArrayList<>(fills.size());

        for (BackgroundFill fill : fills) {
            //create a BackgroundLayer for each BackgroundFill
            BackgroundLayer bglayer = new BackgroundLayer(fill);
            bglayers.add(bglayer);
            stack.getChildren().add(bglayer);
            if (fill.getFill() instanceof LinearGradient) {
                LinearGradientEditor editor = new LinearGradientEditor();
//                editor.setGradient((LinearGradient) fill.getFill());
//                editor.prefWidthProperty().bind(test.widthProperty());
//                editor.prefHeightProperty().bind(test.heightProperty());
//                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
//                bglayer.getChildren().add(editor);
            } else if (fill.getFill() instanceof Color) {
                ColorPicker picker = new ColorPicker();
                picker.valueProperty().addListener((observable, oldValue, newValue) -> bglayer.setBackgroundPaint(newValue));
                bglayer.getChildren().add(picker);
            }

        }

        //alternative view
        FlowPane flow = new FlowPane(10, 5);

        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        scrollPane.autosize();
        s.setTitle("BackgroundLayer Test");
        s.show();
    }

    String css = "-fx-background-color: cornflowerblue,"
            + "linear-gradient(to bottom,#111, #ccc),"
            + "radial-gradient(center 50% 50%,radius 50%, #555555, #aaaaaa);"
            + "-fx-background-insets: 0,2,4;"
            + "-fx-background-radius: 30%;";

    final String cssNamed = "-fx-background-color: cornflowerblue,"
            + "linear-gradient(to bottom,steelblue,springgreen),"
            + "radial-gradient(center 50% 50%,radius 50%, springgreen, steelblue);"
            + "-fx-background-insets: 0,2,4;"
            + "-fx-background-radius: 30%;";

    public void backgroundLayerTest2(Stage s) {
        Button test = new Button("Button");
//        test.setStyle(cssNamed);
//        test.setStyle(css);
        test.getStyleClass().add(".cssNamed");
//        test.setPrefSize(20, 20);
        test.setMouseTransparent(true);
        test.setFocusTraversable(false);
        test.setScaleX(3);
        test.setScaleY(3);
        //add the region to group to turn its transform into layout
        StackPane stack = new StackPane(new Group(test));
        stack.setStyle("-fx-border-color: springgreen");
        stack.maxWidthProperty().bind(test.widthProperty().multiply(test.scaleXProperty()));
        stack.maxHeightProperty().bind(test.heightProperty().multiply(test.scaleYProperty()));
        stack.setOnScroll(se -> {
            if (se.isControlDown()) {
                se.consume();
                test.setScaleX(test.getScaleX() + se.getDeltaY() / 100);
                test.setScaleY(test.getScaleY() + se.getDeltaY() / 100);
            }
        });
        TilePane flow = new TilePane(stack);
        flow.setStyle("-fx-border-color:blue;");
        flow.prefTileHeightProperty().bind(stack.maxHeightProperty());
        flow.prefTileWidthProperty().bind(stack.maxWidthProperty());
        flow.setHgap(15);
        flow.setVgap(15);
        flow.setPadding(new Insets(10));
        //add everything to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(flow);
        scrollPane.setFitToWidth(true);
        BorderPane borderPane = new BorderPane(scrollPane);
        ToggleButton insets = new ToggleButton("Insets");
        ToggleButton lg = new ToggleButton("Linear Gradient");
        ToggleButton rg = new ToggleButton("Overview");

        ToolBar tools = new ToolBar(insets, lg, rg);
        borderPane.setTop(tools);
        Scene scene = new Scene(borderPane);
        //apply css to the button
        scrollPane.applyCss();
        scrollPane.setPrefSize(400, 400);
        //get all bgfills of the button
        List<BackgroundFill> fills = test.getBackground().getFills();
        List<BackgroundLayer> bglayers = new ArrayList<>(fills.size());
        ChangeListener cl = (ob, ol, nw) -> {
            List<BackgroundFill> newBgFills = bglayers.stream().map(BackgroundLayer::getBackgroundFill).collect(Collectors.toList());
            test.setBackground(new Background(newBgFills, null));
        };
        for (BackgroundFill fill : fills) {
            //create a BackgroundLayer for each BackgroundFill
            BackgroundLayer bglayer = new BackgroundLayer(fill);
            bglayer.backgroundFillProperty().addListener(cl);
            bglayers.add(bglayer);
            flow.getChildren().add(bglayer);
            if (fill.getFill() instanceof LinearGradient) {
                LinearGradientEditor editor = new LinearGradientEditor();
                bglayer.getChildren().add(editor);
                editor.setGradient((LinearGradient) fill.getFill());
                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
            } else if (fill.getFill() instanceof Color) {
                ColorPicker picker = new ColorPicker();
                picker.valueProperty().addListener((observable, oldValue, newValue) -> bglayer.setBackgroundPaint(newValue));
                bglayer.getChildren().add(picker);
            } else if (fill.getFill() instanceof RadialGradient) {
                RadialGradient rad = (RadialGradient) fill.getFill();
                RadialGradientEditor editor = new RadialGradientEditor();
                editor.setGradient(rad);
                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
                bglayer.getChildren().add(editor);
            }
        }
//        rg.setOnAction(ae -> {
//            StackPane stacker = new StackPane();
//            if (rg.isSelected()) {
//                stacker.getChildren().addAll(bglayers);
//                flow.getChildren().add(stacker);
//            }else {
//                flow.getChildren().remove(stacker);
//                flow.getChildren().addAll(bglayers);
//            }
//        });

        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        scrollPane.autosize();
        s.setTitle("BackgroundLayer Test");
        s.show();

    }

    public void backgroundLayerTest3(Stage s) {
        Button test = new Button("HI");
//        test.setStyle(
//                "-fx-background-color: cornflowerblue,linear-gradient(to bottom,steelblue,springgreen),radial-gradient(radius 50%, red, blue);"
//                + "-fx-background-insets: 0,2,4;"
//                + "-fx-background-radius: 30%;"
//        );
//        test.setPrefSize(50, 50);
        test.setMouseTransparent(true);
        test.setFocusTraversable(false);
        test.setScaleX(1);
        test.setScaleY(1);
        //add the region to group to turn its transform into layout
        StackPane stack = new StackPane(new Group(test));
        stack.setStyle("-fx-border-color: springgreen");
        stack.maxWidthProperty().bind(test.widthProperty().multiply(test.scaleXProperty()));
        stack.maxHeightProperty().bind(test.heightProperty().multiply(test.scaleYProperty()));
        stack.setOnScroll(se -> {
            if (se.isControlDown()) {
                se.consume();
                test.setScaleX(test.getScaleX() + se.getDeltaY() / 100);
                test.setScaleY(test.getScaleY() + se.getDeltaY() / 100);
            }
        });
        //add everything to a ScrollPane
        TilePane flow = new TilePane(stack);
        flow.setStyle("-fx-border-color:blue;");
        flow.prefTileHeightProperty().bind(stack.maxHeightProperty());
        flow.prefTileWidthProperty().bind(stack.maxWidthProperty());
        flow.setHgap(5);
        flow.setVgap(5);
        ScrollPane scrollPane = new ScrollPane(flow);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane);
        //apply css to the button
        scrollPane.layout();
        scrollPane.applyCss();
        scrollPane.setPrefSize(400, 400);
        //get all bgfills of the button
        List<BackgroundFill> fills = test.getBackground().getFills();
        List<BackgroundLayer> bglayers = new ArrayList<>(fills.size());
        ChangeListener cl = (ob, ol, nw) -> {
            List<BackgroundFill> newBgFills = bglayers.stream().map(BackgroundLayer::getBackgroundFill).collect(Collectors.toList());
            test.setBackground(new Background(newBgFills, null));
        };
        for (BackgroundFill fill : fills) {
            //create a BackgroundLayer for each BackgroundFill
            BackgroundLayer bglayer = new BackgroundLayer(fill);
            bglayer.backgroundFillProperty().addListener(cl);
            bglayers.add(bglayer);
            flow.getChildren().add(bglayer);
            if (fill.getFill() instanceof LinearGradient) {
                LinearGradientEditor editor = new LinearGradientEditor();
                editor.setGradient((LinearGradient) fill.getFill());
                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
                bglayer.getChildren().add(editor);
            } else if (fill.getFill() instanceof Color) {
                ColorPicker picker = new ColorPicker();
                picker.valueProperty().addListener((observable, oldValue, newValue) -> bglayer.setBackgroundPaint(newValue));
                bglayer.getChildren().add(picker);
            } else if (fill.getFill() instanceof RadialGradient) {
                RadialGradient rad = (RadialGradient) fill.getFill();
                RadialGradientEditor editor = new RadialGradientEditor();
                editor.setGradient(rad);
                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
                bglayer.getChildren().add(editor);
            }
        }

        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        scrollPane.autosize();
        s.setTitle("BackgroundLayer Test");
        s.show();

    }

    public void backgroundLayerTestFinal(Stage s) {
        Button test = new Button("HI");
        test.getStyleClass().add(".cssNamed");
//        test.setStyle(
//                "-fx-background-color: cornflowerblue,linear-gradient(to bottom,steelblue,springgreen),radial-gradient(radius 50%, red, blue);"
//                + "-fx-background-insets: 0,2,4;"
//                + "-fx-background-radius: 30%;"
//        );
//        test.setPrefSize(50, 50);
        test.setMouseTransparent(true);
        test.setFocusTraversable(false);
        test.setScaleX(3);
        test.setScaleY(3);
        //add the region to group to turn its transform into layout
        StackPane stack = new StackPane(new Group(test));
        stack.setStyle("-fx-border-color: springgreen");
        stack.maxWidthProperty().bind(test.widthProperty().multiply(test.scaleXProperty()));
        stack.maxHeightProperty().bind(test.heightProperty().multiply(test.scaleYProperty()));
        stack.setOnScroll(se -> {
            if (se.isControlDown()) {
                se.consume();
                test.setScaleX(test.getScaleX() + se.getDeltaY() / 100);
                test.setScaleY(test.getScaleY() + se.getDeltaY() / 100);
            }
        });
        //add everything to a ScrollPane
        TilePane flow = new TilePane(stack);
        flow.setStyle("-fx-border-color:blue;");
        flow.prefTileHeightProperty().bind(stack.maxHeightProperty());
        flow.prefTileWidthProperty().bind(stack.maxWidthProperty());
        flow.setHgap(5);
        flow.setVgap(5);
        ScrollPane scrollPane = new ScrollPane(flow);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        BorderPane bpane = new BorderPane(scrollPane);
        Scene scene = new Scene(bpane);

        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        MenuItem save = new MenuItem("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                final FileChooser SavefileChooser = new FileChooser();

                SavefileChooser.setTitle("Save");
                SavefileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("CSS", "*.css")
                );

                SavefileChooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );

                File file = SavefileChooser.showSaveDialog(s);
                if (file != null) {
                    try {
                        String path = file.getPath();
                        System.out.println(path);

                        String content = ".theme{\n" + getCSS(test.getBackground()) + "\n}";
                        FileWriter fileWriter = null;
                        if (!(path.endsWith(".css"))) {
                            path = path + ".css";
                            File file1 = new File(path);
                            file.renameTo(file1);
                            fileWriter = new FileWriter(file1);
                        } else {
                            fileWriter = new FileWriter(file);
                        }

                        fileWriter.write(content);
                        fileWriter.close();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
        file.getItems().add(save);
        menuBar.getMenus().add(file);
        bpane.setTop(menuBar);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(12));
        vbox.setSpacing(5);
        Button b = new Button("Button");
        b.backgroundProperty().bind(test.backgroundProperty());
        ComboBox box = new ComboBox(FXCollections.observableArrayList("Item 1", "Item 2"));
        box.getSelectionModel().select(0);
        box.backgroundProperty().bind(test.backgroundProperty());
        ColorPicker color = new ColorPicker(Color.CADETBLUE);
        color.backgroundProperty().bind(test.backgroundProperty());

        TextArea css = new TextArea();
        css.setWrapText(true);
        vbox.getChildren().addAll(b, box, color, css);
        bpane.setRight(vbox);
        //apply css to the button
        scrollPane.layout();
        scrollPane.applyCss();
        scrollPane.setPrefSize(400, 400);
        //get all bgfills of the button
        List<BackgroundFill> fills = test.getBackground().getFills();
        List<BackgroundLayer> bglayers = new ArrayList<>(fills.size());
        ChangeListener cl = (ob, ol, nw) -> {
            List<BackgroundFill> newBgFills = bglayers.stream().map(BackgroundLayer::getBackgroundFill).collect(Collectors.toList());
            final Background background = new Background(newBgFills, null);
            test.setBackground(background);
            css.setText(getCSS(background));
        };

        for (BackgroundFill fill : fills) {
            //create a BackgroundLayer for each BackgroundFill
            BackgroundLayer bglayer = new BackgroundLayer(fill);
            bglayer.backgroundFillProperty().addListener(cl);
            bglayers.add(bglayer);
            flow.getChildren().add(bglayer);
            if (fill.getFill() instanceof LinearGradient) {
                LinearGradientEditor editor = new LinearGradientEditor();
                editor.setGradient((LinearGradient) fill.getFill());
                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
                bglayer.getChildren().add(editor);
            } else if (fill.getFill() instanceof Color) {
                ColorPicker picker = new ColorPicker();
                picker.valueProperty().addListener((observable, oldValue, newValue) -> bglayer.setBackgroundPaint(newValue));
                bglayer.getChildren().add(picker);
            } else if (fill.getFill() instanceof RadialGradient) {
                RadialGradient rad = (RadialGradient) fill.getFill();
                RadialGradientEditor editor = new RadialGradientEditor();
                editor.setGradient(rad);
                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
                bglayer.getChildren().add(editor);
            }
        }

        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        scrollPane.autosize();
        s.setTitle("BackgroundLayer Test");
        s.show();

    }

    public String getCSS(Background b) {
        String cssFillvals = "";
        String cssInsetvals = "";
        String cssRadiivals = "";
        List<BackgroundFill> fills = b.getFills();
        for (int i = 0; i < fills.size(); i++) {
            //get paint
            Paint fill = fills.get(i).getFill();
            cssFillvals = cssFillvals + "\n\t" + fill.toString() + ",";
            //get insets
            Insets insets = fills.get(i).getInsets();
            String bottom = Double.toString(insets.getBottom());
            String top = Double.toString(insets.getTop());
            String right = Double.toString(insets.getRight());
            String left = Double.toString(insets.getLeft());
            cssInsetvals = cssInsetvals + "\n\t" + top + ' ' + right + ' ' + bottom + ' ' + left + ",";

            //get radii
            CornerRadii radii = fills.get(i).getRadii();
            String bottomLeftHorizontalRadius = Double.toString(radii.getBottomLeftHorizontalRadius());
            String bottomLeftVerticalRadius = Double.toString(radii.getBottomLeftVerticalRadius());
            String bottomRightHorizontalRadius = Double.toString(radii.getBottomRightHorizontalRadius());
            String bottomRightVerticalRadius = Double.toString(radii.getBottomRightVerticalRadius());
            String topLeftHorizontalRadius = Double.toString(radii.getTopLeftHorizontalRadius());
            String topLeftVerticalRadius = Double.toString(radii.getTopLeftVerticalRadius());
            String topRightHorizontalRadius = Double.toString(radii.getTopRightHorizontalRadius());
            String topRightVerticalRadius = Double.toString(radii.getTopRightVerticalRadius());
//            if(radii.isBottomLeftHorizontalRadiusAsPercentage();
//            radii.isBottomLeftVerticalRadiusAsPercentage(); 
//            radii.isBottomRightHorizontalRadiusAsPercentage();
//            radii.isBottomRightVerticalRadiusAsPercentage();
//            radii.isTopLeftHorizontalRadiusAsPercentage();
//            radii.isTopLeftVerticalRadiusAsPercentage();
//            radii.isTopRightHorizontalRadiusAsPercentage(); 
//            radii.isTopRightVerticalRadiusAsPercentage();
            cssRadiivals = cssRadiivals + "\n\t" + topLeftHorizontalRadius + ' ' + topLeftVerticalRadius + ' ' + topRightVerticalRadius + ' ' + topRightHorizontalRadius + ' ' + bottomRightHorizontalRadius + ' ' + bottomRightVerticalRadius + ' ' + bottomLeftVerticalRadius + ' ' + bottomLeftHorizontalRadius + ',';

        }
        cssFillvals = cssFillvals.substring(0, cssFillvals.lastIndexOf(","));
        cssFillvals = "-fx-background-color: " + cssFillvals + ";";
        cssInsetvals = cssInsetvals.substring(0, cssInsetvals.lastIndexOf(","));
        cssInsetvals = "-fx-background-insets: " + cssInsetvals + ";";
        cssRadiivals = cssRadiivals.substring(0, cssRadiivals.lastIndexOf(","));
        cssRadiivals = "-fx-background-radius: " + cssRadiivals + ";";

        String rules = cssFillvals + '\n' + cssInsetvals + '\n' + cssRadiivals;
        rules = rules.replaceAll("0x", "#");
        return rules;
    }

    
    public void compositeAppTest(Stage s) {
        Rectangle rec = new Rectangle(200, 100);
        RadialGradientEditor editor = new RadialGradientEditor();
//        editor.setOpacity(0.5);
        StackPane overlay = new StackPane(rec, editor);
        rec.setArcWidth(60);
        rec.setArcHeight(60);
        rec.widthProperty().bind(overlay.widthProperty());
        rec.heightProperty().bind(overlay.heightProperty());
        rec.fillProperty().bind(editor.gradientProperty());

        Circle circle = new Circle(50);
        circle.fillProperty().bind(editor.gradientProperty());
        TextArea css = new TextArea("CSS");
        css.textProperty().bind(editor.gradientProperty().asString());
        css.setWrapText(true);
        HBox hbox = new HBox(overlay, circle, css);
        hbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(hbox);
        s.setScene(scene);
        s.setTitle("Composite Application Test");
        s.show();
    }

    public void fxmlLoaderTest(Stage s) {

        Label empty = new Label("No Content");
        StackPane pane = new StackPane(empty);
        empty.setStyle("-fx-font-size:50px; -fx-text-fill: gray;");
        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        Button open = new Button("Open");
        ToolBar toolBar = new ToolBar(open);
        BorderPane borderPane = new BorderPane(scrollPane);
        borderPane.setTop(toolBar);

        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ae) {
                final FileChooser openChooser = new FileChooser();

                openChooser.setTitle("Choose an FXML file");
                openChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("FXML", "*.fxml")
                );

                openChooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );

                File file = openChooser.showOpenDialog(s);
                if (file != null) {
                    String path = file.getPath();
                    System.out.println(path);
                    try {
                        Node content = (Node) FXMLLoader.load(file.toURI().toURL());
                        scrollPane.setContent(content);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(CSSEditors.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CSSEditors.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        Scene scene = new Scene(borderPane, 500, 500);
        s.setScene(scene);
        s.setTitle("FXML Loader Test");
        s.show();
    }

    public void scene3dTest(Stage s) {
        StackPane red = new StackPane();
        red.setStyle("-fx-background-color:tomato;");
        red.setTranslateZ(100);
//        red.setTranslateX(100);
        red.setRotationAxis(new Point3D(0, 1, 0));
        red.setRotate(-45);
        red.setPrefSize(100, 100);
        StackPane green = new StackPane();
        green.setStyle("-fx-background-color:springgreen;");
        green.setTranslateZ(-100);
        green.setRotationAxis(new Point3D(0, 1, 0));
        green.setRotate(-45);
        green.setPrefSize(100, 100);
        AnchorPane con = new AnchorPane(red, green);
        con.setStyle("-fx-border-color:cornflowerblue;");
        PerspectiveCamera camera = new PerspectiveCamera();
        ParallelCamera parallelCamera = new ParallelCamera();
        Scene scene = new Scene(con, 400, 400, true);
        scene.setCamera(camera);
        s.setScene(scene);
        s.setTitle("Scene 3D test");
        s.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
