package org.geektimes.cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * lzm add 2021-04-14 13:10:34
 * @Description: 对象序列化工具类
 */
public class SerializationObjUtil {
    private static Logger log = LoggerFactory.getLogger(SerializationObjUtil.class);

    public SerializationObjUtil() {
    }

    /**
     * @param value
     * @return
     * @return byte[]
     * @throws
     * @Description: 序列化对象
     */
    public static byte[] serialize(Object value) {
        ObjectOutputStream objectOutputStream = null;
        byte[] bytes = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(value);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        if (objectOutputStream != null)
            try {
                objectOutputStream.close();
            } catch (IOException ioexception) {
            }
        return bytes;
    }

    /**
     * @param byteArray
     * @return
     * @return Object
     * @throws
     * @Description: 反序列化对象
     */
    public static Object deserialize(byte[] byteArray) {
        ObjectInputStream objectInputStream = null;
        Object obj;
        try {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArray));
            Object result = objectInputStream.readObject();
            obj = result;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        if (objectInputStream != null)
            try {
                objectInputStream.close();
            } catch (IOException ioexception) {
            }
        return obj;
    }

    /**
     * @param in
     * @return
     * @return Object
     * @throws
     * @Description: 重载反序列化对象
     */
    public static <T> T deserialize(byte[] in, Class<T>... requiredType) {
        Object receiveObj = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            if (in != null) {
                byteArrayInputStream = new ByteArrayInputStream(in);
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                receiveObj = objectInputStream.readObject();
            }
        } catch (Exception e) {
            log.error("serialize error ", e);
        } finally {
            close(objectInputStream);
            close(byteArrayInputStream);
        }
        return (T) receiveObj;
    }

    private static void close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("close stream error", e);
            }
    }
}