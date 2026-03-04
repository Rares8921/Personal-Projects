package main.domain;

import java.util.List;
import java.util.Objects;

public record Commit(
        String treeHash,
        List<String> parents,
        AuthorSignature author,
        AuthorSignature committer,
        String message
) implements GitObject {

    public Commit {
        Objects.requireNonNull(treeHash, "Tree hash cannot be null");
        Objects.requireNonNull(author, "Author cannot be null");
        Objects.requireNonNull(committer, "Committer cannot be null");

        // Immutable list
        parents = List.copyOf(parents);

        if (message == null) message = "";
    }

    @Override
    public ObjectType type() {
        return ObjectType.COMMIT;
    }

    // backward comp. ?
    public String parentHash() {
        return parents.isEmpty() ? null : parents.get(0);
    }
}
