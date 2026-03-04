package main.domain;

import main.domain.tree.TreeEntry;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public record Tree(
        List<TreeEntry> entries
) implements GitObject {

    public Tree {
        // Trees must be sorted by name byte sequence.
        var sortedEntries = new ArrayList<>(entries);
        Collections.sort(sortedEntries);

        // Immutable
        entries = Collections.unmodifiableList(sortedEntries);
    }

    @Override
    public ObjectType type() {
        return ObjectType.TREE;
    }
}