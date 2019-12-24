package kit;

import java.io.*;
import java.net.Socket;

public class IODealer {
    /*
     * 格式：长度+数据  (￣▽￣)"
     */

    private static DataInputStream dataInput;
    private static DataOutputStream dataOutput;
    private static byte[] buffer;
    private static long length;
    private static int WIDTH = 1024;

    private static void setStream(Socket socket) throws IOException {
        dataInput = new DataInputStream(socket.getInputStream());
        dataOutput = new DataOutputStream(socket.getOutputStream());
    }
    public static void send(Socket socket, DataPackage dataPackage, boolean isClose){
        try {
            setStream(socket);

            buffer = ClassConverter.getBytesFromObject(dataPackage);

            dataOutput.writeLong(buffer.length);

            System.out.println("发送数据的长度： " + buffer.length);

            dataOutput.write(buffer);

            if(isClose) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ShowDialog.showAlert("sending data error");
        }
    }

    public static DataPackage receive(Socket socket, boolean isClose){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            setStream(socket);

            length = dataInput.readLong();

            if(length == 0) return null;

            buffer = new byte[WIDTH];

            System.out.println("接受数据的长度： " + length);

            long current = 0;

            while(current < length){
                int tmp = dataInput.read(buffer);
                current += tmp;
                byteArrayOutputStream.write(buffer, 0, tmp);
            }

            if(isClose) {
                socket.close();
            }

            return (DataPackage) ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            ShowDialog.showAlert("receive data error");
        }
        return null;
    }
}
