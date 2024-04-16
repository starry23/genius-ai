import React from "react";

const Equity = ({ memberUserInfo }) => {
  return (
    <div className="equityWarp">
      {memberUserInfo?.rightConfigVoList &&
        memberUserInfo?.rightConfigVoList.map((v, index) => (
          <div className="eqity_item" key={index}>
            {index + 1 + "." + v.rightsDesc}
          </div>
        ))}
    </div>
  );
};

export default Equity;
