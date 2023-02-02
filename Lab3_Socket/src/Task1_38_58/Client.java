// Java implementation for a client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.util.Scanner;

// Client class
public class Client
{
    static DataInputStream dis = null;
    static DataOutputStream dos = null;
    public static void main(String[] args) throws IOException
    {
        try
        {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection with server port 5056
            Socket s = new Socket(ip, 5056);

            // obtaining input and out streams
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // the following loop performs the exchange of
            // information between client and client handler
            while (true)
            {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);

                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
                System.out.println();
                // printing date or time as requested by client
                receiveFile(tosend);
            }

            // closing resources
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void receiveFile(String fileName)
            throws Exception
    {
        int bytes = 0;
        long size
                = dis.readLong(); // read file size
        if(size==0)
        {
            System.out.println("File doesn't exist!");
        }
        else
        {
            FileOutputStream fileOutputStream
                    = new FileOutputStream(fileName);
            byte[] buffer = new byte[4 * 1024];
            while (size > 0
                    && (bytes = dis.read(
                    buffer, 0,
                    (int)Math.min(buffer.length, size)))
                    != -1) {
                // Here we write the file using write method
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes; // read upto file size
            }
            // Here we received file
            System.out.println("File is Received");
            fileOutputStream.close();
        }
    }
}
