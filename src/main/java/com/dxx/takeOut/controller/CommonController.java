package com.dxx.takeOut.controller;

import com.dxx.takeOut.common.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 处理文件上传下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${take_out_system.path}")
    private String basePath;
    //文件上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后，临时文件会被删除
        log.info("上传文件{}",file);

        //原始文件名，但是不建议，因为有可能会重名，这样文件会被覆盖
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.')); //文件后缀，eg: .jgp

        //使用UUID重新生成文件名
        String newFilename = UUID.randomUUID().toString();

        //判断文件夹是否存在
        File dir=new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + newFilename + suffix));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(newFilename + suffix);
    }

    /**
     * 文件下载,回显在页面上
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //返回类型用void就行，图片直接通过输出流写到本地了，不需要返回

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");//设置响应回去的类型

            int len=0;
            byte[] bytes=new byte[1024];
            while( (len=fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
