package tictactoe.ui.screens;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.json.Json;
import javax.json.JsonObject;
import tictactoe.data.repository.Repo;
import tictactoe.domain.model.Tile;
import tictactoe.domain.usecases.GetRandomPositionUseCase;
import tictactoe.domain.usecases.GetTileUseCase;
import tictactoe.domain.usecases.GetXOImageUseCase;
import tictactoe.domain.usecases.IsWinnerUseCase;
import tictactoe.domain.usecases.RecordPositionUseCase;
import tictactoe.domain.usecases.RecordingUseCase;
import tictactoe.domain.usecases.ToJsonUseCase;
import tictactoe.ui.alert.EndGameAlert;
import static tictactoe.ui.screens.Board.isPlaying;

public class OnlineBoard extends Board {

    Repo repo = new Repo();
    static int position;
    public static OnlineBoard board;
    protected IsWinnerUseCase winnerCkeck;

    public OnlineBoard(Stage owner, JsonObject json, boolean isX) {
        super(owner);
        this.isX = isX;
        isRecording = false;
        winnerCkeck = new IsWinnerUseCase();

        if (isX) {
            isPlaying = true;
            player1Score.setText(json.getInt("score-player1") + "");
            player2Score.setText(json.getInt("score-player2") + "");
            userNamePlayer1.setText(json.getString("username-player1"));
            userNamePlayer2.setText(json.getString("username-player2"));
        } else { 
            isPlaying = false;
            player2Score.setText(json.getInt("score-player2") + "");
            player1Score.setText(json.getInt("score-player1") + "");
            userNamePlayer2.setText(json.getString("username-player2"));
            userNamePlayer1.setText(json.getString("username-player1"));
        }
        board = this;
        timer.startTimer(10, true);
        timer.setOnTimeStopped(() -> {
            printXO();
        });
        recordBtn.setOnAction(e -> {
            isRecording = true;
            recordBtn.setText("Recording...");
            recordBtn.setDisable(true);
        });

        forfeitBtn.setOnAction(e -> {
            player1ScoreValue = Integer.parseInt(player1Score.getText());
            player2ScoreValue = Integer.parseInt(player2Score.getText());
            timer.cancel();
            isFinished = true;
            sendRequest(0);
            if (isX) {
                displayEndGameAlertLoseP1('l');
            } else {
                displayEndGameAlertLoseP2('l');
            }
            updateIsAvailableInDatabase(userNamePlayer1.getText());
            updateIsAvailableInDatabase(userNamePlayer2.getText());

        });

    }

    @Override
    protected void printXO(Tile tile) {
        if (!isPlaying) {
            return;
        }
        if (isGameFinished()) {
            return;
        }
        if (!recordPositionsUseCase.getPositions().contains(tile.getPosition())) {
            return;
        }

        if (isFirst) {
            isFirst = false;
            recordBtn.setDisable(true);
        }
        tile.getBtn().setGraphic(GetXOImageUseCase.getXOImage(isX));
        playSound();
        recordPositionsUseCase.recordPositions(tile, isX);
        isPlaying = false;
        timer.cancel();
        timer.startTimer(10, !isX);
        checkWinner();
        sendRequest(tile.getPosition());

    }

    @Override
    protected void printXO() {

        if (isGameFinished()) {
            return;
        }

        if (isFirst) {
            isFirst = false;
            recordBtn.setDisable(true);
        }

        int randomPosition = GetRandomPositionUseCase.getRandomPosition(recordPositionsUseCase.getPositions());
        Tile tile = GetTileUseCase.getTile(tiles, randomPosition);

        if (isX && isPlaying) {
            tile.getBtn().setGraphic(GetXOImageUseCase.getXOImage(isX));
            recordPositionsUseCase.recordPositions(tile, isX);
            playSound();
            isPlaying = false;
            timer.cancel();
            timer.startTimer(10, !isX);
            sendRequest(randomPosition);
        } else if (!isX && isPlaying) {
            tile.getBtn().setGraphic(GetXOImageUseCase.getXOImage(isX));
            recordPositionsUseCase.recordPositions(tile, isX);
            playSound();
            isPlaying = false;
            timer.cancel();
            timer.startTimer(10, !isX);
            sendRequest(randomPosition);
        }
    }

    private void sendRequest(int pos) {
        String json = Json.createObjectBuilder()
                .add("action", 5)
                .add("username-player1", userNamePlayer1.getText())
                .add("username-player2", userNamePlayer2.getText())
                .add("isX", isX)
                .add("position", pos)
                .build().toString();
        repo.sendMove(json);
    }

    protected void printXOOpponent() {
        Tile tile = GetTileUseCase.getTile(tiles, position);
        tile.getBtn().setGraphic(GetXOImageUseCase.getXOImage(!isX));
        recordPositionsUseCase.recordPositions(tile, !isX);
        if (isX && isPlaying) {
            timer.cancel();
            timer.startTimer(10, isX);
        } else if (!isX && isPlaying) {
            timer.cancel();
            timer.startTimer(10, isX);
        }

        if (isFirst) {
            isFirst = false;
            recordBtn.setDisable(true);
        }
        checkWinner();
    }

    public void nextTurn(JsonObject json) {
        Platform.runLater(() -> {
            position = json.getInt("position");
            if (position == 0) {
                timer.cancel();
                if (isX) {
                    player1ScoreValue = Integer.parseInt(player1Score.getText()) + 100;
                    setPlayer1Score(player1ScoreValue);
                    updateScoreInDatabase(userNamePlayer1.getText(), player1ScoreValue);
                    displayEndGameAlertWinP1('w');

                } else {
                    player2ScoreValue = Integer.parseInt(player2Score.getText()) + 100;
                    setPlayer2Score(player2ScoreValue);
                    updateScoreInDatabase(userNamePlayer2.getText(), player2ScoreValue);
                    displayEndGameAlertWinP2('w');

                }
                return;
            }
            isPlaying = true;
            printXOOpponent();

            if (json.getBoolean("isX")) {
                playSound.playSound(2);
            } else {
                playSound.playSound(1);
            }
        });

    }

