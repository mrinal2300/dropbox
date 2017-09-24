package com.example.dropbox.controllers;

import com.example.dropbox.models.Document;
import com.example.dropbox.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

@Controller
public class DocumentController {

    @Autowired
    DocumentRepository documentRepository;

    @Value("${dropbox.path}")
    private String dropboxPath;

    @RequestMapping(value="/document/download/{slug}", method= RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> downloadDocument(@PathVariable(name = "slug") String slug) {

        Document document = documentRepository.findBySlug(slug);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        if (document.getType().equals("file")){


            String filepath = Paths.get(dropboxPath, document.getName()).toString();
            System.out.println(filepath);

            File file = new File(filepath);

            InputStreamResource resource = null;
            try {
                resource = new InputStreamResource(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);


        } else {
            System.out.println("i am foldr");
            return null;
        }
    }

}
