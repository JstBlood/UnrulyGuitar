package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.services.StorageService;

import java.io.IOException;

@RestController
@RequestMapping(value = {"/secure/{username}/{password}/file", "/secure/{username}/file"})
public class FileController {
    @Autowired
    private StorageService service;
    @PostMapping("/add")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadFile = service.uploadFile(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadFile);
    }
    @GetMapping("/{fileName}")
    public ResponseEntity<?> outputFile(@PathVariable String fileName){
        byte[] fileData=service.outputFile(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(service.getType(fileName)))
                .body(fileData);
    }
    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        String message = service.deleteFile(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }
}
