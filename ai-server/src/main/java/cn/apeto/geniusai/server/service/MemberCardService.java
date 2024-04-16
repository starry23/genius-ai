package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.entity.MemberCard;

import java.util.List;

/**
 * <p>
 * 会员卡 服务类
 * </p>
 *
 * @author warape
 * @since 2023-04-08 06:49:07
 */
public interface MemberCardService extends IService<MemberCard> {

    List<MemberCard> selectByViewTypeAndType();

    MemberCard getByCardCode(String memberCardCode);

}
