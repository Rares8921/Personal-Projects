package main.domain.serial;

import main.domain.Blob;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Handles the serialization and deserialization for a Git Blob object.
 * A Blob's content is simply its raw data, so this class
 * reads/writes the raw byte stream.
 */
public class BlobSerializer implements ObjectContentSerializer<Blob> {

    /**
     * Writes the raw byte data from the Blob object directly
     * to the output stream.
     *
     * @param blob The Blob object to serialize.
     * @param dataOutputStream The stream to write to.
     */
    @Override
    public void serialize(Blob blob, DataOutputStream dataOutputStream) throws IOException {
        Objects.requireNonNull(blob, "Blob cannot be null");
        Objects.requireNonNull(dataOutputStream, "OutputStream cannot be null");

        dataOutputStream.write(blob.data());
    }

    /**
     * Reads all available bytes from the input stream and constructs
     * a new Blob object from them.
     *
     * @param dataInputStream The stream to read from.
     * @return The newly deserialized Blob object.
     */
    @Override
    public Blob deserialize(DataInputStream dataInputStream) throws IOException {
        Objects.requireNonNull(dataInputStream, "InputStream cannot be null");

        final var data = dataInputStream.readAllBytes();
        return new Blob(data);
    }

}
