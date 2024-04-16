package cn.apeto.geniusai.server.handler.rights;

import cn.apeto.geniusai.server.entity.MemberRights;
import lombok.Data;

@Data
public class BenefitPair {
    private Benefit benefit;
    private MemberRights memberRights;
    private int weight;

    public BenefitPair(Benefit benefit, int weight) {
        this.benefit = benefit;
        this.weight = weight;
    }
}
