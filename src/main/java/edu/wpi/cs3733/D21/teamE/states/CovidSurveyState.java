package edu.wpi.cs3733.D21.teamE.states;

import edu.wpi.cs3733.D21.teamE.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;

public class CovidSurveyState implements State{

    public void switchScene(ActionEvent event) {

        String buttonName = ((Button) event.getSource()).getText();

        if(buttonName.equals("Cancel")){
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Default.fxml"));
                App.setDraggableAndChangeScene(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        else if(buttonName.equals("Submit")){
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Default.fxml"));
                App.setDraggableAndChangeScene(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
