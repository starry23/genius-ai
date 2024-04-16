package cn.apeto.geniusai.server.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author apeto
 * @create 2023/6/20 7:34 上午
 */
public class MathUtils {


    /**
     * 打折
     *
     * @param amount
     * @param discount
     * @return
     */
    public static BigDecimal multiplyDiscountHalfUp2(BigDecimal amount, BigDecimal discount) {
        return amount.multiply(discount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 折扣显示
     *
     * @param amount
     * @return
     */
    public static String discountHalfUp2(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(10)).setScale(2, RoundingMode.HALF_UP).intValue() + "";
    }

    public static BigDecimal rateHalfUp2(BigDecimal amount, Integer rate) {
        return amount.multiply(BigDecimal.valueOf(rate)).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);

    }

    public static Integer toFen(BigDecimal money) {
        return money.multiply(BigDecimal.valueOf(100L)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public static BigDecimal toYuan(int money) {
        return new BigDecimal(money).divide(BigDecimal.valueOf(100L),2,RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        System.out.println(multiplyDiscountHalfUp2(BigDecimal.valueOf(100), BigDecimal.valueOf(0.8)));
        System.out.println(discountHalfUp2(BigDecimal.valueOf(0.8)));
    }
}
