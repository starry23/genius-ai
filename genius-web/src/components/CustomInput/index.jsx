import React, { useRef } from "react";
import searchBtn from "../../../public/assets/gpts/searchBtn.svg";

import styles from "./index.module.less";
const CustomInput = (props) => {
  const {
    value,
    disabled,
    search,
    style,
    showSearch,
    placeholder,
    onChange,
    type = "text",
  } = props;

  const inputValue = useRef("");
  const onInputChange = (value) => {
    inputValue.current = value;
    onChange && onChange(value);
  };

  const handlerSearch = () => {
    search(inputValue.current);
  };
  return (
    <div
      className={styles.customInputWarp}
      style={{
        ...style,
      }}
    >
      <input
        placeholder={placeholder}
        value={props.value}
        onChange={(e) => onInputChange(e.target.value)}
        disabled={disabled}
        className="inputDom"
        type={type}
      />

      {showSearch ? (
        <img
          onClick={handlerSearch}
          className="searchIcon"
          src={searchBtn}
          alt=""
        />
      ) : (
        ""
      )}
    </div>
  );
};

export default CustomInput;
