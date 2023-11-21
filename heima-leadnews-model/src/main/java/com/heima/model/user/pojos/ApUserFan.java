package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * APP用户粉丝信息表
 * @TableName ap_user_fan
 */
@TableName(value ="ap_user_fan")
@Data
public class ApUserFan implements Serializable {
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
     * 粉丝ID
     */
    private Integer fansId;

    /**
     * 粉丝昵称
     */
    private String fansName;

    /**
     * 粉丝忠实度
            0 正常
            1 潜力股
            2 勇士
            3 铁杆
            4 老铁
     */
    private Integer level;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 是否可见我动态
     */
    private Integer isDisplay;

    /**
     * 是否屏蔽私信
     */
    private Integer isShieldLetter;

    /**
     * 是否屏蔽评论
     */
    private Integer isShieldComment;

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
        ApUserFan other = (ApUserFan) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getFansId() == null ? other.getFansId() == null : this.getFansId().equals(other.getFansId()))
            && (this.getFansName() == null ? other.getFansName() == null : this.getFansName().equals(other.getFansName()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getIsDisplay() == null ? other.getIsDisplay() == null : this.getIsDisplay().equals(other.getIsDisplay()))
            && (this.getIsShieldLetter() == null ? other.getIsShieldLetter() == null : this.getIsShieldLetter().equals(other.getIsShieldLetter()))
            && (this.getIsShieldComment() == null ? other.getIsShieldComment() == null : this.getIsShieldComment().equals(other.getIsShieldComment()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getFansId() == null) ? 0 : getFansId().hashCode());
        result = prime * result + ((getFansName() == null) ? 0 : getFansName().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getIsDisplay() == null) ? 0 : getIsDisplay().hashCode());
        result = prime * result + ((getIsShieldLetter() == null) ? 0 : getIsShieldLetter().hashCode());
        result = prime * result + ((getIsShieldComment() == null) ? 0 : getIsShieldComment().hashCode());
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
        sb.append(", fansId=").append(fansId);
        sb.append(", fansName=").append(fansName);
        sb.append(", level=").append(level);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", isDisplay=").append(isDisplay);
        sb.append(", isShieldLetter=").append(isShieldLetter);
        sb.append(", isShieldComment=").append(isShieldComment);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}