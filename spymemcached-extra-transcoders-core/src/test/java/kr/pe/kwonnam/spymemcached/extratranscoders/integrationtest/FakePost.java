package kr.pe.kwonnam.spymemcached.extratranscoders.integrationtest;

import java.io.Serializable;
import java.util.Date;

public class FakePost implements Serializable {
    private String title;
    private Date createdAt;
    private PostType postType;
    private String contents;

    public FakePost(String title, Date createdAt, PostType postType) {
        this.title = title;
        this.createdAt = createdAt;
        this.postType = postType;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public PostType getPostType() {
        return postType;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "FakePost{" +
                "title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", postType=" + postType +
                ", contents='" + contents + '\'' +
                '}';
    }

    public enum PostType {
        SHORT, LONG
    }
}
