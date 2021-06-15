/*
	Code: Server Interface	ServerInterface.java
	Date: 15th June 2021

	The server interface provides a description of the remote 
    methods available as part of the service provided
	by the remote object ServerServant. 
*/

public interface ServerInterface extends java.rmi.Remote {

    public void add(ClientInterface client, long a, long b) throws java.rmi.RemoteException;

    public void sub(ClientInterface client, long a, long b) throws java.rmi.RemoteException;

    public long pow(long a, int b) throws java.rmi.RemoteException;

}
