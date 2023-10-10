package it.gov.pagopa.mocker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.SneakyThrows;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ObjectRedisSerializer<T> implements RedisSerializer<T> {

    @SneakyThrows
    @Override
    public byte[] serialize(T value) throws SerializationException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
        ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
        objectOut.writeObject(value);
        objectOut.close();
        return baos.toByteArray();
    }

    @SneakyThrows
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes != null) {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipIn = new GZIPInputStream(bais);
            ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
            return (T) objectIn.readObject();
        } else {
            return null;
        }
    }
}