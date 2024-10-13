package sample.application.endedbyuser;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EndedByUserController {
	
	private final SimpleBooleanProperty endedByUser = new SimpleBooleanProperty(false);
	
	@FXML
	VBox root;
	@FXML
	Text message;
	@FXML
	Button cancelButton;
	@FXML
	Button acceptButton;

	@FXML
	private void acceptButtonAction() {
		endedByUser.set(true);
		((Stage) cancelButton.getScene().getWindow()).close();
	}
	
	@FXML
	private void cancelButtonAction() {
		((Stage) cancelButton.getScene().getWindow()).close();
		
	}

	public SimpleBooleanProperty getEndedByUser() {
		return endedByUser;
	}
}
