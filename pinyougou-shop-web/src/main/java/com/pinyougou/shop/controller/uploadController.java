package com.pinyougou.shop.controller;


import com.utils.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class uploadController {

    @Value("${FILE_SERVER_URL}")
    private String file_path; //文件的服务器地址

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        //1.取文件的拓展名
        String originalFilename = file.getOriginalFilename();
        String extendName = originalFilename.substring(originalFilename.lastIndexOf(".")+1); //获取拓展名

        try {
            //1.创建一个上传文件的客户端工具类
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //2.上传文件
            String path = fastDFSClient.uploadFile(file.getBytes(), extendName);
            //3.拼接返回的path和ip
            String URL = file_path+path;
            return new Result(URL,true);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result("上传失败",false);

        }
    }
}
