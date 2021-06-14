import java.rmi.RemoteException;

public class IntSinkimpl extends java.rmi.server.UnicastRemoteObject implements IntSink {

	public IntSinkimpl() throws java.rmi.RemoteException {
		super();
	}

	// public int timer(int i) throws java.rmi.RemoteException {
	// return i;
	// }

	public void ignore(int localParam) throws RemoteException {

	}

}
