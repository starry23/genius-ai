package cn.apeto.geniusai.server.controller.admin;

import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.CardDTO;
import cn.apeto.geniusai.server.domain.dto.CardPageDTO;
import cn.apeto.geniusai.server.entity.Card;
import cn.apeto.geniusai.server.service.CardService;
import cn.apeto.geniusai.server.utils.StpManagerUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author apeto
 * @create 2023/7/6 15:02
 */
@Tag(name = "后台管理系统兑换码相关")
@Slf4j
@RestController
@RequestMapping("/api/manager/card")
@SaCheckLogin(type = StpManagerUtil.TYPE)
public class ManagerCardController {

    @Autowired
    private CardService cardService;

    /**
     * 2. B端兑换码生成(1. 兑换码过期时间 2.生成数量 )
     */
    @Operation(summary = "B端兑换码生成")
    @PostMapping("/create")
    public ResponseResult<?> numberCreate(@RequestBody CardDTO cardDTO) {
        cardService.numberCreate(cardDTO.getExpirationDate(), cardDTO.getNum(), cardDTO.getEachAmount());
        return ResponseResultGenerator.success();
    }

    /**
     * 1. B端兑换码列表
     */
    @Operation(summary = "B端兑换码列表")
    @GetMapping("/list")
    public ResponseResult<IPage<Card>> numberList(CardPageDTO cardPageDTO) {
        IPage<Card> page = cardService.getPage(cardPageDTO);
        return ResponseResultGenerator.success(page);
    }


    /**
     * 3. B端兑换码列表作废按钮(状态置为作废)
     */
    @Operation(summary = "B端兑换码列表作废按钮")
    @DeleteMapping("/invalidate/{id}")
    public ResponseResult<?> numberInvalidate(@PathVariable("id") Long id) {
        cardService.numberInvalidate(id);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "导出")
    @GetMapping("/export/{batchNo}")
    public void export(@PathVariable("batchNo") String batchNo, HttpServletResponse response) {

        try (ServletOutputStream out = response.getOutputStream();
             ExcelWriter writer = ExcelUtil.getWriter(true)
        ) {

            List<Card> cardList = cardService.selectByBatchNo(batchNo);
            writer.write(cardList, true);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + batchNo + ".xlsx");
            writer.flush(out, true);
        } catch (Exception e) {
            log.error("导出异常", e);
        }
    }
}
