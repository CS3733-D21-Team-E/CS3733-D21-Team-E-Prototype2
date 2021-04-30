package edu.wpi.cs3733.D21.teamE.views;

import com.jfoenix.controls.*;

import edu.wpi.cs3733.D21.teamE.DB;
import edu.wpi.cs3733.D21.teamE.database.makeConnection;


import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;

import java.util.ResourceBundle;

import edu.wpi.cs3733.D21.teamE.map.Edge;
import edu.wpi.cs3733.D21.teamE.map.Node;
import edu.wpi.cs3733.D21.teamE.map.Path;


import edu.wpi.cs3733.D21.teamE.App;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Group;
import javafx.scene.Parent;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class newMapEditor {

    /*
     * FXML Values
     */


    @FXML // fx:id="zoomSlider"
    private Slider zoomSlider;
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // fix:id="findPathButton"
    public JFXButton findPathButton; // Value injected by FXMLLoader
    @FXML // fix:id="backButton"
    public Button backButton; // Value injected by FXMLLoader
    @FXML // fx:id="startLocationList"
    private JFXComboBox<String> startLocation; // Value injected by FXMLLoader
    @FXML // fx:id="endLocationList"
    private JFXComboBox<String> endLocation; // Value injected by FXMLLoader
    // fx:id="imageView"
    private ImageView imageView = new ImageView();
    @FXML // fx:id="pane"
    private Pane pane = new Pane();
    @FXML // fx:id="scrollPane"
    private BorderPane rootBorderPane;
    @FXML // fx:id="directionsButton"
    private JFXButton directionsButton; // Value injected by FXMLLoader
    @FXML // fx:id="stackPane"
    private StackPane stackPane; // Value injected by FXMLLoader
    @FXML // fx:id="exit"
    private Polygon exit;
    @FXML // fx:id="lowerAnchorPane"
    private AnchorPane lowerAnchorPane; // Value injected by FXMLLoader
    @FXML
    private TreeTableView<Node> nodeTreeTable;
    @FXML
    private TreeTableView<Edge> edgeTreeTable;
    @FXML
    private JFXTextField xCordInput;
    @FXML
    private JFXTextField yCordInput;
    @FXML
    private JFXComboBox idInput;
    @FXML
    private JFXComboBox floorInput;
    @FXML
    private JFXComboBox typeInput;
    @FXML
    private JFXComboBox buildingInput;
    @FXML
    private JFXTextField longNameInput;
    @FXML
    private JFXTextField shortNameInput;
    @FXML
    private VBox edgeVBox;
    @FXML
    private VBox nodeVBox;
    @FXML
    private JFXComboBox edgeID;
    @FXML
    private JFXComboBox floorSelector;




    /*
     * Additional Variables
     */

    private String startID = "test";

    private String endID = "test";

    private String currentFloor = "1"; // set based on button presses

    private Path currentFoundPath; // the last found path todo null if no path has been found yet

    ArrayList<String> nodeIDArrayList;
    ArrayList<Edge> edgeArrayList;
    ArrayList<Node> nodeArrayList;

    private final String[] floorNames = {"L1", "L2", "G", "1", "2", "3"}; //list of floorNames

    private int currentFloorNamesIndex = 4; //start # should be init floor index + 1 (variable is actually always one beyond current floor)

    ObservableList<String> longNameArrayList;
    ObservableList<String> edgeIDArrayList;

    private double stageWidth;
    private double stageHeight;

    private double imageWidth;
    private double imageHeight;

    private double scale;
    private int selection = 0;


    /**
     * Returns to {@link Default} page.
     *
     * @param event calling event info.
     */
    @FXML
    private void toDefault(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/Default.fxml"));
            App.setDraggableAndChangeScene(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void getHelpDefault(ActionEvent event) {
        //todo, create help modal (refactor name, this was taken from Default page, hence the name)
    }

    /**
     * displays map of current floor
     * @param floorNum current floor number
     */
    public void drawMap(String floorNum) {

        //clear map
        System.out.print("\nCLEARING MAP...");
        pane.getChildren().clear();
        System.out.println(" DONE");


        //if path is null
        if (DB.getAllNodes() == null) {
            //todo snackbar to say error
            return;
        }
        //create group to contain all the shapes before we add them to the scene
        Group g = new Group();

        //retrieves nodes and edges from DB
        ArrayList<Node> nodeArray = new ArrayList<Node>();
        nodeArray = DB.getAllNodesByFloor(floorNum);
        ArrayList<Edge> edgeArray = new ArrayList<Edge>();
        edgeArray = DB.getAllEdges();

        //display all nodes
        scale = imageWidth / imageView.getFitWidth();
        for (int i = 0; i < nodeArray.size(); i++) {
            double xCoord = nodeArray.get(i).getX() / scale;
            double yCoord = nodeArray.get(i).getY() / scale;
            Circle circle = new Circle(xCoord, yCoord, 2, Color.GREEN);
            g.getChildren().add(circle);

        }
        //display all edges
        for(int i = 0; i < edgeArray.size(); i++) {
            double startX = -1;
            double startY = -1;
            double endX = -1;
            double endY = -1;
            //get start and end node for each edge
            String start = edgeArray.get(i).getStartNodeId();
            String end = edgeArray.get(i).getEndNodeId();
            //parse through nodes, when you reach ones that match start and end of this
            //edge, retrieve coordinates
            for (int j = 0; j < nodeArray.size(); j++) {
                if (nodeArray.get(j).get("floor").equals(floorNum)) {
                    if (nodeArray.get(j).get("id").equals(start)) {
                        startX = nodeArray.get(j).getX() / scale;
                        startY = nodeArray.get(j).getY() / scale;
                    }
                    if (nodeArray.get(j).get("id").equals(end)) {
                        endX = nodeArray.get(j).getX() / scale;
                        endY = nodeArray.get(j).getY() / scale;
                    }
                }
                //if you've retrieved the edge, create a line
                if (startX != -1 && startY != -1 && endX != -1 && endY != -1) {
                    Line line = new Line(startX, startY, endX, endY);
                    line.setStrokeLineCap(StrokeLineCap.ROUND);
                    line.setStrokeWidth(1);
                    g.getChildren().add(line);

                }
            }
        }
        pane.getChildren().add(g);
    }

    /**
     * creates node table
     * @param table
     */
    public void prepareNodes(TreeTableView<Node> table) {
        ArrayList<Node> array = DB.getAllNodes();
        //create columns only the first time the table is created
        if (table.getRoot() == null) {
            //Column 1 - Location
            TreeTableColumn<Node, String> column = new TreeTableColumn<>("Location");
            column.setPrefWidth(320);
            column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().get("longName")));
            table.getColumns().add(column);
            //Column 2 - X Coordinate
            TreeTableColumn<Node, Number> column2 = new TreeTableColumn<>("X-Cord");
            column2.setPrefWidth(150);
            column2.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, Number> p) ->
                    new ReadOnlyIntegerWrapper(p.getValue().getValue().getX()));
            table.getColumns().add(column2);
            //Column 3 - Y Coordinate
            TreeTableColumn<Node, Number> column3 = new TreeTableColumn<>("Y-Cord");
            column3.setPrefWidth(150);
            column3.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, Number> p) ->
                    new ReadOnlyIntegerWrapper(p.getValue().getValue().getY()));
            table.getColumns().add(column3);
            //Column 4 - Node ID
            TreeTableColumn<Node, String> column4 = new TreeTableColumn<>("ID");
            column4.setPrefWidth(150);
            column4.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().get("id")));
            table.getColumns().add(column4);
            //Column 5 - Floor
            TreeTableColumn<Node, String> column5 = new TreeTableColumn<>("Floor");
            column5.setPrefWidth(150);
            column5.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().get("floor")));
            table.getColumns().add(column5);
            //Column 6 - Building
            TreeTableColumn<Node, String> column6 = new TreeTableColumn<>("Building");
            column6.setPrefWidth(150);
            column6.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().get("building")));
            table.getColumns().add(column6);
            //Column 7 - Short Name
            TreeTableColumn<Node, String> column7 = new TreeTableColumn<>("Short Name");
            column7.setPrefWidth(150);
            column7.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().get("shortName")));
            table.getColumns().add(column7);
            //Column 8 - Type of Node
            TreeTableColumn<Node, String> column8 = new TreeTableColumn<>("Type");
            column8.setPrefWidth(150);
            column8.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node, String> p) ->
                    new ReadOnlyStringWrapper(p.getValue().getValue().get("type")));
            table.getColumns().add(column8);
        }
        table.setShowRoot(false);

        //Setting up root node
        Node node0 = new
                Node("ID",
                0, 0, "Floor", "Building",
                "Node Type", "Long Name", "Short Name");
        final TreeItem<Node> rootNode = new TreeItem<Node>(node0);
        table.setRoot(rootNode);
        /*
        //Setting up sub-root nodes
        Node l1Node = new Node("", 0, 0 , "", "", "", "L1", "");
        Node l2Node = new Node("", 0, 0 , "", "", "", "L2", "");
        Node f1Node = new Node("", 0, 0 , "", "", "", "Floor 1", "");
        Node f2Node = new Node("", 0, 0 , "", "", "", "Floor 2", "");
        Node f3Node = new Node("", 0, 0 , "", "", "", "Floor 3", "");
        TreeItem<Node> l1Item = new TreeItem<>(l1Node);
        TreeItem<Node> l2Item = new TreeItem<>(l2Node);
        TreeItem<Node> f1Item = new TreeItem<>(f1Node);
        TreeItem<Node> f2Item = new TreeItem<>(f2Node);
        TreeItem<Node> f3Item = new TreeItem<>(f3Node);

        rootNode.getChildren().addAll(l1Item,l2Item,f1Item,f2Item,f3Item);


//        nodeTypeArrayList.add("HALL");
//        nodeTypeArrayList.add("CONF");
//        nodeTypeArrayList.add("DEPT");
//        nodeTypeArrayList.add("ELEV");
//        nodeTypeArrayList.add("INFO");
//        nodeTypeArrayList.add("LABS");
//        nodeTypeArrayList.add("REST");
//        nodeTypeArrayList.add("RETL");
//        nodeTypeArrayList.add("STAI");
//        nodeTypeArrayList.add("SERV");
//        nodeTypeArrayList.add("EXIT");
//        nodeTypeArrayList.add("BATH");

        //Setting up the children for these
        Node l1Hall = new Node("", 0, 0, "", "", "", "Hall","");
        Node l1Conf = new Node("", 0, 0, "", "", "", "Conf","");
        Node l1Dept = new Node("", 0, 0, "", "", "", "Dept","");
        Node l1Elev = new Node("", 0, 0, "", "", "", "Elev","");
        Node l1Info = new Node("", 0, 0, "", "", "", "Info","");
        Node l1Labs = new Node("", 0, 0, "", "", "", "Labs","");
        Node l1Rest = new Node("", 0, 0, "", "", "", "Rest","");
        Node l1Retl = new Node("", 0, 0, "", "", "", "Retl","");
        Node l1Stai = new Node("", 0, 0, "", "", "", "Stai","");
        Node l1Serv = new Node("", 0, 0, "", "", "", "Serv","");
        Node l1Exit = new Node("", 0, 0, "", "", "", "Exit","");
        Node l1Bath = new Node("", 0, 0, "", "", "", "Bath","");
        TreeItem<Node> l1HallItem = new TreeItem<>(l1Hall);
        TreeItem<Node> l1ConfItem = new TreeItem<>(l1Conf);
        TreeItem<Node> l1DeptItem = new TreeItem<>(l1Dept);
        TreeItem<Node> l1ElevItem = new TreeItem<>(l1Elev);
        TreeItem<Node> l1InfoItem = new TreeItem<>(l1Info);
        TreeItem<Node> l1LabsItem = new TreeItem<>(l1Labs);
        TreeItem<Node> l1RestItem = new TreeItem<>(l1Rest);
        TreeItem<Node> l1RetlItem = new TreeItem<>(l1Retl);
        TreeItem<Node> l1StaiItem = new TreeItem<>(l1Stai);
        TreeItem<Node> l1ServItem = new TreeItem<>(l1Serv);
        TreeItem<Node> l1ExitItem = new TreeItem<>(l1Exit);
        TreeItem<Node> l1BathItem = new TreeItem<>(l1Bath);
        l1Item.getChildren().addAll(l1HallItem,l1ConfItem,l1DeptItem,l1ElevItem,l1InfoItem,l1LabsItem,l1RestItem,l1RetlItem,
                l1StaiItem,l1ServItem,l1ExitItem,l1BathItem);

        Node l2Hall = new Node("", 0, 0, "", "", "", "Hall","");
        Node l2Conf = new Node("", 0, 0, "", "", "", "Conf","");
        Node l2Dept = new Node("", 0, 0, "", "", "", "Dept","");
        Node l2Elev = new Node("", 0, 0, "", "", "", "Elev","");
        Node l2Info = new Node("", 0, 0, "", "", "", "Info","");
        Node l2Labs = new Node("", 0, 0, "", "", "", "Labs","");
        Node l2Rest = new Node("", 0, 0, "", "", "", "Rest","");
        Node l2Retl = new Node("", 0, 0, "", "", "", "Retl","");
        Node l2Stai = new Node("", 0, 0, "", "", "", "Stai","");
        Node l2Serv = new Node("", 0, 0, "", "", "", "Serv","");
        Node l2Exit = new Node("", 0, 0, "", "", "", "Exit","");
        Node l2Bath = new Node("", 0, 0, "", "", "", "Bath","");
        TreeItem<Node> l2HallItem = new TreeItem<>(l2Hall);
        TreeItem<Node> l2ConfItem = new TreeItem<>(l2Conf);
        TreeItem<Node> l2DeptItem = new TreeItem<>(l2Dept);
        TreeItem<Node> l2ElevItem = new TreeItem<>(l2Elev);
        TreeItem<Node> l2InfoItem = new TreeItem<>(l2Info);
        TreeItem<Node> l2LabsItem = new TreeItem<>(l2Labs);
        TreeItem<Node> l2RestItem = new TreeItem<>(l2Rest);
        TreeItem<Node> l2RetlItem = new TreeItem<>(l2Retl);
        TreeItem<Node> l2StaiItem = new TreeItem<>(l2Stai);
        TreeItem<Node> l2ServItem = new TreeItem<>(l2Serv);
        TreeItem<Node> l2ExitItem = new TreeItem<>(l2Exit);
        TreeItem<Node> l2BathItem = new TreeItem<>(l2Bath);
        l2Item.getChildren().addAll(l2HallItem,l2ConfItem,l2DeptItem,l2ElevItem,l2InfoItem,l2LabsItem,l2RestItem,l2RetlItem,
                l2StaiItem,l2ServItem,l2ExitItem,l2BathItem);

        Node f1Hall = new Node("", 0, 0, "", "", "", "Hall","");
        Node f1Conf = new Node("", 0, 0, "", "", "", "Conf","");
        Node f1Dept = new Node("", 0, 0, "", "", "", "Dept","");
        Node f1Elev = new Node("", 0, 0, "", "", "", "Elev","");
        Node f1Info = new Node("", 0, 0, "", "", "", "Info","");
        Node f1Labs = new Node("", 0, 0, "", "", "", "Labs","");
        Node f1Rest = new Node("", 0, 0, "", "", "", "Rest","");
        Node f1Retl = new Node("", 0, 0, "", "", "", "Retl","");
        Node f1Stai = new Node("", 0, 0, "", "", "", "Stai","");
        Node f1Serv = new Node("", 0, 0, "", "", "", "Serv","");
        Node f1Exit = new Node("", 0, 0, "", "", "", "Exit","");
        Node f1Bath = new Node("", 0, 0, "", "", "", "Bath","");
        TreeItem<Node> f1HallItem = new TreeItem<>(f1Hall);
        TreeItem<Node> f1ConfItem = new TreeItem<>(f1Conf);
        TreeItem<Node> f1DeptItem = new TreeItem<>(f1Dept);
        TreeItem<Node> f1ElevItem = new TreeItem<>(f1Elev);
        TreeItem<Node> f1InfoItem = new TreeItem<>(f1Info);
        TreeItem<Node> f1LabsItem = new TreeItem<>(f1Labs);
        TreeItem<Node> f1RestItem = new TreeItem<>(f1Rest);
        TreeItem<Node> f1RetlItem = new TreeItem<>(f1Retl);
        TreeItem<Node> f1StaiItem = new TreeItem<>(f1Stai);
        TreeItem<Node> f1ServItem = new TreeItem<>(f1Serv);
        TreeItem<Node> f1ExitItem = new TreeItem<>(f1Exit);
        TreeItem<Node> f1BathItem = new TreeItem<>(f1Bath);
        f1Item.getChildren().addAll(f1HallItem,f1ConfItem,f1DeptItem,f1ElevItem,f1InfoItem,f1LabsItem,f1RestItem,f1RetlItem,
                f1StaiItem,f1ServItem,f1ExitItem,f1BathItem);

        Node f2Hall = new Node("", 0, 0, "", "", "", "Hall","");
        Node f2Conf = new Node("", 0, 0, "", "", "", "Conf","");
        Node f2Dept = new Node("", 0, 0, "", "", "", "Dept","");
        Node f2Elev = new Node("", 0, 0, "", "", "", "Elev","");
        Node f2Info = new Node("", 0, 0, "", "", "", "Info","");
        Node f2Labs = new Node("", 0, 0, "", "", "", "Labs","");
        Node f2Rest = new Node("", 0, 0, "", "", "", "Rest","");
        Node f2Retl = new Node("", 0, 0, "", "", "", "Retl","");
        Node f2Stai = new Node("", 0, 0, "", "", "", "Stai","");
        Node f2Serv = new Node("", 0, 0, "", "", "", "Serv","");
        Node f2Exit = new Node("", 0, 0, "", "", "", "Exit","");
        Node f2Bath = new Node("", 0, 0, "", "", "", "Bath","");
        TreeItem<Node> f2HallItem = new TreeItem<>(f2Hall);
        TreeItem<Node> f2ConfItem = new TreeItem<>(f2Conf);
        TreeItem<Node> f2DeptItem = new TreeItem<>(f2Dept);
        TreeItem<Node> f2ElevItem = new TreeItem<>(f2Elev);
        TreeItem<Node> f2InfoItem = new TreeItem<>(f2Info);
        TreeItem<Node> f2LabsItem = new TreeItem<>(f2Labs);
        TreeItem<Node> f2RestItem = new TreeItem<>(f2Rest);
        TreeItem<Node> f2RetlItem = new TreeItem<>(f2Retl);
        TreeItem<Node> f2StaiItem = new TreeItem<>(f2Stai);
        TreeItem<Node> f2ServItem = new TreeItem<>(f2Serv);
        TreeItem<Node> f2ExitItem = new TreeItem<>(f2Exit);
        TreeItem<Node> f2BathItem = new TreeItem<>(f2Bath);
        f2Item.getChildren().addAll(f2HallItem,f2ConfItem,f2DeptItem,f2ElevItem,f2InfoItem,f2LabsItem,f2RestItem,f2RetlItem,
                f2StaiItem,f2ServItem,f2ExitItem,f2BathItem);

        Node f3Hall = new Node("", 0, 0, "", "", "", "Hall","");
        Node f3Conf = new Node("", 0, 0, "", "", "", "Conf","");
        Node f3Dept = new Node("", 0, 0, "", "", "", "Dept","");
        Node f3Elev = new Node("", 0, 0, "", "", "", "Elev","");
        Node f3Info = new Node("", 0, 0, "", "", "", "Info","");
        Node f3Labs = new Node("", 0, 0, "", "", "", "Labs","");
        Node f3Rest = new Node("", 0, 0, "", "", "", "Rest","");
        Node f3Retl = new Node("", 0, 0, "", "", "", "Retl","");
        Node f3Stai = new Node("", 0, 0, "", "", "", "Stai","");
        Node f3Serv = new Node("", 0, 0, "", "", "", "Serv","");
        Node f3Exit = new Node("", 0, 0, "", "", "", "Exit","");
        Node f3Bath = new Node("", 0, 0, "", "", "", "Bath","");
        TreeItem<Node> f3HallItem = new TreeItem<>(f3Hall);
        TreeItem<Node> f3ConfItem = new TreeItem<>(f3Conf);
        TreeItem<Node> f3DeptItem = new TreeItem<>(f3Dept);
        TreeItem<Node> f3ElevItem = new TreeItem<>(f3Elev);
        TreeItem<Node> f3InfoItem = new TreeItem<>(f3Info);
        TreeItem<Node> f3LabsItem = new TreeItem<>(f3Labs);
        TreeItem<Node> f3RestItem = new TreeItem<>(f3Rest);
        TreeItem<Node> f3RetlItem = new TreeItem<>(f3Retl);
        TreeItem<Node> f3StaiItem = new TreeItem<>(f3Stai);
        TreeItem<Node> f3ServItem = new TreeItem<>(f3Serv);
        TreeItem<Node> f3ExitItem = new TreeItem<>(f3Exit);
        TreeItem<Node> f3BathItem = new TreeItem<>(f3Bath);
        f3Item.getChildren().addAll(f3HallItem,f3ConfItem,f3DeptItem,f3ElevItem,f3InfoItem,f3LabsItem,f3RestItem,f3RetlItem,
                f3StaiItem,f3ServItem,f3ExitItem,f3BathItem);
        */


