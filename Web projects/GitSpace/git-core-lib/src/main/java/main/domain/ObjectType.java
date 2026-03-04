package main.domain;

import lombok.Getter;
import main.domain.serial.BlobSerializer;
import main.domain.serial.CommitSerializer;
import main.domain.serial.ObjectContentSerializer;
import main.domain.serial.TreeSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A generic class that defines a specific type of Git object (Blob, Tree, Commit).
 * It holds the type's name (e.g., "blob") and its specific
 * serializer/deserializer.
 *
 * @param <T> The Java class this type represents (e.g., Blob.class).
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class ObjectType<T extends GitObject> {

    public static final ObjectType<Blob> BLOB = new ObjectType<>("blob", Blob.class, new BlobSerializer());
    public static final ObjectType<Tree> TREE = new ObjectType<>("tree", Tree.class, new TreeSerializer());
    public static final ObjectType<Commit> COMMIT = new ObjectType<>("commit", Commit.class, new CommitSerializer());
    public static final Collection<ObjectType> TYPES = List.of(BLOB, TREE, COMMIT);

    private final String name;
    private final Class<?> objectClass;
    private final ObjectContentSerializer<T> serializer;

    /**
     * Private constructor to create a new ObjectType definition.
     *
     * @param name        The plain-text name (e.g., "blob").
     * @param objectClass The corresponding Java class (e.g., Blob.class).
     * @param serializer  The serializer instance for this type.
     */
    private ObjectType(String name, Class<?> objectClass, ObjectContentSerializer<T> serializer) {
        this.name = Objects.requireNonNull(name);
        this.objectClass = Objects.requireNonNull(objectClass);
        this.serializer = Objects.requireNonNull(serializer);
    }

    /**
     * Serializes a GitObject into the full format required for writing to the ODB.
     * Format: {@code <type> <space> <size> <null_byte> <content>}
     *
     * @param object The object to serialize (e.g., a Blob instance).
     * @return A byte[] containing the full, header-included data.
     * @throws IOException If serialization fails.
     */
    public byte[] serialize(T object) throws IOException {
        byte[] content = serializeContent(object);

        String headerStr = name + " " + content.length + "\0";
        byte[] header = headerStr.getBytes(StandardCharsets.US_ASCII);

        ByteArrayOutputStream out = new ByteArrayOutputStream(header.length + content.length);
        out.write(header);
        out.write(content);

        return out.toByteArray();
    }

    /**
     * Serializes *only* the content of a GitObject.
     * (e.g., for a Blob, just the file bytes).
     *
     * @param object The object to serialize.
     * @return A byte[] containing just the object's content.
     * @throws IOException If serialization fails.
     */
    public byte[] serializeContent(T object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            serializer.serialize(object, dos);
        }
        return baos.toByteArray();
    }

    /**
     * Deserializes *only* the content of an object from a stream.
     * Assumes the header ("blob <size>\0") has already been read/stripped by the caller.
     *
     * @param dataInputStream The stream, positioned at the start of the content.
     * @return The parsed GitObject (e.g., a Blob).
     * @throws IOException If deserialization fails.
     */
    public T deserialize(DataInputStream dataInputStream) throws IOException {
        return serializer.deserialize(dataInputStream);
    }

    /**
     * Deserializes the content of an object from a full byte array.
     *
     * @param bytes A byte[] containing *only* the object's content.
     * @return The parsed GitObject.
     * @throws IOException If deserialization fails.
     */
    public T deserialize(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             DataInputStream dis = new DataInputStream(bais)) {
            return deserialize(dis);
        }
    }

    /**
     * Static factory method to find an ObjectType by its name.
     *
     * @param name The name to find (e.g., "blob").
     * @return The matching ObjectType (e.g., ObjectType.BLOB).
     * @throws IllegalArgumentException if the name is unknown.
     */
    public static ObjectType byName(String name) {
        for (ObjectType type : TYPES) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown object type name: " + name);
    }

    /**
     * Static factory method to find an ObjectType by its class.
     *
     * @param clazz The class to find (e.g., Blob.class).
     * @return The matching ObjectType (e.g., ObjectType.BLOB).
     * @throws IllegalArgumentException if the class is unknown.
     */
    public static ObjectType byClass(Class<? extends GitObject> clazz) {
        for (ObjectType type : TYPES) {

            if (type.objectClass.isAssignableFrom(clazz)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown object type class: " + clazz.getName());
    }
}