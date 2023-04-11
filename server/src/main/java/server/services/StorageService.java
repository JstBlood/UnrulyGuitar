package server.services;

import commons.FileData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.database.StorageRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class StorageService {
    @Autowired
    private StorageRepository repository;


    public HttpStatus uploadFile(MultipartFile file) throws IOException {
        repository.save(new FileData(file.getOriginalFilename(),
                file.getContentType(), compressFile(file.getBytes())));
        return HttpStatus.CREATED;
    }

    public ResponseEntity<byte[]> outputFile(String fileName){
        Optional<FileData> dbFileData = repository.findByName(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(getType(fileName)))
                .body(decompressFile(dbFileData.get().getFileData()));

    }


    public HttpStatus deleteFile(String fileName) {
        Optional<FileData> dbFileData = repository.findByName(fileName);
        if (dbFileData.isPresent()) {
            repository.delete(dbFileData.get());
            return HttpStatus.OK;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }

    public String getType(String fileName){
        Optional<FileData> dbFileData = repository.findByName(fileName);
        if (dbFileData.isPresent()) {
            return dbFileData.get().getType();
        } else {
            return "File not found: " + fileName;
        }
    }

    public static byte[] compressFile(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressFile(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }
}
