package kit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IODealer {
    /*
     * 格式：长度+数据  (￣▽￣)"
     */
    public static void send(Socket socket, DataPackage dataPackage, boolean isClose){
        try {
            byte[] datum = ClassConverter.getBytesFromObject(dataPackage);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(datum.length);
            outputStream.flush();

            System.out.println("发送数据长度 " + datum.length);

            outputStream.write(datum);
            outputStream.flush();

            if(isClose) socket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
            ShowDialog.showAlert("sending data error");
        }
    }

    public static DataPackage receive(Socket socket){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];

        try {
            InputStream inputStream = socket.getInputStream();
            int length = inputStream.read();

            System.out.println("接受数据长度 " + length);

            while(inputStream.read(bytes) != -1){
                byteArrayOutputStream.write(bytes);
                if(byteArrayOutputStream.toByteArray().length >= length) break;
            }

            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return (DataPackage) ClassConverter.getObjectFromBytes(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            ShowDialog.showAlert("receive data error");
        }
        return null;
    }
}
