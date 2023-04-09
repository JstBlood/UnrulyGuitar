package server.database;

import commons.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<FileData,Long> {
    Optional<FileData> findByName(String fileName);
}
