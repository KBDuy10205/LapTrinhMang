package TCP;
import java.io.*;
import java.net.*;

public class byte_stream_client_2 {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int port = 2206;

        String studentCode = "B23DCCN238";
        String qCode = "k0y2hEV3";
        try {
            //Khoi tao ket noi den server
            Socket socket = new Socket(serverAddress, port);
            socket.setSoTimeout(5000); // Set timeout to 5 seconds

            //Lay luong vao/ra dang byte stream
            InputStream in= socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            //Gui ma sinh vien va ma cau hoi den server
            String sendData = studentCode + ";" + qCode;
            out.write(sendData.getBytes()); //gui du lieu di
            out.flush(); //flush luong ra de dam bao du lieu duoc gui di ngay lap tuc

            //Nhan cau tra loi tu server
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer); //doc du lieu tu luong vao
            if (bytesRead != -1) {
                String receivedStr = new String(buffer, 0, bytesRead).trim(); //chuyen doi byte[] sang String
                System.out.println("Received from server: " + receivedStr);

                //Tin gia tri lon thu 2 va vi tri cua no
                String[] parts = receivedStr.split(",");
                int n = parts.length;
                int[] numbers = new int[n];

                for (int i = 0; i < n; i++) {
                    numbers[i] = Integer.parseInt(parts[i].trim());
                }

                int max1 = Integer.MIN_VALUE;
                int max2 = Integer.MIN_VALUE;
                int indexMax2 = -1;

                // Tìm max1 trước
                for (int num : numbers) {
                    if (num > max1) {
                        max1 = num;
                    }
                }

                // Tìm max2 (nhỏ hơn max1 và lớn nhất trong số còn lại)
                for (int i = 0; i < n; i++) {
                    if (numbers[i] < max1 && numbers[i] > max2) {
                        max2 = numbers[i];
                        indexMax2 = i; // Vị trí tính từ 0
                    }
                }

                String response = max2 + "," + indexMax2;
                System.out.println("Dữ liệu gửi lên Server: " + response);

                out.write(response.getBytes());
                out.flush();
            } else {
                System.out.println("No response from server.");
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
