import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        byte[] sendData, receiveData;
        DatagramPacket sendPacket, receivePacket;

        while (true) {
            System.out.print("Enter domain name: ");
            String domainName = scanner.nextLine();
            DNSMessage dnsRequest = new DNSMessage(domainName);
            sendData = dnsRequest.toByteArray();
            sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9876);
            clientSocket.send(sendPacket); //send request to local server
            long startTime = System.currentTimeMillis();
            receiveData = new byte[1024];
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            byte[] data = receivePacket.getData();
            DNSMessage dnsResponse = new DNSMessage(data);
            DNSRecord answer = new DNSRecord(dnsResponse.getAnswer());
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println(answer.getValue());
            System.out.println("Delay: " + elapsedTime + "ms");
        }
    }

}
