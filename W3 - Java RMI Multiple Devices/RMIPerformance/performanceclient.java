import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class performanceclient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String reg_host = "localhost";
		int reg_port = 1099;

		if (args.length == 1) {
			reg_port = Integer.parseInt(args[0]);
		} else if (args.length == 2) {
			reg_host = args[0];
			reg_port = Integer.parseInt(args[1]);
		}

		try {
			IntSink is = (IntSink) Naming.lookup("rmi://localhost/rmiregistry");

			long startTime = System.currentTimeMillis(); // Start timing anything after this line
			// Execute RMI method 1 million times
			for (int i = 0; i < 1000000; i++) {
				is.ignore(i);
			}
			long stopTime = System.currentTimeMillis(); // Stop the timer that you started at line 24
			long elapsedTime = stopTime - startTime;
			System.out.println(elapsedTime + "ms"); // Prints out the time in ms it took to execute the method 1 million
													// time
		}

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
