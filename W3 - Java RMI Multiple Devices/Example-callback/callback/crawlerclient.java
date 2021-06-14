/*
	Code: Distributed Web Crawler (client)		crawlerclient.java
	Date: 29th May 2021

    Explanation
*/

import java.rmi.Naming; //Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException; //Import the RemoteException class so you can catch it
import java.net.MalformedURLException; //Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException; //Import the NotBoundException class so you can catch it

public class crawlerclient extends java.rmi.server.UnicastRemoteObject implements RMIClientIntf {

    private static int clientId = 0;
    private static crawler c;

    public crawlerclient() throws RemoteException {

    }

    public void callBack_ConductAnalysis(String[] topics) throws java.rmi.RemoteException { 
        for (String topic : topics) {
            System.out.println(topic);

            // Todo - Conduct Web Crawler - Jc / Jz

        }
        // Send mock result back to server
        System.out.println("Server Reply: " + c.SentimentResult("M1: 5.25, Starhub: 2.5"));
    }

    
    public boolean callBack_CheckIsActive() throws java.rmi.RemoteException {
        return true;
    }

    public static void main(String[] args) throws NotBoundException, MalformedURLException {

        String reg_host = "192.168.10.107";
        // String reg_host = "localhost";
        int reg_port = 1099;

        if (args.length == 1) {
            reg_port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            reg_host = args[0];
            reg_port = Integer.parseInt(args[1]);
        }

        try {

            crawlerclient cc = new crawlerclient();

            // Create the reference to the remote object through the remiregistry
            c = (crawler) Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/CrawlerService");

            // Now use the reference c to call remote methods
            clientId = c.register(cc);

            
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
}
