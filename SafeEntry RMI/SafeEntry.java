/*
	  Code: SafeEntry (Server Interface)	SafeEntry.java
	  Date: 29th May 2021

	  Usage: A java RMI interface for server side and it will be invoked from client side
*/
import java.util.*;
import java.time.LocalDateTime;

public interface SafeEntry extends java.rmi.Remote {

    // Register the client instance and binds it with NRIC
    public void register(RMIClientIntf client, String nric) throws java.rmi.RemoteException;

    // Un-register the client instance and unbinds it from the NRIC
    public void unregister(Integer clientId, String nric) throws java.rmi.RemoteException;

    // Check in feature with group check-in functionalities
    public void checkIn(String name, String nric, String location, Boolean onBehalf, String guarantor, Integer clientId) throws java.rmi.RemoteException;

    // Check out feature with group check-out functionalities
    public void checkOut(String nric, Boolean onBehalf, Integer clientId) throws java.rmi.RemoteException;

    // View SafeEntry history based on the NRIC
    public void viewHistory(String nric, Integer clientId) throws java.rmi.RemoteException;

    // Special feature to allow the user to declare the timing and venue visited by a Covid 19 patient
    public void declaration(String location, LocalDateTime checkInTimeStamp, LocalDateTime checkOutTimeStamp, Integer clientId) throws java.rmi.RemoteException;

}
