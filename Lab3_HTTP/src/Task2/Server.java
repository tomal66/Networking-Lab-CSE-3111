import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server{
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(9000), 0);
        server.createContext("/dl", new GetFileHandler());
        server.createContext("/save", new PostHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); // creates a default executor
        server.start();
        System.out.println("Server started on port 9000");
    }

    static class GetFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            // Create a new thread to handle the request
            new Thread(() -> {
                try {
                    String requestURI = t.getRequestURI().toString();
                    String filename = "Files/"+requestURI.substring(requestURI.lastIndexOf("/") + 1);
                    File file = new File(filename);
                    String type = Files.probeContentType(file.toPath());
                    byte[] fileBytes = new byte[(int) file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(fileBytes);
                    fis.close();

                    t.sendResponseHeaders(200, fileBytes.length);
                    t.getResponseHeaders().set("Content-Type", type);
                    OutputStream os = t.getResponseBody();
                    os.write(fileBytes);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    static class PostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            new Thread(() -> {
                try{
                    if (exchange.getRequestMethod().equals("POST")) {
                        InputStream inputStream = exchange.getRequestBody();

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        String fileName = extractFileName(exchange.getRequestHeaders().getFirst("Content-Disposition"));
                        FileOutputStream fileOutputStream = new FileOutputStream("Uploads/" + fileName);
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        fileOutputStream.close();

                        exchange.sendResponseHeaders(200, 0);
                        exchange.getResponseBody().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        private String extractFileName(String contentDisposition) {
            return contentDisposition.split("filename=")[1].replace("\"", "");
        }
    }
}
