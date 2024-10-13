package sample.application.endedbyuser;

import static sample.chess.util.GameEvaluator.EvaluationResult.RESIGN;
import static javafx.scene.paint.Color.WHITE;

import java.io.IOException;

import sample.chess.util.GameEvaluator.EvaluationResult;
import sample.application.Controller;
import sample.application.SecondStage;
import sample.application.Sounds;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class EndedByUserStage {

	private final Controller controller;
	private final SecondStage secondStage;
	private final EvaluationResult result;
	private final String activePlayer;
	private final String otherPlayer;

	public EndedByUserStage(Controller _controller, EvaluationResult _result) {
		controller = _controller;
		secondStage = _controller.getSecondStage();
		//result can be 'resign' or offer draw.
		result = _result;
		activePlayer = _controller.getActivePlayer() == WHITE ? "Player White" : "Player Black";
		otherPlayer = _controller.getActivePlayer() == WHITE ? "Player Black" : "Player White";
	}

	public void setSceneAndShow() {
		secondStage.setTitle("");
		String endMessage = result == RESIGN ? activePlayer+" resigns."+"\n"+otherPlayer+" wins." : "Both players agreed on a draw.";
		String acceptButtonText = result == RESIGN ? "Resign" : "Accept";
		String cancelButtonText = result == RESIGN ? "Play On" : "Decline";
		String confirmationText = result == RESIGN ? activePlayer+"\n"+" are you sure you want to resign?" : activePlayer+" offers a draw."+"\n"+otherPlayer+" do you accept?";
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EndedByUserLayout.fxml"));
			Scene endedByUserScene = new Scene(loader.load());
			secondStage.setScene(endedByUserScene);
			EndedByUserController endedByUserController = loader.getController();
			//Listener for "user accepts draw or is sure he wants to resign".
			endedByUserController.getEndedByUser().addListener((ObservableValue, oldValue, newValue) -> {
				if (newValue) {
					if (result == RESIGN) {
						controller.getBoardHandler().discoLights();
						Sounds.resign();
					}
					else Sounds.draw();
				}
				controller.evaluationMessages.setText(endMessage);
				controller.getBoardHandler().endGame();
			});
			endedByUserController.acceptButton.setText(acceptButtonText);
			endedByUserController.cancelButton.setText(cancelButtonText);
			endedByUserController.message.setText(confirmationText);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		secondStage.centerStage();
		secondStage.show();
	}
}
