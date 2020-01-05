package kit;

import java.io.*;
import java.net.Socket;

public class IODealer {
    /*
     * 格式：长度+数据  (￣▽￣)"
     */
    private static final int WIDTH = 1024;

    public static void send(Socket socket, Data data, boolean isClose){
        DataOutputStream dataOutput;
        byte[] buffer;

        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());

            buffer = ClassConverter.getBytesFromObject(data);

            dataOutput.writeLong(buffer.length);

//            System.out.println("发送数据的长度： " + buffer.length);

            dataOutput.write(buffer);

            if(isClose) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ShowDialog.showAlert("sending data error");
        }
    }

    public static Data receive(Socket socket, boolean isClose){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataInputStream dataInput;
        byte[] buffer;
        long length;

        try {
            dataInput = new DataInputStream(socket.getInputStream());

            length = dataInput.readLong();

            if(length == 0) return null;

            buffer = new byte[WIDTH];

//            System.out.println("接受数据的长度： " + length);

            long current = 0;

            while(current < length){
                int tmp = dataInput.read(buffer);
                current += tmp;
                byteArrayOutputStream.write(buffer, 0, tmp);
            }

            if(isClose) {
                socket.close();
            }
//            System.out.println("接受数据byteArrayOutputStream长度: " + byteArrayOutputStream.toByteArray().length);
            return (Data) ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            ShowDialog.showAlert("receive data error");
        }
        return null;
    }
}
