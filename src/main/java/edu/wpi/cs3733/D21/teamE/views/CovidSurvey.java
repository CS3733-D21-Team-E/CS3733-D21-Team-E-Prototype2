package edu.wpi.cs3733.D21.teamE.views;
import com.jfoenix.controls.*;
import edu.wpi.cs3733.D21.teamE.App;

import javafx.event.ActionEvent;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;


import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class CovidSurvey extends ServiceRequests  {

    @FXML private AnchorPane appBarAnchorPane;
    @FXML private StackPane stackPane;
    @FXML JFXCheckBox positiveTest;
    @FXML JFXCheckBox symptoms;
    @FXML JFXCheckBox closeContact;
    @FXML JFXCheckBox quarantine;
    @FXML JFXCheckBox noSymptoms;

    /**
     * Creates a popup if the user indicates any symptoms.
     *
     */
    @FXML
    void popUp() {

        JFXDialogLayout error = new JFXDialogLayout();
        error.setHeading(new Text("Based on your response you should go home"));

        error.setPrefHeight(USE_COMPUTED_SIZE);
        JFXDialog dialog = new JFXDialog(stackPane, error, JFXDialog.DialogTransition.CENTER);
        dialog.setMaxWidth(350);
        JFXButton okay = new JFXButton("Exit");
        okay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Default.fxml"));
                    App.setDraggableAndChangeScene(root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        error.setActions(okay);
        dialog.show();
    }
    /**
     *
     *
     *
     * Detects if the user has entered all required fields
     *
     */
    private void validateInput(){
        JFXDialogLayout error = new JFXDialogLayout();
        error.setHeading(new Text("Please select at least one checkbox"));

        error.setPrefHeight(USE_COMPUTED_SIZE);
        JFXDialog dialog = new JFXDialog(stackPane, error, JFXDialog.DialogTransition.CENTER);
        dialog.setMaxWidth(350);
        JFXButton okay = new JFXButton("done");
        okay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();


            }
        });
        error.setActions(okay);
        dialog.show();

    }
    @FXML
    void submitButton(ActionEvent actionEvent){
        int rating = 0;
        if(noSymptoms.isSelected()){
            rating = 1;
        }else {
            if(symptoms.isSelected()){
            rating = 2;
            }if(closeContact.isSelected()){
                rating = 3;
            }if(quarantine.isSelected()){
                rating = 4;
            }if(positiveTest.isSelected()){
                rating = 5;
            }
        }
        if(rating == 0){
            validateInput();
        }
        else if(rating > 1){
            popUp();
        }
        else{
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Default.fxml"));
                App.setDraggableAndChangeScene(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.print(rating);
    }
    /**
     * Returns to the service request page
     * @param event {@link ActionEvent} info for the cancel button call, passed automatically by system.
     */
    @FXML
    void handleButtonCancel(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Default.fxml"));
            App.setDraggableAndChangeScene(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void initialize() {

        //init appBar
        javafx.scene.Node appBarComponent = null;
        try {
            App.setShowLogin(true);
            App.setShowHelp(true);
            App.setPageTitle("Covid Survey"); //set AppBar title
            App.setHelpText(""); //set help text todo, fill in this field
            App.setStackPane(stackPane); // required for dialog boxes
            appBarComponent = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/AppBarComponent.fxml"));
            appBarAnchorPane.getChildren().add(appBarComponent); //add FXML to this page's anchorPane element
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert  positiveTest  != null : "fx:id=\"positiveTest\" was not injected: check your FXML file 'PathFinder.fxml'.";
        assert  symptoms  != null : "fx:id=\"symptoms\" was not injected: check your FXML file 'PathFinder.fxml'.";
        assert closeContact  != null : "fx:id=\"closeContact\" was not injected: check your FXML file 'PathFinder.fxml'.";
        assert  quarantine != null : "fx:id=\"quarantine\" was not injected: check your FXML file 'PathFinder.fxml'.";
        assert  noSymptoms  != null : "fx:id=\"noSymptoms\" was not injected: check your FXML file 'PathFinder.fxml'.";
    }
}