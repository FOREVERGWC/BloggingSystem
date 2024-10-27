package org.example.springboot.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import org.example.springboot.system.domain.Result;
import org.example.springboot.system.domain.dto.FileChunkDto;
import org.example.springboot.system.domain.vo.AttachmentCheckVo;
import org.example.springboot.system.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 文件前端控制器
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/file")
@Tag(name = "文件", description = "文件")
public class FileController {
    @Value("${upload.path}")
    private String basePath;

    @Resource
    private IFileService fileService;

    /**
     * 校验文件
     *
     * @param hashCode   散列值
     * @param chunkTotal 分片数量
     * @return 结果
     */
    @GetMapping("/check")
    @Operation(summary = "校验文件", description = "校验文件", method = "GET")
    public Result<AttachmentCheckVo> check(String hashCode, Integer chunkTotal) {
        AttachmentCheckVo vo = fileService.checkFile(hashCode, chunkTotal);
        return Result.success(vo);
    }

    /**
     * 上传文件
     *
     * @param dto 文件
     * @return 结果
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件", method = "POST")
    public Result<String> upload(FileChunkDto dto) {
        String path = fileService.uploadFile(dto);
        return Result.success("上传成功！", path);
    }

    /**
     * 上传多个文件
     *
     * @param fileList 文件列表
     * @return 结果
     */
    @PostMapping("/uploads")
    @Operation(summary = "上传多个文件", description = "上传多个文件", method = "POST")
    public Result<List<String>> uploads(List<MultipartFile> fileList) {
        if (CollectionUtil.isEmpty(fileList)) {
            return Result.error("禁止上传空文件列表！");
        }
        List<String> pathList = new ArrayList<>();
        for (MultipartFile file : fileList) {
            if (file.isEmpty()) {
                continue;
            }
            String originalFilename = file.getOriginalFilename();
            String suffixName = "." + FileUtil.extName(originalFilename);
            String fileName = UUID.randomUUID() + suffixName;
            Path filePath = Paths.get(basePath, fileName);
            String path = "/file/" + fileName;
            try (InputStream inputStream = file.getInputStream()) {
                Files.createDirectories(filePath.getParent());
                FileUtil.copyFile(inputStream, filePath.toFile());
                pathList.add(path);
            } catch (IOException e) {
                log.error("上传失败！{}", e.getMessage());
            }
        }
        return Result.success("上传成功！", pathList);
    }

    /**
     * 获取文件
     *
     * @param name     文件名称
     * @param response 响应对象
     */
    @GetMapping("/{name}")
    @Operation(summary = "获取文件", description = "获取文件", method = "GET")
    public void avatarPath(@PathVariable String name, HttpServletResponse response) {
        if (StrUtil.isNotBlank(name)) {
            try (OutputStream outputStream = response.getOutputStream(); FileInputStream fileInputStream = new FileInputStream(Paths.get(basePath, name).toFile())) {
                IoUtil.copy(fileInputStream, outputStream, IoUtil.DEFAULT_BUFFER_SIZE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
