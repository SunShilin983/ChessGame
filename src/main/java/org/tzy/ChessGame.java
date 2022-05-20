package org.tzy;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

public class ChessGame extends Application {

    private Label status;
    private ChessBoard board;
    private static ChessGame instance;

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        primaryStage.setTitle("Chess");
        primaryStage.getIcons().add(Helper.loadImage("images/icon.png", 16, 16));

        //main BorderPane
        BorderPane pane = new BorderPane();

        //chess board with column and row markings
        GridPane table = new GridPane();
        for (int i = 0; i < 8; i++) {
            table.add(newRowLabel(i), 0, i + 1, 1, 1);
            table.add(newRowLabel(i), 9, i + 1, 1, 1);
            table.add(newColLabel(i), i + 1, 0, 1, 1);
            table.add(newColLabel(i), i + 1, 9, 1, 1);
        }
        table.add(board = new ChessBoard(), 1, 1, 8, 8);
        table.setAlignment(Pos.CENTER);
        pane.setCenter(table);

        //menu on the bottom with status text and option buttons
        BorderPane menu = new BorderPane();
        menu.setPadding(new Insets(10, 10, 10, 0));

        //option buttons
        GridPane options = new GridPane();
        options.setAlignment(Pos.BOTTOM_RIGHT);

        options.add(new OptionButton(
                        "images/reset.png",
                        e -> {
                            TextInputDialog dialog = new TextInputDialog("user1");
                            dialog.setTitle("Start New Chess Game");
                            dialog.setContentText("Please enter your name:");
                            Optional<String> result = dialog.showAndWait();
                            AtomicReference<String> username = new AtomicReference<>("");
                            result.ifPresent(username::set);
                            board.clear();
                            board.loadFromResource("init.json");
                            board.setUsername(username.get());
                        },
                        "Reset"),
                2, 0, 1, 1
        );
        options.add(new OptionButton(
                        "images/save.png",
                        e -> {
                            File file = createFileChooser().showSaveDialog(primaryStage);
                            if (file != null) {
                                board.save(file);
                            }
                        },
                        "Save"),
                3, 0, 1, 1
        );
        options.add(new OptionButton(
                        "images/load.png",
                        e -> {
                            File file = createFileChooser().showOpenDialog(primaryStage);
                            if (file != null) {
                                board.load(file);
                            }
                        },
                        "Load"),
                4, 0, 1, 1
        );
        menu.setRight(options);

        //status text
        status = new Label();
        status.setAlignment(Pos.BOTTOM_LEFT);
        status.setPadding(new Insets(10, 0, 10, 10));
        menu.setLeft(status);

        pane.setBottom(menu);

        //scene
        Scene scene = new Scene(pane, 440, 490);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());

        //set IO and load initial setup
        ServiceLoader.load(ChessIO.class).forEach(board::setIO);
        board.loadFromResource("init.json");
        //board.loadFromResource("game.json");
    }

    private Label newRowLabel(int i) {
        Label l = new Label(8 - i + "");
        l.setMinSize(20, 50);
        l.setAlignment(Pos.CENTER);
        return l;
    }

    private Label newColLabel(int i) {
        Label l = new Label((char) (i + 65) + "");
        l.setMinSize(50, 20);
        l.setAlignment(Pos.CENTER);
        return l;
    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        board.getIO().forEach((k, v) -> fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(v.getFileTypeDescription(), "*." + v.getFileExtension())));
        return fileChooser;
    }

    static void displayStatusText(String text) {
        instance.status.setText(text);
    }

    public static ChessBoard getBoard() {
        return instance.board;
    }
}
