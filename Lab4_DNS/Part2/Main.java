public class Main {
    public static void main(String[] args) {
        DNSMessage dnsMessage = new DNSMessage("google.com");
        DNSMessage dnsMessage1 = new DNSMessage(dnsMessage.toByteArray());
        System.out.println(dnsMessage1.getQuestion());
    }
}
