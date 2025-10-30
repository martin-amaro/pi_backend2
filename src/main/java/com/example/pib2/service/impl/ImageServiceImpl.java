package com.example.pib2.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.pib2.model.entity.Image;
import com.example.pib2.model.entity.Product;
import com.example.pib2.repository.ImageRepository;
import com.example.pib2.service.ImageService;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${imagekit.public}")
    private String publicKey;

    @Value("${imagekit.private}")
    private String privateKey;

    @Value("${imagekit.url}")
    private String urlEndpoint;




    @Autowired
    ImageRepository imageRepository;

    public void save(Image image) {
        imageRepository.save(image);
    }

    @Override
    public void saveImageForProduct(String imageUrl, Product product) {
        Image image = new Image();
        image.setUrl(imageUrl);
        image.setProduct(product);
        image.setBusiness(product.getBusiness());
        imageRepository.save(image);
    }

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {

        System.out.println("Public Key: " + publicKey);
        System.out.println("Private Key: " + privateKey);
        System.out.println("URL Endpoint: " + urlEndpoint);

        try {


            // String publicKey = "public_mRv9nAFKwonwRKabZb5ZDFjScFA=";
            // String privateKey = "private_fD8ddXtdssDRScgcqHxxdf9nm0o=";
            // String urlEndpoint = "https://ik.imagekit.io/scritpal";

            ImageKit imageKit = ImageKit.getInstance();
            Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
            imageKit.setConfig(config);
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
                    "uploaded_url", result.getUrl());

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", e.getMessage());
        }
    }
}
