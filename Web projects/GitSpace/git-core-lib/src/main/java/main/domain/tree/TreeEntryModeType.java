package main.domain.tree;

/**
 * Represents the "type" part of a Git tree entry's mode.
 * This indicates whether the entry is a file, directory, symlink, or submodule (gitlink).
 * These are the 4 most significant bits of the 6-byte mode.
 */
public enum TreeEntryModeType {

    DIRECTORY(0b0100), // mode 040000
    REGULAR_FILE(0b1000), // mode 100xxx
    SYMBOLIC_LINK(0b1010), // mode 120000
    GITLINK(0b1110); // mode 160000

    private final int mask;

    /**
     * Private enum constructor.
     * @param value The 4-bit numeric mask.
     */
    TreeEntryModeType(int value) {
        this.mask = value;
    }

    public int mask() {
        return mask;
    }

    /**
     * Calculates the full 16-bit shifted value for this type
     * @return The mask shifted left by 12 bits.
     */
    public int shifted() {
        return mask << 12;
    }

    /**
     * Checks if this type (like DIRECTORY or GITLINK) ignore the permission bits (rwx).
     * @return true if only REGULAR_FILE has permissions.
     */
    public boolean isPermissionless() {
        return this != REGULAR_FILE;
    }

    /**
     * Find the correct type from a full 16-bit mode.
     *
     * @param value The full integer mode (e.g., 0100644 or 0040000).
     * @return The matching TreeEntryModeType.
     * @throws IllegalArgumentException if the mode is unknown.
     */
    public static TreeEntryModeType match(int value) {
        final int typeMask = (value >>> 12) & 0b1111;
        for(var type : values()) {
            if(type.mask == typeMask) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tree entry mode type: " + Integer.toBinaryString(typeMask));
    }
}