import { getSessionStorage } from "@/utils";

export const makeCashbackInfoDom = () => {
  // 获取系统配置
  const sysConfig = JSON.parse(getSessionStorage("sysConfig"));
  let rebateConfig = sysConfig?.activitySetting?.rebateConfig || null;
  let activitySetting = sysConfig?.activitySetting || null;

  let cashbackInfoDom = "";
  if (rebateConfig) {
    cashbackInfoDom = (
      <div className="cashbackInfoDom">
        {activitySetting?.inviteGiveCurrency ? (
          <div className="cashbackInfoLevel">
            <span> 邀请赠送: </span>
            <span>{activitySetting.inviteGiveCurrency}</span>
            <span>{sysConfig.currencyName}</span>
          </div>
        ) : (
          ""
        )}

        {activitySetting?.mpGiveCurrency ? (
          <div className="cashbackInfoLevel">
            <span> 关注公众号领取: </span>
            <span>{activitySetting.mpGiveCurrency}</span>
            <span>{sysConfig.currencyName}</span>
          </div>
        ) : (
          ""
        )}

        {activitySetting?.registerGiveCurrency ? (
          <div className="cashbackInfoLevel">
            <span> 新用户注册: </span>
            <span>{activitySetting.registerGiveCurrency}</span>
            <span>{sysConfig.currencyName}</span>
          </div>
        ) : (
          ""
        )}

        {rebateConfig?.rebate1?.num ? (
          <>
            {" "}
            <div className="cashbackInfoLevel">
              <span> 一级: </span>
              <span> 邀新数量: {rebateConfig?.rebate1?.num} </span>
              <span> 返佣比例： {rebateConfig?.rebate1?.rate}%</span>
            </div>
            <div className="cashbackInfoLevel">
              <span> 二级: </span>
              <span> 邀新数量: {rebateConfig?.rebate2?.num} </span>
              <span> 返佣比例： {rebateConfig?.rebate2?.rate}%</span>
            </div>
            <div className="cashbackInfoLevel">
              <span> 三级: </span>
              <span> 邀新数量: {rebateConfig?.rebate3?.num} </span>
              <span> 返佣比例： {rebateConfig?.rebate3?.rate}%</span>
            </div>
          </>
        ) : (
          ""
        )}
      </div>
    );
  }

  return cashbackInfoDom;
};
