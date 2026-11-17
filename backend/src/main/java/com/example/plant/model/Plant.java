package com.example.plant.model;

import java.time.LocalDateTime;

public class Plant {
    private Long id;
    private String name;
    private String scientificName;
    private String category;
    private String origin;
    private Integer quantity;
    private String status;
    private String imageUrl;
    private Long introRecordId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long value) { id = value; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getScientificName() { return scientificName; }
    public void setScientificName(String value) { scientificName = value; }
    public String getCategory() { return category; }
    public void setCategory(String value) { category = value; }
    public String getOrigin() { return origin; }
    public void setOrigin(String value) { origin = value; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer value) { quantity = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String value) { imageUrl = value; }
    public Long getIntroRecordId() { return introRecordId; }
    public void setIntroRecordId(Long value) { introRecordId = value; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime value) { createTime = value; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime value) { updateTime = value; }
}
