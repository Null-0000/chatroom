package kit;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * convert a class into a byte vector
 * get a byte vector back to a class
 *
 * @author Furyton
 */

public class ClassConverter implements Serializable{
    public static Object getObjectFromBytes(byte[] objectBytes) throws Exception{
        if(objectBytes == null || objectBytes.length == 0){
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        return objectInputStream.readObject();
    }
    public static byte[] getBytesFromObject(Serializable object) throws Exception{
        if(object == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }
}