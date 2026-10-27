package com.fitness.entity;

import java.util.List;

/**
 * 文章视图对象：在 Article 基础上附带作者昵称、是否已点赞、评论列表等展示信息。
 */
public class ArticleVO extends Article {

    private String authorNickname;
    private String authorAvatar;
    private String authorExperienceLevel;
    private boolean liked;
    private List<CommentVO> comments;

    public String getAuthorNickname() { return authorNickname; }
    public void setAuthorNickname(String authorNickname) { this.authorNickname = authorNickname; }
    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }
    public String getAuthorExperienceLevel() { return authorExperienceLevel; }
    public void setAuthorExperienceLevel(String authorExperienceLevel) { this.authorExperienceLevel = authorExperienceLevel; }
    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
    public List<CommentVO> getComments() { return comments; }
    public void setComments(List<CommentVO> comments) { this.comments = comments; }
}
