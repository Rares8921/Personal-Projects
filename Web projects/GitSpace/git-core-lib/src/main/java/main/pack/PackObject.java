package main.pack;

import main.domain.ObjectType;

import java.util.List;
import java.util.Objects;

/**
 * A sealed interface representing a single object read from a packfile.
 * An object can either be "Undeltified" (a complete, whole object)
 * or "Deltified" (a set of instructions for modifying a base object).
 */
@SuppressWarnings("rawtypes")
public sealed interface PackObject permits PackObject.Undeltified, PackObject.Deltified {

    /**
     * Represents a whole, non-delta object (e.g., COMMIT, TREE, BLOB).
     * This contains the full, uncompressed content.
     *
     * @param type The native Git object type (COMMIT, TREE, BLOB). Cannot be null.
     * @param content The raw, uncompressed content of the object. Cannot be null.
     */
    record Undeltified(
            ObjectType type,
            byte[] content
    ) implements PackObject {
        public Undeltified {
            Objects.requireNonNull(type, "Undeltified object type cannot be null");
            Objects.requireNonNull(content, "Undeltified content cannot be null");
        }
    }

    /**
     * Represents a delta object (OFS_DELTA or REF_DELTA).
     * This contains instructions to reconstruct the object from a base.
     *
     * @param baseHash The SHA-1 hash of the base object (for REF_DELTA). Cannot be null/empty.
     * @param size The expected size of the *new* object after patching. Must be non-negative.
     * @param instructions The list of (Copy, Insert) instructions. Immutable.
     */
    record Deltified(
            String baseHash,
            int size,
            List<DeltaInstruction> instructions
    ) implements PackObject {
        public Deltified {
            Objects.requireNonNull(baseHash, "Base hash cannot be null");
            if (baseHash.isBlank()) {
                throw new IllegalArgumentException("Base hash cannot be blank");
            }
            if (size < 0) {
                throw new IllegalArgumentException("Deltified object size cannot be negative: " + size);
            }

            instructions = List.copyOf(instructions);
        }
    }

    /**
     * @param type The native Git object type.
     * @param content The raw, uncompressed content.
     * @return A new Undeltified instance.
     */
    static Undeltified undeltified(ObjectType type, byte[] content) {
        return new Undeltified(type, content);
    }

    /**
     * @param baseHash The SHA-1 hash of the base object.
     * @param size The expected final size of the new object.
     * @param instructions The list of delta instructions.
     * @return A new Deltified instance.
     */
    static Deltified deltified(String baseHash, int size, List<DeltaInstruction> instructions) {
        return new Deltified(baseHash, size, instructions);
    }
}