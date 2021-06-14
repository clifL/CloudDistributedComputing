/*
	Code: SafeEntry (Client)		SafeEntryClient.java
	Date: 29th May 2021

    Usage: This class is for the client to initiate connection to the server via java RMI and the program logic lies.
    It consist of various callback functions for the server to initiate response back to the client.
    This script also consist of text-based program interface and invocation calls to the server.
*/

import java.rmi.Naming; //Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException; //Import the RemoteException class so you can catch it
import java.net.MalformedURLException; //Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException; //Import the NotBoundException class so you can catch it
import java.util.Scanner;  // Import the Scanner class, to allow capturing of user input
import java.util.regex.*; //Import Regex to validate NRIC
import java.util.ArrayList;
import java.time.LocalDateTime; // Helper library for datetime
import java.time.format.DateTimeFormatter;

public class SafeEntryClient extends java.rmi.server.UnicastRemoteObject implements RMIClientIntf {

    // Static variables for referencing purposes, one assumption made is that each client will be on a separate device.
    private static int clientId = 0;
    private static SafeEntry c;
    private static volatile boolean lock = false;

    public SafeEntryClient() throws RemoteException {

    }


    // A callback function for the server to parse the Check-In status to the client
    public void callBack_CheckInStatus(boolean status) throws java.rmi.RemoteException {
        if (status) {
            System.out.println("Check-In Success!");
        }
        else {
            System.out.println("Check-In Failed.");
        }
        lock = false;
    }

    // A callback function for the server to parse the Check-Out status to the client
	public void callBack_CheckOutStatus(boolean status) throws java.rmi.RemoteException {
        if (status) {
            System.out.println("Check-Out Success!");
        }
        else {
            System.out.println("Check-Out Failed.");
        }
        lock = false;
    }

    // A callback function for the server to parse the SafeEntry history to the client
	public void callBack_ViewHistory(ArrayList<History> history, boolean status) throws java.rmi.RemoteException {
        if (status == false) {
            System.out.println("View location history failed.");
        }
        else {
            System.out.println("Your location history are as per below.");
            if (history.size() == 0) {
                System.out.println("Your history is empty!");
            }
            for (History instance : history) {
                System.out.println("Location: " + instance.location + ", Check-In: " + instance.checkInTimeStamp + ", Check-Out: " + instance.checkOutTimeStamp);
            }
        }
        lock = false;
    }


    // This callback can be consider to remove
    public void callBack_DeclarationStatus(boolean status) throws java.rmi.RemoteException {
        if (status) {
            System.out.println("Declaration Success!");
        }
        else {
            System.out.println("Declaration Failed.");
        }
        lock = false;
    }

    // A callback function for the server to parse the client id to the client and also to inform the client if is a singleton session
    public void callBack_SetClientId(Integer clientId, boolean isExist) throws java.rmi.RemoteException {
        if (isExist) {
            System.out.println("Session with this NRIC already exist. Terminating the program...");
            System.exit(0);
        }
        else {
            SafeEntryClient.clientId = clientId;
            System.out.println("Client Id is set to: " + clientId + "\n");
            lock = false;
        }
    }

    // A callback function for server to inform user if he/she has a close encounter with a Covid 19 patient
    public boolean callBack_Notification(String message) throws java.rmi.RemoteException {
        System.out.println(message); 
        lock = false;
        return true;
    }

    // A callback function for the server to check if the client instance is still active
    public boolean callBack_CheckIsActive() throws java.rmi.RemoteException {
        return true;
    }


