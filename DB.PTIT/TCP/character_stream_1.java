package TCP;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class character_stream_1 {
    public static void main(String[] args) {
        String serverIp = "36.50.135.242";
        int port = 2208;

        String studentCode = "B23DCCN238"; 
        String qCode = "ypCRLSAM";

        try {
            Socket socket = new Socket(serverIp, port);
            socket.setSoTimeout(5000); // Set timeout to 5 seconds

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write(studentCode + ";" + qCode);
            writer.newLine();
            writer.flush();

            String receivedStr = reader.readLine();
            if (receivedStr != null && !receivedStr.trim().isEmpty()) {
                String[] domains = receivedStr.split(",");
                List<String> eduDomains = new ArrayList<>();

                for (String domain : domains) {
                    domain = domain.trim();
                    if (domain.endsWith(".edu")) {
                        eduDomains.add(domain);
                    }
                }

                String result = String.join(", ", eduDomains);

                writer.write(result);
                writer.newLine();
                writer.flush();
            }

            reader.close();
            writer.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
