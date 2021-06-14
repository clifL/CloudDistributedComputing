/*
	Code: SafeEntry Remote Object	SafeEntryServant.java
	Date: 29th May 2021

	Usage: This class is an servant object that implements remote method invocation that found in SafeEntry interface,
*/


import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// The implementation Class must implement the rmi interface and be set as a Remote object on a server
public class SafeEntryServant extends java.rmi.server.UnicastRemoteObject implements SafeEntry {
	
	// Hashtable is being used to save the client instance with a integer key
	// Using hashtable ensure the uniqueness of the key
	// Client Integer Reference, Client Instance
	private Hashtable<Integer, RMIClientIntf> clientInstances;

	// Hashtable is being used for mapping multiple NRIC to one single client instance. This is particular useful for group check-in.
	// NRIC, Client Integer Reference
	private Hashtable<String, Integer> nricToClientReference;
	
	// Global Connection and Statement Object for connecting to SQLite Database. 
	private static Connection c = null;
	private static Statement stmt= null;
	
	// Function to connect to SQLite database. Gets the connection and creates the statement object for SQL queries.
	public static void connectDataBase() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:User.db");
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			c.setAutoCommit(false);
		}
		catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}	
	// Implementations must have an explicit constructor
	// in order to declare the RemoteException exception
	public SafeEntryServant() throws java.rmi.RemoteException {
		super();
		connectDataBase();
		// Instantiation of hashtables
		clientInstances = new Hashtable<Integer, RMIClientIntf>();
		nricToClientReference = new Hashtable<String, Integer>();
	}


	// Register client to hashtable for future callback purposes
	public void register(RMIClientIntf client, String nric) throws java.rmi.RemoteException {

		// Introduce concurrency concept with threading
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// Check if nric exist in the session
				if (nricToClientReference.containsKey(nric)) {
					Integer clientInstanceId = nricToClientReference.get(nric);
					try {
						// Check the client active status
						if (clientInstances.get(clientInstanceId).callBack_CheckIsActive()) {
							// Since session already exist, use callback to terminate the current client session
							client.callBack_SetClientId(0, true);
							return;
						}
					}
					// The callback will throw error when unable to connect to the client, this suggest the server have lost connection with the client
					catch (java.rmi.RemoteException e) {
						System.out.println("Detected inactive client, unregistering");
						// Since is inactive, remove its references from the two hashtable
						try {
							unregister(clientInstanceId, nric);
						}
						catch (java.rmi.RemoteException re) {
							System.out.println("Unregistered.");
						}
					}
				}
				// Since NRIC does not exist in the session, register it into hashtables.
				// A random non-duplicate integer is assigned to the respective client
				Random rg = new Random();
				int clientId = rg.nextInt();
				
				// Loop till non-duplication for the key
				while (clientInstances.containsKey(clientId)) {
					clientId = new Random().nextInt();
				}
				System.out.println("Registering Client Id: " + clientId);
				
				// Parse the client id to the client via callback
				try {
					client.callBack_SetClientId(clientId, false);
					
					// Store the client id and the client object into hashtable
					clientInstances.put(clientId, client);

					// Store the nric and client id into hashtable
					nricToClientReference.put(nric, clientId);

					System.out.println("Client active is " + clientInstances.get(clientId).callBack_CheckIsActive());

					// Send client any outstanding notification
					pendingManager(nric, clientId);
					
				} 
				catch (java.rmi.RemoteException e) {
					System.out.println("java.rmi.RemoteException occured");
					// e.printStackTrace();
				}
			}
		});
		// Start the new thread
		thread.start();
	}

	// Check for any pending notification binds to the person nric number
	public void pendingManager(String nric, Integer clientId) {
		
		String sql1 = "SELECT MESSAGE FROM USERCHECKIN WHERE NRIC = "+"\""+nric+"\"" + " AND CLOSEENCOUNTER = TRUE AND INFORMED = FALSE ";
		try {
			ResultSet rs = stmt.executeQuery(sql1);
			while (rs.next()) {
				String message = rs.getString("MESSAGE");
				boolean received = clientInstances.get(clientId).callBack_Notification(message);
				int flag = (received)? 1 : 0;
				// update informed to true
				String sql2 =  "UPDATE USERCHECKIN SET INFORMED = "+"\""+flag+"\" WHERE NRIC= "+"\""+nric+"\"" + " AND CLOSEENCOUNTER = TRUE";
				stmt.executeUpdate(sql2);
			}	
			c.commit();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	// Unregister client from the hashtables
	public void unregister(Integer clientId, String nric) throws java.rmi.RemoteException {
		
		// Introduce concurrency concept with threading
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Unregistering Client Id: " + clientId);
				// Remove record from clientInstances
				if (clientInstances.containsKey(clientId)) {
					clientInstances.remove(clientId);

					// Remove record from nricToClientReference as well
					if (nricToClientReference.containsKey(nric)) {
						nricToClientReference.remove(nric);
					}
				}
			}
		});
		// Start the new thread
		thread.start();
	}

	
	// Check-In the client and store the data in a sqlite db file
	// This function will also initiate a callback to the client indicating the check-in status
	public void checkIn(String name, String nric, String location, Boolean onBehalf, String guarantor, Integer clientId) throws RemoteException {
		
		// Introduce concurrency concept with threading
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Check-In\nName: " + name + ", NRIC: " + nric + ", Location: " + location +
							 ", Group Check-In: " + onBehalf + ", Guarantor: " + guarantor + ", Client Id: " + clientId);

				try {
					// Return 1 if true, 0 if false
					int flag = (onBehalf)? 1 : 0;
					// Update user's NRIC that have check out time Null values and update it to the current time.
					// Checkout locations that user did not checkout when checking in to other locations.
					String sql1 = "UPDATE USERCHECKIN SET CHECKOUTIME = datetime('now' , 'localtime') WHERE NRIC= "+"\""+nric+"\"" + " AND CHECKOUTIME IS NULL";
					stmt.addBatch(sql1);
					// SQL Insertion if user is checking in alone
					if (!onBehalf) {
						String sql2= "INSERT INTO USERCHECKIN (NRIC,NAME,LOCATION,ONBEHALF,GUARANTOR,CHECKINTIME,CHECKOUTIME,CLIENTID) VALUES("+"\""+nric+"\"" +","+ "\""+name+"\"" +","+ "\""+location+"\"" +","+ "\""+flag+"\"" + "," + "NULL" + "," + "datetime('now' , 'localtime')" + "," + "NULL" + "," +  clientId + ")";
						stmt.addBatch(sql2);
					}

					// SQL Insertion if checking in with guarantor
					if (onBehalf) {
						String sql2= "INSERT INTO USERCHECKIN (NRIC,NAME,LOCATION,ONBEHALF,GUARANTOR,CHECKINTIME,CHECKOUTIME,CLIENTID) VALUES("+"\""+nric+"\"" +","+ "\""+name+"\"" +","+ "\""+location+"\"" +","+ "\""+flag+"\"" + "," + "\""+guarantor+"\"" + "," + "datetime('now' , 'localtime')" + "," + "NULL" + "," +  clientId + ")"; 	 
						stmt.addBatch(sql2);
					}
					// Executes the SQL operations 
					stmt.executeBatch();
					// Explicitly commit statements to apply changes
					c.commit();

					// Callback indicate status
					try {
						clientInstances.get(clientId).callBack_CheckInStatus(true);
						pendingManager(nric, clientId);
					}
					catch (java.rmi.RemoteException e) {
						System.out.println("java.rmi.RemoteException occured");
						// e.printStackTrace();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );

					// Callback indicate status
					try {
						clientInstances.get(clientId).callBack_CheckInStatus(false);
					}
					catch (java.rmi.RemoteException re) {
						System.out.println("java.rmi.RemoteException occured");
						// re.printStackTrace();
					}
				}
			}
		});
		// Start the new thread
		thread.start();
	}


	// Check-out the client and update the table in the sqlite db file accordingly
	// This function will also initiate a callback to the client indicating the check-out status
	public void checkOut(String nric, Boolean onBehalf, Integer clientId) throws RemoteException {
		// Introduce concurrency concept with threading
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Check-out - NRIC: " + nric  + ", Group Check-In: " + onBehalf + ", Client Id: " + clientId);
				try {
					//Update checkout time for without guarantor
					if (!onBehalf) {
						//select * from table where NRIC = nric and checkouttime is null;
						String sql1 = "UPDATE USERCHECKIN SET CHECKOUTIME = datetime('now' , 'localtime') WHERE NRIC= "+"\""+nric+"\"" + " AND CHECKOUTIME IS NULL";
						stmt.addBatch(sql1);
					}
					//Update checkout time for with guarantor
					if (onBehalf) {
						String sql2= "UPDATE USERCHECKIN SET CHECKOUTIME = datetime('now' , 'localtime') WHERE NRIC= "+"\""+nric+"\"" + " AND CHECKOUTIME IS NULL";
						stmt.addBatch(sql2);
					}
					// Executes the SQL operations
					stmt.executeBatch();
					//Explicitly commit statements to apply changes
					c.commit();
					
					try {
						clientInstances.get(clientId).callBack_CheckOutStatus(true);
					}
					catch (java.rmi.RemoteException e) {
						System.out.println("java.rmi.RemoteException occured");
						// e.printStackTrace();
					}
				}
				catch (Exception e) {
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					try {
						clientInstances.get(clientId).callBack_CheckOutStatus(true);
					}
					catch (java.rmi.RemoteException re) {
						System.out.println("java.rmi.RemoteException occured");
						// re.printStackTrace();
					}
				}
			}
		});
		// Start the new thread
		thread.start();
	}


	// This function will get the client safe entry history and wrap into an arraylist of history object.
	// It will then initiate a callback to the client parsing the arraylist.
	public void viewHistory(String nric, Integer clientId) throws RemoteException {
		
		// Introduce concurrency concept with threading
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// Database query by NRIC and wrap around to arraylist		
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				try {
					ArrayList<History> userHistory = new ArrayList<History>(); 
					String sql1="SELECT * FROM USERCHECKIN WHERE NRIC = "+"\""+nric+"\"" + " AND CHECKOUTIME IS NOT NULL";
					// Returns a table of data from the SELECT query
					// Store into a ResultSet rs
					ResultSet rs = stmt.executeQuery(sql1);
					
					// Wrap into arraylist of history object
					// Iterate through the rows of data, add the data for each row into a History object and append to userHistory arraylist
					while (rs.next()) {
						History history= new History();
						String location = rs.getString("LOCATION");
						String checkintime = rs.getString("CHECKINTIME");
						String checkoutime= rs.getString("CHECKOUTIME");
						
						// System.out.println("You have visited:" + location + " Check in time: "+ checkintime + " Check out time: "+ checkoutime );
						LocalDateTime parsedCheckintime = LocalDateTime.parse(checkintime, formatter);
				        LocalDateTime parsedCheckoutime = LocalDateTime.parse(checkoutime, formatter);
						history.location= location;
			            history.checkInTimeStamp= parsedCheckintime;
			            history.checkOutTimeStamp=parsedCheckoutime;
			            userHistory.add(history);
					}
					//Explicitly commit statements to apply changes
					c.commit();
					
					try {
						// Initiate callBack_ViewHistory with the user safe entry history
						// The next parameter indicating the operation status whether is it success (true) or failed (false)
						clientInstances.get(clientId).callBack_ViewHistory(userHistory, true);
					}
					catch (java.rmi.RemoteException re) {
						// re.printStackTrace();
						System.out.println("java.rmi.RemoteException occured");
					}
				}
				catch (Exception e) {
					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					System.exit(0);

					// If the above code runs into error, initiate a callback with failed status
					try {
						clientInstances.get(clientId).callBack_ViewHistory(null, true);
					}
					catch (java.rmi.RemoteException re) {
						System.out.println("java.rmi.RemoteException occured");
						// re.printStackTrace();
					}
				}
			}
		});
		// Start the new thread
		thread.start();
	}

	// This function checks if all the clients for their active status, it removes all the inactive clients
	public void checkIsActive() {

		// Get all the client instances
		Set<Integer> setOfClients = clientInstances.keySet();

		// Get all the nric reference
		Set<String> setOfNRICReferences = nricToClientReference.keySet();

		ArrayList<Integer> inactiveClientIds = new ArrayList<Integer>();
		// for-each loop - sieve out all the inactive client
		for (Integer key : setOfClients) {
			try {
				clientInstances.get(key).callBack_CheckIsActive();
			}
			catch (java.rmi.RemoteException e) {
				inactiveClientIds.add(key);
			}
		}

		ArrayList<String> inactiveNRICReference = new ArrayList<String>();
		// for-each loop - sieve out all the nric reference for the inactive client
		for (String key : setOfNRICReferences) {
			Integer currentClientId = nricToClientReference.get(key);
			for (Integer id : inactiveClientIds) {
				if (currentClientId == id) {
					inactiveNRICReference.add(key);
				}
			}
		}

		// for-each loop - remove inactive client
		for (Integer key: inactiveClientIds) {
			clientInstances.remove(key);
		}

		// for-each loop - remove inactive client references
		for (String key: inactiveNRICReference) {
			nricToClientReference.remove(key);
		}
	}

	// Special feature to allow the user to declare the timing and venue visited by a Covid 19 patient
	// This function will then do queries from the database and does callback to the respective users that has a close encounter with the Covid 19 patient
	public void declaration(String location, LocalDateTime checkInTimeStamp, LocalDateTime checkOutTimeStamp, Integer clientId) throws RemoteException {

		// Introduce concurrency concept with threading
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// Database query with callback and while loop to notify clients;
				// Assuming the 14 days is inclusively
				try {	
						String formatcheckInTimeStamp = checkInTimeStamp.toString().replace("T", " ");
					 	String formatcheckOutTimeStamp = checkOutTimeStamp.toString().replace("T", " ");
						ArrayList<String> listOfPotentialUsers = new ArrayList<String>(); 
						String message = "";
						// Accounts for three cases: 1. User check in between COVID-19 case check in timing (CIT) and check out timing (COT)
						// 2. User check out before COVID-19 case's COT
						// 3. User check out after COVID-19 case's COT AND User check in before CIT
						// SELECT query based on the three cases and store the nric result in an arraylist listOfPotentialUsers
						
						String sql1 = "SELECT DISTINCT NRIC FROM USERCHECKIN WHERE LOCATION = " + "\""+location+"\"" + " AND CHECKINTIME BETWEEN '" + formatcheckInTimeStamp + "' AND '" + formatcheckOutTimeStamp + "'" + " UNION" + " SELECT DISTINCT NRIC FROM USERCHECKIN WHERE LOCATION = " + "\""+location+"\"" + " AND CHECKOUTIME <=  '"+ formatcheckOutTimeStamp +"'" + " UNION" + " SELECT DISTINCT NRIC FROM USERCHECKIN WHERE LOCATION = " + "\""+location+"\"" + " AND CHECKOUTIME >=  '"+ formatcheckOutTimeStamp +"'" + " AND CHECKINTIME <='"+ formatcheckInTimeStamp +"'"; 
						try {
							ResultSet rs = stmt.executeQuery(sql1);
							while (rs.next()) {
        						String nricresult = rs.getString("NRIC");
								listOfPotentialUsers.add(nricresult);
        				
      				  		}	
						}
						catch(Exception e) {
							System.err.println( e.getClass().getName() + ": " + e.getMessage() );
							System.exit(0);
						}				
						// System.out.println("your list: " + listOfPotentialUsers);
						// Database mass update CloseEncounter to 1 and Informed to 0
						// Iterate through the arraylist of nric who may have come into contact with COVID-19 case
						// UPDATE SQLite Database column CLOSEENCOUNTER to True according to the respective nric
						for (String nric : listOfPotentialUsers) {
							message = "Dear citizen of NRIC " + nric + ", you were at a place visited by a COVID-19 case. Do monitor your health for 14 days till " 
								+ checkInTimeStamp.plusDays(13).toLocalDate().toString() + ".";
							String sql2= "UPDATE USERCHECKIN SET CLOSEENCOUNTER = TRUE, MESSAGE = "+"\""+message+"\" WHERE NRIC= "+"\""+nric+"\"" + " AND LOCATION = "+"\""+location+"\"";
							
							stmt.executeUpdate(sql2);
							c.commit();

						}
						
						for (String nric : listOfPotentialUsers) {	
							// If current reference contains the nric
							if (nricToClientReference.containsKey(nric)) {
								
								// Configure display message to user
								message = "Dear citizen of NRIC " + nric + ", you were at a place visited by a COVID-19 case. Do monitor your health for 14 days till " 
								+ checkInTimeStamp.plusDays(13).toLocalDate().toString() + ".";
								Integer clientIdInstance = nricToClientReference.get(nric);

								try {
									// Initiate callback and check if the message is delivered

									if (clientInstances.get(clientIdInstance).callBack_Notification(message)) {
										
										// If delivered, update db
										// Database update this particular nric , its informed status to 1
										String sql3= "UPDATE USERCHECKIN SET INFORMED = TRUE WHERE NRIC= "+"\""+nric+"\"" + " AND LOCATION = "+"\""+location+"\"";
										stmt.executeUpdate(sql3);
										c.commit();
									}
								}

								// Callback will throw error if connection unsuccessful
								catch (java.rmi.RemoteException e) {
		
									// This suggest the client is no longer active, thus unregister it
									unregister(clientIdInstance, nric);
								}
								
							}
						}

						// Initiate callback to inform the administrator that declaration is set successfully
						try {
							clientInstances.get(clientId).callBack_DeclarationStatus(true);
						}
						catch (java.rmi.RemoteException re) {
							System.out.println("java.rmi.RemoteException occured");
							// re.printStackTrace();
						} 
				}
				
				catch(Exception ex){
					System.err.println( ex.getClass().getName() + ": " + ex.getMessage() );
					
					// Initiate callback to inform the administrator that declaration has failed
					try {
						clientInstances.get(clientId).callBack_DeclarationStatus(false);
					}
					catch (java.rmi.RemoteException re) {
						System.out.println("java.rmi.RemoteException occured");
						// re.printStackTrace();
					} 
					
				}	
			}
		});
		// Start the new thread
		thread.start();
		
	}

}
