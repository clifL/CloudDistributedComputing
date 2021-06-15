/*
	Code: Java RMI client		Client.java
	Date: 15th June 2021

	Simple client program that remotely calls a set of 
	methods available on the remote ServerServant object

*/

import java.rmi.Naming; //Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException; //Import the RemoteException class so you can catch it
import java.net.MalformedURLException; //Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException; //Import the NotBoundException class so you can catch it

public class Client extends java.rmi.server.UnicastRemoteObject implements ClientInterface {

    public Client() throws RemoteException {

    }

    public void callBack(long s) throws java.rmi.RemoteException {

        System.out.println("callback:" + s);
    }

    public static void main(String[] args) {

        String reg_host = "localhost";
        int reg_port = 1099;

        if (args.length == 1) {
            reg_port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            reg_host = args[0];
            reg_port = Integer.parseInt(args[1]);
        }

        try {

            Client cc = new Client();

            // Create the reference to the remote object through the remiregistry
            ServerInterface c = (ServerInterface) Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/Service");
            // Now use the reference c to call remote methods
            c.add(cc, 3, 21);
            c.sub(cc, 18, 9);
            System.out.println("2^5=" + c.pow(2, 5));
        }
        // Catch the exceptions that may occur - rubbish URL, Remote exception
        // Not bound exception or the arithmetic exception that may occur in
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
