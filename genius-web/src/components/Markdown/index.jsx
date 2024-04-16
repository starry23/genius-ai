/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-05-30 08:28:05
 * @LastEditors: jl.g
 * @LastEditTime: 2023-05-30 16:26:05
 */
import ReactMarkdown from "react-markdown";
import "katex/dist/katex.min.css";
import RemarkMath from "remark-math";
import RehypeKatex from "rehype-katex";
import RemarkGfm from "remark-gfm";
import RehypePrsim from "rehype-prism-plus";
import { useRef } from "react";
import { copyToClipboardFn } from "@/utils";

export function PreCode(props) {
  const ref = useRef(null);

  return (
    <pre ref={ref}>
      <span
        className="copy-code-button"
        onClick={() => {
          if (ref.current) {
            const code = ref.current.innerText;
            copyToClipboardFn(code, "代码复制成功");
          }
        }}
      ></span>
      {props.children}
    </pre>
  );
}

export function Markdown(props) {
  let content = props.content;
  return (
    <ReactMarkdown
      remarkPlugins={[RemarkMath, RemarkGfm]}
      rehypePlugins={[RehypeKatex, [RehypePrsim, { ignoreMissing: true }]]}
      components={{
        pre: PreCode,
      }}
    >
      {content}
    </ReactMarkdown>
  );
}
