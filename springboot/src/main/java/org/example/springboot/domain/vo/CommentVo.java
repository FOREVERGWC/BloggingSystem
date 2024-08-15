package org.example.springboot.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.example.springboot.domain.entity.Comment;
import org.example.springboot.domain.entity.User;

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
@EqualsAndHashCode(callSuper = false)
@Schema(name = "评论实体", description = "评论")
public class CommentVo extends Comment {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 回复
     */
    @Schema(description = "回复")
    private CommentVo reply;
    /**
     * 用户
     */
    @Schema(description = "用户")
    private User user;
}
