/**
 *@author Gibran Akmal, 30094918
 *@author John Kvellestad, 10125207
 *@author Aashik Ilangovan, 30085993
 *@author Nikhil Naikar, 30039350

 *@version 12
 *@since 11
*/
// package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.StringBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Integer;

/**
DataHandler is the main functionality of the program apart from user input
It handles interaction with the database and calculation of recommendations
*/
public class DataHandler {
	
	public final String DBURL = "jdbc:mysql://localhost/inventory"; //store the database url information
    public final String USERNAME = "scm";	//store the user's account username
    public final String PASSWORD =  "ensf409";	//store the user's account password
	
    private Connection connect; //connection
    private ResultSet results;	//results of query

    private String inputCategory; //category from user
    private String inputType; //type from user
    private int inputAmount; //amount from user

	private int checkList = 0; //how many columns the current list has
	private int totalCost = 0; //total cost of current combination
	private int count = 0; //count of options for a specified column
	private String a1[] = new String[2]; //data column 1
	private String a2[] = new String[2]; //data column 2
	private String a3[] = new String[2]; //data column 3
	private String a4[] = new String[2]; //data column 4
	private	ArrayList <String> newID = new ArrayList <String>(); //stores IDs of new valid combination
	private ArrayList<ArrayList<String>> finalIds = new ArrayList<ArrayList<String>>();
	//stores the specified number of combinations of IDs
	private ArrayList<Integer> finalCost = new ArrayList<Integer>();
	//stores the total cost of each combination in finalIds
	private ArrayList<ArrayList<String>> allPossibleCombinations = new ArrayList<ArrayList<String>>();
	//stores all possible valid combinations
	private ArrayList<Integer> allCosts = new ArrayList<Integer>();
	//stores all possible valid costs
	private int numberOfRows;
	//private int whereAreWe;

	/**
	 * When code is run
	 * Program will ask for user input of category, type and amount separately
	 * Looks for the cheapest combination(s) if avaiable
	 * If no combination found, recommendations of manufacturers will be displayed
	 * If yes combination found, will show and ask user if they are happy with the combination
	 * If user says yes, then a orderform.txt file will be made and the UsedIds will be deleted from the database
	 * If user says no, then recommendations of manufacturers will be displayed
	 */
    public static void main(String[] args){
		IO test = new IO();
		test.start();
	}

    /**
	 * Constructor for DataHandler Class
	 * initializes data members
	 */	
    public DataHandler(String category, String type, int amount){
    	//these data members initalized based on where the user's database is located
    	initializeConnection();
    	inputCategory = category;
    	inputType = type;
    	inputAmount = amount;
    }
    
    public String getInputCategory() {
    	// returns input Category
    	return inputCategory;
    }
    
    public String getInputType() {
    	// returns input Type
    	return inputType;
    }
    
    public int getInputAmount() {
    	// returns input Type
    	return inputAmount;
    }
	public ArrayList<ArrayList<String>> getFinalIds(){
		//returns finalIds
		return finalIds;
	}

	public ArrayList<Integer> getFinalCost(){
		//returns finalCost
		return finalCost;
	}
    
	//returns the sum of all the costs 
	public int sumOfCosts(){
		int sum = 0;
		for(int i = 0; i < finalCost.size(); i++){
			sum = sum + finalCost.get(i);
		}
		return sum;
	}

