package cn.apeto.geniusai.server.domain.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/02/14:36
 * @Description:
 */
@Data
public class DeleteResourceDto {
    private String itemId;
    private String fileName;
}
