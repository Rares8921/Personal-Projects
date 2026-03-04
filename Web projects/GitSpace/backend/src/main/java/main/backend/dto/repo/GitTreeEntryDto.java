package main.backend.dto.repo;

// type:
//DIRECTORY(0b0100), // mode 040000
//REGULAR_FILE(0b1000), // mode 100xxx
//SYMBOLIC_LINK(0b1010), // mode 120000
//GITLINK(0b1110); // mode 160000
public record GitTreeEntryDto(String name, String type, String hash, String mode) {}
