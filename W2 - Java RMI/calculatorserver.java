/*
	Code: calculator server		CalculatorServer.java

	Server code for hosting the CalculatorImpl object
*/


import java.rmi.Naming;	//Import naming classes to bind to rmiregistry

public class calculatorserver {
	static int port = 1099;
   //calculatorserver constructor
   public calculatorserver() {

		 try {
		 		//Construct a new CalculatorImpl object and bind it to the local rmiregistry
     		//N.b. it is possible to host multiple objects on a server
			 calculator c = new calculatorimpl();
			 Naming.rebind("rmi://localhost/CalculatorService", c);
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
