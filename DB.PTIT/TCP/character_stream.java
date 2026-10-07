package TCP;
import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class character_stream {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int port = 2208;

        String studentCode = "B23DCCN238";
        String qCode = "tXe0bpzy";

        try{
            //Khoi tao ket noi den server
            Socket socket = new Socket(serverAddress, port);
            socket.setSoTimeout(5000); // Set timeout to 5 seconds

            //Lay luong vao/ra dang character stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //Gui ma sinh vien va ma cau hoi den server
            String sendData = studentCode + ";" + qCode;
            writer.write(sendData); //gui du lieu di
            writer.newLine();
            writer.flush(); //flush luong ra de dam bao du lieu duoc gui di ngay lap tuc    

            //Nhan cau tra loi tu server
            String receivedStr = reader.readLine(); //doc du lieu tu luong vao
            if (receivedStr != null && !receivedStr.isEmpty()) {
                //Dem so lan xuat hien cua cac ki tu trong chuoi nhan duoc
                Map<Character, Integer> charCountMap = new LinkedHashMap<>();
                for (char c : receivedStr.toCharArray()) {
                    if (Character.isLetterOrDigit(c)) {
                        charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
                    }
                }

                //Ghep cac ki tu xuat hien nhieu lan
                StringBuilder result = new StringBuilder();
                for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
                    if (entry.getValue() > 1) {
                        result.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
                    }
                }

                //Gui ket qua ve server
                String respone = result.toString();
                System.out.println("Sending to server: " + respone);
                writer.write(respone);
                writer.newLine();
                writer.flush();
            }

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
}
