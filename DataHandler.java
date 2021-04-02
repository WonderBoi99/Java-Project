/**
 *@author Gibran Akmal <a 

   href="mailto:gibran.akmal@ucalgary.ca">gibran.akmal@ucalgary.ca</a>
 *@version 1.2
 *@since 1.0
*/
//Hackathon/ENSF 409 Final project

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

//REMEMBER TO CHANGE LOGIN CREDENTIALS FOR DATABASE
public class DataHandler 
{
	public final String DBURL = "jdbc:mysql://localhost/inventory"; //store the database url information
    public final String USERNAME = "naikar";	//"scm"; //store the user's account username
    public final String PASSWORD =  "ensf409";	//"ensf409"; //store the user's account password
    
    private Connection connect;  //data member for connection
    private ResultSet results;	// data member for results
    
    
    private String inputCategory;
    private String inputType;
    private int inputAmount;

	private String a1[] = new String[2];
	private String a2[] = new String[2];
	private String a3[] = new String[2];
	private String a4[] = new String[2];
	private int checkList = 0;
	private int totalCost = 0;
	private int id = 0;

    
    public static void main(String[] args) {
		DataHandler test = new DataHandler("lamp", "Swing Arm", 1);
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
    
	//String a1[] = new String[2];
	//String a2[] = new String[2];
	//String a3[] = new String[2];
	//String a4[] = new String[2];
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
    	//string buffer can hold alot together
    	//StringBuffer firstNlast = new StringBuffer();
    	
    	try 
    	{
			Statement myStatement = connect.createStatement();
    		results = myStatement.executeQuery("SELECT count(ID) FROM " + inputCategory);
			results.next();
			int numberOfRows = results.getInt(1);
		
    		myStatement = connect.createStatement();
    		results = myStatement.executeQuery("SELECT * FROM " + inputCategory + " ORDER BY Price ASC");
			int count = 0;
			int cost = 0;
			//int start = 0;
			ArrayList <String> oldID = new ArrayList <String>();
			ArrayList <String> newID = new ArrayList <String>();
			
			int oldCost = 1000;
			int counting = 0;
			/*
			
				int counting = 0;
			for(int start = 3; start < numberOfRows;start++){
				while(counting < start){
					results.next();
					counting++;	
				}
				
				while(results.next());
				counting = 0;
			}
			*/
			

			for(int start = 0; start < numberOfRows;start++){
				while(counting < start){
					results.next();
					counting++;	
				}
    			 while (results.next())
    		 	{
    			 	//printing the output to the console makes it more visual and helpful during debugging
    			 	// will occur throughout this program
            
				 	String temp = results.getString("Type");
				 	if(temp.equals(inputType)){
						if(checkList == 4)
						{	
							if(results.getString(a1[0]).equals("Y") && a1[1] == null){
							
								a1[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}
	
							}
						
							if(results.getString(a2[0]).equals("Y") && a2[1] == null){
								a2[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}
	
							}
							if(results.getString(a3[0]).equals("Y") && a3[1] == null){
								a3[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}

							}
							if(results.getString(a4[0]).equals("Y") && a4[1] == null){
								a4[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}

							}
						}
						
						else if(checkList == 3){
							if(results.getString(a1[0]).equals("Y") && a1[1] == null){
								a1[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}

							}
							if(results.getString(a2[0]).equals("Y") && a2[1] == null){
								a2[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}

							}
							if(results.getString(a3[0]).equals("Y") && a3[1] == null){
								a3[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}

							}
						}
						else if(checkList == 2){
							if(results.getString(a1[0]).equals("Y") && a1[1] == null){
								a1[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}

							}
							if(results.getString(a2[0]).equals("Y") && a2[1] == null){
								a2[1] = "Y";
								if(count == 0){
									cost = totalCost + results.getInt("Price");
									newID.add(results.getString("ID"));
									count++;
								}
							}
						}	
					}
					count = 0;
					totalCost = cost;
					oldCost = totalCost;     
				}
				System.out.println("<<<<"+oldCost+">>>>>>");
				System.out.println(newID);
				if(totalCost < oldCost){
					oldCost = totalCost;
					oldID = new ArrayList <String>(newID);
					newID.clear();
				}
				counting = 0;
			}
    		myStatement.close(); //close statement very important
			System.out.println("=================== "+oldCost);
    	}
    	catch(SQLException e) 
    	{
    		e.printStackTrace();    		
    	}
    	
    	return false;
    }    
    

}
