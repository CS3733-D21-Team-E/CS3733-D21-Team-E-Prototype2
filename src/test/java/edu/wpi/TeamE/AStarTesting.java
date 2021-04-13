package edu.wpi.TeamE;

import edu.wpi.TeamE.algorithms.pathfinding.AStarSearch;
import edu.wpi.TeamE.algorithms.pathfinding.Node;
import edu.wpi.TeamE.algorithms.pathfinding.Path;
import edu.wpi.TeamE.algorithms.pathfinding.Searcher;
import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.TeamE.databases.makeConnection;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

public class AStarTesting {
    static Searcher search = new AStarSearch();
    static ArrayList<Path> expectedList = new ArrayList<>();
    static ArrayList<Pair<String, String>> testList = new ArrayList<>();
    Path expected;
    Pair<String, String> test;
    static int index;

    @BeforeAll
    public static void setupExpected(){
        System.out.println("STARTING UP!!!");
        makeConnection connection = makeConnection.makeConnection();
        System.out.println("Connected to the DB");
        File nodes = new File("src/main/resources/edu/wpi/TeamE/csv/bwEnodes.csv");
        File edges = new File("src/main/resources/edu/wpi/TeamE/csv/bwEedges.csv");
        try {
            connection.deleteAllTables();
            connection.createTables();
            connection.populateTable("node", nodes);
            connection.populateTable("hasEdge", edges);
            System.out.println("Tables were reset");
        } catch (Exception e) {
            connection.createTables();
            connection.populateTable("node", nodes);
            connection.populateTable("hasEdge", edges);
            System.out.println("Tables were created and populated");
        }

        Path exp1 = new Path();
        Node a1 = search.getNode("FEXIT00201");
        Node a2 = (search.getNode("FEXIT00201"));
        Node a3 = (search.getNode("eWALK01001"));
        Node a4 = (search.getNode("eWALK00701"));
        Node a5 = (search.getNode("eWALK00601"));
        Node a6 = (search.getNode("eWALK00401"));
        Node a7 = (search.getNode("eWALK00301"));
        Node a8 = (search.getNode("eWALK00201"));
        Node a9 = (search.getNode("eWALK00101"));
        Node a10 = (search.getNode("ePARK00101"));

        a9.setNext(a10);
        a8.setNext(a9);
        a7.setNext(a8);
        a6.setNext(a7);
        a5.setNext(a6);
        a4.setNext(a5);
        a3.setNext(a4);
        a2.setNext(a3);
        a1.setNext(a2);

        exp1.add(a1);

        expectedList.add(exp1);

        testList.add(new Pair("FEXIT00201", "ePARK00101"));

        Path exp2 = new Path();

        Node b1 = (search.getNode("ePARK01701"));
        Node b2 = (search.getNode("eWALK01601"));
        Node b3 = (search.getNode("eWALK01301"));
        Node b4 = (search.getNode("eWALK01201"));
        Node b5 = (search.getNode("eWALK00801"));
        Node b6 = (search.getNode("eWALK00901"));
        Node b7 = (search.getNode("eWALK01101"));
        Node b8 = (search.getNode("FEXIT00301"));

        b7.setNext(b8);
        b6.setNext(b7);
        b5.setNext(b6);
        b4.setNext(b5);
        b3.setNext(b4);
        b2.setNext(b3);
        b1.setNext(b2);

        exp2.add(b1);

        expectedList.add(exp2);

        testList.add(new Pair("ePARK01701", "FEXIT00301"));

        Path exp3 = new Path();

        Node c1 = (search.getNode("FEXIT00301"));
        Node c2 = (search.getNode("eWALK01101"));
        Node c3 = (search.getNode("eWALK01001"));
        Node c4 = (search.getNode("FEXIT00201"));

        c3.setNext(c4);
        c2.setNext(c3);
        c1.setNext(c2);

        exp3.add(c1);

        expectedList.add(exp3);

        testList.add(new Pair("FEXIT00301", "FEXIT00201"));

        index = 0;


    }

    @BeforeEach
    public void setupNextTest(){
        expected = expectedList.get(index);
        test = testList.get(index);
    }

    @Test
    public void getNodeTest(){
        Node Test1 = new Node("FEXIT00301", 2099, 1357, "1", "Tower", "EXIT", "Emergency Department Entrance", "Emergency Entrance");
        assertTrue(Test1.equals(search.getNode("FEXIT00301")));

        Node Test2 = new Node("AHALL00603",1580,2858,"3", "BTM", "HALL", "HallNode" , "HallNode");
        assertTrue(Test2.equals(search.getNode("AHALL00603")));

        Node Test3 = new Node("GHALL02901",1262,1850,"1","Shapiro","HALL","Hallway MapNode 29 Floor 1","Hall");
        assertTrue(Test3.equals(search.getNode("GHALL02901")));

        Node Test4 = new Node("GHALL008L2",1577,2195,"L2","Shapiro","HALL","Hallway MapNode 8 Floor L2","Hall");
        assertTrue(Test4.equals(search.getNode("GHALL008L2")));

    }

    @Test
    public void nodeInfoGettersTest(){
        Node exit = search.getNode("FEXIT00301");
        Node hall = search.getNode("GHALL008L2");
        Node elev = search.getNode("GELEV00N02");
        Node rest = search.getNode("CREST001L2");
        Node dept = search.getNode("GDEPT02402");
        Node stai = search.getNode("FSTAI00301");
        Node serv = search.getNode("CSERV001L1");
        Node labs = search.getNode("GLABS003L2");

        assertEquals("FEXIT00301", exit.get("id"));
        assertEquals(1577, hall.getX());
        assertEquals(1931, elev.getY());
        assertEquals("L2", rest.get("floor"));
        assertEquals("Shapiro", dept.get("building"));
        assertEquals("STAI", stai.get("type"));
        assertEquals("Volunteers Floor L1", serv.get("longName"));
        assertEquals("Cardiovascular Imaging Center", labs.get("shortName"));

    }

    @Test
    public void testLobbyToParking(){
        Path out = search.search(test.getKey(), test.getValue());
        assertTrue(expected.equals(out));
        index++;
    }

    @Test
    public void testParkingToER(){
        Path out = search.search(test.getKey(), test.getValue());
        assertTrue(expected.equals(out));
        index++;
    }

    @Test
    public void testERToLobby(){
        Path out = search.search(test.getKey(), test.getValue());
        assertTrue(expected.equals(out));
    }

}