    public static void main(String[] args) throws NotBoundException, MalformedURLException {

        // Set the java rmi connection to localhost
        String reg_host = "localhost";
        // Set the communication port to 1099
        int reg_port = 1099;

        // For dynamic setting of host and port via startup parameters
        if (args.length == 1) {
            reg_port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            reg_host = args[0];
            reg_port = Integer.parseInt(args[1]);
        }

        try {
            // Create an instance of SafeEntryClient object
            SafeEntryClient cc = new SafeEntryClient();

            // Create the reference to the remote object through the remiregistry
            c = (SafeEntry) Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/SafeEntryService");

            System.out.println("\nWelcome to Safe Entry!\n");

            //Gather, validate & return user NRIC input
            System.out.println("Please authenticate yourself by entering your NRIC & name \n");
            System.out.println("Enter your NRIC:");
            String userNRIC = getNRIC();

            //Gather, validate, return user name input & convert name to upper case
            System.out.println("Enter your name (At least 5 characters): ");
            String userName = getName();
            String userCleanName = userName.toUpperCase();

            System.out.println("Authenticating, please hold on");

            // Now use the reference c to call remote methods
            String regNric = userNRIC;
            c.register(cc, regNric);

            // Prevent the user from continuing until a respond from server
            lockConsole();

            System.out.println("Welcome " + userCleanName);

            //Display features menu, validate and gather user choice on the menu option
            //Features are numbered from 1 to 5
            displayMenu();
            int choice = getUserChoice();

            while(choice != 5){
                switch (choice){
                    case 1:
                    System.out.println("\nCheck-in selected\n");

                    //Gather, validate and return user chosen location
                    String userLocation = getUserLocation();
                    System.out.println(userLocation + " have been selected");

                    //Gather, validate and return user choice if they are checking in for next of kin
                    System.out.println("Are you checking in on behalf for someone? \n1. Yes \n2. No");
                    Integer nokCheckInOption = nextOfKinOption();

                    //if user are checking in for next of kin
                    if(nokCheckInOption == 1){
                        //Calling the nok check in method, which take in user entered location and NRIC
                        nokCheckIn(userLocation, userNRIC);
                    }
                    else{
                        try {
                            //user are checking in for himself when no is selected, hence taking in user entered upper case name, nric and location
                            //there will be no guarantor, no on-behalf and clientId will be gathered from the initial authenticating process
                            c.checkIn(userCleanName, userNRIC, userLocation, false, "", clientId); 
                        }
                        catch (java.rmi.RemoteException e) {
                            e.printStackTrace();
                        }
                        // Prevent the user from continuing until a respond from server
                        lockConsole();
                    }

                    break;
                    case 2:
                    System.out.println("\nCheck-out selected\n");

                    //Gather, validate and return user choice if they are checking out for next of kin
                    System.out.println("Are you checking out on behalf for someone? \n1. Yes \n2. No");
                    Integer nokCheckOutOption = nextOfKinOption();

                    //if user are checking out for next of kin
                    if (nokCheckOutOption == 1){
                        
                        //Gathering of nok NRIC for checking out
                        System.out.println("What's their NRIC number?");
                        String nextOfKinNric = getNRIC();

                        try {
                            //Calling method for checking out, taking the validated nok NRIC input, true for onBehalf & clientId return from the server
                            c.checkOut(nextOfKinNric, true, clientId); 
                        }
                        catch (java.rmi.RemoteException e) {
                            e.printStackTrace();
                        }
                        // Prevent the user from continuing until a respond from server
                        lockConsole();
                        
                    }
                    else{
                        //Solo checkout
                        try {
                            //Self checkout option, hence taking user entered NRIC at the begining of the program, setting on behalf as false & the clientId
                            c.checkOut(userNRIC, false, clientId); 
                        }
                        catch (java.rmi.RemoteException e) {
                            e.printStackTrace();
                        }
                        // Prevent the user from continuing until a respond from server
                        lockConsole();
                    }
                    break;
                    case 3:
                        System.out.println("\nViewing of history selected\n");

                        //Gathering of validated NRIC from user
                        System.out.println("Enter the person NRIC number for viewing of history: ");
                        String nricInstance = getNRIC();

                        try {
                            //Viewing of history would require NRIC & clientId
                            c.viewHistory(nricInstance, clientId); 
                        }
                        catch (java.rmi.RemoteException e) {
                            e.printStackTrace();
                        }
                         // Prevent the user from continuing until a respond from server
                        lockConsole();


                    break;
                    case 4:
                        System.out.println("\nSpecial Remote Function selected\n");

                        //Gathering validated location from the user for the special remote function
                        String adminLocation = getUserLocation();
                        //Set the format for date time
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
                        LocalDateTime checkInDateTime = null;
                        LocalDateTime checkOutDateTime = null;
                        boolean isValid = false;
                        while (isValid == false){
                            //Get both check-in & check-out date from user, any error will be caught by try and catch
                            System.out.println("Please enter the patient check-in time (format yyyy-MM-dd HH:mm): ");
                            String checkInDateTimeInput = getDateTime();
                            System.out.println("Please enter the patient check-out time (format yyyy-MM-dd HH:mm):");
                            String checkOutDateTimeInput = getDateTime();
                            try {
                                checkInDateTime = LocalDateTime.parse(checkInDateTimeInput, formatter);
                                checkOutDateTime = LocalDateTime.parse(checkOutDateTimeInput, formatter);
                               
                                //Comparing date and ensure check in come before check out
                                //resultCompare = 0 Date1 is equal to Date2, = 1 Date1 is after Date2, =-1 Date1 is before Date2
                                Integer resultCompare = checkInDateTime.compareTo(checkOutDateTime);
                                
                                if(resultCompare < 0){
                                    isValid = true;
                                    System.out.println("Both date time entered successfully");
                                }
                                else{
                                    System.out.println("Invalid date entered, try again...");
                                    isValid = false;
                                }

                            }
                            catch (Exception ex) {
                                System.out.println("Invalid syntax or date, try again...");
                            }
                        }
                        

                        try {
                            //Calling declaration method, which would require a location, the check in date time
                            //check out date time and clientId
                            //this will return if there is any covid cases at that time frame
                            c.declaration(adminLocation, checkInDateTime, checkOutDateTime, clientId);
                        }
                        catch (java.rmi.RemoteException e) {
                            e.printStackTrace();
                        }
                        // Prevent the user from continuing until a respond from server
                        lockConsole();

                    break;
                }

                displayMenu();
                choice = getUserChoice();
            }

            System.out.println("Thank you for using Safe Entry, stay safe and have a good day!");
            System.exit(0);
            
            // c.unregister(clientId);
        }
        // Catch the exceptions that may occur - rubbish URL, Remote exception
        // Not bound exception or the arithmetic exception that may occur in
        // one of the methods creates an arithmetic error (e.g. divide by zero)
        catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        } catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        } catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        } catch (java.lang.ArithmeticException ae) {
            System.out.println();
            System.out.println("java.lang.ArithmeticException");
            System.out.println(ae);
        }
    }

    //NRIC validation method
    public static boolean validateNric(String inputString) {
        String validatingNRIC = inputString.toUpperCase();

        // first letter must start with S, T, F or G. Last letter must be A - Z
        if (!Pattern.compile("^[STFG]\\d{7}[A-Z]$").matcher(validatingNRIC).matches()) {
            return false;
        } else {
            char[] icArray = new char[9];
            char[] st = "JZIHGFEDCBA".toCharArray();
            char[] fg = "XWUTRQPNMLK".toCharArray();

            for (int i = 0; i < 9; i++) {
                icArray[i] = validatingNRIC.charAt(i);
            }

            // calculate weight of positions 1 to 7
            int weight = (Integer.parseInt(String.valueOf(icArray[1]), 10)) * 2 + 
                    (Integer.parseInt(String.valueOf(icArray[2]), 10)) * 7 +
                    (Integer.parseInt(String.valueOf(icArray[3]), 10)) * 6 +
                    (Integer.parseInt(String.valueOf(icArray[4]), 10)) * 5 +
                    (Integer.parseInt(String.valueOf(icArray[5]), 10)) * 4 +
                    (Integer.parseInt(String.valueOf(icArray[6]), 10)) * 3 +
                    (Integer.parseInt(String.valueOf(icArray[7]), 10)) * 2;

            int offset = icArray[0] == 'T' || icArray[0] == 'G' ? 4 : 0;

            int lastCharPosition = (offset + weight) % 11;

            if (icArray[0] == 'S' || icArray[0] == 'T') {
                return icArray[8] == st[lastCharPosition];
            } else if (icArray[0] == 'F' || icArray[0] == 'G') {
                return icArray[8] == fg[lastCharPosition];
            } else {
                return false; // this line should never reached due to regex above
            }
        }
    }

    //Get validated NRIC from user
    public static String getNRIC(){
        Scanner userNric = new Scanner(System.in);
        String nricInput = userNric.nextLine();

        while (validateNric(nricInput) == false){
            System.out.println("Please enter a valid NRIC");
            System.out.println("Re-enter NRIC: ");
            nricInput = userNric.nextLine();
        }

        return nricInput;
    }

    //Name validation method
    public static boolean validateName(String name){
  
        // Regex to check valid name
        String regex =  "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}";
  
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
  
        // check if name is empty
        if (name == null) {
            return false;
        }

        // Check if name is more than 6 characters & less than 30 characters
        if (name.length() < 6 || name.length() > 30){
            return false;
        }
        // Pattern class contains matcher() method to find matching between given name and regular expression.
        Matcher m = p.matcher(name);
  
        // Return if the name matched the ReGex
        return m.matches();
    }

    //Get validated name from user
    public static String getName(){
        Scanner userName = new Scanner(System.in);
        String nameInput = userName.nextLine();

            while(validateName(nameInput) == false){
                System.out.println("Please enter a valid name (At least 5 characters):");
                System.out.println("Re-enter name:");
                nameInput = userName.nextLine();
            }
        return nameInput;
    }

    //Features Menu
    public static void displayMenu(){
        String[] features = {"\n1. Check-in", "2. Check-out", "3. View History", "4. Special Remote Function", "5. Exit \n"};

        System.out.println("\nFeatures Available");
            for(int i=0; i < features.length; i++){
                System.out.println(features[i]);
            }
    }

    //Gathering user features choice & validate if choice are integer
    public static Integer getUserChoice(){
        
        Scanner userChoice = new Scanner(System.in);
        Integer selectedFeaturesNumber;

        do{
            System.out.println("Please select the features from the menu provided (1 to 5)");
            while(userChoice.hasNextInt() == false){
                System.out.println("Please enter number only");
                userChoice.next();
            }
            selectedFeaturesNumber = userChoice.nextInt();
        } while(selectedFeaturesNumber < 1 ||  selectedFeaturesNumber > 5);
        
        return selectedFeaturesNumber;
    }

    //Validating and gathering of user input on next of kin option, finally returning user decision
    public static Integer nextOfKinOption(){
        
        Scanner decision = new Scanner(System.in);
        Integer userDecision;

        do{
            System.out.println("Please select 1 for yes or 2 for no");
            while(decision.hasNextInt() == false){
                System.out.println("Please enter number only");
                decision.next();
            }
            userDecision = decision.nextInt();
        } while(userDecision > 2 || userDecision < 1);

        return userDecision;
    }

    //Allow user to check-in on behalf of next of kin which include asking for NRIC & name
    public static boolean nokCheckIn(String location, String guarantor){

        System.out.println("What's their NRIC number?");
        String nokNRIC = getNRIC();

        System.out.println("What's their name?");
        String nokName = getName();

        try {
            c.checkIn(nokName, nokNRIC, location, true, guarantor, clientId); 
        }
        catch (java.rmi.RemoteException e) {
            e.printStackTrace();
        }
        lockConsole();
        return true;
    }


    //Locking of console while waiting for server to respond
    public static void lockConsole() {
        lock = true; 
        System.out.println("Waiting for server respond, please hold on");
        while (lock == true) {

        }
    }

    //Validate user choice on location and return the validated location
    public static String getUserLocation(){
        String[] locationMenu = {"1. Jurong East", "2. Ang Mo Kio", "3. Punggol", "4. Yio Chu Kang", "5. Orchard"};
        String[] locationMenuClean = {"Jurong East", "Ang Mo Kio", "Punggol", "Yio Chu Kang", "Orchard"};

        System.out.println("Select 1 location using the number displayed");
            for(int i=0; i < locationMenu.length; i++){
                System.out.println(locationMenu[i]);
            }

        Scanner userChoice = new Scanner(System.in);
        Integer selectedLocation;
        
        do{
            System.out.println("Please select the location from the choices available");  
            while(userChoice.hasNextInt() == false){
                System.out.println("Please enter number only");
                userChoice.next();
            }
            selectedLocation = userChoice.nextInt();
        } while(selectedLocation > locationMenu.length || selectedLocation < 1); 
        
        return locationMenuClean[selectedLocation-1];
    }

    //Gather data time input from user
    public static String getDateTime(){
        Scanner dateTimeScan = new Scanner(System.in);
        String  userInput = dateTimeScan.nextLine();

        return userInput;
    }

}