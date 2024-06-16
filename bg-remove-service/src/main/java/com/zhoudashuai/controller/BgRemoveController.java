package com.zhoudashuai.controller;


import com.zhoudashuai.api.CommonResult;
import com.zhoudashuai.api.ResultCode;
import com.zhoudashuai.exception.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.Objects;

@RequestMapping("/image")
@RestController
public class BgRemoveController {

    /**
     * 图片处理
     *
     * @param image     图片流
     * @param auth      认证头
     * @param imageBase 图片base64
     * @param imageUrl  图片url
     * @throws IOException
     */
    @PostMapping("/parse")
    public CommonResult<Void> parseImage(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("auth") String auth,
            @RequestParam(value = "imageBase", required = false) String imageBase,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            HttpServletResponse response) throws IOException {

        try (HttpEntity httpEntity = getProcessedEntity(image, auth, imageBase, imageUrl, response)) {
            writeEntityToResponse(httpEntity, response);
        }
        return CommonResult.success();
    }

    private HttpEntity getProcessedEntity(MultipartFile imageFile, String auth, String imageBase, String imageUrl, HttpServletResponse response) throws IOException {
        Request request = createRequest(imageFile, auth, imageBase, imageUrl);
        HttpResponse httpResponse = executeRequest(request);
        validateHttpResponse(httpResponse, response);
        return extractEntityFromResponse(httpResponse);
    }

    private HttpResponse executeRequest(Request request) throws IOException {
        return request.execute().returnResponse();
    }

    private void validateHttpResponse(HttpResponse httpResponse, HttpServletResponse response) {
        int statusCode = httpResponse.getCode();
        response.setStatus(statusCode);

        switch (statusCode) {
            case 200:
                return;
            case 401:
                throw new ApiException(ResultCode.UNAUTHORIZED);
            case 402:
                throw new ApiException(ResultCode.PAYMENT_REQUIRED);
            case 403:
                throw new ApiException(ResultCode.FORBIDDEN);
            case 500:
                throw new ApiException(ResultCode.FAILED);
            default:
                throw new ApiException(ResultCode.UNKNOWN_ERROR);
        }
    }

    private HttpEntity extractEntityFromResponse(HttpResponse httpResponse) {
        return ((BasicClassicHttpResponse) httpResponse).getEntity();
    }

    private void writeEntityToResponse(HttpEntity httpEntity, HttpServletResponse response) throws IOException {
        try (InputStream inputStream = httpEntity.getContent()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        }
    }

    private Request createRequest(MultipartFile imageFile, String auth, String imageBase, String imageUrl) throws IOException {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();

        if (StringUtils.isNotBlank(imageUrl)) {
            entityBuilder.addTextBody("image.url", imageUrl);
        } else if (StringUtils.isNotBlank(imageBase)) {
            entityBuilder.addTextBody("image.base64", imageBase);
        } else {
            entityBuilder.addBinaryBody("image", convertToFile(imageFile));
        }

        entityBuilder.addTextBody("test", "true");

        return Request.post("https://api.pixian.ai/api/v2/remove-background")
                .addHeader("Authorization", auth)
                .body(entityBuilder.build());
    }

    private File convertToFile(MultipartFile file) throws IOException {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }

}
