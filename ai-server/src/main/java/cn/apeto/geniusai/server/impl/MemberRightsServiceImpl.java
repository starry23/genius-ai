package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.entity.MemberRights;
import cn.apeto.geniusai.server.mapper.MemberRightsMapper;
import cn.apeto.geniusai.server.service.MemberRightsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 会员策略 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-05-01 12:47:39
 */
@Service
public class MemberRightsServiceImpl extends ServiceImpl<MemberRightsMapper, MemberRights> implements MemberRightsService {

    @Override
    public List<MemberRights> getByMemberCode(String cardCode) {
        LambdaQueryWrapper<MemberRights> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MemberRights::getMemberCode, cardCode);
        return list(queryWrapper);
    }

    @Override
    public MemberRights getByRightsTypeAndMemberCodeOne(String cardCode, Constants.MemberRightsTypeEnum memberRightsTypeEnum) {
        LambdaQueryWrapper<MemberRights> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MemberRights::getMemberCode, cardCode);
        queryWrapper.eq(MemberRights::getRightsType, memberRightsTypeEnum.getType());
        return baseMapper.selectOne(queryWrapper);
    }
}
