package com.Project.Ecommerce.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {
        String originalFileName=image.getOriginalFilename();//Retrieves the original file name sent by client in HTTP request
        String randomId= UUID.randomUUID().toString();//just generating a random id
        String fileName=randomId.concat(originalFileName.
                substring(originalFileName.lastIndexOf(".")));
        String filePath=path+ File.separator+fileName;//the full path where file is gonna get stored.
        // the total filepath until image/1234.png

        File folder=new File(path);  //Creates a Java object representing the path images
        // creating a folder/file if not exist..here the main part is we did images/ in path
        //means its a relative path,means it will create a file in current working directory of this project
        if(!folder.exists())
            folder.mkdir();
        Files.copy(image.getInputStream(), Paths.get(filePath));//here getinput stream means what ever the stream of bit is sended
        //by client ,the image,it will recieved and copied to the path file path(path will .get the bits in the (filepath))
        return fileName;
    }
}
