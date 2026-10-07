package TCP;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NIO_stream_2 {

    public static void main(String[] args) {
        String serverIp = "36.50.135.242"; // Server IP trong đề bài
        int port = 2211;                   // Cổng kết nối

        String studentCode = "B23DCCN238"; // Thay bằng Mã sinh viên thực tế của bạn
        String qCode = "ayV12eko";        // Mã câu hỏi hiện tại

        try (SocketChannel channel = SocketChannel.open()) {
            // Kết nối tới Server
            channel.connect(new InetSocketAddress(serverIp, port));
            channel.finishConnect();

            // a. Gửi mã sinh viên và mã câu hỏi: "studentCode;qCode"
            String initialMsg = studentCode + ";" + qCode;
            sendFrame(channel, initialMsg);

            // b. Nhận đúng 2 frame liên tiếp từ server và ghép lại thành chuỗi JSON hoàn chỉnh
            StringBuilder jsonBuilder = new StringBuilder();
            for (int i = 0; i < 2; i++) {
                String payload = readFrame(channel);
                jsonBuilder.append(payload);
            }

            String fullJson = jsonBuilder.toString();

            // c. Trích xuất các trường event, user, ok từ chuỗi JSON
            String result = parseJsonAndFormat(fullJson);

            // Gửi lại lên server theo định dạng: event=<event>;user=<user>;ok=<0|1>
            sendFrame(channel, result);

            // d. Đóng kết nối (được tự động xử lý bởi try-with-resources)[cite: 2]

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

        // Đọc payload với độ dài tương ứng
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

        buffer.putInt(payloadBytes.length); // 4 byte độ dài (int32)[cite: 2]
        buffer.put(payloadBytes);          // Payload (UTF-8)[cite: 2]

        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    /**
     * Trích xuất các trường event, user, ok từ chuỗi JSON đơn giản và trả về định dạng theo yêu cầu
     */
    private static String parseJsonAndFormat(String json) {
        String event = extractValue(json, "event");
        String user = extractValue(json, "user");
        String okRaw = extractValue(json, "ok");

        // Chuyển đổi boolean: true -> 1, false -> 0
        String ok = "0";
        if ("true".equalsIgnoreCase(okRaw) || "1".equals(okRaw)) {
            ok = "1";
        }

        return String.format("event=%s;user=%s;ok=%s", event, user, ok);
    }

    /**
     * Hàm dùng Regex đơn giản để trích xuất giá trị theo key trong JSON
     */
    private static String extractValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\"[^\"]*\"|true|false|\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String val = matcher.group(1);
            // Nếu giá trị có dấu ngoặc kép, loại bỏ dấu ngoặc kép
            if (val.startsWith("\"") && val.endsWith("\"")) {
                return val.substring(1, val.length() - 1);
            }
            return val;
        }
        return "";
    }
}