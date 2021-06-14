
import java.rmi.Naming;	//Import naming classes to bind to rmiregistry
import java.rmi.registry.LocateRegistry;


public class performanceserver {

	static int port = 1099;
	   //calculatorserver constructor
	   public performanceserver() {

			 try {
			 	//Construct a new CalculatorImpl object and bind it to the local rmiregistry
	     		//N.b. it is possible to host multiple objects on a server
				 System.out.println("Init server...\n");
				 IntSink c = new IntSinkimpl(); // Instantiate the servant class into an object
				 System.out.println("Reg RMI...\n");
				 LocateRegistry.createRegistry(port);
				 Naming.rebind("rmi://localhost/rmiregistry", c); // Binding it to the naming service, the name will be used to call its method
				 System.out.println("Server Started!");
	     }
	     catch (Exception e) {
	       System.out.println("Server Error: " + e);
	     }
	   }

	   public static void main(String args[]) {
	     	//Create the new Calculator server
				if (args.length == 1)
					port = Integer.parseInt(args[0]);
					new performanceserver();
	   		}
}
