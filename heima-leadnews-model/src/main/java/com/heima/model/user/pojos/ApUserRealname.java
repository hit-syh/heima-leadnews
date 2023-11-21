package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * APP实名认证信息表
 * @TableName ap_user_realname
 */
@TableName(value ="ap_user_realname")
@Data
public class ApUserRealname implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 账号ID
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 资源名称
     */
    private String idno;

    /**
     * 正面照片
     */
    private String fontImage;

    /**
     * 背面照片
     */
    private String backImage;

    /**
     * 手持照片
     */
    private String holdImage;

    /**
     * 活体照片
     */
    private String liveImage;

    /**
     * 状态
            0 创建中
            1 待审核
            2 审核失败
            9 审核通过
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 提交时间
     */
    private Date submitedTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

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
        ApUserRealname other = (ApUserRealname) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getIdno() == null ? other.getIdno() == null : this.getIdno().equals(other.getIdno()))
            && (this.getFontImage() == null ? other.getFontImage() == null : this.getFontImage().equals(other.getFontImage()))
            && (this.getBackImage() == null ? other.getBackImage() == null : this.getBackImage().equals(other.getBackImage()))
            && (this.getHoldImage() == null ? other.getHoldImage() == null : this.getHoldImage().equals(other.getHoldImage()))
            && (this.getLiveImage() == null ? other.getLiveImage() == null : this.getLiveImage().equals(other.getLiveImage()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getReason() == null ? other.getReason() == null : this.getReason().equals(other.getReason()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getSubmitedTime() == null ? other.getSubmitedTime() == null : this.getSubmitedTime().equals(other.getSubmitedTime()))
            && (this.getUpdatedTime() == null ? other.getUpdatedTime() == null : this.getUpdatedTime().equals(other.getUpdatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getIdno() == null) ? 0 : getIdno().hashCode());
        result = prime * result + ((getFontImage() == null) ? 0 : getFontImage().hashCode());
        result = prime * result + ((getBackImage() == null) ? 0 : getBackImage().hashCode());
        result = prime * result + ((getHoldImage() == null) ? 0 : getHoldImage().hashCode());
        result = prime * result + ((getLiveImage() == null) ? 0 : getLiveImage().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getReason() == null) ? 0 : getReason().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getSubmitedTime() == null) ? 0 : getSubmitedTime().hashCode());
        result = prime * result + ((getUpdatedTime() == null) ? 0 : getUpdatedTime().hashCode());
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
        sb.append(", name=").append(name);
        sb.append(", idno=").append(idno);
        sb.append(", fontImage=").append(fontImage);
        sb.append(", backImage=").append(backImage);
        sb.append(", holdImage=").append(holdImage);
        sb.append(", liveImage=").append(liveImage);
        sb.append(", status=").append(status);
        sb.append(", reason=").append(reason);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", submitedTime=").append(submitedTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}