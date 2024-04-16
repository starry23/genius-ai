import React, { useState } from "react";
import { Input, message } from "antd";
import CustomBtn from "@/components/CustomBtn";
import { sendFeedback } from "@/api/user";
import _ from "lodash";
const Propose = () => {
  const [proposeContent, setProposeContent] = useState("");

  const changeProposeContent = (e) => {
    setProposeContent(e.target.value);
  };

  /**
   * @description: 提交意见
   * @return {*}
   * @author: jl.g
   */
  const submit = async () => {
    if (!proposeContent) {
      message.error("请输入反馈内容");
      return;
    }
    try {
      let data = {
        desc: proposeContent,
      };
      let res = await sendFeedback(data);
      if (res.code === 200) {
        message.success("反馈成功，感谢您的建议");
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="proposeWarp">
      <div className="proposeTitle">
        为了您的使用更加便捷,我们期待您的建议。
      </div>

      <div className="propose_content">
        <Input.TextArea
          onChange={changeProposeContent}
          value={proposeContent||''}
          rows={4}
          placeholder="请输入您的建议与反馈"
        />
      </div>

      <div className="user_detail_bottom">
        <CustomBtn
          height={35}
          onClick={_.debounce(submit, 500)}
          className="tolls_btn"
        >
          提交
        </CustomBtn>
      </div>
    </div>
  );
};

export default Propose;
