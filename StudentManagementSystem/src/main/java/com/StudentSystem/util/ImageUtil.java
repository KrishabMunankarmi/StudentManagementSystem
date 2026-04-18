package com.StudentSystem.util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    /**
     * Uploads an image file to the /resources directory inside the web application.
     * 
     * @param filePart    the uploaded file part from the request
     * @param appRealPath the real file system path of the web application root
     * @return the relative path to the uploaded image for storing in the database
     * @throws IOException if an I/O error occurs during file saving
     */
    public String uploadImage(Part filePart, String appRealPath) throws IOException {
        // Generate unique filename using timestamp + original filename
        String fileName = System.currentTimeMillis() + "_" + getFileName(filePart);

        // Construct absolute path to the /resources folder inside webapp
        String resourcesPath = appRealPath + "resources";

        // Create the resources directory if it does not exist
        File resourcesDir = new File(resourcesPath);
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs();
        }

        // Full absolute path for saving the uploaded file
        String fullPath = resourcesPath + File.separator + fileName;
        // Write file to disk
        filePart.write(fullPath);

        System.out.println("Saving to: " + fullPath);

        // Return the relative path (used in DB to reference image)
        return "/resources/" + fileName;
    }

    /**
     * Extracts the original file name from the content-disposition header of the Part.
     * 
     * @param part the uploaded file part
     * @return the original filename or null if not found
     */
    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                // Extract filename from content-disposition header
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
