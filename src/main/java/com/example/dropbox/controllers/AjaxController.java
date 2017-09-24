package com.example.dropbox.controllers;

import com.example.dropbox.models.Document;
import com.example.dropbox.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by mrinalbhambhu on 23/09/2017.
 */
@RestController
public class AjaxController {

    @Autowired
    DocumentRepository documentRepository;

    @Value("${dropbox.path}")
    private String dropboxPath;



    @ResponseBody
    @RequestMapping(value = "ajax/document/savefolder", method = RequestMethod.POST)
    public boolean savefolder(@RequestParam(name = "name") String name,
                              @RequestParam(name = "parentId") Long parentId) {

        String folderId = UUID.randomUUID().toString();
        String folderPath = getFilePath();

        Document document = new Document();
        document.setName(name);
        document.setUserId(1);
        document.setSlug(folderId);
        document.setPath(folderPath);
        document.setType("folder");
        document.setParentId(parentId);

        new File(folderPath+"/"+folderId).mkdir();


        documentRepository.save(document);

        return true;

    }

    public String getFilePath(){

        String userBucketPath = dropboxPath;

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);

        //for year
        userBucketPath += "/"+year;

        System.out.println(userBucketPath);

        if (!new File(userBucketPath).exists()) {
            new File(userBucketPath).mkdir();
        }

        //for month
        userBucketPath = userBucketPath+"/"+month;

        if (!new File(userBucketPath).exists()) {
            new File(userBucketPath).mkdir();
        }

        return userBucketPath;

    }

    @ResponseBody
    @RequestMapping(value = "ajax/document/deletedocument", method = RequestMethod.POST)
    public boolean savefolder(@RequestParam(name = "slug") String slug) {

        List<Document> childDocs = new ArrayList<Document>();
        Document document = documentRepository.findBySlug(slug);
        getChildDocs(document.getId() , childDocs);
        //2,6,10
        System.out.println(childDocs.size());

        document.setIsDeleted(1);
        documentRepository.save(document);

        for (Document chidDocument : childDocs){

            chidDocument.setIsDeleted(1);
            documentRepository.save(chidDocument);
        }

        return true;


    }

    public void getChildDocs(Long id, List<Document> childDocs ){


        List<Document> documentList = documentRepository.findByParentIdAndIsDeleted(id, 0);

        if (documentList.size() > 0) {

            for (Document document : documentList) {

                childDocs.add(document);
                getChildDocs(document.getId(), childDocs);
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/api/upload",method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(Model model, @RequestParam(name = "fileData") MultipartFile[] filesData,
                                        @RequestParam(name = "parentId") Long parentId){

        System.out.println(filesData.length);

        for ( MultipartFile fileData:filesData) {

            String filename = fileData.getOriginalFilename();
            String filepath = Paths.get(dropboxPath, filename).toString();

            // Save the file locally

            try {
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                stream.write(fileData.getBytes());
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String folderId = UUID.randomUUID().toString();

            Document document = new Document();
            document.setName(filename);
            document.setUserId(1);
            document.setPath(folderId);
            document.setType("file");
            document.setParentId(parentId);

            documentRepository.save(document);

        }



        return new ResponseEntity<>(HttpStatus.OK);

    }
}