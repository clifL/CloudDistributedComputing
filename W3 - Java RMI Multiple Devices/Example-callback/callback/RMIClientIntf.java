/*
	  Code: Distributed Web Crawler (Client Interface)	RMIClientIntf.java
	  Date: 29th May 2021

	  Explanation
*/

import java.rmi.*;

public interface RMIClientIntf extends Remote {

	public void callBack_ConductAnalysis(String[] topics) throws java.rmi.RemoteException;

	public boolean callBack_CheckIsActive() throws java.rmi.RemoteException;


	
}