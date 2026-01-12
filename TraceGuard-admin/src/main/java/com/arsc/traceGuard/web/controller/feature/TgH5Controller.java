package com.arsc.traceGuard.web.controller.feature;

import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.utils.ip.IpUtils;
import com.arsc.traceGuard.feature.service.ITgTraceCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 消费者H5端专用接口 (无需登录)
 */
@RestController
@RequestMapping("/h5")
public class TgH5Controller {

    @Autowired
    private ITgTraceCodeService traceCodeService;

    /**
     * 扫码验证接口
     * URL: /api/h5/verify
     */
    @GetMapping("/verify")
    public AjaxResult verify(@RequestParam("code")String code, HttpServletRequest request) {
        if (code == null) {
            return AjaxResult.error("无效的防伪码");
        }

        // 获取消费者环境信息
        String ip = IpUtils.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");

        // 调用之前写好的 Service 业务逻辑
        return traceCodeService.verifyCode(code, ip, userAgent);
    }
}