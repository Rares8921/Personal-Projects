package main.domain;

public sealed interface GitObject permits Blob, Commit, Tree {
    /**
     * Returns the strict type of the Git object.
     * Useful for dispatching logic without excessive instanceof checks.
     */
    ObjectType type();
}