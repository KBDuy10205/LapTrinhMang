package TCP;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class byte_stream_client_1 {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int port = 2206;

        String studentCode = "B23DCCN238";
        String qCode = "IFuAyG8I";

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
            byte[] buffer = new byte[2048];
            int bytesRead = in.read(buffer); //doc du lieu tu luong vao
            if (bytesRead != -1) {
                String receivedStr = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();

                // c. Xử lý tìm khoảng cách nhỏ nhất và 2 giá trị lớn nhất tạo nên khoảng cách đó
                String[] parts = receivedStr.split(",");
                int[] numbers = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    numbers[i] = Integer.parseInt(parts[i].trim());
                }

                // Sắp xếp mảng tăng dần để dễ dàng tìm khoảng cách nhỏ nhất
                Arrays.sort(numbers);

                int minDiff = Integer.MAX_VALUE;
                int num1 = 0;
                int num2 = 0;

                // Tìm cặp số liền kề có hiệu nhỏ nhất
                for (int i = 0; i < numbers.length - 1; i++) {
                    int diff = numbers[i + 1] - numbers[i];
                    // Ưu tiên chọn cặp số lớn hơn nếu khoảng cách bằng nhau (diff <= minDiff)
                    if (diff <= minDiff) {
                        minDiff = diff;
                        num1 = numbers[i];
                        num2 = numbers[i + 1];
                    }
                }

                // Định dạng chuỗi kết quả: "khoảng cách nhỏ nhất,số thứ nhất,số thứ hai"
                String result = minDiff + "," + num1 + "," + num2;

                // Gửi kết quả lên server
                out.write(result.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
            in.close();
            out.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
