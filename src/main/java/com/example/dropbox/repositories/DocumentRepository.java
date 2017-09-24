package com.example.dropbox.repositories;

import com.example.dropbox.models.Document;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by mrinalbhambhu on 23/09/2017.
 */
public interface DocumentRepository extends CrudRepository<Document, Long> {

    List<Document> findByUserId(int user_id);

    List<Document> findByUserIdAndParentIdAndIsDeleted(int user_id, Long parent_id, int isDeleted);

    List<Document> findByParentIdAndIsDeleted(Long parent_id, int isDeleted);

    Document findBySlug(String slug);


}
