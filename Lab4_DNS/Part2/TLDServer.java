import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TLDServer {
    public static void main(String[] args) throws IOException {

        DatagramSocket serverSocket = new DatagramSocket(9878);
        byte[] receiveData = new byte[1024];
        String domainName;

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            byte[] data = receivePacket.getData();
            InetAddress localAddress = receivePacket.getAddress();
            int localPort = receivePacket.getPort();
            String ipAddress = null;

            DNSMessage dnsMessage = new DNSMessage(data);

            //Search in TLD
            domainName = dnsMessage.getQuestion();
            DNSRecord record = NSLookup(domainName);
            if(record!=null)
            {
                ipAddress = record.getValue();
                dnsMessage.setAnswer(record.toString());
            }
            else{
                record = cnameLookup(domainName);
                if(record!=null)
                {
                    record = NSLookup(record.getValue());
                    if(record!=null)
                    {
                        ipAddress = record.getValue();
                        dnsMessage.setAnswer(record.toString());
                    }
                }
                else {
                    ipAddress = "error_TLD";
                }
            }
            dnsMessage.setAnswer(ipAddress);
            System.out.println(ipAddress);
            byte[] sendData = dnsMessage.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, localAddress, localPort);
            serverSocket.send(sendPacket);
        }

    }

    static DNSRecord cnameLookup(String name)
    {

        try (BufferedReader reader = new BufferedReader(new FileReader("src/TLDRecords.txt"))) {
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

    static DNSRecord NSLookup(String name)
    {

        try (BufferedReader reader = new BufferedReader(new FileReader("src/TLDRecords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts[0].equals(name) && (parts[2].equals("A")||parts[2].equals("NS"))) {
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
