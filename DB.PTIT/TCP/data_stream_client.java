package TCP;
import java.io.*;
import java.net.*;

public class data_stream_client {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int port = 2207;

        String studentCode = "B23DCCN238";
        String qCode = "K6pNG5ZR";

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
            int a = input.readInt();
            int b = input.readInt();

            System.out.println("Received from server: a = " + a + ", b = " + b);
            int sum = a + b;
            int product = a * b;

            output.writeInt(sum);
            output.writeInt(product);
            output.flush();

            System.out.println("Sent to server: sum = " + sum + ", product = " + product);

            input.close();
            output.close();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
