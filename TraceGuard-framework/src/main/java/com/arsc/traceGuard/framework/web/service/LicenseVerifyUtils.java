package com.arsc.traceGuard.framework.web.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.arsc.traceGuard.common.core.domain.model.LicenseContent;
import com.arsc.traceGuard.common.exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class LicenseVerifyUtils {

    private static final Logger log = LoggerFactory.getLogger(LicenseVerifyUtils.class);

    // 公钥文件名 (必须放在 resources 目录下)
    private static final String PUBLIC_KEY_PATH = "public.key";
    // License 文件在服务器上的默认位置 (也可以写死绝对路径，或者从配置文件读)
    private static final String LICENSE_FILE_PATH = "license.lic";

    /**
     * 校验 License 的入口方法
     */
    public void verifyLicense() {
        try {
            // 1. 读取 license.lic 文件
            // 假设文件放在项目运行根目录下，或者指定一个绝对路径
            File licenseFile = new File(LICENSE_FILE_PATH);
            if (!licenseFile.exists()) {
                throw new ServiceException("未检测到许可文件 (license.lic)，请联系管理员授权！");
            }
            String licenseJsonStr = FileUtils.readFileToString(licenseFile, StandardCharsets.UTF_8);

            // 2. 解析 JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(licenseJsonStr);
            if (!root.has("content") || !root.has("signature")) {
                throw new ServiceException("许可文件格式错误！");
            }

            String contentStr = root.get("content").asText();
            String signatureStr = root.get("signature").asText();

            // 3. 验证数字签名 (防篡改核心)
            boolean isValid = verifySignature(contentStr, signatureStr);
            if (!isValid) {
                throw new ServiceException("系统许可文件校验失败，文件已被非法篡改！");
            }

            // 4. 解析明文内容并校验过期时间
            LicenseContent content = mapper.readValue(contentStr, LicenseContent.class);
            long now = System.currentTimeMillis();
            if (now > content.getExpireTime()) {
                throw new ServiceException("系统许可已到期，请联系供应商续费！");
            }

            // log.info("License校验通过，授权给: {}", content.getIssuedTo());

        } catch (ServiceException e) {
            throw e; // 直接抛出业务异常给前端
        } catch (Exception e) {
            log.error("License校验异常", e);
            throw new ServiceException("许可校验发生未知错误，禁止登录");
        }
    }

    /**
     * RSA 验签逻辑
     */
    private boolean verifySignature(String content, String signature) throws Exception {
        // 读取公钥
        ClassPathResource resource = new ClassPathResource(PUBLIC_KEY_PATH);
        if (!resource.exists()) {
            throw new RuntimeException("缺少公钥文件 public.key");
        }
        byte[] keyBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        byte[] decodedKey = Base64.getDecoder().decode(keyBytes);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);

        // 验签
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(content.getBytes());
        return sig.verify(Base64.getDecoder().decode(signature));
    }
}