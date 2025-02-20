package tictactoe.ui.screens;

import tictactoe.ui.alert.MessagePopup;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.json.Json;
import javax.json.JsonObject;
import tictactoe.data.repository.Repo;
import tictactoe.domain.usecases.RandomAvatarUseCase;
import tictactoe.domain.usecases.ShowPopupUseCase;
import tictactoe.ui.alert.ConnectionLostPopup;
import static tictactoe.ui.screens.SignUp.errorLable;

public class NewGame1Base extends BorderPane {

    protected final GridPane gridPane;
    protected final ColumnConstraints columnConstraints;
    protected final RowConstraints rowConstraints;
    protected final RowConstraints rowConstraints0;
    protected final RowConstraints rowConstraints1;
    protected final RowConstraints rowConstraints2;
    protected final Button LogOut;
    protected final ImageView Avater;
    protected final Label username;
    protected final Label score;
    protected final ImageView imageView;
    protected final Button NEWGAME;
    protected final Button History;
    protected static Stage mystage;
    protected RandomAvatarUseCase randomAvatarUseCase;

     public NewGame1Base(Stage mystage, String username, int score) {
        this.mystage = mystage;
        gridPane = new GridPane();
        columnConstraints = new ColumnConstraints();
        rowConstraints = new RowConstraints();
        rowConstraints0 = new RowConstraints();
        rowConstraints1 = new RowConstraints();
        rowConstraints2 = new RowConstraints();
        LogOut = new Button();
        Avater = new ImageView();
        this.username = new Label();
        this.score = new Label();
        imageView = new ImageView();
        NEWGAME = new Button();
        History = new Button();
        randomAvatarUseCase=new RandomAvatarUseCase();

        this.getStylesheets().add(getClass().getResource("/resources/style/style.css").toExternalForm());
        
        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(600.0);
        setPrefWidth(800.0);

        BorderPane.setAlignment(gridPane, javafx.geometry.Pos.CENTER);
        gridPane.setPrefHeight(558.0);
        gridPane.setPrefWidth(800.0);

        columnConstraints.setHgrow(javafx.scene.layout.Priority.SOMETIMES);
        columnConstraints.setMinWidth(10.0);
        columnConstraints.setPrefWidth(100.0);

        rowConstraints.setMaxHeight(184.0);
        rowConstraints.setMinHeight(10.0);
        rowConstraints.setPrefHeight(100.0);
        rowConstraints.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints0.setMaxHeight(277.0);
        rowConstraints0.setMinHeight(10.0);
        rowConstraints0.setPrefHeight(138.0);
        rowConstraints0.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints1.setMaxHeight(321.0);
        rowConstraints1.setMinHeight(10.0);
        rowConstraints1.setPrefHeight(130.0);
        rowConstraints1.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        rowConstraints2.setMaxHeight(194.0);
        rowConstraints2.setMinHeight(10.0);
        rowConstraints2.setPrefHeight(194.0);
        rowConstraints2.setVgrow(javafx.scene.layout.Priority.SOMETIMES);

        LogOut.setMnemonicParsing(false);
        LogOut.setText("");
        ImageView imageView0 = new ImageView(new Image(getClass().getResource("/resources/images/logout.png").toExternalForm()));
        imageView0.setFitWidth(75);
        imageView0.setFitHeight(75);
        LogOut.setGraphic(imageView0);
        LogOut.setId("LogOut");
        LogOut.setOnAction((ActionEvent event) -> {
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("action", 6)
                    .add("username", this.username.getText())
                    .build();
            String json = jsonObject.toString();
            Repo repo = new Repo();
            if (!repo.logout(json)) {
                errorLable.setText("Disconnection, Please try again.");
            }

            Scene scene = new Scene(new LogInBase(mystage), 800, 600);
            mystage.setScene(scene);
            scene.getStylesheets().add(getClass().getResource("/resources/style/style.css").toExternalForm());
        });

        GridPane.setMargin(LogOut, new Insets(0.0, 20.0, 0.0, 680.0));

        Avater.setImage(randomAvatarUseCase.getRandomAvatar());
         Avater.setFitWidth(80);
         Avater.setFitHeight(80);
         Circle mask = new Circle(40, 40, 40);
         Avater.setClip(mask);
        GridPane.setMargin(Avater, new Insets(0.0, 0.0, 0.0, 25.0));

        this.username.setText(username);
        this.username.setId("UserName");
        GridPane.setMargin(this.username, new Insets(0.0, 0.0, 25.0, 125.0));

        this.score.setText(String.valueOf(score));
        this.score.setId("score");
        GridPane.setMargin(this.score, new Insets(50.0, 0.0, 0.0, 125.0));

        GridPane.setRowIndex(imageView, 1);
        imageView.setFitHeight(118.0);
        imageView.setFitWidth(308.0);
        imageView.setImage(new Image(getClass().getResource("/resources/images/logo.png").toExternalForm()));
        GridPane.setMargin(imageView, new Insets(0.0, 0.0, 0.0, 246.0));

        GridPane.setRowIndex(NEWGAME, 2);
        NEWGAME.setMnemonicParsing(false);
        NEWGAME.setPrefHeight(90.0);
        NEWGAME.setPrefWidth(480.0);
        NEWGAME.setText("NEW GAME");
        NEWGAME.setId("NEWGAME");

        NEWGAME.setOnAction(e -> {
            Stage onlineStage = new Stage();
            OnlineUsers onlineUsers = new OnlineUsers(onlineStage, mystage,
                    this.username.getText(), Integer.valueOf(this.score.getText())
            );
            
            onlineStage.setScene(new Scene(onlineUsers, 480, 520));
            onlineStage.show();
            
        });

        GridPane.setMargin(NEWGAME, new Insets(0.0, 0.0, 0.0, 160.0));

        GridPane.setRowIndex(History, 3);
        History.setMnemonicParsing(false);
        History.setPrefHeight(90.0);
        History.setPrefWidth(480.0);
        History.setText("HISTORY");
        History.setId("HISTORY");
        History.setOnAction(e -> {
            Stage historyStage = new Stage();
            historyStage.setScene(new Scene(new History(historyStage,mystage, username, score), 600, 520));
            ShowPopupUseCase.showPopup(mystage, historyStage, 300, StageStyle.UTILITY);
            historyStage.show();
        });

        GridPane.setMargin(History, new Insets(0.0, 0.0, 100.0, 160.0));
        setCenter(gridPane);

        gridPane.getColumnConstraints().add(columnConstraints);
        gridPane.getRowConstraints().add(rowConstraints);
        gridPane.getRowConstraints().add(rowConstraints0);
        gridPane.getRowConstraints().add(rowConstraints1);
        gridPane.getRowConstraints().add(rowConstraints2);
        gridPane.getChildren().add(LogOut);
        gridPane.getChildren().add(Avater);
        gridPane.getChildren().add(this.username);
        gridPane.getChildren().add(this.score);
        gridPane.getChildren().add(imageView);
        gridPane.getChildren().add(NEWGAME);
        gridPane.getChildren().add(History);

    }

