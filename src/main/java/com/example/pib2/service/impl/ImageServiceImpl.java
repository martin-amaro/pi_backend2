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
    public void saveImageForProduct(String imageUrl, String fileId, Product product, boolean isMain) {
        Image image = new Image();
        image.setUrl(imageUrl);
        image.setFileId(fileId);
        image.setProduct(product);
        image.setBusiness(product.getBusiness());
        image.setMain(isMain);
        imageRepository.save(image);
    }

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {
        try {
            ImageKit imageKit = ImageKit.getInstance();
            Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
            imageKit.setConfig(config);

            byte[] fileBytes = file.getBytes();
            FileCreateRequest request = new FileCreateRequest(fileBytes, file.getOriginalFilename());
            Result result = imageKit.upload(request);

            return Map.of(
                    "success", true,
                    "fileName", result.getName(),
                    "fileType", file.getContentType(),
                    "uploaded_url", result.getUrl(),
                    "fileId", result.getFileId());

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", e.getMessage());
        }
    }

    @Override
    public void deleteImageByUrl(String imageUrl, Product product) {
        Image image = imageRepository.findByUrlAndProduct(imageUrl, product);

        if (image != null) {
            try {
                ImageKit imageKit = ImageKit.getInstance();
                Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
                imageKit.setConfig(config);

                // borrar en ImageKit
                imageKit.deleteFile(image.getFileId());

                // borrar en DB
                imageRepository.delete(image);

            } catch (Exception e) {
                System.err.println("Error al eliminar imagen en ImageKit: " + e.getMessage());
            }
        }
    }
}
