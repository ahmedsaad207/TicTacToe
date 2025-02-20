package tictactoe;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tictactoe.domain.usecases.PlayBackgroundMusicUseCase;
import tictactoe.ui.screens.Splash;

/**
 *
 * @author medos
 */
public class TicTacToe extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new Splash(stage);
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/resources/style/style.css").toExternalForm());
        PlayBackgroundMusicUseCase.getInstance().startBackgroundMusic();

        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
