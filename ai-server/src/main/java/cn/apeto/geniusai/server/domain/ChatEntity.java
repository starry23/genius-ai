package cn.apeto.geniusai.server.domain;

import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/20 10:44 下午
 */
@Data
public class ChatEntity {

    @Schema(description = "上线文分割字数限制")
    private Integer contextSplit = 300;

    @Schema(description = "上线文限制")
    private Integer contextLimit = 5;

    @Schema(description = "反代地址 注意: 只填域名 不要加任何斜杠")
    public String domain;

    @Schema(description = "模型")
    public String model = ChatCompletion.Model.GPT_3_5_TURBO.getName();

    private ChatCompletion.ModelType modelType;

    public ChatEntity(ChatCompletion.ModelType modelType) {
        this.modelType = modelType;
    }

    public ChatEntity() {
    }
}
