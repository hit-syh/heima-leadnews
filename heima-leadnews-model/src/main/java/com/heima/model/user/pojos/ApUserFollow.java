package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * APP用户关注信息表
 * @TableName ap_user_follow
 */
@TableName(value ="ap_user_follow")
@Data
public class ApUserFollow implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 关注作者ID
     */
    private Integer followId;

    /**
     * 粉丝昵称
     */
    private String followName;

    /**
     * 关注度
            0 偶尔感兴趣
            1 一般
            2 经常
            3 高度
     */
    private Integer level;

    /**
     * 是否动态通知
     */
    private Integer isNotice;

    /**
     * 创建时间
     */
    private Date createdTime;

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
        ApUserFollow other = (ApUserFollow) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getFollowId() == null ? other.getFollowId() == null : this.getFollowId().equals(other.getFollowId()))
            && (this.getFollowName() == null ? other.getFollowName() == null : this.getFollowName().equals(other.getFollowName()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getIsNotice() == null ? other.getIsNotice() == null : this.getIsNotice().equals(other.getIsNotice()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getFollowId() == null) ? 0 : getFollowId().hashCode());
        result = prime * result + ((getFollowName() == null) ? 0 : getFollowName().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getIsNotice() == null) ? 0 : getIsNotice().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", followId=").append(followId);
        sb.append(", followName=").append(followName);
        sb.append(", level=").append(level);
        sb.append(", isNotice=").append(isNotice);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}