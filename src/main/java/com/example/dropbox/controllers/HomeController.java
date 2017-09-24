package com.example.dropbox.controllers;


import com.example.dropbox.models.Document;
import com.example.dropbox.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by mrinalbhambhu on 23/09/2017.
 */
@Controller
public class HomeController {

    @Autowired
    DocumentRepository documentRepository;

    @Value("${dropbox.path}")
    private String dropboxPath;


    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String showHome(Model model){

        //Document document = new Document();
//        document.setName("hello");
//        document.setPath("app/aaa");
//        document.setType("file");
//
//        documentRepository.save(document);

    List<Document> documentList = documentRepository.findByUserIdAndParentIdAndIsDeleted(1, Long.valueOf(0), 0);

    model.addAttribute("documents", documentList);
    model.addAttribute("parentId", 0);


        return "home";
    }

    @RequestMapping(value = "/document/{slug}",method = RequestMethod.GET)
    public String showHome(Model model, @PathVariable(name = "slug") String slug){

        Document document = documentRepository.findBySlug(slug);

        List<Document> documentList = documentRepository.findByUserIdAndParentIdAndIsDeleted(1, document.getId(), 0);

        model.addAttribute("documents", documentList);
        model.addAttribute("parentId", document.getId());

//        DocumentHelper helper = new DocumentHelper();
//        List<Document> list = helper.getChildDocs(Long.valueOf(1));
//        System.out.println(list.size());



        return "home";


    }





}
