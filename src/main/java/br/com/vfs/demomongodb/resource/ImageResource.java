package br.com.vfs.demomongodb.resource;

import br.com.vfs.demomongodb.dto.Image;
import br.com.vfs.demomongodb.repository.ImageRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImageResource {

    private final ImageRepository imageRepository;
    @Autowired
    public ImageResource(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity singleFileUpload(@RequestParam("file") MultipartFile multipart) {
        final String uuid = UUID.randomUUID().toString();
        try {
            final Image image = Image.builder()
                    .id(uuid)
                    .docType(multipart.getContentType())
                    .file(new Binary(BsonBinarySubType.BINARY.BINARY, multipart.getBytes()))
                    .build();
            imageRepository.save(image);
            System.out.println(image);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.created(URI.create(String.format("/images/%s", uuid))).build();
    }

    @GetMapping("/retrieve/{uuid}")
    public ResponseEntity retrieveFile(@PathVariable("uuid") String uuid){
        Image image = imageRepository.findById(uuid).orElseThrow(RuntimeException::new);
        System.out.println(image);
        List<String> list = Arrays.asList(image.getDocType().split("/"));
        final String type = list.get(list.size()-1);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getDocType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=image.%s", type))
                .body(image.getFile().getData());
    }
}
