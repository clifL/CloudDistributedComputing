/*
	Code: CalculatorImpl remote object	calculatorimpl.java
	Date: 10th October 2000

	Contains the arithmetic methods that can be remotley invoked
*/

// The implementation Class must implement the rmi interface (calculator)
// and be set as a Remote object on a server
import java.util.*;

public class calculatorimpl extends java.rmi.server.UnicastRemoteObject implements calculator {

	private RMIClientIntf c;

	// Implementations must have an explicit constructor
	// in order to declare the RemoteException exception

	public calculatorimpl() throws java.rmi.RemoteException {
		super();
	}

	// Implementation of the add method
	// The two long parameters are added added and the result is retured
	public void add(RMIClientIntf client, long a, long b) throws java.rmi.RemoteException {
		System.out.println("performing addition: " + a + " + " + b);
		c = client;

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Random rg = new Random();
				int timer = rg.nextInt(5000);
				try {
					Thread.sleep(timer);
					c.callBack(a + b);
				} catch (java.rmi.RemoteException e) {
					e.printStackTrace();
				} catch (InterruptedException ee) {
				}
			}
		});
		thread.start();
		return;
	}

	// Subtract the second parameter from the first and return the result
	public void sub(RMIClientIntf client, long a, long b) throws java.rmi.RemoteException {
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
		return;
	}

	// Multiply the two parameters and return the result
	public long mul(long a, long b) throws java.rmi.RemoteException {
		System.out.println("performing multiplication: " + a + " * " + b);
		return a * b;
	}

	// Divide first parameter by the second and return the result
	public long div(long a, long b) throws java.rmi.RemoteException {
		System.out.println("performing division: " + a + " / " + b);
		return a / b;
	}

	// Recursive power definition
	public long pow(long a, int b) throws java.rmi.RemoteException {

		System.out.println("performing power operation: " + a + " ^ " + b);
		if (b == 0)
			return 1;
		else
			return a * pow(a, b - 1);
	}
}