    public static void navigateToOnlineBoard(JsonObject json, boolean isX) {
        Platform.runLater(() -> {
            mystage.setScene(new Scene(new OnlineBoard(mystage, json, isX)));
        });
    }

    public static void navigateToHome(JsonObject jsonObj) {
        String status = jsonObj.getString("status");
        Platform.runLater(() -> {
            if ("success".equals(status)) {
                Scene scene = new Scene(new Home(mystage), 800, 600);
                mystage.setScene(scene);
            }
        });

    }

    public static void showConnectionLost() {
        if (mystage != null) {
            Platform.runLater(() -> {
                Stage connectionLostStage = new Stage();
                connectionLostStage.setScene(new Scene(new ConnectionLostPopup(mystage, connectionLostStage), 300, 300));
                ShowPopupUseCase.showPopup(mystage, connectionLostStage, 150, StageStyle.UNDECORATED);
            });
        }
    }

    public static void showMessagePopup(String username, String message) {
        if (mystage != null) {
            Platform.runLater(() -> {
                Stage messageStage = new Stage();
                messageStage.setScene(new Scene(new MessagePopup(username, message)));
                messageStage.initModality(Modality.APPLICATION_MODAL);
                messageStage.initOwner(mystage);
                messageStage.setX(mystage.getX() + (mystage.getWidth() / 2) - 225);
                messageStage.setY(mystage.getY() + (mystage.getHeight() / 2));
                messageStage.initStyle(StageStyle.UTILITY);
                messageStage.setResizable(false);
                messageStage.show();
            });
        }
    }
}
