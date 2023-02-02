// A Java program for a Server
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    //initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private static List<Account> accounts = new ArrayList<>();
    // constructor with port
    public Server(int port)
    {
// starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");
// takes input from the client socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            // sends output to the socket
            out = new DataOutputStream(
                    socket.getOutputStream());
            String line = "";
// reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    String response = "";
                    line = in.readUTF();
                    int uid = Integer.parseInt(line);
                    Account acc = findByID(uid);

                    if(acc==null)
                    {
                        response = "100";
                        out.writeUTF(response);
                    }
                    else
                    {
                        line = in.readUTF();
                        int pass = Integer.parseInt(line);
                        if(acc.getPass() == pass) {
                            out.writeUTF("99");
                        }

                    }

                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");
// close connection
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
    public static void main(String args[])
    {
        createDummyAccounts();
        Server server = new Server(5000);
    }

    static void createDummyAccounts()
    {
        Account a1, a2, a3;
        a1 = new Account(1,2000, 50000);
        a2 = new Account(2,1234, 500000);
        a3 = new Account(3,4567, 10000);
        accounts.add(a1);
        accounts.add(a2);
        accounts.add(a3);
    }


    public Account findByID(int uid){
        System.out.println("Checking uid");
        for (Account account : accounts) {
            if (account.getUserId() == uid) {
                return account;
            }
        }
        return null;
    }
}