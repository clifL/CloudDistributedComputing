/*
	Code: crawlerservant remote object	crawlerservant.java
	Date: 29th May 2021

	Explanation
*/

// The implementation Class must implement the rmi interface and be set as a Remote object on a server
import java.util.*;

public class crawlerservant extends java.rmi.server.UnicastRemoteObject implements crawler {

	private Hashtable<Integer, RMIClientIntf> clientInstances;

	// Implementations must have an explicit constructor
	// in order to declare the RemoteException exception

	public crawlerservant() throws java.rmi.RemoteException {
		super();
		clientInstances = new Hashtable<Integer, RMIClientIntf>();
	}

	// Register client to arraylist for future callback purposes
	public int register(RMIClientIntf client) throws java.rmi.RemoteException {
		Random rg = new Random();
		int clientId = rg.nextInt();
		while (clientInstances.containsKey(clientId)) {
			clientId = new Random().nextInt();
		}
		System.out.println("Registering Client Id: " + clientId);
		clientInstances.put(clientId, client);

		// Mock callback for reference
		try {
			System.out.println("Client active is " + clientInstances.get(clientId).callBack_CheckIsActive());
			String topics = "Bitcoin,Dogecoin,Ethereum";
			delegateWorkload(topics);
		} catch (java.rmi.RemoteException e) {
			e.printStackTrace();
		}
		return clientId;
	}

	// Subtract the second parameter from the first and return the result
	public boolean unregister(int clientId) throws java.rmi.RemoteException {
		System.out.println("Unregistering Client Id: " + clientId);
		if (clientInstances.containsKey(clientId)) {
			clientInstances.remove(clientId);
			return true;
		} else {
			return false;
		}
	}

	// Multiply the two parameters and return the result
	public String SentimentResult(String result) throws java.rmi.RemoteException {
		System.out.println("Received Result: " + result);

		// Todo - For frontend/database developer to use these result and do something

		return "Received";
	}

	public void checkIsActive() {

		// Get all the client instances
		Set<Integer> setOfClients = clientInstances.keySet();

		// for-each loop - remove inactive client
		ArrayList<Integer> templist = new ArrayList<Integer>();
		for (Integer key : setOfClients) {
			try {
				clientInstances.get(key).callBack_CheckIsActive();
			}
			catch (java.rmi.RemoteException e) {
				templist.add(key);
			}
		}
		for (Integer key: templist) {
			clientInstances.remove(key);
		}
	}

	// Delegating workload to clients
	public void delegateWorkload(String topics) throws java.rmi.RemoteException {

		// Check if client is still exist
		checkIsActive();
		System.out.println("Current client count: " + clientInstances.size());
		// Check if there is any active client
		// Inform user and or queue the topic(s) and execute when there's available
		// resource
		Set<Integer> setOfClients = clientInstances.keySet();
		if (clientInstances.size() == 0) {
			System.out.println("No available resources at the moment");
		}

		// Split csv to string array
		String[] topicsArray = topics.split(",");

		// Loop through to delegate tasks
		for (int i = 0; i < topicsArray.length; i++) {
			topicsArray[i] = topicsArray[i].trim();
		}

		int bitesize = topicsArray.length / clientInstances.size();
		int remainder = topicsArray.length % clientInstances.size();
		setOfClients = clientInstances.keySet();
		int count = 0;

		// Means there is more client instances than the number of topics
		if (clientInstances.size() >= topicsArray.length) {
			String[] delegatedTopics = new String[1];
			for (Integer key : setOfClients) {
				delegatedTopics[0] = topicsArray[count];
				if (count < topicsArray.length) {
					clientInstances.get(key).callBack_ConductAnalysis(delegatedTopics);
				} else {
					break;
				}
				count++;
			}
		} else {
			int lastTrack = 0;
			for (Integer key : setOfClients) {
				String[] delegatedTopics;
				int size = 0;
				if (count == (clientInstances.size() - 1)) {
					size = bitesize + remainder;
					delegatedTopics = new String[bitesize + remainder];
				} else {
					size = bitesize;
					delegatedTopics = new String[bitesize];
				}
				int currentMaxIteration = lastTrack + size;
				int j = 0;
				for (int k = lastTrack; k < currentMaxIteration; k++) {
					delegatedTopics[j] = topicsArray[k];
					j++;
				}
				clientInstances.get(key).callBack_ConductAnalysis(delegatedTopics);
				lastTrack += size;
				count++;
			}

		}
	}
}
