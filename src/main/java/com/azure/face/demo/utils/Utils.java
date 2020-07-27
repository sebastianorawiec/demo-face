package com.azure.face.demo.utils;

import org.imgscalr.Scalr;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class Utils {

    public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    public static Path handleUpload (MultipartFile file, String uploadDir) throws IOException {

        Path copyLocation = Paths
                .get(uploadDir + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        // make thumb for GUI
        BufferedImage thumbImg;
        BufferedImage img = ImageIO.read(file.getInputStream());
        thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 200, Scalr.OP_ANTIALIAS);
        ImageIO.write(thumbImg, "jpg", new File(copyLocation.toFile().
                getAbsolutePath().replace(".jpg",".thumb.jpg")));

        return copyLocation;
    }

    public static void recreateThumbs (String name) throws IOException{

        File file = new File(name);
        BufferedImage thumbImg;
        BufferedImage img = ImageIO.read(new FileInputStream(file));
        thumbImg = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 200, Scalr.OP_ANTIALIAS);
        ImageIO.write(thumbImg, "jpg", new File(file.getAbsolutePath().
                replace(".jpg",".thumb.jpg")));


    }

}
