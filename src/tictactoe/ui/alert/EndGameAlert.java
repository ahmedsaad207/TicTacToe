package tictactoe.ui.alert;

import java.io.File;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Pos;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import tictactoe.ui.screens.Board;
import tictactoe.ui.screens.OfflineBase;

public class EndGameAlert {

    protected File file;
    private Media video;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Stage stage;
    private Stage alertStage;
    private Board board;

    public EndGameAlert(char status, Stage stage, Board board) {
        this.stage = stage;
        this.board = board;
        if (status == 'w') {
            file = new File("src/resources/videos/win.mp4");
        }
        if (status == 'l') {
            file = new File("src/resources/videos/lose.mp4");
        }
    }

    public void show() {
        if (file != null) {

            video = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(video);
            mediaView = new MediaView(mediaPlayer);

            mediaPlayer.setOnReady(() -> mediaPlayer.play());

            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());

            Label videoLabel = new Label();
            videoLabel.setGraphic(mediaView);
            VBox.setMargin(videoLabel, new Insets(0, 200, 20, 200));

            Button restartButton = new Button("Restart");
            Button exitButton = new Button("Exit");
            restartButton.setId("restartBtn");
            exitButton.setId("exitBtn");
            restartButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
            exitButton.setStyle("-fx-background-color: red; -fx-text-fill: white;-fx-font-size: 24px; -fx-font-weight: bold;");

            restartButton.setOnAction(e -> {
                mediaPlayer.stop();
                board.restartGame();
                alertStage.close();
            });

            exitButton.setOnAction(e -> {
                mediaPlayer.stop();
                alertStage.close();
                Scene offlineScene = new Scene(new OfflineBase(stage), 800, 600);
                stage.setScene(offlineScene);
                offlineScene.getStylesheets().add(getClass().getResource("/resources/style/style.css").toExternalForm());
            });

            FlowPane buttonPane = new FlowPane();
            buttonPane.setHgap(200);
            buttonPane.setAlignment(Pos.CENTER);
            buttonPane.getChildren().addAll(restartButton, exitButton);

            VBox vbox = new VBox(10, videoLabel, buttonPane);
            vbox.setStyle("-fx-alignment: center; -fx-padding: 5; -fx-background-color: #1F509A;");

            Scene alertScene = new Scene(vbox, 700, 500);
            alertStage = new Stage();

            alertStage.setScene(alertScene);

            alertStage.setResizable(false);

            alertStage.setOnCloseRequest(e -> {
                e.consume();
            });

            /*
                alertStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        alertStage.setIconified(false);  
                    }
                });
             */
            alertStage.show();

        } else {
            System.out.println("Sorry File is Empty");
        }
    }
}
