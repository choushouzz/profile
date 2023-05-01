package com.example.spring;


import com.example.spring.S3BucketStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class S3BucketStorageController {
    @Autowired
    S3BucketStorageService service;

    @RequestMapping(value = "/form_submit",method = RequestMethod.POST)
    @ResponseBody
    public String submitForm(@RequestParam("day") String day,
                             @RequestParam("month") String month,
                             @RequestParam("year") String year,
                             @RequestParam("company name") String name,
                             @RequestParam("file type") String type,
                             @RequestParam("file") MultipartFile multipartFile) throws IOException
    {
        //multipartFile.transferTo(new File("D:\\java_test\\spring\\src\\main\\resources\\upload_files\\" + multipartFile.getOriginalFilename()));
        String filename = type + "/" + year + "-" + month + "-" + day + " " + name + " "
                + multipartFile.getOriginalFilename();

        System.out.println(uploadFile(filename, multipartFile));
        return "Completed <br>" +  "生年月日：" +
                day + "日"  + month + "月" + year + "年" + "<br>" + "会社名："
                + name + "<br>" + "種類：" + type + "<br>"
                + multipartFile.getOriginalFilename() + "<br>";
        // Process the form data and generate JSON response
        //return "{ \"name\": \"" + name + "\", \"email\": \"" + email + "\" }";
    }

    private ResponseEntity<String> uploadFile(@RequestParam("fileName") String fileName,
                                              @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(service.uploadFile(fileName, file), HttpStatus.OK);
    }

}