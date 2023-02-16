import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AuthoritativeServer {
    public static void main(String[] args) throws IOException {

        DatagramSocket serverSocket = new DatagramSocket(9879);
        byte[] receiveData = new byte[1024];
        String domainName;

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            byte[] data = receivePacket.getData();
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            DNSMessage dnsMessage = new DNSMessage(data);
            domainName = dnsMessage.getQuestion();
            System.out.println(domainName);

            DNSRecord record = ALookup(domainName);
            if(record!=null)
            {
                dnsMessage.setAnswer(record.toString());
            }
            else{
                record = cnameLookup(domainName);
                if(record!=null)
                {
                    record = ALookup(record.getValue());
                    if(record!=null)
                    {
                        dnsMessage.setAnswer(record.toString());
                    }
                }
                else {
                    dnsMessage.setAnswer("authserv_error");
                }
            }

            System.out.println(dnsMessage.getAnswer());
            byte[] sendData = dnsMessage.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            serverSocket.send(sendPacket);
        }

    }


    static DNSRecord cnameLookup(String name)
    {

        try (BufferedReader reader = new BufferedReader(new FileReader("src/authRecords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts[0].equals(name) && parts[2].equals("CNAME")) {
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

    static DNSRecord ALookup(String name)
    {

        try (BufferedReader reader = new BufferedReader(new FileReader("src/authRecords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts[0].equals(name) && (parts[2].equals("A")||parts[2].equals("AAAA"))) {
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