package cn.apeto.geniusai.server.impl;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.dto.CardPageDTO;
import cn.apeto.geniusai.server.entity.Card;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.mapper.CardMapper;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.service.CardService;
import cn.apeto.geniusai.server.utils.RedeemCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 卡密兑换 服务类
 * </p>
 *
 * @author hsg
 * @since 2023-07-04 16:28:17
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    @Autowired
    private AccountService accountService;

    @Override
    public IPage<Card> getPage(CardPageDTO cardPageDTO) {
        LambdaQueryWrapper<Card> objectQueryWrapper = new LambdaQueryWrapper<Card>();
        objectQueryWrapper.eq(StrUtil.isNotBlank(cardPageDTO.getBatch()), Card::getBatch, cardPageDTO.getBatch());
        objectQueryWrapper.eq(StrUtil.isNotBlank(cardPageDTO.getCode()), Card::getCode, cardPageDTO.getCode());
        objectQueryWrapper.eq(cardPageDTO.getStatus() != null, Card::getStatus, cardPageDTO.getStatus());
        objectQueryWrapper.ge(cardPageDTO.getRedeemedUid() != null, Card::getCreateTime, cardPageDTO.getRedeemedUid());
        objectQueryWrapper.le(cardPageDTO.getRedemptionDate() != null, Card::getCreateTime, cardPageDTO.getRedemptionDate());
        objectQueryWrapper.orderByDesc(Card::getId);
        return baseMapper.selectPage(new Page<>(cardPageDTO.getCurrent(), cardPageDTO.getSize()), objectQueryWrapper);
    }

    @Override
    public void numberCreate(LocalDateTime dateTime, Integer num, BigDecimal amount)  {
        //根据时间生成批次号
        String batchNumber = generateBatchNumber();
        if(num == null || num < 1){
            throw new ServiceException(CommonRespCode.CARD_IS_NOT_NULL);
        }
        if(amount == null){
            throw new ServiceException(CommonRespCode.CARD_AMOUNT_IS_NOT_NULL);
        }
        for(int i=0;i<num;i++){
            String code = RedeemCodeUtils.createBigStrOrNumberRadom(16);
            Card card = new Card();
            card.setBatch(batchNumber);
            card.setExpirationDate(dateTime);
            card.setStatus(Constants.CardStatusEnum.WAIT.getStatus());
            card.setCode(code);
            card.setAmount(amount);
            baseMapper.insert(card);
        }

    }

    @Override
    public void numberInvalidate(Long id) {
        Card card = new Card();
        card.setId(id);
        card.setStatus(Constants.CardStatusEnum.CANCEL.getStatus());
        baseMapper.updateById(card);
    }

    @Override
    public void redeemCard(long loginIdAsLong,String code) {
        if(code == null){
            throw new ServiceException(CommonRespCode.CARD_CODE_IS_NOT_NULL);
        }
        Card card = baseMapper.selectOne(new LambdaQueryWrapper<Card>().eq(Card::getCode, code));
        if(card == null){
            throw new ServiceException(CommonRespCode.CARD_CODE_INFO_CANNOT_GET);
        }
        if(!Constants.CardStatusEnum.WAIT.getStatus().equals(card.getStatus())){
            throw new ServiceException(CommonRespCode.CARD_CODE_NOT_RIGHT_STATUS);
        }
        if(LocalDateTime.now().isAfter(card.getExpirationDate())){
            throw new ServiceException(CommonRespCode.CARD_CODE_IS_EXPIRATION);
        }else{
            card.setRedeemedUid(loginIdAsLong);
            card.setRedemptionDate(LocalDateTime.now());
            card.setStatus(Constants.CardStatusEnum.SUCCESS.getStatus());
            baseMapper.updateById(card);
        }
        BigDecimal amount = card.getAmount();
        String reqId = IdUtil.fastSimpleUUID();
        Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.CARD_RECHARGE;
        Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
        accountService.commonUpdateAccount(loginIdAsLong, amount, reqId, code, recharge, directionTypeEnum, "卡密兑换");

    }

    @Override
    public List<Card> selectByBatchNo(String batchNo) {

        return baseMapper.selectList(new LambdaQueryWrapper<Card>().eq(Card::getBatch, batchNo));
    }


    private static String generateBatchNumber() {
        // 生成格式化器，定义要输出的日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        // 格式化日期时间对象得到字符串表示
        LocalDateTime dateTime = LocalDateTime.now();
        String dateStr = dateTime.format(formatter);
        return "BATCH_" + dateStr;
    }

}
