package main.domain;

public record Blob(byte[] data) implements GitObject {

    public Blob {
        // data = data.clone();
    }

    @Override
    public ObjectType type() {
        return ObjectType.BLOB;
    }
}