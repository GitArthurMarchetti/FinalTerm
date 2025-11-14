package com.arthur.marchetti.repo;

import com.arthur.marchetti.interfaces.TaxCalculator.ReceiptRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileReceiptRepository implements ReceiptRepository {
    private final Path dir;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public FileReceiptRepository(Path dir){ this.dir = dir; }

    @Override
    public Path save(List<String> lines) throws IOException {
        if (lines == null || lines.isEmpty()) throw new IllegalArgumentException("lines required");
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String name = "receipt_" + LocalDateTime.now().format(fmt) + ".txt";
        Path file = dir.resolve(name);
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        return file;
    }
}
