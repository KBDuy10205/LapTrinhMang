package TCP;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TCPClientGZIP {

    private static final String SERVER_IP = "36.50.135.242";
    private static final int SERVER_PORT = 2210;

    private static final String STUDENT_CODE = "B23DCCN238";
    private static final String QUESTION_CODE = "u4fVc7yC";

    public static void main(String[] args) {

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {

            socket.setSoTimeout(5000);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            // =====================================================
            // a. Gửi studentCode;qCode
            // =====================================================

            String request = STUDENT_CODE + ";" + QUESTION_CODE;

            GZIPOutputStream gzipOut = new GZIPOutputStream(output);

            byte[] requestBytes =
                    (request + "\n").getBytes(StandardCharsets.UTF_8);

            gzipOut.write(requestBytes);

            // Hoàn tất GZIP stream
            gzipOut.finish();

            // Đảm bảo toàn bộ dữ liệu đã được ghi xuống socket
            gzipOut.flush();

            System.out.println("Đã gửi: " + request);


            // =====================================================
            // b. Nhận dữ liệu từ server
            // =====================================================

            GZIPInputStream gzipIn = new GZIPInputStream(input);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] temp = new byte[1024];

            int n;

            while ((n = gzipIn.read(temp)) != -1) {
                buffer.write(temp, 0, n);
            }

            String received =
                    buffer.toString(StandardCharsets.UTF_8).trim();

            System.out.println("Server gửi: " + received);


            // =====================================================
            // c. Đảo ngược chuỗi
            // =====================================================

            String reversed =
                    new StringBuilder(received)
                            .reverse()
                            .toString();

            String base64 =
                    Base64.getEncoder()
                            .encodeToString(
                                    reversed.getBytes(StandardCharsets.UTF_8)
                            );

            String result = reversed + "|" + base64;

            System.out.println("Chuỗi đảo ngược: " + reversed);
            System.out.println("Base64: " + base64);
            System.out.println("Gửi server: " + result);


            // =====================================================
            // Gửi kết quả
            // =====================================================

            GZIPOutputStream gzipOut2 =
                    new GZIPOutputStream(output);

            gzipOut2.write(
                    (result + "\n").getBytes(StandardCharsets.UTF_8)
            );

            gzipOut2.finish();
            gzipOut2.flush();

            System.out.println("Đã gửi kết quả.");
            System.out.println("Hoàn thành.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}