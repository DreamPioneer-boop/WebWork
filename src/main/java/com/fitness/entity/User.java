package com.fitness.entity;

import java.math.BigDecimal;

/**
 * 用户实体，对应表 t_user。
 */
public class User {

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String gender;
    private Integer age;
    private BigDecimal height;
    private BigDecimal weight;
    private String experienceLevel;
    private String role;
    private Integer status;
    private String createTime;
    private String updateTime;

    /** 是否管理员 */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /** 是否有发布内容权限（管理员或有健身经验） */
    public boolean canPublish() {
        return "ADMIN".equals(role) || "EXPERIENCED".equals(experienceLevel);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}
