package com.arsc.traceGuard.common.core.domain.model;

import java.util.Map;

/**
 * License 明文内容模型
 */
public class LicenseContent {
    private String subject;      // 项目名
    private String issuedTo;     // 授权给谁
    private long issuedTime;     // 颁发时间
    private long expireTime;     // 过期时间 (核心)
    private Map<String, Object> extra; // 扩展信息

    // Getter 和 Setter 方法
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getIssuedTo() { return issuedTo; }
    public void setIssuedTo(String issuedTo) { this.issuedTo = issuedTo; }
    public long getIssuedTime() { return issuedTime; }
    public void setIssuedTime(long issuedTime) { this.issuedTime = issuedTime; }
    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }
}