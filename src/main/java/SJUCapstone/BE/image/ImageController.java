package SJUCapstone.BE.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {
    @Autowired
    S3ImageService s3ImageService;

    @PostMapping(value = "/s3/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image) {
        String profileImageURL = s3ImageService.upload(image);
        return ResponseEntity.ok(profileImageURL);
    }

    @GetMapping(value = "/s3/delete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> s3delete(@RequestParam("addr") String addr) {
        s3ImageService.deleteImageFromS3(addr);
        return ResponseEntity.ok(null);
    }
}
