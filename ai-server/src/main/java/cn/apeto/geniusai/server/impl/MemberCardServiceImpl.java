package cn.apeto.geniusai.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.Constants.MemberStateEnum;
import cn.apeto.geniusai.server.entity.MemberCard;
import cn.apeto.geniusai.server.mapper.MemberCardMapper;
import cn.apeto.geniusai.server.service.MemberCardService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 会员卡 服务实现类
 * </p>
 *
 * @author warape
 * @since 2023-04-08 06:49:07
 */
@Service
public class MemberCardServiceImpl extends ServiceImpl<MemberCardMapper, MemberCard> implements MemberCardService {


    @Override
    public List<MemberCard> selectByViewTypeAndType() {
        LambdaQueryWrapper<MemberCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MemberCard::getCardState, MemberStateEnum.ONLINE.getState());
        return list(queryWrapper);
    }

    @Override
    public MemberCard getByCardCode(String memberCardCode) {
        LambdaQueryWrapper<MemberCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MemberCard::getCardCode, memberCardCode);
        queryWrapper.eq(MemberCard::getCardState, MemberStateEnum.ONLINE.getState());
        return getOne(queryWrapper);
    }



}
