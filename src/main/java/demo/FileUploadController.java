package demo;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Controller
public class FileUploadController {

    @Autowired
    private GridFsOperations gridFsOperations;

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name,
                                                 @RequestParam("file") MultipartFile file){

        if (!file.isEmpty()) {
            try {

                InputStream inputStream = file.getInputStream();
                System.out.println("File name:" + file.getName());
                System.out.println("File content type:" + file.getContentType());
                System.out.println("File original file name:" + file.getOriginalFilename());
                System.out.println("File size:" + file.getSize());

                DBObject metaData = new BasicDBObject();
                metaData.put("fileName", file.getOriginalFilename());
                metaData.put("fileSize", file.getSize());

                String id = UUID.randomUUID().toString();

                gridFsOperations.store(inputStream, id, file.getContentType(), metaData);

                return "http://localhost:8080/images/" + id;
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

}
