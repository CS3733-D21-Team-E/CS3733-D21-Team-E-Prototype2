package edu.wpi.TeamE.views;

import edu.wpi.TeamE.App;
import edu.wpi.TeamE.algorithms.Edge;
import edu.wpi.TeamE.databases.EdgeDB;
import edu.wpi.TeamE.databases.csvDB;
import edu.wpi.TeamE.databases.makeConnection;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EdgeCSVUpload {

    @FXML
    TreeTableView<Edge> treeTable;

    public void prepareEdges(TreeTableView<Edge> table) {
        makeConnection connection = makeConnection.makeConnection();
        ArrayList<Edge> array = EdgeDB.getAllEdges();
        if (table.getRoot() == null) {
            Edge edge0 = new
                    Edge("ID", "0", "1", 0.00);
            final TreeItem<Edge> rootEdge = new TreeItem<>(edge0);
            table.setRoot(rootEdge);
            //column 1 - ID
            TreeTableColumn<Edge, String> column1 = new TreeTableColumn<>("ID");
            column1.setPrefWidth(320);
            column1.setCellValueFactory((TreeTableColumn.CellDataFeatures<Edge, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().getId()));
            table.getColumns().add(column1);
            //column 2 - Start Node
            TreeTableColumn<Edge, String> column2 = new TreeTableColumn<>("Start Node ID");
            column2.setPrefWidth(320);
            column2.setCellValueFactory((TreeTableColumn.CellDataFeatures<Edge, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().getStartNodeId()));
            table.getColumns().add(column2);
            //column 3 - End Node
            TreeTableColumn<Edge, String> column3 = new TreeTableColumn<>("End Node ID");
            column3.setPrefWidth(320);
            column3.setCellValueFactory((TreeTableColumn.CellDataFeatures<Edge, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().getEndNodeId()));
            table.getColumns().add(column3);
        }
        treeTable.setShowRoot(false);
        for (int i = 0; i < array.size(); i++) {
            Edge s = array.get(i);
            //int n = array.get(i).getX();
            final TreeItem<Edge> edge = new TreeItem<>(s);
            table.getRoot().getChildren().add(edge);
        }
    }

    @FXML
    private void toNavigation(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/TeamE/fxml/MapEditorNavigation.fxml"));
            App.setDraggableAndChangeScene(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void getHelpDefault(ActionEvent e) {
    }

    @FXML
    public void fileOpener(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
        makeConnection connection = makeConnection.makeConnection();
        if (file != null) {
            EdgeDB.deleteEdgeTable();
            EdgeDB.createEdgeTable();
            csvDB.populateTable("hasEdge", file);
            System.out.println("Success");
        }
        prepareEdges(treeTable);
    }

    @FXML
    private void openFile(ActionEvent e) throws IOException {

        makeConnection connection = makeConnection.makeConnection();
        csvDB.getNewCSVFile("hasEdge");
        File file = new File("CSVs/outputEdge.csv");
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);
        prepareEdges(treeTable);
    }

    @FXML
    void initialize() {

        prepareEdges(treeTable);

    }
}