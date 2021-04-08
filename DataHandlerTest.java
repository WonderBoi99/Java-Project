import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.*;
import static org.junit.Assert.*;
/**
 * 
 */

/**
*@author Gibran Akmal, 30094918
 *@author John Kvellestad, 10125207
 *@author Aashik Ilangovan, 30085993
 *@author Nikhil Naikar, 30039350
 *@version 1.3
 *@since 1.0
 *Unit Tests File
 */
public class DataHandlerTest {
	
	// TESTS NEED TO MEET DESIGN SPECIFICATIONS WHICH ARE
	/** 1
     * Take in user Input for 1) a furniture category, 2) the type and 3) number of items requested
	 */
	@Test
	public void test_InputSpecifications() {
		String furnCat = "chair"; //Furniture Category
		String type = "Task"; // Type
		int items = 1; //Number
		DataHandler d = new DataHandler(furnCat, type, items);
		assertEquals(furnCat, d.getCategory()); //should Return the input for category
		assertEquals(type, d.getType()); //should return the input for type
		assertEquals(items, d.getAmount()); //should return the amount for category
		fail("Your Inputs are not initialized"); // If Failed
		// Will initialize
	}
	
	// /** 2
    //  * Calculate cheapest option for requested pieces
	//  */
	// @Test
	// public void test_calcCheap() {
	// 	String furnCat = "chair";
	// 	String type = "Task";
	// 	int items = 1;
	// 	DataHandler d = new DataHandler(furnCat, type, items);
	// 	boolean combFound;
	// 	combFound = d.findCombo(); //Finding If A Combination Exists (Method)
		
	// 	ArrayList<ArrayList<String>> allIds = new ArrayList<ArrayList<String>>();
	// 	ArrayList<Integer> allCosts = new ArrayList<Integer>(); 
		
	// 	allIds = d.getFinalIds(); 
    //     allCosts = d.getFinalCost();
		
    //     // HOW TO TEST FOR CORRECT CHEAPEST OPTION****
	// 	assertEquals(true,combFound); //Should return TRUE if a combination is found
	// 	fail("The cheapest options were not calculated"); // If Failed 
	// }
	
	// /** 3
    //  * A fullfiled request
	//  */
	// @Test
	// public void test_fulFill() {
	// 	String furnCat = "chair";
	// 	String type = "Task";
	// 	int items = 1;
	// 	DataHandler d = new DataHandler(furnCat, type, items);
	// 	boolean combFound;
	// 	combFound = d.findCombo(); //Finding If A Combination Exists (Method)
		
	// 	ArrayList<ArrayList<String>> allIds = new ArrayList<ArrayList<String>>();
	// 	ArrayList<Integer> allCosts = new ArrayList<Integer>(); 
		
	// 	allIds = d.getFinalIds(); 
    //     allCosts = d.getFinalCost();
		
    //     IO i = new IO();
    //     // make answer true?
    //     boolean answer = i.checkWithUser(); // CHECKING IF USER IS OKAY WITH THE COMBINATIONS
        
    //     assertEquals(true,answer); // ANSWER WILL RETURN TRUE IF CUSTOMER IS HAPPY
       
    //     // ASSERTEQUALS FILE ORDER
	// 	fail("Requests has not been fullfilled"); // If Failed
	// }
	
	// /** 4
    //  * updated base when an order form is produced, to account for items no longer available
	//  */
	// @Test
	// public void test_updateInventory() {
	// 	// UPDATE - N AND J WILL FINISH THAT METHOD I BELIEVE
	// 	String furnCat = "chair";
	// 	String type = "Task";
	// 	int items = 1;
	// 	DataHandler d = new DataHandler(furnCat, type, items);
		
	// 	fail("Inventory was not updated"); // If Failed
	// }
	
	// /** 5
    //  * test for when requests cannot be fullfilled
	//  */
	// @Test
	// public void test_Input() {
	// 	String furnCat = "chair";
	// 	String type = "Task";
	// 	int items = 99;
	// 	DataHandler d = new DataHandler(furnCat, type, items);
	// 	boolean combFound;
	// 	combFound = d.findCombo(); //Finding If A Combination Exists (Method)
		
	// 	ArrayList<ArrayList<String>> allIds = new ArrayList<ArrayList<String>>();
	// 	ArrayList<Integer> allCosts = new ArrayList<Integer>(); 
		
	// 	allIds = d.getFinalIds(); 
    //     allCosts = d.getFinalCost(); // No Final Cost
		
    //     IO i = new IO();
    //     boolean answer = i.checkWithUser(); // CHECKING IF USER IS OKAY WITH THE COMBINATIONS
        
    //     d.printRecommedations(); //RECCOMENDATIONS WILL BE PRINTED
    //     assertEquals(false,combFound); //WE WILL NOT FIND COMBINATIONS
		
	// 	fail("Recommendations were not given"); // If Failed
	// }
	
	//Tests for main methods
	
}
