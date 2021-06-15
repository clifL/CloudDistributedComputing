/*
	Code: calculator server		CalculatorServer.java
	Date: 15th June 2021

	Server code for hosting the ServerServant object
*/

import java.rmi.Naming; 
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
  static int port = 1099;

  // Server constructor
  public Server() {

    // Construct a new ServerServant object and bind it to the local rmiregistry
    // N.b. it is possible to host multiple objects on a server by repeating the
    // following method.

    try {
      ServerInterface c = new ServerServant();

      // Starts the rmiregistry automatically
      System.setProperty("java.rmi.server.hostname", "localhost");
      Registry reg = LocateRegistry.createRegistry(port);
      // Bind service to registry
      reg.rebind("Service", c);

    } catch (Exception e) {
      System.out.println("Server Error: " + e);
    }
  }

  public static void main(String args[]) {
    // Create the new Calculator server
    if (args.length == 1)
      port = Integer.parseInt(args[0]);

    new Server();
  }
}
