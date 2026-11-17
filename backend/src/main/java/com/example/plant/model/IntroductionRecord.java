package com.example.plant.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class IntroductionRecord {
    private Long id;
    private Long userId;
    private String creatorName;
    private String plantName;
    private String scientificName;
    private String origin;
    private LocalDate introductionDate;
    private Integer quantity;
    private String description;
    private String imageUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public Long getUserId() { return userId; }
    public void setUserId(Long value) { userId = value; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String value) { creatorName = value; }
    public String getPlantName() { return plantName; }
    public void setPlantName(String value) { plantName = value; }
    public String getScientificName() { return scientificName; }
    public void setScientificName(String value) { scientificName = value; }
    public String getOrigin() { return origin; }
    public void setOrigin(String value) { origin = value; }
    public LocalDate getIntroductionDate() { return introductionDate; }
    public void setIntroductionDate(LocalDate value) { introductionDate = value; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer value) { quantity = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String value) { imageUrl = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime value) { createTime = value; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime value) { updateTime = value; }
}
