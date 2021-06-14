/*
		Code: SafeEntry (Server)	SafeEntryServer.java
	  Date: 29th May 2021

	  Usage: Server code for hosting the SafeEntryServant object
*/

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SafeEntryServer {
  static int port = 1099;

  // SafeEntryServer constructor
  public SafeEntryServer() {

    try {
      // Create an instance of the servant object
      SafeEntry c = new SafeEntryServant();
      // Set hostname to localhost
      System.setProperty("java.rmi.server.hostname", "localhost");
      // Usage of LocateRegistry class for automates the "start registry" function
      Registry reg = LocateRegistry.createRegistry(port);
      // Binding registry with the supplied remote reference name
      reg.rebind("SafeEntryService", c);
    } 
    
    // For catching of error (if any)
    catch (Exception e) {
      System.out.println("Server Error: " + e);
    }
  }

  public static void main(String args[]) {
    // Create the new SafeEntry server
    new SafeEntryServer();
  }
}
