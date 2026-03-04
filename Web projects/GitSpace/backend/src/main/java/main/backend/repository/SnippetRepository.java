package main.backend.repository;

import main.backend.model.Snippet;
import main.backend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SnippetRepository extends JpaRepository<Snippet, Long> { // Issue 8
//    List<Snippet> findByIsPublicTrue();
//    List<Snippet> findByOwnerId(Long ownerId);
//    List<Snippet> findByIsPublicTrueOrderByIdDesc();
//    List<Snippet> findByOwnerUsername(String username);
    void deleteAllByOwner(User owner);
    List<Snippet> findByIsPublicTrue(Pageable pageable);
}
