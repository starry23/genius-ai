/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2022-06-13 14:24:04
 * @LastEditors: jinglin.gao
 * @LastEditTime: 2024-04-08 14:38:38
 * @Author: jl.g
 */

const CracoLessPlugin = require("craco-less");
const ModuleScopePlugin = require("react-dev-utils/ModuleScopePlugin");
const path = require("path");
const resolve = (dir) => path.resolve(__dirname, dir);
const env = process.env.NODE_ENV;
const UglifyJsPlugin = require("uglifyjs-webpack-plugin");
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");

module.exports = {
  webpack: {
    alias: {
      // @映射src路径
      "@": resolve("src"),
      components: resolve("src/components"),
    },

    // 删除控制台注释
    plugins: [].concat(
      process.env.NODE_ENV === "production" ? [
        new UglifyJsPlugin({
          // 删除console
          uglifyOptions: {
            parallel: true,
            warnings: false,
            compress: {
              drop_console: true, //是否清除所有console
              drop_debugger: true, //是否清除debugger
              pure_funcs: ["console.log"], //drop_console 设置false,需要特殊清除的
            },
          },
        }),
      ] : []
    ).concat([new NodePolyfillPlugin()]),

    configure: (webpackConfig, {
      env,
      paths
    }) => {
      // 修改build的生成文件名称
      paths.appBuild = "base_web";
      webpackConfig.output = {
        ...webpackConfig.output,
        path: path.resolve(__dirname, "base_web"),
        publicPath: process.env.REACT_APP_PUBLIC_PATH,
      };
      // 设置可以从src外部通过相对路径的方式引入其他文件
      webpackConfig.resolve.plugins = webpackConfig.resolve.plugins.filter(
        (plugin) => !(plugin instanceof ModuleScopePlugin)
      );
      return webpackConfig;
    },
  },
  plugins: [{
    plugin: CracoLessPlugin,
    options: {
      lessLoaderOptions: {
        lessOptions: {
          modifyVars: {
            "@primary-color": "#1DA57A"
          },
          javascriptEnabled: true,
        },
      },
    },
  }, ],
  devServer: {
    open: true,
    host: "0.0.0.0",
    port: 8888,
    https: false,
    hot: true,
    proxy: {
      "/api": {
        // target: "http://chat2.warape.top",
        // target: "https://chat.huanbodadi.com/",
        target: "https://chat.apeto.cn",
        // target: "http://164.155.208.133:8081",
        changeOrigin: true,
        secure: false,
      },

      "/api/ws-genius": {
        // target: "https://chat.huanbodadi.com/",
        // target: "http://chat2.warape.top",
        target: "http://164.155.208.133:8081",
        ws: true, //开启ws, 如果是http代理此处可以不用设置
        changeOrigin: true,
        secure: false,
      },
    },
  },
};