//        if (table.getRoot().getChildren().isEmpty() == false && array.size() > 0) {
//            table.getRoot().getChildren().remove(0, array.size() - 1);
//        }
        //iterate over list of nodes from DB, add to table
        for (int i = 0; i < array.size(); i++) {
            Node s = array.get(i);
            final TreeItem<Node> node = new TreeItem<Node>(s);
            table.getRoot().getChildren().add(node);
        }
            /*

            if(s.get("floor").equals("L1")) {
                addToTable(node, l1HallItem,l1ConfItem,l1DeptItem,l1ElevItem,l1InfoItem,l1LabsItem,l1RestItem,l1RetlItem,
                        l1StaiItem,l1ServItem,l1ExitItem,l1BathItem);
            }
            if(s.get("floor").equals("L2")) {
                addToTable(node, l2HallItem,l2ConfItem,l2DeptItem,l2ElevItem,l2InfoItem,l2LabsItem,l2RestItem,l2RetlItem,
                        l2StaiItem,l2ServItem,l2ExitItem,l2BathItem);
            }
            if(s.get("floor").equals("1")) {
                addToTable(node, f1HallItem,f1ConfItem,f1DeptItem,f1ElevItem,f1InfoItem,f1LabsItem,f1RestItem,f1RetlItem,
                        f1StaiItem,f1ServItem,f1ExitItem,f1BathItem);
            }
            if(s.get("floor").equals("2")) {
                addToTable(node, f2HallItem,f2ConfItem,f2DeptItem,f2ElevItem,f2InfoItem,f2LabsItem,f2RestItem,f2RetlItem,
                        f2StaiItem,f2ServItem,f2ExitItem,f2BathItem);
            }
            if(s.get("floor").equals("3")) {
                addToTable(node, f3HallItem,f3ConfItem,f3DeptItem,f3ElevItem,f3InfoItem,f3LabsItem,f3RestItem,f3RetlItem,
                        f3StaiItem,f3ServItem,f3ExitItem,f3BathItem);
            }
            //int n = array.get(i).getX();
            //table.getRoot().getChildren().add(node);
        }*/
    }

    /**
     * for creating dropdowns in node table
     * @param add
     * @param hall
     * @param conf
     * @param dept
     * @param elev
     * @param info
     * @param labs
     * @param rest
     * @param retl
     * @param stai
     * @param serv
     * @param exit
     * @param bath
     */
    private void addToTable(TreeItem<Node> add, TreeItem<Node> hall, TreeItem<Node> conf, TreeItem<Node> dept, TreeItem<Node> elev,
                            TreeItem<Node> info, TreeItem<Node> labs, TreeItem<Node> rest, TreeItem<Node> retl, TreeItem<Node> stai,
                            TreeItem<Node> serv, TreeItem<Node> exit, TreeItem<Node> bath) {
        if (add.getValue().get("type").equals("HALL")) {
            hall.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("CONF")) {
            conf.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("DEPT")) {
            dept.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("ELEV")) {
            elev.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("INFO")) {
            info.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("LABS")) {
            labs.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("REST")) {
            rest.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("RETL")) {
            retl.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("STAI")) {
            stai.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("SERV")) {
            serv.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("EXIT")) {
            exit.getChildren().add(add);
        }
        if (add.getValue().get("type").equals("BATH")) {
            bath.getChildren().add(add);
        }
    }

    /**
     * creates edge table
     * @param table
     */
    public void prepareEdges(TreeTableView<Edge> table) {
        ArrayList<Edge> array = DB.getAllEdges();
        //make columns only first time table is created
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
        //gets rid of dropdown
        table.setShowRoot(false);
        //iterate over edge list from DB, add to table
        for (int i = 0; i < array.size(); i++) {
            Edge s = array.get(i);
            final TreeItem<Edge> edge = new TreeItem<>(s);
            table.getRoot().getChildren().add(edge);
        }
    }

    /**
     * retrieves all the inputted info from the user, creates a new node and adds it to database
     * using database's addNode fcn
     * @return
     */
    public int addNode() {
        int i = -1;
        if (floorInput.getValue().toString().equals("")) {
            errorPopup("Must input Floor");
            return i;
        }
        if (longNameInput.getText().equals("")) {
            errorPopup("Must input Long Name");
            return i;
        }
        if (shortNameInput.getText().equals("")) {
            errorPopup("Must input Short Name");
            return i;
        }
        if (typeInput.getSelectionModel().equals("")) {
            errorPopup("Must input Type");
            return i;
        }
        if (buildingInput.getValue().toString().equals("")) {
            errorPopup("Must input Building");
            return i;
        }
        if (xCordInput.getText().equals("")) {
            errorPopup("Must input X Coordinate");
            return i;
        }
        if (yCordInput.getText().equals("")) {
            errorPopup("Must input Y Coordinate");
            return i;
        }
        int xVal = Integer.parseInt(xCordInput.getText());
        int yVal = Integer.parseInt(yCordInput.getText());
        i = DB.addNode(genNodeID(typeInput.getValue().toString(),floorInput.getValue().toString(), longNameInput.getText()), xVal, yVal, floorInput.getValue().toString(), buildingInput.getValue().toString(), typeInput.getValue().toString(), longNameInput.getText(), shortNameInput.getText());
        System.out.println(i);
        return i;
    }

    /**
     * calls the addNode fcn when the add node button is pressed
     * @param e
     */
    @FXML
    public void addNodeButton(ActionEvent e) {
        addNode();
    }

    /**
     * Autogenerate NodeIDs
     * Elevators need to have the format `Elevator X xxxxx`
     * @param type The type of Node this is
     * @param floor The floor this Node is on
     * @param longName The longName of the node
     * @return The NodeID of the given Node
     */
    public String genNodeID(String type, String floor, String longName){
        StringBuilder SB = new StringBuilder("e");
        SB.append(type);

        if (type.equalsIgnoreCase("ELEV")) {
            SB.append("00");
            SB.append(longName.charAt(9));
            //Elevator names need to start with 'Elevator X xxxxx"
        } else {
            makeConnection connection = makeConnection.makeConnection();
            int instance = DB.countNodeTypeOnFloor("e", floor, type) + 1;
            SB.append(String.format("%03d", instance));
        }

        try{
            int num = Integer.parseInt(floor);
            SB.append("0").append(num);
        } catch (NumberFormatException e) {
            if (floor.equalsIgnoreCase("G") || floor.equalsIgnoreCase("GG")){
                SB.append("GG");
            } else {
                SB.append(floor);
            }
        }

        return SB.toString();
    }

    /**
     * retrieves the ID of the selected item in the table, passes that into deleteNode fcn from database
     * @param table
     */
    public int deleteNode(TreeTableView<Node> table) {
        int s = -1;
        ArrayList<Node> array = DB.getAllNodes();
        //for using tree table
        if (table.getSelectionModel().getSelectedItem() != null) {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).get("id").equals(table.getSelectionModel().getSelectedItem().getValue().get("id"))) {
                    s = DB.deleteNode(array.get(i).get("id"));
                }
            }
            //for using map
        } else {
            if (idInput.getValue() != null) {
                for (int i = 0; i < array.size(); i++) {
                    if (array.get(i).get("id").equals(idInput.getValue().toString())) {
                        s = DB.deleteNode(array.get(i).get("id"));
                    }
                }

            }
        }
        return s;
    }

    /**
     * calls the deleteNode fcn when the delete button is clicked
     * @param e
     */
    @FXML
    public void deleteNodeButton(ActionEvent e) {
        deleteNode(nodeTreeTable);
    }

    public void editNode(TreeTableView<Node> table) {
        String id = null;
        Integer xVal = null;
        Integer yVal = null;
        String floor = null;
        String longName = null;
        String shortName = null;
        String type = null;
        String building = null;
        //for selecting item from table
        if (table.getSelectionModel().getSelectedItem() != null) {
            id = table.getSelectionModel().getSelectedItem().getValue().get("id");
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            floor = table.getSelectionModel().getSelectedItem().getValue().get("floor");
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            longName = table.getSelectionModel().getSelectedItem().getValue().get("longName");
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            shortName = table.getSelectionModel().getSelectedItem().getValue().get("shortName");
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            type = table.getSelectionModel().getSelectedItem().getValue().get("type");
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            building = table.getSelectionModel().getSelectedItem().getValue().get("building");
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            xVal = table.getSelectionModel().getSelectedItem().getValue().getX();
        }
        if (table.getSelectionModel().getSelectedItem() != null) {
            yVal = table.getSelectionModel().getSelectedItem().getValue().getY();
        }

        //for selecting item from map
        if (idInput.getValue() != null) {
            id = idInput.getValue().toString();
        }
        if (floorInput.getValue() != null) {
            floor = floorInput.getValue().toString();
        }
        if (!longNameInput.getText().equals("")) {
            longName = longNameInput.getText();
        }
        if (!shortNameInput.getText().equals("")) {
            shortName = shortNameInput.getText();
        }
        if (typeInput.getValue() != null) {
            type = typeInput.getValue().toString();
        }
        if (buildingInput.getValue() != null) {
            building = buildingInput.getValue().toString();
        }
        if (!xCordInput.getText().equals("")) {
            xVal = Integer.parseInt(xCordInput.getText());
            xVal = Integer.valueOf(xVal);
        }
        if (!yCordInput.getText().equals("")) {
            yVal = Integer.parseInt(yCordInput.getText());
            yVal = Integer.valueOf(yVal);
        }
        DB.modifyNode(id, xVal, yVal, floor, building, type, longName, shortName);
    }

    public void editNodeButton(ActionEvent e) {
        editNode(nodeTreeTable);
    }

    /**
     * brings user to the node table, clears fields, brings you to
     * node editor fields as well. Resets click count to 0 for edges
     * @param e
     */

    public void toNodeTable(ActionEvent e) {
        nodeTreeTable.toFront();
        nodeVBox.toFront();
        nodeVBox.setVisible(true);
        edgeVBox.setVisible(false);
        longNameInput.clear();
        shortNameInput.clear();
        floorInput.getSelectionModel().clearSelection();
        floorInput.setValue(null);
        idInput.getSelectionModel().clearSelection();
        idInput.setValue(null);
        buildingInput.getSelectionModel().clearSelection();
        buildingInput.setValue(null);
        typeInput.getSelectionModel().clearSelection();
        typeInput.setValue(null);
        xCordInput.clear();
        yCordInput.clear();
        selection = 0;


    }

    /**
     * brings user to the edge table, clears fields, brings you to
     * edge editor fields as well. Resets click count to 0 for edges
     * @param e
     */
    public void toEdgeTable(ActionEvent e) {
        edgeTreeTable.toFront();
        edgeVBox.toFront();
        edgeVBox.setVisible(true);
        nodeVBox.setVisible(false);
        edgeID.setValue(null);
        startLocation.getSelectionModel().clearSelection();
        startLocation.setValue(null);
        endLocation.getSelectionModel().clearSelection();
        endLocation.setValue(null);
        selection = 0;

    }

    /**
     * brings user to map
     * @param e
     */
    public void toMap(ActionEvent e) {
        rootBorderPane.toFront();
    }

    /**
     * resets tables and map after edits made
     */
    public void refresh() {
        initialize();
    }

    /**
     * button that refreshes
     * @param e
     */
    public void refreshButton(ActionEvent e) {
        toNodeMode(e);
        refresh();
    }

    /**
     * brings up error popup
     * @param errorMessage what the popup will say
     */
    @FXML
    private void errorPopup(String errorMessage) {
        JFXDialogLayout error = new JFXDialogLayout();
        error.setHeading(new Text("Error!"));
        error.setBody(new Text(errorMessage));
        JFXDialog dialog = new JFXDialog(stackPane, error, JFXDialog.DialogTransition.CENTER);
        JFXButton okay = new JFXButton("Okay");
        okay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();

            }
        });
        error.setActions(okay);
        dialog.show();
    }


    /**
     * allows user to retrieve current data being used
     * @param e
     */
    @FXML
    public void fileOpenerNode(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
        makeConnection connection = makeConnection.makeConnection();
        if (file != null) {
            //Have to save edge table so we can get it back after deleting
            DB.getNewCSVFile("hasEdge");
            File saveEdges = new File("CSVs/outputEdge.csv");

            //This is where tables are cleared and refilled
            DB.deleteEdgeTable();
            DB.deleteNodeTable();
            DB.createNodeTable();
            DB.createEdgeTable();
            DB.populateTable("node", file);
            DB.populateTable("hasEdge", saveEdges);
            System.out.println("Success");
        }


    }

    /**
     *opens the file explorer on user's device, allows user to select CSV file,
     * uploads file to database, refreshes page
     * @param e actionevent
     */
    @FXML
    private void openFileNode(ActionEvent e) throws IOException {

        DB.getNewCSVFile("node");
        File file = new File("CSVs/outputNode.csv");
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);

    }

    /**
     * Method called by FXMLLoader when initialization is complete. Propagates initial fields in FXML:
     * Namely, adds FloorMap PNG and fills dropdowns with DB data, sets default floor.
     */
    @FXML
    void initialize() {

        //Creating ID dropdown
        ArrayList<String> idList = DB.getListOfNodeIDS();
        ObservableList<String> listOfIDS = FXCollections.observableArrayList();
        listOfIDS.addAll(idList);

        //Creating Type dropdown
        ArrayList<String> nodeTypeArrayList = new ArrayList<String>();
        nodeTypeArrayList.add("HALL");
        nodeTypeArrayList.add("CONF");
        nodeTypeArrayList.add("DEPT");
        nodeTypeArrayList.add("HALL");
        nodeTypeArrayList.add("ELEV");
        nodeTypeArrayList.add("INFO");
        nodeTypeArrayList.add("LABS");
        nodeTypeArrayList.add("REST");
        nodeTypeArrayList.add("RETL");
        nodeTypeArrayList.add("STAI");
        nodeTypeArrayList.add("SERV");
        nodeTypeArrayList.add("EXIT");
        nodeTypeArrayList.add("BATH");
        ObservableList<String> listOfType = FXCollections.observableArrayList();
        listOfType.addAll(nodeTypeArrayList);

        //Creating Floor Dropdown
        ArrayList<String> nodeFloorArrayList = new ArrayList<String>();
        nodeFloorArrayList.add("G");
        nodeFloorArrayList.add("L1");
        nodeFloorArrayList.add("L2");
        nodeFloorArrayList.add("1");
        nodeFloorArrayList.add("2");
        nodeFloorArrayList.add("3");
        ObservableList<String> listOfFloors = FXCollections.observableArrayList();
        listOfFloors.addAll(nodeFloorArrayList);

        //Creating Building Dropdown
        ArrayList<String> nodeBuildingArrayList = new ArrayList<String>();
        nodeBuildingArrayList.add("BTM");
        nodeBuildingArrayList.add("45 Francis");
        nodeBuildingArrayList.add("15 Francis");
        nodeBuildingArrayList.add("Tower");
        nodeBuildingArrayList.add("Shapiro");
        ObservableList<String> listOfBuildings = FXCollections.observableArrayList();
        listOfBuildings.addAll(nodeBuildingArrayList);

        //add ObservableLists to dropdowns
        typeInput.setItems(listOfType);
        floorInput.setItems(listOfFloors);
        buildingInput.setItems(listOfBuildings);
        idInput.setItems(listOfIDS);
        floorSelector.setItems(listOfFloors);

        //get primaryStage
        Stage primaryStage = App.getPrimaryStage();

        //If exit button is clicked, exit app
        exit.setOnMouseClicked(event -> {
            App app = new App();
            app.stop();
        });

        //get dimensions of stage
        stageWidth = primaryStage.getWidth();
        stageHeight = primaryStage.getHeight();

        assert startLocation != null : "fx:id=\"startLocation\" was not injected: check your FXML file 'PathFinder.fxml'.";
        assert endLocation != null : "fx:id=\"endLocation\" was not injected: check your FXML file 'PathFinder.fxml'.";

        //Get longNames & IDs
        longNameArrayList = FXCollections.observableArrayList();

        //nodes and longnames
        nodeIDArrayList = new ArrayList<String>();
        nodeArrayList = DB.getAllNodes();
        for (int i = 0; i < nodeArrayList.size(); i++) {
            longNameArrayList.add(nodeArrayList.get(i).get("longName"));
            nodeIDArrayList.add(nodeArrayList.get(i).get("id"));
        }

        //edges
        edgeIDArrayList = FXCollections.observableArrayList();
        edgeArrayList = DB.getAllEdges();
        for(int i = 0; i < edgeArrayList.size(); i++) {
            edgeIDArrayList.add(edgeArrayList.get(i).getId());

        }


        //add ObservableLists to dropdowns
        edgeID.setItems(edgeIDArrayList);
        startLocation.setItems(longNameArrayList);
        endLocation.setItems(longNameArrayList);
        System.out.println("done");

        new AutoCompleteComboBoxListener<>(startLocation);
        new AutoCompleteComboBoxListener<>(endLocation);

        //Set up zoomable and pannable panes
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);

        //set default/initial floor for map
        Image image = new Image("edu/wpi/cs3733/D21/teamE/maps/1.png");
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        imageView.setImage(image);

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(primaryStage.getWidth());

        StackPane stackPane = new StackPane(imageView, borderPane);
        ScrollPane scrollPane = new ScrollPane(new Group(stackPane));

        //make scroll pane pannable
        scrollPane.setPannable(true);

        //get rid of side scroll bars
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //bind the zoom slider to the map
        stackPane.scaleXProperty().bind(zoomSlider.valueProperty());
        stackPane.scaleYProperty().bind(zoomSlider.valueProperty());

        rootBorderPane.setCenter(scrollPane);
        rootBorderPane.setPrefWidth(stageWidth);
        rootBorderPane.setPrefHeight(stageHeight);

        System.out.println("Finish PathFinder Init.");

        drawMap(currentFloor);
        prepareNodes(nodeTreeTable);
        prepareEdges(edgeTreeTable);
        edgeVBox.setVisible(false);

        //populates fields with information of selected node in table
        nodeTreeTable.setOnMouseClicked(e -> {
            if (nodeTreeTable.getSelectionModel().getSelectedItem() != null) {
                System.out.println("in!");
                longNameInput.setText(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().get("longName"));
                shortNameInput.setText(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().get("shortName"));
                xCordInput.setText(Integer.toString(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().getX()));
                yCordInput.setText(Integer.toString(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().getY()));
                floorInput.setValue(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().get("floor"));
                typeInput.setValue(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().get("type"));
                buildingInput.setValue(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().get("building"));
                idInput.setValue(nodeTreeTable.getSelectionModel().getSelectedItem().getValue().get("id"));

            }
        });

        //populates fields with information of selected edge in table
        edgeTreeTable.setOnMouseClicked(e -> {
            if(edgeTreeTable.getSelectionModel().getSelectedItem() != null) {
                edgeID.setValue(edgeTreeTable.getSelectionModel().getSelectedItem().getValue().getId());
                startLocation.setValue(edgeTreeTable.getSelectionModel().getSelectedItem().getValue().getStartNodeId());
                endLocation.setValue(edgeTreeTable.getSelectionModel().getSelectedItem().getValue().getEndNodeId());

            }
        });

        //retrieving nodes
        ArrayList<Node> array = DB.getAllNodes();

        //creating group for adding nodes/edges
        Group g = new Group();

        //for clicks interacting with map
        pane.setOnMouseClicked(e -> {
            //double click
            if (e.getClickCount() == 2) {
                //ints for displaying
                double xCoordScale = e.getX();
                xCoordScale = xCoordScale * scale;
                int xCordIntScale = (int) xCoordScale;
                double yCoordScale = e.getY();
                yCoordScale = yCoordScale * scale;
                int yCordIntScale = (int) yCoordScale;
                //ints for placing circle
                double xCoord = e.getX();
                double yCoord = e.getY();
                Circle circle = new Circle(xCoord, yCoord, 2, Color.GREEN);
                g.getChildren().add(circle);
                pane.getChildren().add(g);
                //clears fields, populates X, Y coordinates of created node in fields
                longNameInput.clear();
                shortNameInput.clear();
                floorInput.getSelectionModel().clearSelection();
                floorInput.setValue(null);
                idInput.getSelectionModel().clearSelection();
                idInput.setValue(null);
                buildingInput.getSelectionModel().clearSelection();
                buildingInput.setValue(null);
                typeInput.getSelectionModel().clearSelection();
                typeInput.setValue(null);
                xCordInput.setText(Integer.toString(xCordIntScale));
                yCordInput.setText(Integer.toString(yCordIntScale));
            } else {
                //single click
                if (e.getClickCount() == 1) {
                    //coordinates of click
                    double X = e.getX();
                    int xInt = (int) X;
                    double Y = e.getY();
                    int yInt = (int) Y;
                    for (int i = 0; i < array.size(); i++) {
                        if(array.get(i).get("floor").equals(currentFloor)) {
                            //coordinates of current node
                            double nodeX = array.get(i).getX() / scale;
                            int nodeXInt = (int) nodeX;
                            double nodeY = array.get(i).getY() / scale;
                            int nodeYInt = (int) nodeY;

                            //if node coordinates match click coordinates +- 1, autofill fields with node info
                            if (Math.abs(nodeXInt - xInt) <= 1 && Math.abs(nodeYInt - yInt) <= 1) {
                                idInput.setValue(array.get(i).get("id"));
                                floorInput.setValue(array.get(i).get("floor"));
                                longNameInput.setText(array.get(i).get("longName"));
                                shortNameInput.setText(array.get(i).get("shortName"));
                                xCordInput.setText(Integer.toString(array.get(i).getX()));
                                yCordInput.setText(Integer.toString(array.get(i).getY()));
                                typeInput.setValue(array.get(i).get("type"));
                                buildingInput.setValue(array.get(i).get("building"));

                                //for edges, use counter to determine if it is first or second node selected
                                //populate edge fields with information
                                selection++;
                                if (selection == 1) {
                                    startID = array.get(i).get("id");
                                    startLocation.setValue(array.get(i).get("longName"));
                                }
                                if (selection == 2) {
                                    endLocation.setValue(array.get(i).get("longName"));
                                    endID = array.get(i).get("id");
                                    edgeIDArrayList.add(startID + "_" + endID);
                                    edgeID.setItems(edgeIDArrayList);
                                    edgeID.setValue(startID + "_" + endID);
                                    selection = 0;
                                }
                            }
                        }
                    }
                }
            }
        });

    }


    /**
     * cancel button function
     * @param e
     */
    public void cancelButton(ActionEvent e) {
        cancelEdge();
    }

    /**
     * clears fields and selection of edge, resets edge process
     */
    public void cancelEdge() {
        startLocation.setValue(null);
        endLocation.setValue(null);
        edgeID.setValue(null);
        selection = 0;
    }

    /**
     * delete edge button function
     * @param actionEvent
     */
    public void deleteEdgeButton(ActionEvent actionEvent) {
        deleteEdge();
    }

    /**
     * deletes edge if ID in the dropdown matches ID of an edge in DB
     */
    public void deleteEdge() {
        ArrayList<Edge> array = DB.getAllEdges();
        if(edgeID.getValue() != null && startLocation.getValue() != null && endLocation.getValue() != null) {
            for(int i = 0; i < array.size(); i++) {
                if(array.get(i).getId().equals(edgeID.getValue().toString())) {
                    System.out.println("This lies between " + startLocation.getValue() + " and " + endLocation.getValue());
                    DB.deleteEdge(array.get(i).getStartNodeId(), array.get(i).getEndNodeId());
                }
            }
        }
    }

    /**
     * add edge button function
     * @param actionEvent
     */
    public void addEdgeButton(ActionEvent actionEvent) {
        addEdge();
    }

    /**
     * creates an edge using information taken from fields
     */
    public void addEdge() {
        ArrayList<Node> array = DB.getAllNodes();
        String startInput = null;
        String endInput = null;
        if(startLocation.getValue() != null && endLocation.getValue() != null) {
            System.out.println(startLocation.getValue());
            String ID = null;
            //retrieves start and end node of new edge
            for(int i = 0; i < array.size(); i++) {
                if(array.get(i).get("longName").equals(startLocation.getValue())) {
                    startInput = array.get(i).get("id");
                }
                if(array.get(i).get("longName").equals(endLocation.getValue())) {
                    endInput = array.get(i).get("id");
                }
            }
            //creates node ID
            if(startInput != null && endInput != null) {
                ID = startInput + "_" + endInput;
            }
            //adds edge, populates edge ID field with new ID
            DB.addEdge(ID, startInput, endInput);
            edgeID.setValue(ID);
        }
    }

    /**
     * brings user to Edge editor mode, which brings edge fields into view and clears
     * the fields
     * @param actionEvent
     */
    public void toEdgeMode(ActionEvent actionEvent) {
        edgeVBox.toFront();
        edgeVBox.setVisible(true);
        nodeVBox.setVisible(false);
        edgeID.getSelectionModel().clearSelection();
        edgeID.setValue(null);
        startLocation.getSelectionModel().clearSelection();
        startLocation.setValue(null);
        endLocation.getSelectionModel().clearSelection();
        endLocation.setValue(null);
        selection = 0;
    }

    /**
     * brings user to Node editor mode, which brings node fields into view and clears
     * the fields
     * @param actionEvent
     */
    public void toNodeMode(ActionEvent actionEvent) {
        nodeVBox.toFront();
        nodeVBox.setVisible(true);
        edgeVBox.setVisible(false);
        longNameInput.clear();
        shortNameInput.clear();
        floorInput.getSelectionModel().clearSelection();
        floorInput.setValue(null);
        idInput.getSelectionModel().clearSelection();
        idInput.setValue(null);
        buildingInput.getSelectionModel().clearSelection();
        buildingInput.setValue(null);
        typeInput.getSelectionModel().clearSelection();
        typeInput.setValue(null);
        xCordInput.clear();
        yCordInput.clear();
    }

    /**
     * function for floor dropdown, allows user to change which floor's
     * map they are currently viewing
     */
    public void selectFloor() {
        //set image
        currentFloor = floorSelector.getValue().toString();
        Image image = new Image("edu/wpi/cs3733/D21/teamE/maps/" + floorSelector.getValue().toString() + ".png");
        imageView.setImage(image);

        //draw path for new floor
        drawMap(currentFloor);
    }
}

