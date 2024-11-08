package org.example.springboot.generator.gen.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.example.springboot.generator.gen.domain.entity.GenTableFile;

import java.io.Serial;

/**
 * <p>
 * 代码生成业务文件
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "代码生成业务文件实体", description = "代码生成业务文件")
public class GenTableFileVo extends GenTableFile {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 占位符
     */
    @Schema(description = "占位符")
    private String placeholder;
}