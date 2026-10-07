package TCP;
import java.io.*;
import java.net.*;

public class data_stream_client_2 {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int port = 2207;

        String studentCode = "B23DCCN238";
        String qCode = "M5IdkoEo";

        try {
            // Khoi tao ket noi den server
            Socket socket = new Socket(serverAddress, port);
            socket.setSoTimeout(5000); // Set timeout to 5 seconds

            // Lay luong vao/ra dang data stream
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Gui ma sinh vien va ma cau hoi den server
            String sendData = studentCode + ";" + qCode;
            output.writeUTF(sendData); // gui du lieu di
            output.flush(); // flush luong ra de dam bao du lieu duoc gui di ngay

            // Nhan response tu server
            String response = input.readUTF();
            int s = input.readInt();
            System.out.println("Nhan chuoi ma hoa: " + response);
            System.out.println("Nhan s: " + s);

            // Giai ma hoa Caesar
            StringBuilder decrypted = new StringBuilder();

            for (char c : response.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    char decryptedChar = (char) ((c - 'A' - s + 26) % 26 + 'A');
                    decrypted.append(decryptedChar);
                } else if (Character.isLowerCase(c)) {
                    char decryptedChar = (char) ((c - 'a' - s) % 26 + 'a');
                    decrypted.append(decryptedChar);
                } else {
                    decrypted.append(c);
                }
            }

            
            String decryptedString = decrypted.toString();
            System.out.println("Chuoi giai ma: " + decryptedString);
            output.writeUTF(decryptedString);
            output.flush();

            input.close();
            output.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
