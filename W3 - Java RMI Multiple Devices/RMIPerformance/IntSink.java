import java.rmi.RemoteException;
import java.rmi.Remote;

public interface IntSink extends java.rmi.Remote {
    /**
     * a single method with a single int argument used to time RMI calls
     *
     * @param localParam the single parameter to ignore
     */

    // public int timer(int i) throws java.rmi.RemoteException;

    public void ignore(int localParam) throws java.rmi.RemoteException;
}
