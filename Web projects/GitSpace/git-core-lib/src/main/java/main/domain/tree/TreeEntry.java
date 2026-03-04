package main.domain.tree;

import java.util.Objects;

public record TreeEntry(
        TreeEntryMode mode,
        String name,
        String hash
) implements Comparable<TreeEntry> {

    public TreeEntry {
        Objects.requireNonNull(mode, "Mode cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(hash, "Hash cannot be null");
    }

    /**
     * Compares two tree entries according to Git's specific sorting rules.
     * Git sorts tree entries by name bytes. However, there is a catch:
     * If an entry is a DIRECTORY, it is treated as if it ends with a '/' character.
     * This ensures that "foo" (file) comes before "foo" (directory) which acts like "foo/".
     */
    @Override
    public int compareTo(TreeEntry other) {
        final String thisName = this.name + (this.mode.type() == TreeEntryModeType.DIRECTORY ? "/" : "");
        final String otherName = other.name + (other.mode.type() == TreeEntryModeType.DIRECTORY ? "/" : "");

        return thisName.compareTo(otherName);
    }
}
