package com.fitness.entity;

/**
 * 评论视图对象：在 Comment 基础上附带评论者昵称、头像。
 */
public class CommentVO extends Comment {

    private String nickname;
    private String avatar;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
