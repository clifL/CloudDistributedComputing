/*
	  Code: SafeEntry (Mutual Interface)	SafeEntry.java
	  Date: 29th May 2021

	  Explanation
*/

public interface SafeEntry extends java.rmi.Remote {

    public int register(RMIClientIntf client) throws java.rmi.RemoteException;

    public boolean unregister(int clientId) throws java.rmi.RemoteException;

    public String SentimentResult(String result) throws java.rmi.RemoteException;
}
