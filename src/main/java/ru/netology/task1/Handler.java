package ru.netology.task1;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class Handler implements Runnable {

    private static final Map<Integer, String> MESSAGES = new HashMap<>() {{
        put( HttpURLConnection.HTTP_NOT_FOUND, "NOT FOUND");
    }};

    private static final List<String> FILES_WITH_TEMPLATES = new ArrayList<>() {{
        add("classic.html");
    }};

    private final Socket socket;

    private final String directory;

    public Handler(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try (
                InputStream in = this.socket.getInputStream();
                OutputStream out = this.socket.getOutputStream()
                ) {
            int code = HttpURLConnection.HTTP_OK;

            String url = this.getRequestUrl(in);
            if (url == null) return;
            Path filePath = Path.of(this.directory, url);
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                String type = Files.probeContentType(filePath);
                byte[] fileBytes = processFileAsTemplate(filePath);
                long length = fileBytes != null ? fileBytes.length : Files.size(filePath);
                this.sendHeader(out, code, "OK", type, length);
                if (fileBytes != null) {
                    out.write(fileBytes);
                } else  {
                    Files.copy(filePath, out);
                }
            } else {
                // NOT FOUND
                code = HttpURLConnection.HTTP_NOT_FOUND;
                String msg = MESSAGES.get(code);
                this.sendHeader(out, code, msg, "text/plain", msg.length());
                out.write(msg.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] processFileAsTemplate(Path filePath) throws IOException {
        if (FILES_WITH_TEMPLATES.contains(filePath.getFileName().toString())) {
            List<String> lines = Files.readAllLines(filePath);
            StringBuilder sb = new StringBuilder();
            for (String line: lines) {
                sb.append(line.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ));
            }
            return sb.toString().getBytes();
        } else {
            return null;
        }
    }

    private String getRequestUrl(InputStream in) {
        Scanner sc = new Scanner(in).useDelimiter("\r\n");
        String[] pars = sc.next().split(" ");
        return pars.length == 3 ? pars[1] : null;
    }


    private void sendHeader(OutputStream out, int statusCode, String statusText, String type, long length) {
        PrintStream ps = new PrintStream(out);
        ps.printf("HTTP/1.1 %s %s%n", statusCode, statusText);
        ps.printf("Content-Type: %s%n", type);
        ps.print("Connection: close%n");
        ps.printf("Content-Length:%s%n%n", length);
    }
}
