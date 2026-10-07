package TCP;
import java.io.*;
import java.net.*;

import TCP.TCP.Laptop;

public class object_stream_client_1 {
        public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int port = 2209;

        String studentCode = "B23DCCN238";
        String qCode = "j1Zy1OSm";

        try (Socket socket = new Socket(serverAddress, port); 
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // Gui ma sinh vien va ma cau hoi den server
            output.writeObject(studentCode + ";" + qCode);
            output.flush();

            // Nhan phan hoi tu server
            Laptop laptop = (Laptop)input.readObject();
            System.out.println("Tên ban đầu: " + laptop.getName());
            System.out.println("Số lượng ban đầu: " + laptop.getQuantity());

            // Sua ten 
            String originalName = laptop.getName().trim();
            String[] words = originalName.split("\\s+");

            if(words.length > 0) {
                String temp = words[0];
                words[0] = words[words.length - 1];
                words[words.length - 1] = temp;
                laptop.setName(String.join(" ", words));
            }

            // Sua so luong
            String quantityStr = String.valueOf(laptop.getQuantity());
            String reversedQuantityStr = new StringBuilder(quantityStr).reverse().toString();
            laptop.setQuantity(Integer.parseInt(reversedQuantityStr));

            System.out.println("Tên sau khi sửa: " + laptop.getName());
            System.out.println("Số lượng sau khi sửa: " + laptop.getQuantity());

            // Gui lai doi tuong Laptop da sua den server
            output.writeObject(laptop);
            output.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
