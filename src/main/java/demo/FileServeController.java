package demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class FileServeController {

    private GridFsOperations gridFsOperations;

    @Autowired
    public FileServeController(GridFsOperations gridFsOperations) {
        this.gridFsOperations = gridFsOperations;
    }

    @RequestMapping(value="/images/{imageId}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String imageId) {
        GridFsResource resource = gridFsOperations.getResource(imageId);

        ResponseEntity<byte[]> result = null;
        if (resource != null) {
            InputStream inputStream = null;
            try {
                inputStream = resource.getInputStream();
                MultiValueMap<String, String> headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, resource.getContentType());

                result = new ResponseEntity<>(StreamUtils.copyToByteArray(inputStream), headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } finally {
                try {
                    inputStream.close();
                } catch(Exception e) {}
            }
        } else {
            result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return result;
    }
}
