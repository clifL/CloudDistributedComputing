/*
	Code: calculator server		CalculatorServer.java
	Date: 10th October 2000

	Server code for hosting the CalculatorImpl object
*/


import java.rmi.Naming;	//Import naming classes to bind to rmiregistry

public class calculatorserver {
	static int port = 1099;
   //calculatorserver constructor
   public calculatorserver() {
     
     //Construct a new CalculatorImpl object and bind it to the local rmiregistry
     //N.b. it is possible to host multiple objects on a server by repeating the
     //following method. 

     try {
       	calculator c = new calculatorimpl();
       	Naming.rebind("rmi://localhost:" + port + "/CalculatorService", c);
     } 
     catch (Exception e) {
       System.out.println("Server Error: " + e);
     }
   }

   public static void main(String args[]) {
     	//Create the new Calculator server
	if (args.length == 1)
		port = Integer.parseInt(args[0]);
	
	new calculatorserver();
   }
}
