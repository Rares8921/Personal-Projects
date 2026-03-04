package main.protocol;

import main.domain.Reference;
import java.io.IOException;
import java.util.List;

/**
 * Defines the contract for fetching data from a remote Git repository.
 * Implementations handle the specific transport protocol (HTTP vs SSH).
 */
public interface GitTransport extends AutoCloseable {

    /**
     * Connects to the remote and retrieves the list of references (branches/tags).
     */
    List<Reference> fetchReferences() throws IOException;

    /**
     * Negotiates and downloads the packfile data for a specific reference.
     */
    byte[] getPack(Reference reference) throws IOException;

    /**
     * Pushes a local commit and its associated objects to a remote repository.
     */
    void push(String remoteUrl, String commitHash, String refName, byte[] packData) throws IOException;

    @Override
    default void close() throws Exception {
        // Default no-op for transports that don't hold open resources (like HTTP)
    }
}
