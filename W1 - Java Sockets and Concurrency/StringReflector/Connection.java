import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        }
        catch(IOException e) {
            System.out.println("Connection: " + e.getMessage());
        }
    }

    public void run() {
        try {
            System.out.println(Thread.currentThread().getName());
            String data = (String)in.readUTF();
            System.out.println("message= "+data);  
            out.writeUTF(data);
        }
        catch(EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        }
        catch(IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
        finally {
            try {
                clientSocket.close();
            }
            catch (IOException e) {
                System.out.println("clientSocket close failed");
            }
        }
    }
    
}
