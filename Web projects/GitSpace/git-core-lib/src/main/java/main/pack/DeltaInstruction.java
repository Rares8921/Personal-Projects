package main.pack;

import java.util.Objects;

/**
 * Represents a single instruction within a deltified object.
 * A delta is just a list of these instructions, which are either
 * "copy" (from the base object) or "insert" (new data).
 */
public sealed interface DeltaInstruction permits DeltaInstruction.Copy, DeltaInstruction.Insert {

    /**
     * Represents a "copy" instruction.
     * This tells the parser to copy 'size' bytes from the
     * base object, starting at 'offset'.
     *
     * @param offset The starting position in the base object's data. Must be non-negative.
     * @param size The number of bytes to copy. Must be non-negative.
     */
    record Copy(
            int offset,
            int size
    ) implements DeltaInstruction {
        public Copy {
            if (offset < 0) {
                throw new IllegalArgumentException("Copy offset cannot be negative: " + offset);
            }
            if (size < 0) {
                throw new IllegalArgumentException("Copy size cannot be negative: " + size);
            }
        }
    }

    /**
     * Represents an "insert" instruction.
     * This tells the parser to insert the given 'data'
     * directly into the new object's stream.
     *
     * @param data The new bytes to be inserted. Cannot be null.
     */
    record Insert(
            byte[] data
    ) implements DeltaInstruction {
        public Insert {
            Objects.requireNonNull(data, "Insert data cannot be null");
        }
    }

    /**
     * Factory method to create a new Copy instruction.
     *
     * @param offset The starting position in the base object.
     * @param size The number of bytes to copy.
     * @return A new Copy instance.
     */
    static Copy copy(int offset, int size) {
        return new Copy(offset, size);
    }

    /**
     * Factory method to create a new Insert instruction.
     *
     * @param data The new bytes to be inserted.
     * @return A new Insert instance.
     */
    static Insert insert(byte[] data) {
        return new Insert(data);
    }
}