    @Override
    protected void checkWinner() {
        player1ScoreValue = Integer.parseInt(player1Score.getText());
        player2ScoreValue = Integer.parseInt(player2Score.getText());

        int winner = winnerCkeck.isWinner(recordPositionsUseCase);
        if (winner != 0) {

            isFinished = true;
            isPlaying = false;
            timer.cancel();
            if (winner == 1) {
                reset();
                highlightWinningTiles(winnerCkeck.getWinningPositions());
                updateIsAvailableInDatabase(userNamePlayer1.getText());
                updateIsAvailableInDatabase(userNamePlayer2.getText());
                if (isRecording) {
                    if (isX) {
                        RecordingUseCase.saveToFileOnline(userNamePlayer1.getText(), RecordingUseCase.Pos, userNamePlayer1.getText(), userNamePlayer2.getText(), 'W');
                    } else if (!isX) {
                        RecordingUseCase.saveToFileOnline(userNamePlayer2.getText(), RecordingUseCase.Pos, userNamePlayer1.getText(), userNamePlayer2.getText(), 'W');
                    }
                    recordHansel();
                }
                if (isX) {
                    player1ScoreValue = Integer.parseInt(player1Score.getText()) + 100;
                    setPlayer1Score(player1ScoreValue);
                    updateScoreInDatabase(userNamePlayer1.getText(), player1ScoreValue);
                    displayEndGameAlertWinP1('w');
                } else {

                    displayEndGameAlertLoseP2('l');
                }

            } else if (winner == 2) {
                reset();
                highlightWinningTiles(winnerCkeck.getWinningPositions());
                updateIsAvailableInDatabase(userNamePlayer1.getText());
                updateIsAvailableInDatabase(userNamePlayer2.getText());
                if (isRecording) {
                    if (isX) {
                        RecordingUseCase.saveToFileOnline(userNamePlayer1.getText(), RecordingUseCase.Pos, userNamePlayer1.getText(), userNamePlayer2.getText(), 'L');
                    } else if (!isX) {
                        RecordingUseCase.saveToFileOnline(userNamePlayer2.getText(), RecordingUseCase.Pos, userNamePlayer1.getText(), userNamePlayer2.getText(), 'L');
                    }
                    recordHansel();
                }
                if (!isX) {
                    player2ScoreValue = Integer.parseInt(player2Score.getText()) + 100;
                    setPlayer2Score(player2ScoreValue);
                    updateScoreInDatabase(userNamePlayer2.getText(), player2ScoreValue);
                    displayEndGameAlertWinP2('w');
                } else {
                    displayEndGameAlertLoseP1('l');
                }

            } else if (winner == 3) {
                reset();
                updateIsAvailableInDatabase(userNamePlayer1.getText());
                updateIsAvailableInDatabase(userNamePlayer2.getText());
                if (isRecording) {
                    if (isX) {
                        RecordingUseCase.saveToFileOnline(userNamePlayer1.getText(), RecordingUseCase.Pos, userNamePlayer1.getText(), userNamePlayer2.getText(), 'E');
                    } else if (!isX) {
                        RecordingUseCase.saveToFileOnline(userNamePlayer2.getText(), RecordingUseCase.Pos, userNamePlayer1.getText(), userNamePlayer2.getText(), 'E');
                    }
                    recordHansel();
                }
                if (isX) {
                    new EndGameAlert('e', stage, this, player1ScoreValue, userNamePlayer1.getText()).show();
                }
                if (!isX) {
                    new EndGameAlert('e', stage, this, player2ScoreValue, userNamePlayer2.getText()).show();
                }
            }
            recordPositionsUseCase.clear();
            return;
        }

    }

    private void displayEndGameAlertWinP1(char result) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                e -> {
                    new EndGameAlert(result, stage, this, player1ScoreValue, userNamePlayer1.getText()).show();
                }
        ));
        timeline.play();
    }

    private void displayEndGameAlertLoseP1(char result) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                e -> {
                    new EndGameAlert(result, stage, this, player1ScoreValue, userNamePlayer1.getText()).show();
                }
        ));
        timeline.play();
    }

    private void displayEndGameAlertWinP2(char result) {
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                e -> {
                    new EndGameAlert(result, stage, this, player2ScoreValue, userNamePlayer2.getText()).show();
                }
        ));
        timeline.play();
        
    }

    private void displayEndGameAlertLoseP2(char result) {    
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(2),
                e -> {
                    new EndGameAlert(result, stage, this, player2ScoreValue, userNamePlayer2.getText()).show();
                }
        ));
        timeline.play();
    }

    private void updateScoreInDatabase(String playerName, int score) {
        String updateScoreRequest = ToJsonUseCase.toJsonScoreUpdate(playerName, score);
        if (!repo.updateScore(updateScoreRequest)) {
            System.err.println("Failed to update " + playerName + "score");
        }
    }

    private void updateIsAvailableInDatabase(String playerName) {
        String updateIsAvailableRequest = ToJsonUseCase.updateIsAvailable(playerName);
        if (!repo.updateIsAvailable(updateIsAvailableRequest)) {
            System.err.println("Failed to update " + playerName + "isAvailable");
        }
    }

    private void recordHansel() {
        isRecording = false;
        RecordingUseCase.Pos = "";

    }

    private void reset() {
        recordPositionsUseCase = new RecordPositionUseCase();
    }
}
