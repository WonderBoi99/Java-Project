/**
 * @author Gibran Akmal, 30094918
 * @author John Kvellestad, 10125207
 * @author Aashik Ilangovan, 30085993
 * @author Nikhil Naikar, 30039350
 * @version 1.4
 * @since 1.0 Unit Tests File
 */
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.File;
import org.junit.Test;
import java.sql.*;


public class DataHandlerTest {
	
	/** TEST 1
	 * Take in user Input for 1) a furniture category, 2) the type and 3) amount
	 * of items requested
	 * Tests Constructor with three arguments for each Input,
	 * Returns the Input
	 */
	@Test
	public void testConstructorInputs() {
		String furnCat = "chair"; // Furniture Category
		String type = "Task"; // Type
		int amount = 1; // Number
		// Constructor with three arguments, respective to category, type and amount
		DataHandler d = new DataHandler(furnCat, type, amount); // INPUTS
		// Should return set Inputs
		assertEquals("ERROR: Your Category Input is not initialized", furnCat, d.getInputCategory());																																
		assertEquals("ERROR: Your Type Input is not initialized", type, d.getInputType()); 																		
		assertEquals("ERROR: Your Amount Input is not initialized", amount, d.getInputAmount()); 																	
	}

	/** TEST 2
	 * Tests a Successfully Fulfilled Order, and produces a formatted order in a .txt format
	 * Tests For The Output File being made, and its existence
	 */
	@Test
	public void testTextFileMade() {
		String furnCat = "chair"; // Furniture Category
		String type = "Task"; // Type
		int amount = 1; // Number
		// Constructor with three arguments, respective to category, type and amount
		DataHandler d = new DataHandler(furnCat, type, amount); // INPUTS
		// Setting up connections
		Connection connect;
		Statement myStatement = null;
		// SELF NOTE: DEFAULTS
		final String DBURL = "jdbc:mysql://localhost/inventory"; // store the database url information
		final String USERNAME = "scm"; // store the user's account username
		final String PASSWORD = "ensf409"; // store the user's account password
		try {
			connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
			myStatement = connect.createStatement();
			// Adding sql commands 
			String sql = "INSERT INTO CHAIR (ID, Type, Legs, Arms, Seat, Cushion, Price, ManuID) VALUES ('A0000', 'Task', 'Y', 'Y', 'Y', 'Y', '1' , '002')";
			// Executing Command
			myStatement.executeUpdate(sql);	
			//Finding Combination Method to find combination
			d.findCombo();
			// Deleting the used ID's
			d.deleteUsedIds();
			// makeFile Arguments correspond to Category, Type, Amount, Items Ordered, and Total Price
			d.makeFile();
			File temps = new File("orderform.txt"); // name of .txt file
			boolean exists = temps.exists(); //Checking its existence, once created
			assertEquals("ERROR: orderform.txt File does not exist", true, exists);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** TEST 3
	 * Inserts and Updates Inventory With Desired Output
	 * Tests if ID's and sum of costs for cheapest combination are correct
	 */
	@Test
	public void testInsertUpdateInv() {
		String furnCat = "chair";
		String type = "Task";
		int items = 1;
		DataHandler d = new DataHandler(furnCat, type, items);
		// Setting up connections
		Connection connect;
		Statement myStatement = null;
		// SELF NOTE: DEFAULTS
		final String DBURL = "jdbc:mysql://localhost/inventory"; // store the database url information
		final String USERNAME = "scm"; // store the user's account username
		final String PASSWORD = "ensf409"; // store the user's account password
		ArrayList<ArrayList<String>> finalIDs = new ArrayList<ArrayList<String>>();
		try {
			connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
			myStatement = connect.createStatement();
			// Adding sql commands 
			String sql = "INSERT INTO CHAIR (ID, Type, Legs, Arms, Seat, Cushion, Price, ManuID) VALUES ('A0000', 'Task', 'Y', 'Y', 'Y', 'Y', '1' , '002')";
			// Executing Command
			myStatement.executeUpdate(sql);	
			//Finding Combination Method to find combination
			d.findCombo();
			// Finding Final Ids of request 
			finalIDs = d.getFinalIds();
			// Deleting the used ID's
			d.deleteUsedIds();
			assertEquals("ERROR: Total is incorrect", 1, d.sumOfCosts()); // Tests Sum of costs
			assertEquals("ERROR: Cheapest Combination not found", "A0000", finalIDs.get(0).get(0)); // Test if right ID picked
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** TEST 4
	 * Inserts and Updates Inventory With a Desired Output
	 * Test if the cheapest combination ID is deleted once used
	 */
	@Test
	public void testDeleteUpdateInv() {
		String furnCat = "chair";
		String type = "Task";
		int items = 1;
		DataHandler d = new DataHandler(furnCat, type, items);
		// Setting up connections
		Connection connect;
		Statement myStatement = null;
		// SELF NOTE: DEFAULTS
		final String DBURL = "jdbc:mysql://localhost/inventory"; // store the database url information
		final String USERNAME = "scm"; // store the user's account username
		final String PASSWORD = "ensf409"; // store the user's account password
		ResultSet rs = null;
		try {
			connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
			myStatement = connect.createStatement();
			// Adding sql commands to 
			String sql = "INSERT INTO CHAIR (ID, Type, Legs, Arms, Seat, Cushion, Price, ManuID) VALUES ('A0000', 'Task', 'Y', 'Y', 'Y', 'Y', '1' , '002')";
			// Executes Insertion/Update
			myStatement.executeUpdate(sql);
			d.findCombo(); //Find's combination
			d.deleteUsedIds(); // DELETES USED ID'S
			// Searching for Inserted ID (Now deleted)
			sql = "SELECT * from chair where ID = 'A0000'";
			rs = myStatement.executeQuery(sql);
			// Test To Find Deleted ID
			assertEquals("ERROR: Did not Delete Used ID's", false, rs.next());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** TEST 5
	 * Test if recommendations are outputted when a request is unfulfilled
	 */
	@Test
	public void testUnFullfilled() {
		String furnCat = "chair";
		String type = "Task";
		int items = 100; // WAY TOO MANY ITEMS ORDERED
		// Putting Inputs into Constructor To be initialized
		DataHandler d = new DataHandler(furnCat, type, items);
		// Tries to find a possible combination (which it cannot as there are not enough items in inventory)
		boolean itemoverload = d.findCombo(); // should result in false
		assertEquals("ERROR: Order must be unfillable", false, itemoverload);
	}

	/** Test 6
	 * Testing user's "no" input is being read correctly by the program
	 */
	@Test
	public void testUnhappyUser() {
		String furnCat = "chair";
		String type = "Task";
		int items = 1;
		DataHandler d = new DataHandler(furnCat, type, items);
		// New IO Object
		IO i = new IO();
		String input = "no"; // INPUT IS INSERTED "no" WHEN CUSTOMER IS ASKED IF THEY ARE HAPPY
		InputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		boolean check = i.checkWithUser(); // Should return false
		//Tests check is false after user input is unhappy with their offer and inputs "no"
		assertEquals("ERROR: Error in receiving a \"no\" from input", false, check);
	}

	/** TEST 7
	 * Testing user's "yes" input is being read correctly by the program
	 */
	@Test
	public void testHappyUser() {
		String furnCat = "chair";
		String type = "Task";
		int items = 1;
		DataHandler d = new DataHandler(furnCat, type, items);
		// New IO Object
		IO i = new IO();
		String input = "yes"; // INPUT IS INSERTED "yes" WHEN CUSTOMER IS ASKED IF THEY ARE HAPPY
		InputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
		boolean check = i.checkWithUser(); // Should return True
		// Tests if User is happy with the offer, will return true if they are
		assertEquals("ERROR: Error in receiving a \"yes\" from input", true, check);
	}
}
