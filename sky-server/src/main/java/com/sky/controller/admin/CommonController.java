package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Api(tags = "常用接口")
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @RequestMapping("/upload")
    @ApiOperation("上传图片")
    public Result<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String extension=originalFilename.substring(originalFilename.lastIndexOf("."));
        //将文件上传的阿里云
        String fileName = UUID.randomUUID().toString() + extension;
        try {
            String filePath=aliOssUtil.upload(file.getBytes(),fileName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("上传失败");
            e.printStackTrace();
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
