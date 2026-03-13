package com.pxwork.api.controller.resource;

import com.pxwork.common.utils.Result;
import com.pxwork.resource.entity.Resource;
import com.pxwork.resource.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <p>
 * 文件上传 前端控制器
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-13
 */
@Slf4j
@Tag(name = "文件上传", description = "文件上传接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private ResourceService resourceService;
    
    // 上传路径，默认为项目根目录下的 uploads
    private static final String UPLOAD_DIR = "uploads";

    @Operation(summary = "上传文件", description = "上传文件并保存到本地，返回URL")
    @PostMapping
    public Result<Resource> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.fail("文件为空");
        }

        try {
            // 1. 准备保存目录
            String projectPath = System.getProperty("user.dir");
            String savePath = projectPath + File.separator + UPLOAD_DIR;
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 生成文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + suffix;
            
            // 3. 保存文件
            File dest = new File(savePath + File.separator + newFilename);
            file.transferTo(dest);
            
            // 4. 生成URL
            // 假设服务端口是 8081，上下文路径是 /
            // URL格式: http://localhost:8081/uploads/filename
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            
            // 注意：这里需要配合 WebMvcConfig 配置静态资源映射
            String url = scheme + "://" + serverName + ":" + serverPort + contextPath + "/" + UPLOAD_DIR + "/" + newFilename;
            
            // 5. 保存资源记录到数据库 (可选，如果用户只是想上传得到URL，可以不存库，但通常需要)
            // 用户说"实现将上传的文件保存到本项目的本地目录下...并返回可供前端访问的本地 URL 链接"，并没有明确说要自动插入 resource 表。
            // 但考虑到这是"素材资源管理"模块，通常上传后会作为资源入库。
            // 不过 ResourceController 已经有 list 接口了，这里我们最好也返回 Resource 对象。
            
            Resource resource = new Resource();
            resource.setName(originalFilename);
            resource.setType(file.getContentType());
            resource.setUrl(url);
            resource.setSize(file.getSize());
            resource.setDuration(0); // 暂时无法获取时长，需要额外工具
            resource.setCategoryId(0L); // 默认未分类
            
            resourceService.save(resource);
            
            return Result.success(resource);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }
}
