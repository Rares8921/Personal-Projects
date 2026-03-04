package main.backend.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import main.backend.dto.snippet.SnippetDto;
import main.backend.exception.ResourceNotFoundException;
import main.backend.model.Snippet;
import main.backend.model.User;
import main.backend.repository.SnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnippetService {
    private final SnippetRepository snippetRepository;

    @Autowired
    public SnippetService(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    @Transactional
    public SnippetDto create(User owner, SnippetDto dto) {
        Snippet snippet = new Snippet();
        snippet.setOwner(owner);
        snippet.setFilename(dto.filename());
        snippet.setDescription(dto.description());
        snippet.setContent(dto.content());
        snippet.setPublic(true);

        snippet.setLanguage(detectLanguage(dto.filename()));
        snippet.setCreatedAt(java.time.Instant.now());

        Snippet saved = snippetRepository.save(snippet);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SnippetDto> getPublic() {
        Pageable limit = PageRequest.of(0, 1000, Sort.by("id").descending());

        return snippetRepository.findByIsPublicTrue(limit).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SnippetDto get(Long id) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet not found"));
        return mapToDto(snippet);
    }

    private SnippetDto mapToDto(Snippet s) {
        String authorName = (s.getOwner() != null) ? s.getOwner().getUsername() : "Anonymous";
        return new SnippetDto(
                s.getId(),
                s.getDescription(),
                s.getFilename(),
                s.getContent(),
                s.isPublic(),
                authorName,
                s.getLanguage(),
                s.getCreatedAt()
        );
    }

    private String detectLanguage(String filename) {
        if (filename == null || !filename.contains(".")) return "plaintext";
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        return switch (ext.toLowerCase()) {
            case "java" -> "Java";
            case "ts" -> "TypeScript";
            case "js" -> "JavaScript";
            case "py" -> "Python";
            case "html" -> "HTML";
            case "css", "scss" -> "CSS";
            default -> ext;
        };
    }
}
