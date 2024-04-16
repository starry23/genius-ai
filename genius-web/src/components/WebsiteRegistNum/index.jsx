import React from "react";
import { useSelector } from "react-redux";
import styles from "./index.module.less";

const WebsiteRegistNum = () => {
  const sysConfig = useSelector((state) => state.sysConfig);
  return (
    <div className={styles.websiteRegistNum}>
      {sysConfig?.filingNumber ? (
        <a target="_blank" href="https://beian.miit.gov.cn/">
          互联网ICP备案: {sysConfig?.filingNumber}
        </a>
      ) : (
        ""
      )}
    </div>
  );
};

export default WebsiteRegistNum;
