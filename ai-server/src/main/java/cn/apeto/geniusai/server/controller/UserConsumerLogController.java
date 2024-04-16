package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ReqPage;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.vo.UserConsumerLogsVO;
import cn.apeto.geniusai.server.entity.BaseEntity;
import cn.apeto.geniusai.server.entity.UserConsumerLog;
import cn.apeto.geniusai.server.service.UserConsumerLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户消耗日志 前端控制器
 * </p>
 *
 * @author apeto
 * @since 2023-08-30 03:37:25
 */
@Tag(name = "用户消费日志")
@RestController
@RequestMapping("/api/userConsumerLog")
public class UserConsumerLogController {

    @Autowired
    private UserConsumerLogService userConsumerLogService;

    @Operation(summary = "用户消耗")
    @GetMapping("/list")
    public ResponseResult<IPage<UserConsumerLogsVO>> list(ReqPage reqPage) {
        LambdaQueryWrapper<UserConsumerLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserConsumerLog::getUserId, StpUtil.getLoginIdAsLong());
        queryWrapper.orderByDesc(BaseEntity::getId);
        Page<UserConsumerLog> page = userConsumerLogService.page(new Page<>(reqPage.getCurrent(), reqPage.getSize()), queryWrapper);
        IPage<UserConsumerLogsVO> result = page.convert(this::convert);
        return ResponseResultGenerator.success(result);
    }


    @Operation(summary = "查询最后一条")
    @GetMapping("/last")
    public ResponseResult<UserConsumerLogsVO> last() {
        LambdaQueryWrapper<UserConsumerLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserConsumerLog::getUserId, StpUtil.getLoginIdAsLong());
        queryWrapper.orderByDesc(BaseEntity::getId);
        queryWrapper.last("limit 1");
        UserConsumerLog userConsumerLog = userConsumerLogService.getOne(queryWrapper);
        return ResponseResultGenerator.success(convert(userConsumerLog));
    }

    private UserConsumerLogsVO convert(UserConsumerLog userConsumerLog) {
        UserConsumerLogsVO vo = new UserConsumerLogsVO();
        vo.setProductTypeDesc(Constants.ProductTypeEnum.getByEnum(userConsumerLog.getProductType()).getName());
        vo.setOriginalAmount(userConsumerLog.getOriginalAmount());
        vo.setRealAmount(userConsumerLog.getRealAmount());
        vo.setMemberAmount(userConsumerLog.getMemberAmount());
        vo.setDiscountAmount(userConsumerLog.getDiscountAmount());
        return vo;
    }

}
