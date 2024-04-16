/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-06-29 11:10:03
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-03-31 20:45:52
 */
import chatIcon from "../../public/assets/imgs/chat.svg";
import darw from "../../public/assets/imgs/darw.svg";
import navigation from "../../public/assets/imgs/navigation.svg";
import menuApplication from "../../public/assets/imgs/menu-application.svg";
import knowsBase from "../../public/assets/imgs/knows-base.svg";
import minMapMenuIcon from "../../public/assets/mindMap/minMapMenuIcon.svg";
import gptsMenuIcon from "../../public/assets/gpts/gptsMenuIcon.svg";

// 高亮
import chatIconAc from "@/assets/chat/chatAc.png";
import mjAc from "@/assets/chat/mjAc.png";
import navigationAc from "@/assets/chat/navigationAc.png";
import applicationAc from "@/assets/chat/applicationAc.png";
import knowledgeAc from "@/assets/chat/knowledgeAc.png";
import mindMapAc from "@/assets/chat/mindMapAc.png";
import dalleAc from "@/assets/chat/dalleAc.png";
import gptsAc from "@/assets/chat/gptsAc.png";

// 暗黑
import chatIconDark from "@/assets/chat/chatDark.png";
import mjDark from "@/assets/chat/mjDark.png";
import navigationDark from "@/assets/chat/navigationDark.png";
import applicationDark from "@/assets/chat/applicationDark.png";
import knowledgeDark from "@/assets/chat/knowledgeDark.png";
import mindMapDark from "@/assets/chat/mindMapDark.png";
import dalleDark from "@/assets/chat/dalleDark.png";
import gptsDark from "@/assets/chat/gptsDark.png";

export const menuDictionary = [
  {
    id: 1,
    label: "聊天",
    path: "/ai/home",
    icon: chatIcon,
    iconAc: chatIconAc,
    iconDark: chatIconDark,
  },
  {
    id: 2,
    label: "MJ绘画",
    path: "/ai/mj/generate",
    icon: darw,
    iconAc: mjAc,
    iconDark: mjDark,
  },
  {
    id: 3,
    label: "AI导航",
    path: "/ai/aiNavigation",
    icon: navigation,
    iconAc: navigationAc,
    iconDark: navigationDark,
  },
  {
    id: 4,
    label: "应用广场",
    path: "/ai/aiAssistant",
    icon: menuApplication,
    iconAc: applicationAc,
    iconDark: applicationDark,
  },
  {
    id: 5,
    label: "知识库",
    path: "/ai/doc/knowledge",
    icon: knowsBase,
    iconAc: knowledgeAc,
    iconDark: knowledgeDark,
  },
  {
    id: 6,
    label: "脑图",
    path: "/ai/mindMap",
    icon: minMapMenuIcon,
    iconAc: mindMapAc,
    iconDark: mindMapDark,
  },
  {
    id: 7,
    label: "DALLE",
    path: "/ai/dalle3",
    icon: darw,
    iconAc: dalleAc,
    iconDark: dalleDark,
  },
  {
    id: 8,
    label: "GPTS",
    path: "/ai/gpts",
    icon: gptsMenuIcon,
    iconAc: gptsAc,
    iconDark: gptsDark,
  },

  {
    id: 9,
    label: "微信客服",
    path: "/ai/wechatboot",
    icon: chatIcon,
    iconAc: chatIconAc,
    iconDark: chatIconDark,
  },
];
