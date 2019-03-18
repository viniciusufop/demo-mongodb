package br.com.vfs.demomongodb.resource;

import br.com.vfs.demomongodb.dto.Image;
import br.com.vfs.demomongodb.repository.ImageRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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

}
