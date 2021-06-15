/*
	Code: ServerServant remote object	ServerServant.java
	Date: 15th June 2021

	Contains the methods that can be remotley invoked
*/

// The implementation Class must implement the rmi interface (ServerInterface)
// and be set as a Remote object on a server
import java.util.*;

public class ServerServant extends java.rmi.server.UnicastRemoteObject implements ServerInterface {

	private ClientInterface c;

	// Implementations must have an explicit constructor
	// in order to declare the RemoteException exception

	public ServerServant() throws java.rmi.RemoteException {
		super();
	}

	// Implementation of the add method
	public void add(ClientInterface client, long a, long b) throws java.rmi.RemoteException {
		System.out.println("performing addition: " + a + " + " + b);
		c = client;

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				long startTime = System.currentTimeMillis(); // Start timing anything after this line
				Random rg = new Random();
				int timer = rg.nextInt(5000);
				try {
					Thread.sleep(timer);
					c.callBack(a + b);
				} catch (java.rmi.RemoteException e) {
					e.printStackTrace();
				} catch (InterruptedException ee) {
				}
				long stopTime = System.currentTimeMillis(); // Stop the timer that you started at line 24
				long elapsedTime = stopTime - startTime;
				System.out.println("The add() runs for " + elapsedTime + "ms"); // Prints out the time in ms it took to execute the thread
			}
		});
		thread.start();
	}

	// Implementation of the sub method
	public void sub(ClientInterface client, long a, long b) throws java.rmi.RemoteException {
		System.out.println("performing substraction: " + a + " - " + b);
		c = client;

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Random rg = new Random();
				int timer = rg.nextInt(5000);
				try {
					Thread.sleep(timer);
					c.callBack(a - b);
				} catch (java.rmi.RemoteException e) {
					e.printStackTrace();
				} catch (InterruptedException ee) {
				}
			}
		});
		thread.start();
	}

	public long pow(long a, int b) throws java.rmi.RemoteException {

		System.out.println("performing power operation: " + a + " ^ " + b);
		if (b == 0)
			return 1;
		else
			return a * pow(a, b - 1);
	}
}
