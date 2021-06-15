/*
	Code: Client Interface	ClientInterface.java
	Date: 15th June 2021

	The client interface provides a description of the remote 
    methods available for the ServerServant to initiate a callback. 
*/

import java.rmi.*;

public interface ClientInterface extends Remote {

	public void callBack(long s) throws java.rmi.RemoteException;
}