package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
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
import java.io.IOException;
import java.util.UUID;

//32. 文件上传下载
@RestController
//Request URL: http://localhost:8081/common/upload
//Request Method: POST
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")//和配置文件的自定义名保持一致就能访问到
    private String basePath;

    //上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {//file是一个临时文件，在本次请求结束后会消失，所以还要找个地方保存起来
        log.info(file.toString());

//        获取原始文件名
        String originalFilename = file.getOriginalFilename();
//        因为每个文件都有不同后缀，使用需要截取文件后缀
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
//        使用UUID随机重新生成文件名，防止文件名重复造成覆盖
        String fileName = UUID.randomUUID().toString()+suffix;

//        转存之前判断路径path存不存在，不存在就创建一个
        File dir=new File(basePath);
        if(!dir.exists()){
            //不存在
            dir.mkdirs();
        }
        try {//动态转存
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        由于后续在新增菜品时需要传上来的图片，所以这里返回文件
        return R.success(fileName);
    }

//    33.下载,不需要返回值，通过数据流写回数据即可,输出流需要response获得
    @GetMapping("/download")
    public void dowonload(String name, HttpServletResponse response){

        try {//输入流，读取输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));

            //输出流：通过输出流将文件写回浏览器
            ServletOutputStream outputStream=response.getOutputStream();

            response.setContentType("image/jpeg");//告诉浏览器这是什么类型的文件

            int len=0;
            byte[] bytes=new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
//            完事后关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
