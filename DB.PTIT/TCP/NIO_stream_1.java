package TCP;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NIO_stream_1 {

    public static void main(String[] args) {
        String serverIp = "36.50.135.242"; //
        int port = 2211; //
        
        String studentCode = "B23DCCN238"; // Thay bằng Mã sinh viên của bạn
        String qCode = "Gdr1RVoG";        // Mã câu hỏi từ hệ thống[cite: 1]

        try (SocketChannel channel = SocketChannel.open()) {
            // Kết nối tới Server
            channel.connect(new InetSocketAddress(serverIp, port));
            channel.finishConnect();

            // a. Gửi mã sinh viên và mã câu hỏi: "studentCode;qCode"
            String initialMsg = studentCode + ";" + qCode; //[cite: 1]
            sendFrame(channel, initialMsg);

            // b. Nhận đúng 3 frame liên tiếp từ server
            StringBuilder httpRequest = new StringBuilder();
            for (int i = 0; i < 3; i++) { //[cite: 1]
                String payload = readFrame(channel);
                httpRequest.append(payload);
            }

            String fullHttpRequest = httpRequest.toString();

            // c. Trích xuất METHOD, PATH (bao gồm query string) và HOST từ HTTP request
            String result = parseHttpRequest(fullHttpRequest);

            // Gửi lại kết quả theo định dạng "METHOD;PATH;HOST"
            sendFrame(channel, result);

            // d. Đóng kết nối (được tự động xử lý bởi try-with-resources)[cite: 1]

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đọc chính xác n byte từ SocketChannel vào ByteBuffer
     */
    private static void readFully(SocketChannel channel, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                throw new IOException("Kết nối bị đóng trước khi đọc đủ dữ liệu");
            }
        }
    }

    /**
     * Đọc 1 frame: 4 byte độ dài (int32) + payload (UTF-8)
     */
    private static String readFrame(SocketChannel channel) throws IOException {
        // Đọc 4 byte độ dài
        ByteBuffer lenBuffer = ByteBuffer.allocate(4);
        readFully(channel, lenBuffer);
        lenBuffer.flip();
        int length = lenBuffer.getInt();

        // Đọc payload với độ dài thu được
        ByteBuffer payloadBuffer = ByteBuffer.allocate(length);
        readFully(channel, payloadBuffer);
        payloadBuffer.flip();

        byte[] bytes = new byte[payloadBuffer.remaining()];
        payloadBuffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Gửi 1 frame: 4 byte độ dài + payload
     */
    private static void sendFrame(SocketChannel channel, String message) throws IOException {
        byte[] payloadBytes = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + payloadBytes.length);
        
        buffer.putInt(payloadBytes.length); // 4 byte độ dài (int32)[cite: 1]
        buffer.put(payloadBytes);          // Payload (UTF-8)[cite: 1]
        
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    /**
     * Phân tích HTTP request để trích xuất METHOD, PATH, HOST
     */
    private static String parseHttpRequest(String request) {
        String method = "";
        String path = "";
        String host = "";

        String[] lines = request.split("\r\n"); //[cite: 1]
        if (lines.length > 0) {
            // Dòng đầu tiên dạng: GET /path/to/resource?query=123 HTTP/1.1
            String[] firstLineTokens = lines[0].trim().split("\\s+");
            if (firstLineTokens.length >= 2) {
                method = firstLineTokens[0];
                path = firstLineTokens[1]; // PATH bao gồm query-string[cite: 1]
            }
        }

        // Tìm header Host
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.toLowerCase().startsWith("host:")) {
                host = line.substring(5).trim();
                break;
            }
        }

        return method + ";" + path + ";" + host; //[cite: 1]
    }
}