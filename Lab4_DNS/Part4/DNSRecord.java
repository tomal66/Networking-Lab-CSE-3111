public class DNSRecord {
    private String name;
    private String value;
    private String type;
    private String TTL;

    public DNSRecord(String name, String value, String type, String TTL) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.TTL = TTL;
    }

    public DNSRecord(String line)
    {
        String[] parts = line.split("\\s+");
        if(parts.length>1)
        {
            name = parts[0];
            value = parts[1];
            type = parts[2];
            TTL = parts[3];
        }
        else
        {
            name = "";
            value = parts[0];
            type = "";
            TTL = "";
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTTL() {
        return TTL;
    }

    public void setTTL(String TTL) {
        this.TTL = TTL;
    }

    public void print(){
        System.out.println(this);
    }

    public String toString(){
        return name+"  "+value+"  "+type+"  "+TTL;
    }

}