    /**
	 * initializes connection with the database
	 */	
    public void initializeConnection(){
    	try{
    		connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * closes results and connect 
	 */
    public void close(){
        try{
            results.close();
            connect.close();
        } 
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
	 * determines what attributes the item has based on its category
	 */
	private void makeChecklist(){
		if(inputCategory.equals("chair")){
			a1[0] = "Legs";
			a2[0] = "Arms";
			a3[0] = "Seat";
			a4[0] = "Cushion";
			checkList = 4;
		}
		else if(inputCategory.equals("desk")){
			a1[0] = "Legs";
			a2[0] = "Top";
			a3[0] = "Drawer";
			checkList = 3;
		}
		else if(inputCategory.equals("filing")){
			a1[0] = "Rails";
			a2[0] = "Drawers";
			a3[0] = "Cabinet";
			checkList = 3;
		}
		else if(inputCategory.equals("lamp")){
			a1[0] = "Base";
			a2[0] = "Bulb";
			checkList = 2;
		}
	}

	/**
	 * finds the valid combinations for the user's order
	 */
    public boolean findCombo(){
		makeChecklist();
    	try{
			Statement myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//statement for getting the number of rows in table
    		results = myStatement.executeQuery("SELECT count(ID) FROM " + inputCategory);
			results.next();
			numberOfRows = results.getInt(1);
			//statement for getting and putting the data from user picked table in ascending order depending on Price
    		results = myStatement.executeQuery("SELECT * FROM " + inputCategory + " ORDER BY Price ASC");
			//finds and stores all combinatiions
			lookForAllCombination();
    		myStatement.close(); //close statement very important
			//System.out.println(allPossibleCombinations);
			while (allPossibleCombinations.size() > 0){ //While possible combination remain 
				int minIndex = allCosts.indexOf(Collections.min(allCosts)); //get index of lowest cost option
				boolean duplicate = false; //intially not a duplicate
				for (String id : allPossibleCombinations.get(minIndex)){ //for each id in the cheapest combination
					for (ArrayList<String> comboFinal : finalIds){ //for each combination already in finalIds
						if (comboFinal.contains(id)) { //set duplicate to true if already exist
							duplicate = true;
						}
					}
				}
				if (!duplicate){ // if not duplicate add to finalIds
					finalIds.add(new ArrayList <String>(allPossibleCombinations.get(minIndex)));
					finalCost.add(Collections.min(allCosts));
				}
				allPossibleCombinations.remove(allPossibleCombinations.get(minIndex)); //removes from allPossibleCombinations
				allCosts.remove(minIndex);
				if (finalIds.size() == inputAmount) { //return true if order is filled
					return true;
				}
			}				
    	}
    	catch(SQLException e){
    		e.printStackTrace();    		
    	}	
    	return false;
    } 

	/**
	 * determines if the most recent valid combination should be stored
	 */
	private void mainStrategy(){
		if (allPossibleCombinations.size() == 0){ //if first combination always store
			allPossibleCombinations.add(new ArrayList <String>(newID));
			allCosts.add(totalCost);
		}
		else {
			if (!allPossibleCombinations.contains(newID)) { //otherwise store only if not duplicate
					allPossibleCombinations.add(new ArrayList <String>(newID));
					allCosts.add(totalCost);
			}
		}
	}

	/**
	 * finds and stores all possible combinations in allPossibleCombination variable
	 */
	private void lookForAllCombination(){
		try{
			while (results.next()){
				String temp = results.getString("Type");
				if(temp.equals(inputType)){ //if this row has type that we want
					if(checkList == 4){	
						checklistForChair(); //look for every possible combination
					}
					else if(checkList == 3){
						checklistForDeskOrFiling(); //look for every possible combination
					}
					else if(checkList == 2){ //look for every possible combination
						checklistForLamp();
					}	
				}
				count = 0;     
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}	
	}

	/**
	 * check if row has 'Y' for the 4 columns of chair
	 */
	private void checkFourColumns(){
		checklist(a1);
		checklist(a2);
		checklist(a3);
		checklist(a4);
	}

	/**
	 * check if row has 'Y' for the 3 columns of desk/filing
	 */
	private void checkThreeColumns(){
		checklist(a1);
		checklist(a2);
		checklist(a3);
	}

	/**
	 * check if row has 'Y' for the 2 columns of lamp
	 */
	private void checkTwoColumns(){
		checklist(a1);
		checklist(a2);
	}

	/**
	 * empties all checklist values
	 */
	private void resetAttributes(){
		newID.clear();
		totalCost = 0;
		a1[1] = null;
		a2[1] = null;
		a3[1] = null;
		a4[1] = null;
	}

	/**
	 * a recursive method that implements a strategy to find all possible chair combinations
	 */
	private void checklistForChair(){
		try{
			count = 0;
			if(a1[1] == null && a2[1] == null && a3[1] == null && a4[1] == null){ //making sure it is the starting point
				int startingPoint = results.getRow(); //getting the starting point, useful later
				checkFourColumns(); //storing the starting information
				results.beforeFirst();
				for(int index = 0; index < numberOfRows; index++){ //interating through rows to find all different combinations with start ID
					int counting = 0;
					while(counting < index){ //for skipping rows
						results.next();
						counting++;
					}
					while (results.next()){ //a recursive strategy to find all the combinations 
						String temp = results.getString("Type");
						if(temp.equals(inputType)){
							checklistForChair();	
						}
						count = 0;  
						if(a1[1] != null && a2[1] != null && a3[1] != null && a4[1] != null){ //when combination is found
							mainStrategy(); //making sure to not store duplicates
							break;
						}   
					}
					resetAttributes();
					results.absolute(startingPoint); //prep for getting starting information
					checkFourColumns();
					results.beforeFirst(); //reset interator
				}
				resetAttributes();
				results.absolute(startingPoint); //prep for keeping the strategy on track
			}
			else{
				checkFourColumns();
				count = 0;
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
	 * a recursive method that implements a strategy to find all possible desk/filing combinations
	 */
	private void checklistForDeskOrFiling(){
		try{
			count = 0;
			if(a1[1] == null && a2[1] == null && a3[1] == null){ //making sure it is the starting point
				int startingPoint = results.getRow(); //getting the starting point, useful later
				checkThreeColumns(); //storing the starting information
				results.beforeFirst();
				for(int index = 0; index < numberOfRows; index++){ //interating through rows to find all different combinations with start ID
					int counting = 0;
					while(counting < index){ //for skipping rows
						results.next();
						counting++;
					}
					while (results.next()){ //a recursive strategy to find all the combinations 
						String temp = results.getString("Type");
						if(temp.equals(inputType)){
							checklistForDeskOrFiling();	
						}
						count = 0;  
						if(a1[1] != null && a2[1] != null && a3[1] != null){ //when combination is found
							mainStrategy(); //making sure to not store duplicates
							break;
						}   
					}
					resetAttributes();
					results.absolute(startingPoint); //prep for getting starting information
					checkThreeColumns();
					results.beforeFirst(); //reset interator
				}
				resetAttributes();
				results.absolute(startingPoint); //prep for keeping the strategy on track
			}
			else{
				checkThreeColumns();
				count = 0;
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
	 * a recursive method that implements a strategy to find all possible lamp combinations
	 */
	private void checklistForLamp(){
		try{
			count = 0;
			if(a1[1] == null && a2[1] == null){ //making sure it is the starting point
				int startingPoint = results.getRow(); //getting the starting point, useful later
				checkTwoColumns(); //storing the starting information
				results.beforeFirst();
				for(int index = 0; index < numberOfRows; index++){ //interating through rows to find all different combinations with start ID
					int counting = 0;
					while(counting < index){ //for skipping rows
						results.next();
						counting++;
					}
					while (results.next()){ //a recursive strategy to find all the combinations
						String temp = results.getString("Type");
						if(temp.equals(inputType)){
							checklistForLamp();								
						}
						count = 0;  
						if(a1[1] != null && a2[1] != null){ //when combination is found
							mainStrategy(); //making sure to not store duplicates
							break;
						}   
					}
					resetAttributes();
					results.absolute(startingPoint); //prep for getting starting information
					checkTwoColumns();
					results.beforeFirst();	//reset interator
				}
				resetAttributes();
				results.absolute(startingPoint); //prep for keeping the strategy on track
			}
			else{
				checkTwoColumns();
				count = 0;
			}
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
     * UsedIds will be deleted from the database 
     */
	public void deleteUsedIds(){
		try{
			String qe = "DELETE FROM " + inputCategory + " WHERE ID = ?";
			PreparedStatement myFinalStatment = connect.prepareStatement(qe);
			for(int o = 0; o < finalIds.size(); o++){
				ArrayList<String> tmp = finalIds.get(o);
				for(int i = 0; i < tmp.size(); i++){
					myFinalStatment.setString(1, tmp.get(i));
					myFinalStatment.executeUpdate();	
				}
			}
			myFinalStatment.close();
		}
		catch(SQLException e){
    		e.printStackTrace();    		
    	}
	}

	/**
	 * performs a validity check on a column in one row of results
	 */	
	private void checklist(String[] x){
		try{
			if(results.getString(x[0]).equals("Y") && x[1] == null){ 
				x[1] = "Y";
				//only want to add cost to totalCost once for each row
				if(count == 0){
					totalCost = totalCost + results.getInt("Price");
					//recording the IDs
					// System.out.println("Adding to newID: " + results.getString("ID"));
					newID.add(results.getString("ID"));
					// System.out.println("newID: " + newID);
					count++;
				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();    		
		}
	}

    /**
	 * prints recommendations for manufacturers if not able to fill order
	 */
	public void printRecommedations(){
		try{
			StringBuilder suggestions = new StringBuilder();
			ArrayList<String> manuIDs = new ArrayList<String>();
			Statement myStatement;
			PreparedStatement mySecondStatment;
			String qe;
			//making prepared statement for getting all ManuID related to type
			qe = "SELECT DISTINCT ManuID FROM "+ inputCategory;
			mySecondStatment = connect.prepareStatement(qe);
			results = mySecondStatment.executeQuery();
			//storing the ManuID in manuIDs for later use
			while(results.next()){
				manuIDs.add(results.getString("ManuID"));
			}
			//making statement for getting data from manufacturer table
			myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		results = myStatement.executeQuery("SELECT * FROM manufacturer");
			//storing the names for the manuIDs collected earlier
			while(results.next()){
				if(manuIDs.contains(results.getString("ManuID"))){
					suggestions = suggestions.append(results.getString("Name") + ", ");
				}
			}
			//formatting stuff
			if(suggestions.length() == 0){
				System.out.println("No manufacturers provide this item");
			}
			else{
				suggestions.setLength(suggestions.length()-2);
				System.out.println("Order cannot be fulfilled. Suggested manufacturer(s) are: ");
				System.out.println(suggestions.toString());
			}
			myStatement.close();
			mySecondStatment.close();
		}
		catch(SQLException e){
			e.printStackTrace();    		
		}
	}
	
	/**
	 * Makes a orderform.txt file with some information  
	 * Following the specified format of the given example
	 */	
	public void makeFile(){
		try{   
            FileWriter output;
			//the output FileWriter will output to a text file called "output.txt"			
			output = new FileWriter("orderform.txt", false); //the second parameter of false tells it to overwrite an existing file of that name if it exists
			output.write("Furniture Order Form\n\n");
			output.write("Faculty Name: \n");
			output.write("Contact: \n");
			output.write("Date: \n\n");
			output.write("Original Request: "+inputType+" "+inputCategory+", "+inputAmount+"\n\n");
			output.write("Items Ordered:\n");
            for(int o = 0; o < finalIds.size(); o++){
                ArrayList<String> tmp = finalIds.get(o);
                for(int i = 0; i < tmp.size(); i++){
                    output.write(tmp.get(i)+"\n");
                }
            }
			output.write("\nTotal Price: $" + sumOfCosts());
            output.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}	
}
