/*
	  Code: SafeEntry (Client Interface)	RMIClientIntf.java
	  Date: 29th May 2021

	  Usage: A java RMI interface for client side and it will be invoked from server side
*/

import java.rmi.*;
import java.util.ArrayList;

public interface RMIClientIntf extends Remote {

	// A callback function for server to inform user if he/she has a close encounter with a Covid 19 patient
	public boolean callBack_Notification(String message) throws java.rmi.RemoteException;

	// A callback function for the server to check if the client instance is still active
	public boolean callBack_CheckIsActive() throws java.rmi.RemoteException;

	// A callback function for the server to parse the client id to the client and also to inform the client if is a singleton session
	public void callBack_SetClientId(Integer clientId, boolean isExist) throws java.rmi.RemoteException;

	// A callback function for the server to parse the Check-In status to the client
	public void callBack_CheckInStatus(boolean status) throws java.rmi.RemoteException;

	// A callback function for the server to parse the Check-Out status to the client
	public void callBack_CheckOutStatus(boolean status) throws java.rmi.RemoteException;

	// A callback function for the server to parse the SafeEntry history to the client
	public void callBack_ViewHistory(ArrayList<History> history, boolean status) throws java.rmi.RemoteException;

	// A callback function for the server to parse the Covid 19 Patient Declaration status to the client
    public void callBack_DeclarationStatus(boolean status) throws java.rmi.RemoteException;
}