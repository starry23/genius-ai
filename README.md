# :smirk: genius-ai

> geniusAI项目是一个商用版服务平台，基于Java语言实现服务端功能，前端使用React框架。用户可以私有化部署商业版GPT平台。支持两种大模型： openai及文心一言 。支持openai官方所有api，包括余额查询、模型检索、Completions chatgpt对话、Images 图片模型、模型自定义训练、文件上传自定义模型、微调、文本审核和敏感词鉴别，以及GPT 3.5、4.0和4.0-32k等功能 亮点: 基于向量数据库milvus的知识库 可实现和企业对接客服 打造私有化 的知识库 训练专属ai 聊天思维导图  大模型集成微信对话平台实现公众号/小程序/H5的客接入  GPTS应用商店

## :raised_hands: 为何开源?
1. 补缺java语言的商业版ChatGPT
2. 加速创新,提高软件质量
3. 共同学习

## 集成框架

| 名称                        |   版本   | 
|:--------------------------|:------:|
| springboot                | 3.2.4  | 
| redisson                  | 3.28.0 | 
| mybatis-plus-spring-boot3 | 3.5.6  | 
| IJPay                     | 2.9.6  | 
| knife4j                   | 4.1.0  | 
| sa-token-spring-boot3     | 1.37.0 | 
| weixin-java-mp            | 4.4.0  | 
| jtokkit                   | 0.6.1  |

## :rocket: Docker快速开始

```shell
git clone https://github.com/apetoo/genius-ai.git
cd genius-ai
mvn clean package -Dmaven.test.skip=true
docker-compose up -d
```
>前端:http://localhost:8080  后台管理: http://localhost/admin 
 

## 核心功能
1. 知识库
2. GPTS
3. Midjourney
4. DALL*E
5. 微信智能对话开放平台
6. 会员及消费支付系统
7. 开放平台(三方免密登录)

以上只列举了核心功能 其他功能请看文档: https://www.yuque.com/apetoo/qri3fg/mcv5pmzcosn2yyq4

## 部分截图


## 打赏

## 其他
> 如果觉得这个项目对您有所帮助，请帮忙点个star

