package kit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
class BytesUtil {
    public static int bytes2Int(byte[] bytes) {
        int result = 0;
        //将每个byte依次搬运到int相应的位置
        result = bytes[0] & 0xff;
        result = result << 8 | bytes[1] & 0xff;
        result = result << 8 | bytes[2] & 0xff;
        result = result << 8 | bytes[3] & 0xff;
        return result;
    }

    public static byte[] int2Bytes(int num) {
        byte[] bytes = new byte[4];
        //通过移位运算，截取低8位的方式，将int保存到byte数组
        bytes[0] = (byte)(num >>> 24);
        bytes[1] = (byte)(num >>> 16);
        bytes[2] = (byte)(num >>> 8);
        bytes[3] = (byte)num;
        return bytes;
    }
}
public class IODealer {
    /*
     * 格式：长度+数据  (￣▽￣)"
     */

    public static void send(Socket socket, DataPackage dataPackage, boolean isClose){
        try {
            byte[] datum = ClassConverter.getBytesFromObject(dataPackage);
            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(BytesUtil.int2Bytes(datum.length));
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
            byte[] bt = new byte[4];
            int length;
            while (true){
                inputStream.read(bt);
                length = BytesUtil.bytes2Int(bt);
                if(length != 0) break;
            }

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
