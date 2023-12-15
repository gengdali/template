package com.huizi.easydinner.util;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @PROJECT_NAME: easy-dinner
 * @DESCRIPTION:上传文件工具类
 * @AUTHOR: 12615
 * @DATE: 2023/9/8 10:58
 */
public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 上传文件到指定路径下
     *
     * @param file
     * @param filePath
     * @throws FileUploadException
     * @PROJECT_NAME: easy-dinner
     */
    public static String uploadByFilePath(MultipartFile file, String filePath) throws IOException {
        if (file.isEmpty()) {
            throw new FileUploadException("上传文件为空");
        }
        // 获取文件名
        String originalFileName = file.getOriginalFilename();
        // 生成UUID作为文件名
        long timestamp = System.currentTimeMillis();
        // 获取文件扩展名
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 拼接新文件名
        String fileName = timestamp + extension;
        File dest = null;
        dest = new File(filePath + "/" + fileName);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        // 保存文件
        file.transferTo(dest);
        return fileName;
    }

    /**
     * 下载指定路径文件到浏览器
     *
     * @param filePath
     * @param response
     * @throws IOException
     */
    public static void downLoadByFilePath(String filePath, HttpServletResponse response) throws IOException {
        // path是指想要下载的文件的路径
        File file = new File(filePath);
        LOGGER.info(file.getPath());
        // 获取文件名
        String filename = file.getName();
        // 获取文件后缀名
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        LOGGER.info("文件后缀名：" + ext);

        // 将文件写入输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStream fis = new BufferedInputStream(fileInputStream);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();

        // 清空response
        response.reset();
        // 设置response的Header
        response.setCharacterEncoding("UTF-8");
        //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        // 告知浏览器文件的大小
        response.addHeader("Content-Length", "" + file.length());
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        outputStream.write(buffer);
        outputStream.flush();
    }

    /**
     * 移除指定路径文件
     *
     * @param filePath
     * @throws IOException
     */
    public static boolean removeFile(String filePath) throws IOException {
        // path是指想要下载的文件的路径
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在");
        }
        boolean delete = file.delete();
        return delete;
    }
}
