import java.rmi.*;
	
public interface RMIClientIntf extends Remote {

	public void callBack(long s) throws java.rmi.RemoteException;
}