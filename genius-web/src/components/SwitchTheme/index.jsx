import React, { useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import { systemThemAction } from "@/store/actions/home_action";
import { getLocalStorage, setLocalStorage } from "@/utils";
import "./index.less";
const SwitchTheme = () => {
  const dispatch = useDispatch();

  const switchThemeCheckboxRef = useRef(null);
  const theme = useRef("dark");

  const changeTheme = () => {
    theme.current = getLocalStorage("theme") || "dark";
    switch (theme.current) {
      case "dark":
        theme.current = "light";
        setLocalStorage("theme", theme.current);
        switchThemeCheckboxRef.current.checked = false;
        break;
      case "light":
        theme.current = "dark";
        setLocalStorage("theme", theme.current);
        switchThemeCheckboxRef.current.checked = true;
        break;
      default:
        theme.current = "dark";
        setLocalStorage("theme", theme.current);
        switchThemeCheckboxRef.current.checked = true;
        break;
    }

    document.body.setAttribute("theme-mode", theme.current);

    dispatch(systemThemAction(theme.current));
  };

  // 获取默认主题
  useEffect(() => {
    switchThemeCheckboxRef.current = document.getElementById(
      "switch_theme-checkbox"
    );
    theme.current = getLocalStorage("theme") || "dark";
    switch (theme.current) {
      case "dark":
        switchThemeCheckboxRef.current.checked = true;
        break;
      case "light":
        switchThemeCheckboxRef.current.checked = false;
        break;
      default:
        switchThemeCheckboxRef.current.checked = true;
        break;
    }
  }, []);
  return (
    <label className="switch_theme-warp">
      <input
        onChange={changeTheme}
        id="switch_theme-checkbox"
        type="checkbox"
      />
      <span className="switch_theme-slider"></span>
    </label>
  );
};

export default SwitchTheme;
