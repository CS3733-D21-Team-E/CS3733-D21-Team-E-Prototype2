package edu.wpi.TeamE.algorithms.pathfinding;

import edu.wpi.TeamE.algorithms.Node;
import edu.wpi.TeamE.algorithms.Path;
import edu.wpi.TeamE.databases.*;
import edu.wpi.cs3733.D21.teamE.database.appointmentDB;
import edu.wpi.cs3733.D21.teamE.database.csvDB;
import edu.wpi.cs3733.D21.teamE.DB;
import edu.wpi.cs3733.D21.teamE.DB;
import edu.wpi.cs3733.D21.teamE.database.makeConnection;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Examples {

    public static void main(String[] args){
        System.out.println("STARTING UP!!!");
        makeConnection connection = makeConnection.makeConnection();
        System.out.println("Connected to the DB");

        File nodes = new File("CSVs/MapEAllnodes.csv");
        File edges = new File("CSVs/MapEAlledges.csv");
        boolean tablesExist = connection.allTablesThere();
        if(!tablesExist){
            try {
                DB.createNodeTable();
                DB.createEdgeTable();
                UserAccountDB.createUserAccountTable();
                RequestsDB.createRequestsTable();
                RequestsDB.createFloralRequestsTable();
                RequestsDB.createSanitationTable();
                RequestsDB.createExtTransportTable();
                RequestsDB.createMedDeliveryTable();
                RequestsDB.createSecurityServTable();
                appointmentDB.createAppointmentTable();
                csvDB.populateTable("node", nodes);
                csvDB.populateTable("hasEdge", edges);
//                csvDB.addDataForPresentation();
                System.out.println("Tables were created");
            } catch (Exception e) {
                System.out.println("Tables already there");

                System.out.println("Tables were created and populated");
            }
        }


        String startNode = "ADEPT00201";
        String endNode = "BHALL03802";

        //the pathfinding API is now entirely wrapped up in the SearchContext class
        //this will allow you to flexibly configure what kind of search you wish to execute
        //if you want vanilla a* you can instantiate like this
        SearchContext search = new SearchContext();
        //or like this (these lines (^v) are equivalent)
        search = new SearchContext("VANILLA");
        //safe search will avoid the emergency room
        search = new SearchContext("SAFE");
        //handicap search will avoid stairs
        //also nothing is case sensitive
        search = new SearchContext("A*", "handicap");

        //if you don't want to instantiate a new one every time (recommended, better for memory)
        //you can specify a new algorithm or new conditions like this
        search.setAlgo("DFS");
        search.setAlgo("A*");

        search.setConstraint("HANDICAP");
        search.setConstraint("HaNdICap");
        search.setConstraint("SAFE");
        search.setConstraint("VANILLA");

        //searching for a new path is the same
        Path p = search.search(startNode, endNode);
        Node start = p.getStart();
        Node end = p.getEnd();
        double length = p.getPathLength();

        p.print("id", "floor");

        for(Path leg : p.splitByFloor()){
            leg.print("id", "floor");
        }
        
        System.out.println();

        Node node1 = new Node("1001", 5, 5, "L2", "building1", "type1", "name 1", "name 1");
        Node node2 = new Node("1002", 7, 7, "L2", "building1", "type1", "name 1", "name 1");

        Path path = new Path(node1, node2);
        for (String dir : path.makeDirectionsWithDist()) {
            System.out.println(dir);
        }
        System.out.println();


        List<Node> nodeList = p.toList();

        for(Node node : p){
            //you can also iterate through all like this
        }


        //however it should be mentioned that search algorithms
        //use a local copy of the db, and this local copy does not update when the db is edited
        //but it pulls in fresh data on every instantiation of Searcher or DFSSearch
        //so when using these objects make sure the algorithm instantiations aren't too persistent
        //or you have the option of saying
        //search.refresh();
        //if you're not sure if a change has been made to the db
        //this method pulls in the whole node table and the whole edge table
        //so should be used only if necessary

        //additionally it should be noted that using search with constraints leads
        //to a higher likelihood of search returning null (i.e. no path being found)
    }
}
