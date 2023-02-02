import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client{

    public static void main(String[] args) throws IOException {
        sendGET();
        sendPOST();
    }

    private static void sendGET() throws IOException {
        String fileName = "main.cpp";
        URL url = new URL("http://localhost:9000/dl/"+fileName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {
            System.out.println("GET Response Code :: " + con.getResponseCode());
            InputStream inputStream = con.getInputStream();
            String saveFilePath = "Downloads/" + fileName;

            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            System.out.println("GET request did not work.");
        }
    }

    private static void sendPOST() throws IOException {

        String fileName  = "image.jpg";
        String filePath = "Files/"+fileName;
        File file = new File(filePath);
        Path path = Paths.get(filePath);
        String mimeType = Files.probeContentType(path);
        byte[] fileData = Files.readAllBytes(path);

        URL url = new URL("http://localhost:9000/save/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.setRequestProperty("Content-Type", mimeType);
        connection.setRequestProperty("Content-Disposition", "attachment; filename=" + fileName);
        connection.setRequestProperty("Content-Length", Integer.toString(fileData.length));

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(fileData);
        outputStream.close();

        int responseCode = connection.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);
        if(responseCode==200)
        {
            System.out.println("File Uploaded");
        }
        else
        {
            System.out.println("POST request did not work!");
        }

    }
}
