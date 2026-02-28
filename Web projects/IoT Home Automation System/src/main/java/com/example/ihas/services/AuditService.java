package com.example.ihas.services;

import com.example.ihas.models.Audit;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class AuditService {

    private static final String CSV_FILE = "audit.csv";
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private final BlockingQueue<Audit> auditQueue = new LinkedBlockingQueue<>();
    private Thread writerThread;
    private volatile boolean running = true;

    public void log(String action) {
        try {
            auditQueue.put(new Audit(action, LocalDateTime.now()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while logging audit", e);
        }
    }

    @PostConstruct
    public void startWriter() {
        writerThread = new Thread(() -> {
            while (running || !auditQueue.isEmpty()) {
                try {
                    Audit audit = auditQueue.take();
                    rotateIfNeeded();
                    writeToCsv(audit);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        writerThread.setDaemon(true);
        writerThread.start();
    }

    @PreDestroy
    public void stopWriter() {
        running = false;
        writerThread.interrupt();
    }

    private synchronized void writeToCsv(Audit audit) {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            writer.write(String.format("%s,%s\n", audit.getAction(), audit.getTimestamp()));
        } catch (IOException e) {
            throw new RuntimeException("Could not write to audit log file", e);
        }
    }

    private synchronized void rotateIfNeeded() {
        File file = new File(CSV_FILE);
        if (file.exists() && file.length() >= MAX_FILE_SIZE_BYTES) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File archiveFile = new File("audit-" + timestamp + ".csv");
            if (!file.renameTo(archiveFile)) {
                throw new RuntimeException("Could not archive audit file");
            }
        }
    }
}
