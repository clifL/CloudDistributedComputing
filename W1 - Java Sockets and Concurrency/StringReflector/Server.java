import java.net.*;
import java.io.*;

public class Server {
    public static void main(String args[]) {
        try {
            int serverPort = 8765;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while(true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        }
        catch(IOException e) {
            System.out.println("Listen: " + e.getMessage());
        }
    }



}
