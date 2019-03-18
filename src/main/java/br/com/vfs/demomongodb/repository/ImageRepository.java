package br.com.vfs.demomongodb.repository;

import br.com.vfs.demomongodb.dto.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, String> {
}
