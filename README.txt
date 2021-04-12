To run the program, compile the DataHandler.java file with included mysql-connector-java-8.0.23.jar
Then run DataHandler from the commandline with included mysql-connector-java-8.0.23.jar
The program will first prompt for the category of furniture the user is looking for (chair, desk, filing, or lamp). This is case-sensitive.
If the category is invalid, the program will state the category does not exist and prompt for new input.
After a valid category is entered, the program will prompt for the specific type the user is looking for within the category.
If the type is invalid, the program will state the type does not exist and prompt for new input.
After a valid type is entered, the program will prompt for the amount of items the user would like of that type and category.
If the order cannot be filled from the database, the program will output that the order cannot be fulfilled and print out suggested manufacturers to contact.
If the order can be filled, the program will output the combinations of ID's needed to fill the order as well as their individual and total costs.
The program finally will ask if the user is satisfied with the combinations and total cost.
If the user is not satisfied, the program will print out suggested manufacturers to contact.
If the user is satisfied, the program will create "orderform.txt" to detail the order.