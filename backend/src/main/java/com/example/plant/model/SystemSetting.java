package com.example.plant.model;

import java.time.LocalDateTime;

public class SystemSetting {
    private String settingKey;
    private String settingValue;
    private Integer secret;
    private Long updatedBy;
    private LocalDateTime updateTime;

    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String value) { settingKey = value; }
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String value) { settingValue = value; }
    public Integer getSecret() { return secret; }
    public void setSecret(Integer value) { secret = value; }
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long value) { updatedBy = value; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime value) { updateTime = value; }
}
