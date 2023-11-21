package com.heima.model.article.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * APP收藏信息表
 * @TableName ap_collection
 */
@TableName(value ="ap_collection")
@Data
public class ApCollection implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体ID
     */
    private Integer entryId;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 点赞内容类型
            0文章
            1动态
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date collectionTime;

    /**
     * 发布时间
     */
    private Date publishedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ApCollection other = (ApCollection) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEntryId() == null ? other.getEntryId() == null : this.getEntryId().equals(other.getEntryId()))
            && (this.getArticleId() == null ? other.getArticleId() == null : this.getArticleId().equals(other.getArticleId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getCollectionTime() == null ? other.getCollectionTime() == null : this.getCollectionTime().equals(other.getCollectionTime()))
            && (this.getPublishedTime() == null ? other.getPublishedTime() == null : this.getPublishedTime().equals(other.getPublishedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEntryId() == null) ? 0 : getEntryId().hashCode());
        result = prime * result + ((getArticleId() == null) ? 0 : getArticleId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getCollectionTime() == null) ? 0 : getCollectionTime().hashCode());
        result = prime * result + ((getPublishedTime() == null) ? 0 : getPublishedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", entryId=").append(entryId);
        sb.append(", articleId=").append(articleId);
        sb.append(", type=").append(type);
        sb.append(", collectionTime=").append(collectionTime);
        sb.append(", publishedTime=").append(publishedTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}