package org.example.springboot.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.example.springboot.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

/**
 * <p>
 * 评论
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@TableName("biz_comment")
@Schema(name = "评论实体", description = "评论")
public class Comment extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private Long bizId;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizKey;
    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;
    /**
     * 回复ID
     */
    @Schema(description = "回复ID")
    private Long replyId;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;
    /**
     * IP
     */
    @Schema(description = "IP")
    private String ip;
    /**
     * IP属地
     */
    @Schema(description = "IP属地")
    private String location;
}
