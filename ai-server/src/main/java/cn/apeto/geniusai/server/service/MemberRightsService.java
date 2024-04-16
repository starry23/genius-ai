package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.entity.MemberRights;

import java.util.List;

/**
 * <p>
 * 会员策略 服务类
 * </p>
 *
 * @author warape
 * @since 2023-05-01 12:47:39
 */
public interface MemberRightsService extends IService<MemberRights> {

    List<MemberRights> getByMemberCode(String cardCode);

    MemberRights getByRightsTypeAndMemberCodeOne(String cardCode, Constants.MemberRightsTypeEnum memberRightsTypeEnum);

}
