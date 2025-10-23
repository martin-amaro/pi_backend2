package com.example.pib2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.pib2.model.entity.User;
import com.example.pib2.service.UserService;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.results.Result;


@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Map<String, String> test() {

        return Map.of("test", "example");
        
    }

    @GetMapping("users")
    public List<User> getAllUsers() {
        
        return userService.getAll();

    }
    
    @PostMapping("/img")
    public Map<String, Object> uploadRemote(@RequestParam("file") MultipartFile file) {
        String publicKey = "public_mRv9nAFKwonwRKabZb5ZDFjScFA=";
        String privateKey = "private_fD8ddXtdssDRScgcqHxxdf9nm0o=";
        String urlEndpoint = "https://ik.imagekit.io/scritpal";

        ImageKit imageKit = ImageKit.getInstance();
        Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
        imageKit.setConfig(config);

        try {

            byte[] fileBytes = file.getBytes();

            // sube una imagen remota (URL pública)
            FileCreateRequest request = new FileCreateRequest(
                    fileBytes,
                    // "https://ik.imagekit.io/ikmedia/red_dress_woman.jpeg", // URL de la imagen
                    // original
                    // "mifoto.jpg" // nombre que tendrá en tu ImageKit
                    file.getOriginalFilename());

            Result result = imageKit.upload(request);
            return Map.of(
                    "success", true,
                    "fileName", result.getName(),
                    "fileType", file.getContentType(),
                    "uploaded_url", result.getUrl()
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage()
        );
        }
    }
    
}
