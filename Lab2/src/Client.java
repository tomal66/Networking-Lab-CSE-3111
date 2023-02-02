// A Java program for a Client
import java.io.*;
import java.net.*;

public class Client {
    // initialize socket and input output streams
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    private DataInputStream input2 = null;

    // constructor to put ip address and port
    public Client(String address, int port)
    {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            input2 = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            // sends output to the socket
            out = new DataOutputStream(
                    socket.getOutputStream());
        }
        catch (UnknownHostException u) {
            System.out.println(u);
            return;
        }
        catch (IOException i) {
            System.out.println(i);
            return;
        }

        // string to read message from input
        String line = "";
        String line2 = "";

        // keep reading until "Over" is input
        while (!line.equals("Over")) {
            try {

                System.out.println("Enter User ID:");
                line = input.readLine();
                out.writeUTF(line);
                line2 = input2.readUTF();
                //System.out.println(line2);
                if(line2.equals("100"))
                {
                    System.out.println("Invalid User");
                }
                else {
                    System.out.println("Enter Password:");
                    line = input.readLine();
                    out.writeUTF(line);
                    line2 = input2.readUTF();
                    if(line2.equals("99"))
                    {
                        System.out.println("User Authenticated");
                    }
                    else{
                        System.out.println("Invalid Password");
                    }
                }


            }
            catch (IOException i) {
                System.out.println(i);
            }
        }

        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        }
        catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String args[])
    {
        Client client = new Client("localhost", 5000);
    }
}
