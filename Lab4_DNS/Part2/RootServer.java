import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RootServer {
    public static void main(String[] args) throws IOException {

        DatagramSocket serverSocket = new DatagramSocket(9877);
        byte[] receiveData = new byte[1024];
        String domainName;

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            byte[] data = receivePacket.getData();
            InetAddress localAddress = receivePacket.getAddress();
            int localPort = receivePacket.getPort();
            String ipAddress;

            DNSMessage dnsMessage = new DNSMessage(data);

            //Search in root records
            domainName = dnsMessage.getQuestion();
            String s[] = domainName.split("\\.");
            String name = s[s.length-1];
            DNSRecord record = rootLookup(name);
            if(record!=null)
            {
                ipAddress = record.toString();
            }
            else{
                ipAddress = "error_root";
            }
            dnsMessage.setAnswer(ipAddress);
            System.out.println(ipAddress);
            byte[] sendData = dnsMessage.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, localAddress, localPort);
            serverSocket.send(sendPacket);
        }

    }

    static DNSRecord rootLookup(String name)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/rootRecords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts[0].equals(name)) {
                    String value = parts[1];
                    String type = parts[2];
                    String ttl = parts[3];

                    DNSRecord r = new DNSRecord(name, value, type, ttl);
                    r.print();
                    return r;
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
