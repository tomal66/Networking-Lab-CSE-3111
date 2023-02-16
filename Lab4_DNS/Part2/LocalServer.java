import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LocalServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        String domainName;

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            byte[] data = receivePacket.getData();
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            String ipAddress = null;

            DNSMessage dnsMessage = new DNSMessage(data);

//            //Search in local cache
//            domainName = dnsMessage.getQuestion();
//            DNSRecord record = ALookup(domainName);
//            if(record!=null)
//            {
//                ipAddress = record.getValue();
//                dnsMessage.setAnswer(record.toString());
//            }
//            else{
//                record = cnameLookup(domainName);
//                if(record!=null)
//                {
//                    record = ALookup(record.getValue());
//                    if(record!=null)
//                    {
//                        ipAddress = record.getValue();
//                        dnsMessage.setAnswer(record.toString());
//                    }
//                }
//                else {
//                    ipAddress = "error_local";
//                }
//            }

            ipAddress = "error_local";

            //If not found in local cache, send iterative requests
            if(ipAddress.equals("error_local"))
            {
                //send-receive from root
                InetAddress rootAddress = InetAddress.getByName("127.0.0.1");
                int rootPort = 9877;
                DatagramPacket sendPacketToRoot = new DatagramPacket(data, data.length, rootAddress, rootPort);
                serverSocket.send(sendPacketToRoot);
                byte[] receiveFromRootData = new byte[1024];
                DatagramPacket receiveFromRootPacket = new DatagramPacket(receiveFromRootData, receiveFromRootData.length);
                serverSocket.receive(receiveFromRootPacket);
                byte[] rootData = receiveFromRootPacket.getData();
                DNSMessage rootDNSMessage = new DNSMessage(rootData);
                ipAddress = rootDNSMessage.getAnswer();
                if(ipAddress.equals("error_root"))
                {
                    System.out.println("root e nai");
                }
                else
                {
                    //send-receive from TLD
                    DNSRecord r = new DNSRecord(ipAddress);
                    InetAddress TLDAddress = InetAddress.getByName(r.getValue());
                    int TLDPort = 9878;
                    DatagramPacket sendPacketToTLD = new DatagramPacket(data, data.length, TLDAddress, TLDPort);
                    serverSocket.send(sendPacketToTLD);
                    byte[] receiveFromTLDData = new byte[1024];
                    DatagramPacket receiveFromTLDPacket = new DatagramPacket(receiveFromTLDData, receiveFromTLDData.length);
                    serverSocket.receive(receiveFromTLDPacket);
                    byte[] TLDData = receiveFromTLDPacket.getData();
                    DNSMessage TLDDNSMessage = new DNSMessage(TLDData);
                    ipAddress = TLDDNSMessage.getAnswer();
                    if(ipAddress.equals("error_tld"))
                    {
                        System.out.println("tld e nai");
                    }
                    else
                    {
                        //send-receive from authoritative
                        r = new DNSRecord(ipAddress);
                        InetAddress AuthAddress = InetAddress.getByName(r.getValue());
                        int AuthPort = 9879;
                        DatagramPacket sendPacketToAuth = new DatagramPacket(data, data.length, AuthAddress, AuthPort);
                        serverSocket.send(sendPacketToAuth);
                        byte[] receiveFromAuthData = new byte[1024];
                        DatagramPacket receiveFromAuthPacket = new DatagramPacket(receiveFromAuthData, receiveFromAuthData.length);
                        serverSocket.receive(receiveFromAuthPacket);
                        byte[] AuthData = receiveFromAuthPacket.getData();
                        DNSMessage AuthDNSMessage = new DNSMessage(AuthData);
                        ipAddress = AuthDNSMessage.getAnswer();
                    }
                }
            }

            dnsMessage.setAnswer(ipAddress);
            System.out.println(ipAddress);
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
