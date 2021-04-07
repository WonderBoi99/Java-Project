/**
 *@author Gibran Akmal, 30094918
 *@author John Kvellestad, 10125207
 *@author Aashik Ilangovan, 30085993
 *@author Nikhil Naikar, 30039350

 *@version 9.0
 *@since 1.0
*/

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.lang.StringBuilder;
import java.lang.Integer;

//REMEMBER TO CHANGE LOGIN CREDENTIALS FOR DATABASE
public class DataHandler 
{
	public final String DBURL = "jdbc:mysql://localhost/inventory"; 
    public final String USERNAME = "naikar";	
    public final String PASSWORD =  "ensf409";	
    
    private Connection connect; 
    private ResultSet results;	
    
    private String inputCategory;
    private String inputType;
    private int inputAmount;
	private int checkList = 0;
	private int totalCost = 0;
	private int count = 0;
	private String a1[] = new String[2];
	private String a2[] = new String[2];
	private String a3[] = new String[2];
	private String a4[] = new String[2];
	private	ArrayList <String> newID = new ArrayList <String>();
	
    public static void main(String[] args) {
		DataHandler test = new DataHandler("chair", "Task", 2);
		test.findCombo();
	}


    /**
	 * Constructor for DataHandler Class
	 * initializes data members 
	 * @param DBURL is the specific database URL that the user inputs that is for the target database
	 * @param USERNAME is the username to access the database with
	 * @param PASSWORD is the password corresponding to that username
	 */	
    public DataHandler(String category, String type, int amount) 
    {
    	//these data members initalized based on where the user's database is located
    	initializeConnection();
    	inputCategory = category;
    	inputType = type;
    	inputAmount = amount;
    }
    
    
    /**
	 * Initializes connection with the database
	 * no return type or params
	 */	
    public void initializeConnection() 
    {
    	// enclose within try catch so any SQL exceptions are caught
    	try 
    	{
    		//connection initialized using the credentials provided on object creation
    		connect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    	}
    	catch(SQLException e) 
    	{
    		e.printStackTrace();
    	}
    }
    
    
    /**
     * private custom method that closes everything and releases all resources
	 * no return type and has no args
	 */
    public void close() 
    {
        try {
            results.close();
            connect.close();
        } 
        
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
    
	
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

	
    public boolean findCombo() 
    {
		makeChecklist();
    	try 
    	{
			ArrayList<ArrayList<String>> finalIds = new ArrayList<ArrayList<String>>();
			ArrayList<Integer> finalCost = new ArrayList<Integer>();
			int oldCost = 1000;
			int newCost = 0;
			int counting = 0;
			boolean combMade = false;
			Statement myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
    		results = myStatement.executeQuery("SELECT count(ID) FROM " + inputCategory);
			results.next();
			int numberOfRows = results.getInt(1);
    		myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
    		results = myStatement.executeQuery("SELECT * FROM " + inputCategory + " ORDER BY Price ASC");
			for(int start = 0; start < numberOfRows;start++){
				while(counting < start){
					results.next();
					counting++;
				}
    			while (results.next())
    		 	{
				 	String temp = results.getString("Type");
					System.out.println(results.getString("ID"));
				 	if(temp.equals(inputType)){
						if(checkList == 4)
						{	
							this.checklist(a1);
							this.checklist(a2);
							this.checklist(a3);
							this.checklist(a4);
							if(a1[1] != null && a2[1] != null && a3[1] != null && a4[1] != null){
								combMade = true;
							}
						}
						
						else if(checkList == 3){
							this.checklist(a1);
							this.checklist(a2);
							this.checklist(a3);
							if(a1[1] != null && a2[1] != null && a3[1] != null){
								combMade = true;
							}
						}
						else if(checkList == 2){
							this.checklist(a1);
							this.checklist(a2);
							if(a1[1] != null && a2[1] != null){
								combMade = true;
							}
						}	
					}
					count = 0;
					newCost = totalCost;     
				}
				boolean used = false;
				for(int o = 0; o < finalIds.size(); o++){
					ArrayList<String> tmp = finalIds.get(o);
					for(int i = 0; i < tmp.size(); i++){
						if(newID.contains(tmp.get(i))){
							used = true;
						}
					}
				}
				if(combMade && !used){
					if(finalCost.size() < inputAmount){
						finalCost.add(newCost);
						finalIds.add(new ArrayList <String>(newID));
					}
					else if(newCost < Collections.max(finalCost)){
						int index = finalCost.indexOf(Collections.max(finalCost));
						finalCost.remove(index);
						finalIds.remove(index);
						finalCost.add(newCost);
						finalIds.add(new ArrayList <String>(newID));
					}
				}
				counting = 0;
				a1[1] = null;
				a2[1] = null;
				a3[1] = null;
				a4[1] = null;
				totalCost = 0;
				results.beforeFirst();
				combMade = false;
				newID.clear();
				System.out.println(finalCost);
				System.out.println(finalIds);
			}
    		myStatement.close(); //close statement very important
			if(finalIds.size() != inputAmount){
				printRecommedations();
			}
    	}
    	catch(SQLException e) 
    	{
    		e.printStackTrace();    		
    	}
    	
    	return false;
    }    
	
	private void checklist(String[] x){
		try{
			if(results.getString(x[0]).equals("Y") && x[1] == null){
				x[1] = "Y";
				if(count == 0){
					totalCost = totalCost + results.getInt("Price");
					newID.add(results.getString("ID"));
					count++;
				}
			}
		}
		catch(SQLException e) 
		{
			e.printStackTrace();    		
		}
	}
    
	private void printRecommedations(){
		try{
			ArrayList<String> manuIDs = new ArrayList<String>();
			Statement myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
			
			String qe = "SELECT DISTINCT ManuID FROM "+ inputCategory + " WHERE Type = ?";
			PreparedStatement mySecondStatment = connect.prepareStatement(qe);
			mySecondStatment.setString(1, inputType);
			results = mySecondStatment.executeQuery();

			while(results.next()){
				manuIDs.add(results.getString("ManuID"));
			}
			
			myStatement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			ResultSet.CONCUR_READ_ONLY);
    		results = myStatement.executeQuery("SELECT * FROM manufacturer");
			StringBuilder suggestions = new StringBuilder();
			while(results.next()){
				if(manuIDs.contains(results.getString("ManuID"))){
					suggestions = suggestions.append(results.getString("Name") + ", ");
				}
			}
			suggestions.setLength(suggestions.length()-2);
			System.out.println("Suggested manufacturer: ");
			System.out.println(suggestions.toString());
			
		}
		catch(SQLException e) 
		{
			e.printStackTrace();    		
		}
	}
}
