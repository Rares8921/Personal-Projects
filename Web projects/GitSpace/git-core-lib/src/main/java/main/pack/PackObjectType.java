package main.pack;

import main.domain.ObjectType;

/**
 * Represents the type of object *within* a packfile.
 * These are numeric codes (1-7) that differ slightly from
 * the standard Git object types (e.g., adding deltas).
 */
@SuppressWarnings("rawtypes")
public enum PackObjectType {

    /**
     * A standard commit object. (Type 1)
     */
    COMMIT(1, ObjectType.COMMIT),

    /**
     * A standard tree object. (Type 2)
     */
    TREE(2, ObjectType.TREE),

    /**
     * A standard blob (file content) object. (Type 3)
     */
    BLOB(3, ObjectType.BLOB),

    /**
     * A standard tag object. (Type 4) (Currently unsupported in parser)
     */
    TAG(4, null),

    /**
     * A delta referencing a previous object in the *same pack* by its offset. (Type 6)
     */
    OFS_DELTA(6, null),

    /**
     * A delta referencing a base object by its full SHA-1 hash. (Type 7)
     */
    REF_DELTA(7, null);

    /**
     * The numeric code (1-7) used in the packfile header.
     */
    private final int value;

    /**
     * The corresponding "native" Git object type (Commit, Tree, Blob)
     * if this is a non-delta object.
     */
    private final ObjectType nativeType;

    /**
     * @param value The numeric code (1-7).
     * @param nativeType The corresponding ObjectType, or null for deltas/tags.
     */
    PackObjectType(int value, ObjectType nativeType) {
        this.value = value;
        this.nativeType = nativeType;
    }

    /**
     * @return The integer value (1-7).
     */
    public int value() {
        return value;
    }

    /**
     * @return The corresponding ObjectType, or null.
     */
    public ObjectType nativeType() {
        return nativeType;
    }

    /**
     * @param value The integer code (1-7) read from the pack header.
     * @return The corresponding PackObjectType enum constant.
     * @throws IllegalArgumentException if the value is invalid.
     * @throws UnsupportedOperationException if the value is for an unsupported type (like TAG).
     */
    public static PackObjectType valueOf(int value) {
        return switch (value) {
            case 1 -> COMMIT;
            case 2 -> TREE;
            case 3 -> BLOB;
            case 4 -> throw new UnsupportedOperationException("TAG objects (type 4) are currently unsupported in this parser.");
            case 6 -> OFS_DELTA;
            case 7 -> REF_DELTA;
            default -> throw new IllegalArgumentException("Unknown or invalid pack object type code: " + value);
        };
    }
}
