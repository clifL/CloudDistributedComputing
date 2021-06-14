/*
		Code: Distributed Web Crawler (server)	crawlerserver.java
	  Date: 29th May 2021

	  Server code for hosting the crawlerservant object
*/

import java.rmi.Naming; //Import naming classes to bind to rmiregistry
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class crawlerserver {
  static int port = 1099;

  // crawlerserver constructor
  public crawlerserver() {

    // Construct a new CalculatorImpl object and bind it to the local rmiregistry
    // N.b. it is possible to host multiple objects on a server by repeating the
    // following method.

    try {
      crawler c = new crawlerservant();
      System.setProperty("java.rmi.server.hostname", "192.168.10.107");
      Registry reg = LocateRegistry.createRegistry(port);
      // Naming.rebind("rmi://192.168.10.107:" + port + "/CalculatorService", c);
      reg.rebind("CrawlerService", c);
      // reg.rebind("rmi://192.168.10.107:" + port + "/CalculatorService", c);
    } catch (Exception e) {
      System.out.println("Server Error: " + e);
    }
  }

  public static void main(String args[]) {
    // Create the new Calculator server
    if (args.length == 1)
      port = Integer.parseInt(args[0]);

    new crawlerserver();
  }
}
