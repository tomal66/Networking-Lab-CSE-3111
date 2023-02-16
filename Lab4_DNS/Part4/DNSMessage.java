import java.nio.ByteBuffer;
import java.util.UUID;

public class DNSMessage {
    private short id;
    private short flag;
    private short question_rrs;
    private short answer_rrs;
    private short authority_rrs;
    private short additional_rrs;
    private String question;
    private String answer;
    private String authority;
    private String additional;

    public DNSMessage(String question) {
        this.question = question;
        UUID uuid = UUID.randomUUID();
        id = (short) (uuid.getLeastSignificantBits() & 0xFFFF);
        flag = (short) 0x8180;
        question_rrs = 1;
        answer_rrs = 0;
        authority_rrs = 0;
        additional_rrs = 0;
        answer = "";
        authority = "";
        additional = "";
    }

    public DNSMessage(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(java.nio.ByteOrder.BIG_ENDIAN);

        // Read header section
        id = buffer.getShort();
        flag = buffer.getShort();
        question_rrs = buffer.getShort();
        answer_rrs = buffer.getShort();
        authority_rrs = buffer.getShort();
        additional_rrs = buffer.getShort();

        // Read question section
        StringBuilder questionBuilder = new StringBuilder();
        byte labelLength = buffer.get();
        while (labelLength != 0) {
            byte[] label = new byte[labelLength];
            buffer.get(label);
            questionBuilder.append(new String(label)).append(".");
            labelLength = buffer.get();
        }
        question = questionBuilder.substring(0, questionBuilder.length() - 1);
        buffer.getShort();  // Type: A
        buffer.getShort();  // Class: IN

        // Read answer section
        StringBuilder answerBuilder = new StringBuilder();
        labelLength = buffer.get();
        while (labelLength != 0) {
            byte[] label = new byte[labelLength];
            buffer.get(label);
            answerBuilder.append(new String(label)).append(".");
            labelLength = buffer.get();
        }
        if (answerBuilder.length() > 0) {
            answer = answerBuilder.substring(0, answerBuilder.length() - 1);
        } else {
            answer = "";
        }
        buffer.getShort();
        buffer.getShort();  // Class: IN
        buffer.getInt();
        buffer.getShort();
        buffer.get();

        authority = "";
        additional = "";
    }

    public byte[] toByteArray(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.order(java.nio.ByteOrder.BIG_ENDIAN);

        // Fill the header section
        buffer.putShort(id);  // Transaction ID
        buffer.putShort(flag);  // Flags
        buffer.putShort(question_rrs);  // Questions
        buffer.putShort(answer_rrs);  // Answer RRs
        buffer.putShort(authority_rrs);  // Authority RRs
        buffer.putShort(additional_rrs);  // Additional RRs

        // Fill the question section
        for (String label : question.split("\\.")) {
            buffer.put((byte) label.length());
            buffer.put(label.getBytes());
        }
        buffer.put((byte) 0);
        buffer.putShort((short) 1);  // Type: A
        buffer.putShort((short) 1);  // Class: IN

        // Fill the answer section
        String name = answer;
        for (String label : name.split("\\.")) {
            buffer.put((byte) label.length());
            buffer.put(label.getBytes());
        }
        buffer.put((byte) 0);
        buffer.putShort((short) 0);
        buffer.putShort((short) 1);  // Class: IN
        buffer.putInt(0);
        buffer.putShort((short) 0);
        buffer.put((byte) 0);

        return buffer.array();
    }

    public short getId() {
        return id;
    }

    public short getFlag() {
        return flag;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public short getQuestion_rrs() {
        return question_rrs;
    }

    public void setQuestion_rrs(short question_rrs) {
        this.question_rrs = question_rrs;
    }

    public short getAnswer_rrs() {
        return answer_rrs;
    }

    public void setAnswer_rrs(short answer_rrs) {
        this.answer_rrs = answer_rrs;
    }

    public short getAuthority_rrs() {
        return authority_rrs;
    }

    public void setAuthority_rrs(short authority_rrs) {
        this.authority_rrs = authority_rrs;
    }

    public short getAdditional_rrs() {
        return additional_rrs;
    }

    public void setAdditional_rrs(short additional_rrs) {
        this.additional_rrs = additional_rrs;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }
}
