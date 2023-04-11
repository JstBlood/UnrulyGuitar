package server.api;

import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.status(service.uploadFile(file))
                .build();
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> outputFile(@PathVariable String fileName){
        return service.outputFile(fileName);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        return ResponseEntity.status(service.deleteFile(fileName))
                .build();
    }
}
