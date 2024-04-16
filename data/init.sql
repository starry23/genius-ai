/*!40101 SET NAMES utf8mb4 */;

create table account
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    user_id         bigint                                not null comment '用户ID',
    account_balance decimal(10, 2)                        not null comment '账户余额',
    account_type    int(10)     default 10                not null comment '账户类型 10: 代币 20: RMB',
    create_by       varchar(64) default ''                not null comment '创建人',
    create_time     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by       varchar(64) default ''                not null comment '修改人',
    update_time     datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn              tinyint     default 1                 not null comment '0无效1有效'
)
    comment '账户表';

create index idx_user_id
    on account (user_id);

create table account_log
(
    id                   bigint unsigned auto_increment comment '主键'
        primary key,
    account_id           bigint                                null comment '账户ID',
    user_id              bigint                                not null comment '用户ID',
    amount               decimal(10, 2)                        not null comment '变动金额',
    balance_amount       decimal(10, 2)                        not null comment '变动后余额',
    request_id           varchar(54) default ''                not null comment '请求ID',
    outside_code         varchar(54) default ''                not null comment '业务单号',
    log_description      varchar(54)                           not null comment '描述',
    log_description_type int(5)                                not null comment '操作日志类型',
    direction_type       tinyint     default 10                not null comment '类型 10: 转入 20:转出',
    create_by            varchar(64) default ''                not null comment '创建人',
    create_time          datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by            varchar(64) default ''                not null comment '修改人',
    update_time          datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn                   tinyint     default 1                 not null comment '0无效1有效'
)
    comment '账户表日志';

create index idx_account_id
    on account_log (account_id);

create index idx_user_id
    on account_log (user_id);

create index mj_image_info
    on account_log (user_id);

create table advertise_config
(
    id             bigint unsigned auto_increment comment '主键'
        primary key,
    advertise_name varchar(64)  default ''                not null comment '广告名称',
    advertise_link varchar(500) default ''                not null comment '广告链接',
    img_link       varchar(500) default ''                not null comment '图片链接',
    advertise_type varchar(20)  default ''                not null comment '广告类型 10: 加入我们(第一位) 11:加入我们(第二位) 20:右侧广告位列表',
    create_by      varchar(64)  default ''                not null comment '创建人',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by      varchar(64)  default ''                not null comment '修改人',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn             tinyint      default 1                 not null comment '0无效1有效'
)
    comment '广告配置表';

create table ai_role
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    role_name   varchar(64)   default ''                not null comment '角色名称',
    role_desc   varchar(200)  default ''                null comment '角色描述',
    role_type   tinyint       default 1                 not null comment '1: openai常规',
    image_url   varchar(200)                            null comment '图片',
    prompt      varchar(5000) default ''                not null comment '提示词',
    create_by   varchar(64)   default ''                not null comment '创建人',
    create_time datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)   default ''                not null comment '修改人',
    update_time datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint       default 1                 not null comment '0无效1有效'
)
    comment 'AI角色';

create table bot_chat_log
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    user_id     bigint                                not null comment '用户ID',
    bot_token   varchar(64) default ''                not null comment 'token',
    chat_role   varchar(54) default ''                not null comment '角色',
    content     text                                  not null comment '信息',
    open_id     varchar(64)                           not null comment 'openid',
    create_by   varchar(64) default ''                not null comment '创建人',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '修改人',
    update_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint     default 1                 not null comment '0无效1有效'
);

create index idx_bot_token
    on bot_chat_log (bot_token);

create index idx_open_id
    on bot_chat_log (open_id);

create index idx_user_id
    on bot_chat_log (user_id);

create table bot_config
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    user_id      bigint                                 not null comment '用户ID',
    app_id       varchar(20)  default ''                not null comment 'app_id',
    aesKey       varchar(64)  default ''                not null comment 'aesKey',
    token        varchar(64)  default ''                not null comment 'token',
    bot_name     varchar(20)  default ''                not null comment '机器人名称',
    bot_desc     varchar(50)  default ''                not null comment '机器人描述',
    kefu_name    varchar(20)  default ''                not null comment '客服名称',
    kefu_avatar  varchar(200) default ''                not null comment '客服头像',
    h5_url       varchar(200) default ''                not null comment 'H5机器人地址',
    product_type tinyint(1)   default 1                 not null comment '产品类型',
    state        tinyint(1)   default 1                 not null comment '状态 0下线 1上线',
    knowledge_id bigint       default -1                not null comment '知识库ID',
    create_by    varchar(64)  default ''                not null comment '创建人',
    create_time  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    varchar(64)  default ''                not null comment '修改人',
    update_time  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn           tinyint      default 1                 not null comment '0无效1有效'
)
    comment '机器人配置';

create index idx_app_id
    on bot_config (app_id);

create index idx_token
    on bot_config (token);

create index idx_user_id
    on bot_config (user_id);

create table card
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    redeemed_uid    bigint                                 null comment '已兑换的用户ID。如果尚未被兑换，则此值为空。',
    code            varchar(255)                           not null comment '兑换码。',
    expiration_date datetime                               not null comment '兑换码过期时间。',
    redemption_date datetime                               null comment '兑换时间。如果尚未被兑换，则此值为空。',
    amount          decimal(10, 2)                         not null comment '兑换额度。',
    status          tinyint      default 0                 null comment '状态0:待兑换,1:已兑换,2:已过期,3:已作废',
    batch           varchar(255) default ''                not null comment '批次号，用于标识该兑换码属于哪个批次。',
    create_by       varchar(64)  default ''                not null comment '创建人',
    create_time     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by       varchar(64)  default ''                not null comment '修改人',
    update_time     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn              tinyint      default 1                 not null comment '0无效1有效',
    constraint unq_code
        unique (code)
)
    comment '兑换码';

create index idx_redeemed_uid
    on card (redeemed_uid);

create table chat_detail_log
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    user_id      bigint                                not null comment '用户ID',
    request_id   varchar(64) default ''                not null comment '请求id',
    chat_role    varchar(54) default ''                not null comment '角色',
    content      text                                  not null comment '信息',
    create_by    varchar(64) default ''                not null comment '创建人',
    create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    varchar(64) default ''                not null comment '修改人',
    update_time  datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn           tinyint     default 1                 not null comment '0无效1有效',
    role_id      bigint      default -1                not null comment '角色ID',
    product_type tinyint(1)  default 1                 not null comment '1:GPT3.5  2:GPT4.0  3:MJ绘画 4: wxqf',
    token        bigint      default 0                 not null comment 'token',
    log_type     varchar(10) default 'CHAT'            null comment '日志类型  CHAT: 聊天 KNOWLEDGE: 知识库'
)
    comment '问答记录表';

create index idx_request_id
    on chat_detail_log (request_id);

create index idx_role_id
    on chat_detail_log (role_id);

create index idx_user_id
    on chat_detail_log (user_id);

create table chat_gpt_config
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    api_key     varchar(200) default ''                not null comment 'apiKey',
    create_by   varchar(64)  default ''                not null comment '创建人',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)  default ''                not null comment '修改人',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint      default 1                 not null comment '0无效1有效'
)
    comment '用户信息表';

create table currency_config
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    currency_count  int(10)                                  not null comment '币数量',
    line_amount     decimal(10, 2) default 0.00              not null comment '划线金额',
    currency_amount decimal(10, 2)                           not null comment '商品金额',
    recommend       tinyint(1)     default 0                 not null comment '推荐',
    create_by       varchar(64)    default ''                not null comment '创建人',
    create_time     datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by       varchar(64)    default ''                not null comment '修改人',
    update_time     datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn              tinyint        default 1                 not null comment '0无效1有效'
)
    comment '币配置';

create table dall_e_image_info
(
    id               bigint unsigned auto_increment comment '主键'
        primary key,
    user_id          bigint                                  not null comment '用户ID',
    file_id          varchar(64)   default ''                not null comment '文件ID',
    openai_image_url varchar(500)  default ''                not null comment 'openai图片地址',
    cos_url          varchar(500)  default ''                not null comment 'url',
    file_prompt      varchar(2000) default ''                not null comment '提示词',
    fail_reason      varchar(100)  default ''                not null comment '失败原因',
    create_by        varchar(64)   default ''                not null comment '创建人',
    create_time      datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by        varchar(64)   default ''                not null comment '修改人',
    update_time      datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn               tinyint       default 1                 not null comment '0无效1有效'
)
    comment 'mj图片信息';

create index idx_file_id
    on dall_e_image_info (file_id);

create index idx_user_id
    on dall_e_image_info (user_id);

create table exchange_card_detail
(
    id             bigint unsigned auto_increment comment '主键'
        primary key,
    user_id        bigint                                not null comment '用户ID',
    member_card_id bigint                                not null comment '会员卡ID',
    total_count    int         default 0                 not null comment '总次数',
    surplus_count  int         default 0                 not null comment '剩余次数',
    expires_time   datetime                              not null comment '失效时间',
    exchange_state tinyint                               not null comment '状态 1:已兑换 2:过期',
    create_by      varchar(64) default ''                not null comment '创建人',
    create_time    datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by      varchar(64) default ''                not null comment '修改人',
    update_time    datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn             tinyint     default 1                 not null comment '0无效1有效',
    account_id     bigint                                null comment '账户ID'
)
    comment '兑换卡详情';

create index idx_expires_time
    on exchange_card_detail (expires_time);

create index idx_member_card_id
    on exchange_card_detail (member_card_id);

create index idx_user_id
    on exchange_card_detail (user_id);

create table feedback
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    user_id     bigint                                 not null comment '用户ID',
    proposals   varchar(500) default ''                not null comment '建议',
    create_by   varchar(64)  default ''                not null comment '创建人',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)  default ''                not null comment '修改人',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint      default 1                 not null comment '0无效1有效'
)
    comment '用户反馈表';

create index idx_f_user_id
    on feedback (user_id);

create table file_info
(
    id                 bigint unsigned auto_increment comment '主键'
        primary key,
    user_id            bigint       default -1                not null comment '用户ID',
    path               varchar(500) default ''                not null comment '地址',
    original_file_name varchar(100) default ''                not null comment '原文件名',
    type               tinyint                                not null comment 'oss类型 0本地 1七牛云 2腾讯云 3阿里云',
    view_type          tinyint      default 1                 not null comment '显示类型 1:c端  2:b端',
    create_by          varchar(64)  default ''                not null comment '创建人',
    create_time        datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by          varchar(64)  default ''                not null comment '修改人',
    update_time        datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn                 tinyint      default 1                 not null comment '0无效1有效'
)
    comment '附件信息';

create index idx_user_id
    on file_info (user_id);

create table invite_log
(
    id                bigint unsigned auto_increment comment '主键'
        primary key,
    invite_user_id    bigint                                not null comment '邀请人用户ID',
    to_invite_user_id bigint                                not null comment '被邀请人用户ID',
    currency_count    int(10)     default 0                 not null comment '代币数量',
    create_by         varchar(64) default ''                not null comment '创建人',
    create_time       datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by         varchar(64) default ''                not null comment '修改人',
    update_time       datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn                tinyint     default 1                 not null comment '0无效1有效'
)
    comment '邀请记录';

create index idx_invite_user_id
    on invite_log (invite_user_id);

create index idx_to_invite_user_id
    on invite_log (to_invite_user_id);

create table item_partition
(
    id             bigint unsigned auto_increment comment '主键'
        primary key,
    item_id        bigint                                 not null comment '项目ID',
    user_id        bigint                                 not null comment '用户id',
    partition_name varchar(64)                            not null comment '分区名字',
    partition_desc varchar(128) default ''                not null comment '分区描述',
    partition_code varchar(128)                           not null comment '向量数据库分区名',
    create_by      varchar(64)  default ''                not null comment '创建人',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by      varchar(64)  default ''                not null comment '修改人',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn             tinyint      default 1                 not null comment '0无效1有效'
)
    comment '分区表';

create index idx_item_id
    on item_partition (item_id);

create index idx_user_id
    on item_partition (user_id);

create table item_resource
(
    id            bigint unsigned auto_increment comment '主键'
        primary key,
    user_id       bigint                                 not null comment '用户ID',
    item_id       bigint                                 not null comment '知识库项目ID',
    file_name     varchar(100) default ''                not null comment '文件名称',
    original_name varchar(100) default ''                not null comment '文件原名',
    file_path     varchar(500) default ''                not null comment '对象存储地址',
    partition_id  bigint       default -1                not null comment '分区ID',
    summary_desc  varchar(500) default ''                not null comment '总结',
    q1            varchar(300) default ''                not null comment '问题1',
    q2            varchar(300) default ''                not null comment '问题2',
    create_by     varchar(64)  default ''                not null comment '创建人',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by     varchar(64)  default ''                not null comment '修改人',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn            tinyint      default 1                 not null comment '0无效1有效'
)
    comment '知识库项目资源表';

create index idx_knowledge_id
    on item_resource (item_id);

create index idx_partition_id
    on item_resource (partition_id);

create index idx_user_id
    on item_resource (user_id);

create table knowledge_chat_binding
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    user_id      bigint                                not null comment '用户ID',
    knowledge_id bigint                                not null comment '知识库ID',
    chat_log_req varchar(64)                           not null comment '聊天日志ID',
    create_by    varchar(64) default ''                not null comment '创建人',
    create_time  datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    varchar(64) default ''                not null comment '修改人',
    update_time  datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn           tinyint     default 1                 not null comment '0无效1有效'
)
    comment '知识库&聊天记录绑定';

create index idx_chat_log_id
    on knowledge_chat_binding (chat_log_req);

create index idx_knowledge_id
    on knowledge_chat_binding (knowledge_id);

create index idx_user_id
    on knowledge_chat_binding (user_id);

create table knowledge_item
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    user_id     bigint                                 not null comment '用户ID',
    item_name   varchar(50)  default ''                not null comment '项目名称',
    item_desc   varchar(500) default ''                not null comment '项目描述',
    create_by   varchar(64)  default ''                not null comment '创建人',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)  default ''                not null comment '修改人',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint      default 1                 not null comment '0无效1有效'
)
    comment '知识库项目表';

create index idx_item_name
    on knowledge_item (item_name);

create index idx_user_id
    on knowledge_item (user_id);

create table member_card
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    card_code   varchar(64)                              not null comment '会员卡编码',
    card_name   varchar(64)                              not null comment '会员卡名',
    card_state  tinyint        default 1                 not null comment '卡状态',
    amount      decimal(10, 2)                           not null comment '卡金额',
    line_amount decimal(10, 2) default 0.00              not null comment '划线金额',
    card_day    int                                      not null comment '卡天数',
    recommend   tinyint(1)     default 0                 not null comment '推荐',
    create_by   varchar(64)    default ''                not null comment '创建人',
    create_time datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)    default ''                not null comment '修改人',
    update_time datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint        default 1                 not null comment '0无效1有效'
)
    comment '会员卡';

create index idx_card_code
    on member_card (card_code);

create table member_rights
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    rights_name varchar(64)   default ''                not null comment '策略名称',
    member_code varchar(64)   default ''                not null comment '会员编码',
    rights_desc varchar(100)  default ''                not null comment '权益描述',
    rights_type int(5)        default 1                 not null comment '权益类型',
    count       int(10)       default 0                 not null comment '次数',
    discount    decimal(3, 2) default 1.00              not null comment '折扣',
    create_by   varchar(64)   default ''                not null comment '创建人',
    create_time datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)   default ''                not null comment '修改人',
    update_time datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint       default 1                 not null comment '0无效1有效'
)
    comment '会员策略';

create index idx_member_code
    on member_rights (member_code);

create table mj_image_info
(
    id                 bigint unsigned auto_increment comment '主键'
        primary key,
    user_id            bigint                                  not null comment '用户ID',
    file_id            varchar(64)   default ''                not null comment '文件ID',
    mj_id              varchar(64)   default ''                not null comment 'mjID',
    cos_url            varchar(500)  default ''                not null comment 'url',
    file_action        varchar(54)   default ''                not null comment '生成图片: IMAGINE,选中放大:UPSCALE,选中其中的一张图，生成四张相似的:VARIATION,重新生成:REROLL,图转prompt:DESCRIBE,多图混合:BLEND',
    file_status        varchar(54)   default ''                not null comment '文件状态: 未启动: NOT_START 已提交: SUBMITTED 执行中: IN_PROGRESS 失败: FAILURE 成功: SUCCES ',
    publish_state      varchar(20)   default 'NOT_PUBLISH'     not null comment '发布状态',
    file_prompt        varchar(2000) default ''                not null comment '提示词',
    progress           varchar(10)   default ''                not null comment '进度',
    fail_reason        varchar(100)  default ''                not null comment '失败原因',
    create_by          varchar(64)   default ''                not null comment '创建人',
    create_time        datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by          varchar(64)   default ''                not null comment '修改人',
    update_time        datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn                 tinyint       default 1                 not null comment '0无效1有效',
    service_type       varchar(20)   default 'apeto'           null comment '业务类型',
    change_button_info varchar(500)  default ''                not null comment '变换按钮信息',
    product_type       int           default 10                not null comment '类型'
)
    comment 'mj图片信息';

create index idx_file_id
    on mj_image_info (file_id);

create index idx_user_id
    on mj_image_info (user_id);

create table open_api_mch_config
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    mch_id      varchar(20)   default ''                not null comment '商户号',
    public_key  varchar(4096) default ''                not null comment '公钥',
    private_key varchar(4096) default ''                not null comment '私钥',
    menu_code   int(10)                                 null comment '菜单编码',
    mch_desc    varchar(100)  default ''                not null comment '商户描述',
    create_by   varchar(64)   default ''                not null comment '创建人',
    create_time datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)   default ''                not null comment '修改人',
    update_time datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint       default 1                 not null comment '0无效1有效'
)
    comment '开放api商户配置';

create index idx_mch_id
    on open_api_mch_config (mch_id);

create table payment_info
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    user_id      bigint                                   not null comment '用户ID',
    pay_sn       varchar(64)    default ''                not null comment '支付流水',
    goods_name   varchar(64)    default ''                not null comment '商品名称',
    goods_code   varchar(64)    default ''                not null comment '商品编码',
    goods_type   int(2)                                   null comment '商品类型: 10会员 20:流量包',
    out_pay_sn   varchar(64)    default ''                not null comment '三方支付流水',
    pay_amount   decimal(10, 2) default 0.00              not null comment '支付金额',
    pay_state    int            default 0                 not null comment '支付状态 0: 支付中 1: 已支付 2: 支付失败 3: 支付取消',
    pay_type     int                                      not null comment '支付类型 10:微信 20:支付宝',
    pay_merchant varchar(64)    default ''                not null comment '商户号',
    pay_time     datetime                                 null comment '支付时间',
    create_by    varchar(64)    default ''                not null comment '创建人',
    create_time  datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    varchar(64)    default ''                not null comment '修改人',
    update_time  datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn           tinyint        default 1                 not null comment '0无效1有效'
)
    comment '用户信息表';

create index idx_pay_sn
    on payment_info (pay_sn);

create index idx_user_id
    on payment_info (user_id);

create table product_consume_config
(
    id               bigint unsigned auto_increment comment '主键'
        primary key,
    product_type     tinyint(1)                            not null comment '1:GPT3.5  2:GPT4.0  3:MJ绘画',
    consume_currency int(10)     default 1                 not null comment '消耗虚拟币数量',
    consume_type     tinyint(1)  default 1                 not null comment '1.次数，2.token',
    create_by        varchar(64) default ''                not null comment '创建人',
    use_auth         tinyint(1)  default 2                 not null comment '使用权限 1: 会员 2: 常规用户 默认:2',
    status           tinyint(1)  default 1                 not null comment '状态 1: 上线 2:下线 默认1',
    create_time      datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by        varchar(64) default ''                not null comment '修改人',
    update_time      datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn               tinyint     default 1                 not null comment '0无效1有效'
)
    comment '产品类型消耗配置表';

create table resource_vector
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    resource_id bigint                                not null comment '资源ID',
    doc_id      bigint                                not null comment '向量文档ID',
    create_by   varchar(64) default ''                not null comment '创建人',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '修改人',
    update_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint     default 1                 not null comment '0无效1有效'
)
    comment '资源文档ID表';

create index resource_vector_doc_id_index
    on resource_vector (doc_id);

create index resource_vector_resource_id_index
    on resource_vector (resource_id);

create table session_record
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    user_id      bigint                                 not null comment '用户ID',
    request_id   varchar(64)  default ''                not null comment '请求id',
    session_name varchar(64)  default ''                not null comment '名字',
    session_desc varchar(200) default ''                not null comment '创建人',
    model        varchar(54)  default ''                not null comment 'model',
    icon         varchar(54)  default ''                not null comment 'icon',
    product_type tinyint(1)   default 1                 not null comment '1:GPT3.5  2:GPT4.0  3:MJ绘画 4: wxqf',
    role_id      bigint       default -1                not null comment '角色ID',
    log_type     varchar(10)  default 'CHAT'            not null comment '日志类型  CHAT: 聊天 KNOWLEDGE: 知识库',
    create_time  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    varchar(64)  default ''                not null comment '修改人',
    update_time  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn           tinyint      default 1                 not null comment '0无效1有效',
    create_by    varchar(64)  default 'system'          not null comment '创建人',
    file_id      bigint       default -1                null comment '文件ID'
)
    comment '会话记录';

create index idx_request_id
    on session_record (request_id);

create index idx_user_id
    on session_record (user_id);

create table share_item
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    user_id     bigint                                not null comment '用户ID',
    item_id     bigint                                not null comment '项目ID',
    uuid        varchar(64)                           not null comment 'UUID',
    is_enable   tinyint                               not null comment '是否开启',
    share_type  int(2)      default 10                not null comment '分享类型 10: web',
    create_by   varchar(64) default ''                not null comment '创建人',
    create_time datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64) default ''                not null comment '修改人',
    update_time datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint     default 1                 not null comment '0无效1有效'
)
    comment '分享项目';

create index idx_item_id
    on share_item (item_id);

create index idx_user_id
    on share_item (user_id);

create index idx_uuid
    on share_item (uuid);

create table share_item_detail_log
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    share_id     bigint                                 not null comment '分享Id，关联uid和itemId',
    question     text                                   null comment '问题',
    answer       text                                   null comment '回答',
    req_id       varchar(64)                            not null comment '请求ID',
    token        mediumint                              not null comment 'token数',
    product_type tinyint                                not null comment '商品类型',
    log_desc     varchar(100) default ''                not null comment '日志描述',
    create_by    varchar(64)  default ''                not null comment '创建人',
    create_time  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    varchar(64)  default ''                not null comment '修改人',
    update_time  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn           tinyint      default 1                 not null comment '0无效1有效'
)
    comment '分享项目详情对话日志表';

create index share_item_detail_log_req_id_index
    on share_item_detail_log (req_id);

create index share_item_detail_log_share_id_index
    on share_item_detail_log (share_id);

create table top_bar_config
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    button_name varchar(64)  default ''                not null comment '按钮名称',
    button_desc varchar(500) default ''                not null comment '按钮描述',
    button_type int(2)       default 10                not null comment '10:弹窗 20:跳转',
    jump_url    varchar(500) default ''                not null comment '跳转url',
    image_url   varchar(500) default ''                not null comment '图片',
    create_by   varchar(64)  default 'system'          not null comment '创建人',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(64)  default 'system'          not null comment '修改人',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn          tinyint      default 1                 not null comment '0无效1有效'
)
    comment '上拦配置表';

create table user_consumer_log
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    user_id         bigint                                not null comment '用户ID',
    account_id      bigint                                not null comment '账户id',
    order_no        varchar(64) default ''                not null comment '订单号',
    product_type    int(2)                                not null comment '商品类型',
    real_amount     decimal(10, 2)                        not null comment '实付',
    original_amount decimal(10, 2)                        not null comment '原价',
    member_amount   decimal(10, 2)                        null comment '会员价',
    discount_amount decimal(10, 2)                        not null comment '优惠金额',
    create_by       varchar(64) default ''                not null comment '创建人',
    create_time     datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by       varchar(64) default ''                not null comment '修改人',
    update_time     datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn              tinyint     default 1                 not null comment '0无效1有效'
)
    comment '用户消耗日志';

create index idx_account_id
    on user_consumer_log (account_id);

create index idx_user_id
    on user_consumer_log (user_id);

create table user_info
(
    id                bigint unsigned auto_increment comment '主键'
        primary key,
    phone             varchar(13) default ''                not null comment '手机号',
    email             varchar(64) default ''                not null comment '邮箱',
    user_password     varchar(64) default ''                not null comment '密码',
    user_status       tinyint     default 1                 not null comment '状态 1: 正常 2: 注销 3: 冻结',
    member_valid_time datetime                              null comment '会员有效日期',
    register_type     tinyint(1)  default 1                 not null comment '1: 邮箱 2: 短信 3: 公众号授权登录',
    invite_count      int(10)     default 0                 not null comment '邀请人数',
    create_by         varchar(64) default ''                not null comment '创建人',
    create_time       datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by         varchar(64) default ''                not null comment '修改人',
    update_time       datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn                tinyint     default 1                 not null comment '0无效1有效'
)
    comment '用户信息表';

create index idx_email
    on user_info (email);

create index idx_phone
    on user_info (phone);

create table wechat_user_info
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    user_id      bigint                                 not null comment '用户ID',
    open_id      varchar(64)  default ''                not null comment '信息',
    unionId      varchar(64)  default ''                not null comment '用户统一标识',
    nick_name    varchar(64)  default ''                not null comment '普通用户昵称',
    city         varchar(10)  default ''                not null comment '普通用户个人资料填写的城市',
    province     varchar(10)  default ''                not null comment '普通用户个人资料填写的省份',
    country      varchar(10)  default ''                not null comment '国家',
    headImgUrl   varchar(500) default ''                not null comment '头像',
    sex          tinyint                                null comment '普通用户性别，1为男性，2为女性',
    snapshotUser tinyint                                null comment '是否为快照页模式虚拟账号，值为0时是普通用户，1时是虚拟帐号',
    create_by    varchar(64)  default ''                not null comment '创建人',
    create_time  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by    varchar(64)  default ''                not null comment '修改人',
    update_time  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    yn           tinyint      default 1                 not null comment '0无效1有效'
)
    comment '用户信息表';

create index idx_open_id
    on wechat_user_info (open_id);

create index idx_user_id
    on wechat_user_info (user_id);



-- 测试账号 账号:123@qq.com 密码:123
INSERT INTO user_info (phone, email, user_password, user_status, member_valid_time, register_type,
                                      invite_count, create_by, create_time, update_by, update_time, yn)
VALUES ('', '123@qq.com', '002a85dc98d6bc2697e1e9a29ed608d4', 1, now(), 1, 1, 'system',
        now(), 'system', now(), 1);



INSERT INTO member_card (card_code, card_name, card_state, amount, line_amount, card_day, recommend, create_by,
                         create_time, update_by, update_time, yn)
VALUES ('month_1', '尊贵月卡', 1, 19.90, 29.90, 30, 0, '', now(), 'system', now(), 1);
INSERT INTO member_card (card_code, card_name, card_state, amount, line_amount, card_day, recommend, create_by,
                         create_time, update_by, update_time, yn)
VALUES ('season_1', '至尊季卡', 1, 49.00, 59.90, 90, 0, '', now(), 'system', now(), 1);
INSERT INTO member_card (card_code, card_name, card_state, amount, line_amount, card_day, recommend, create_by,
                         create_time, update_by, update_time, yn)
VALUES ('year_01', '颠峰年卡', 1, 199.00, 249.00, 365, 0, '', now(), 'system', now(), 1);
INSERT INTO member_card (card_code, card_name, card_state, amount, line_amount, card_day, recommend, create_by,
                         create_time, update_by, update_time, yn)
VALUES ('优惠周卡', '优惠周卡', 1, 8.00, 20.00, 7, 1, 'system', now(), 'system', now(), 1);


INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('月卡代币', 'month_1', '会员一次性赠送200个方糖🍬永久使用', 1001, 200, 1.00, 'system', now(), 'system', now(),
        1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('月卡加油包折扣', 'month_1', '购买加油包享受9折优惠', 1002, 0, 0.90, 'system', now(), 'system', now(), 1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('季卡代币', 'season_1', '会员一次性赠送500个方糖🍬永久使用', 1001, 500, 1.00, 'system', now(), 'system', now(),
        1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('季卡流量包折扣', 'season_1', '购买流量包享受8折优惠', 1002, 0, 0.80, 'system', now(), 'system', now(), 1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('季卡会员消费折扣', 'season_1', 'GPT&AI绘画享受8折优惠', 1003, 0, 0.80, 'system', now(), 'system', now(), 1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('周卡代币', '优惠周卡', '一次性赠送200个方糖🍬，永久使用', 1001, 200, 1.00, 'system', now(), 'system', now(), 1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('周卡加油包折扣', '优惠周卡', '购买加油包享受85折优惠', 1002, 0, 0.85, 'system', now(), 'system', now(), 1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('年卡权益', 'year_01', '购买加油包享75折优惠', 1002, 0, 7.50, 'system', now(), 'system', now(), 1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('年卡消费折扣', 'year_01', '消费享受9折优惠', 1003, 0, 0.90, 'system', now(), 'system', now(), 1);
INSERT INTO member_rights (rights_name, member_code, rights_desc, rights_type, count, discount, create_by, create_time,
                           update_by, update_time, yn)
VALUES ('周卡消耗折扣', '优惠周卡', '会员消费打9折', 1003, 0, 0.90, 'system', now(), 'system', now(), 1);

INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (1, 1, 2, 'system', 2, 1, '2023-06-20 07:04:30', 'system', now(), 1);
INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (3, 1, 1, 'system', 2, 1, '2023-06-20 07:04:44', 'system', now(), 1);
INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (2, 1, 2, 'system', 1, 2, '2023-06-30 09:59:05', 'system', now(), 1);
INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (4, 1, 1, 'system', 2, 1, '2023-07-22 23:03:12', 'system', '2023-07-22 23:03:12', 1);
INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (5, 1, 2, 'system', 2, 1, now(), 'system', now(), 1);
INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (6, 1, 1, 'system', 2, 1, now(), 'system', now(), 1);
INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (7, 1, 1, 'system', 2, 1, now(), 'system', now(), 1);
INSERT INTO product_consume_config (product_type, consume_currency, consume_type, create_by, use_auth, status,
                                    create_time, update_by, update_time, yn)
VALUES (8, 1, 1, 'system', 2, 1, now(), 'system', now(), 1);



INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('写作助理', '👉 最常使用的 prompt，用于优化文本的语法、清晰度和简洁度，提高可读性。', 1,
        '作为一名中文写作改进助理，你的任务是改进所提供文本的拼写、语法、清晰、简洁和整体可读性，同时分解长句，减少重复，并提供改进建议。请只提供文本的更正版本，避免包括解释。请从编辑以下文本开始：',
        'system', '2023-06-01 08:56:23', 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('小红书风格', '👉 将文本改写成类似小红书的 Emoji 风格。', 1,
        '请使用 Emoji 风格编辑以下段落，该风格以引人入胜的标题、每个段落中包含表情符号和在末尾添加相关标签为特点。每句话结尾都要换行,请确保保持原文的意思。',
        'system', '2023-06-01 11:11:14', 'system', '2023-06-03 23:00:45', 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('论文式回答', '👉 以论文形式讨论问题，能够获得连贯的、结构化的和更高质量的回答。', 1,
        '写一篇高度详细的文章，包括引言、主体和结论段落，以回应以下内容：[问题］', 'system', now(), 'system',
        now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('论文①', '👉 根据主题撰写内容翔实、有信服力的论文。', 1,
        '我希望你能作为一名学者行事。你将负责研究一个你选择的主题，并将研究结果以论文或文章的形式呈现出来。你的任务是确定可靠的来源，以结构良好的方式组织材料，并以引用的方式准确记录。我的第一个建议要求是 ''论文主题''',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('小说家 ', '👉 根据故事类型输出小说，例如奇幻、浪漫或历史等类型。', 1,
        '我希望你能作为一个小说家。你要想出有创意的、吸引人的故事，能够长时间吸引读者。你可以选择任何体裁，如幻想、浪漫、历史小说等--但目的是要写出有出色的情节线、引人入胜的人物和意想不到的高潮。我的第一个要求是 ''小说类型''',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('周报生成器 ', '👉 根据日常工作内容，提取要点并适当扩充，以生成周报。', 1,
        '使用下面提供的文本作为中文周报的基础，生成一个简洁的摘要，突出最重要的内容。该报告应以 markdown 格式编写，并应易于阅读和理解，以满足一般受众的需要。特别是要注重提供对利益相关者和决策者有用的见解和分析。你也可以根据需要使用任何额外的信息或来源。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('编剧', '👉 根据主题创作一个包含故事背景、人物以及对话的剧本。', 1,
        '我希望你能作为一个编剧。你将为一部长篇电影或网络剧开发一个吸引观众的有创意的剧本。首先要想出有趣的人物、故事的背景、人物之间的对话等。一旦你的角色发展完成--创造一个激动人心的故事情节，充满曲折，让观众保持悬念，直到结束。我的第一个要求是 ''剧本主题''',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('按关键词写故事', '👉 用你提供的几个单词来写个小故事', 1,
        '现在你来扮演我的英语老师，接下来的对话我会给你发几个英语单词，你要用我给出的单词编一个有趣的英文小故事。我发的英语单词会用括号括住，只有括号里面的单词才是你需要编成故事的，单词之间我会用逗号隔开。如果我的话里面没有括号，代表我不需要你进行编故事。这个故事你需要按照下面的模板进行回答。注意，该模板一共包括三部分，你必须将三部分都写出来。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('算法入门讲解', '👉 向初学者介绍 Python 编程语言入门知识。', 1,
        '我想让你在学校里担任教员，向初学者教授算法。你将使用 python 编程语言提供代码实例。首先，开始简要地解释什么是算法，并继续举出简单的例子，包括气泡排序和快速排序。稍后，等待我的提示，提出其他问题。一旦你解释并给出代码示例，我希望你尽可能地包括相应的可视化的 ascii 艺术。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('数学老师', '👉 用易于理解的术语解释数学概念。', 1,
        '我希望你充当一名数学老师。我将提供一些数学方程式或概念，而你的工作是用易于理解的术语解释它们。这可能包括提供解决问题的分步说明，用视觉效果演示各种技巧，或建议进一步学习的在线资源。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('历史学家', '👉 使用史实资料分析历史主题。', 1,
        '我希望你能作为一名历史学家行事。你将研究和分析过去的文化、经济、政治和社会事件，从原始资料中收集数据，并利用它来发展关于各个历史时期发生的理论。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('IT 编程问题', '👉 模拟编程社区来回答你的问题，并提供解决代码', 1,
        '我想让你充当 Stackoverflow 的帖子。我将提出与编程有关的问题，你将回答答案是什么。我希望你只回答给定的答案，在没有足够的细节时写出解释。当我需要用英语告诉你一些事情时，我会把文字放在大括号里{像这样}。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('代码释义器 ', '👉 让 AI 解释每步代码的作用', 1, '我希望你能充当代码解释者，阐明代码的语法和语义。', 'system',
        now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('健身教练', '👉 通过输入身高、体重、年龄等指标，来制定健身方案。', 1,
        '我希望你能充当私人教练。我将为你提供一个希望通过体能训练变得更健康、更强壮、更健康的人所需要的所有信息，而你的职责是根据这个人目前的体能水平、目标和生活习惯，为其制定最佳计划。你应该运用你的运动科学知识、营养建议和其他相关因素，以便制定出适合他们的计划。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('厨师', '👉 Personal Chef', 1,
        '我想让你充当我的私人厨师。我将告诉你我的饮食偏好和过敏症，你将建议我尝试的食谱。你应该只回复你推荐的菜谱，而不是其他。不要写解释。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('占星家', '👉 从占星学家的角度来解读周遭的事。', 1,
        '我希望你能作为一名占星师。你将学习十二星座及其含义，了解行星位置及其对人类生活的影响，能够准确解读星座，并与寻求指导或建议的人分享你的见解。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('解梦', '👉 对你描述的梦境进行解读。', 1,
        '我希望你能充当一个解梦者。我将给你描述我的梦，而你将根据梦中出现的符号和主题提供解释。不要提供关于梦者的个人意见或假设。只提供基于所给信息的事实性解释。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('生活自助百科', '👉 为你的生活/工作提供建议和提示，比如如何改善人际关系。', 1,
        '我希望你能作为一本自助书。你将为我提供如何改善我生活中某些领域的建议和提示，如人际关系、职业发展或财务规划。例如，如果我在与重要的另一半的关系中挣扎，你可以建议有用的沟通技巧，使我们的关系更紧密。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('职业顾问 ', '👉 基于你的技能、兴趣和经验，提供相关岗位建议。', 1,
        '我希望你充当职业顾问。我将为你提供一个在职业生活中寻求指导的人，你的任务是根据他们的技能、兴趣和经验，帮助他们确定他们最适合的职业。你还应该对现有的各种选择进行研究，解释不同行业的就业市场趋势，并就哪些资格有利于追求特定领域提出建议。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('异性对话生成器',
        '👉 根据自己和对方的一段对话，来继续对话，用于扩展话题避免冷场。提示词需要根据自身情况修改。（在 New Bing 中直接输入中文提示器可能 AI 会不干，输入英文即可，后续可输中文）',
        1,
        '我想让你充当一个对话生成器，我会输入两句话，分别是我和另一个认识两个月的女生说的话，例如：“我：你好吗？她：我很好，谢谢。”。请根据上下文进行分析，然后以我（男生）的角度进行回话。你的回答应该为“我：”的格式，且不需要连续进行对话。风格要幽默、有趣、体贴、温柔，并尽可能地扩展话题，让对话轻松愉快。如果你明白，请回答：“好的，请提供初始对话。”',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('深度思考助手',
        '👉 根据关键词、主题或者概念，提供高质量、有价值的问题，涉及人类认知、情感和行为的各个方面，训练自己的深度思考能力。这个提示词的回复结构很清晰，适合整理概念时使用',
        1, '角色：你是一个帮助我训练深度思考的 AI 助手。
输入：关键词、主题或概念。
处理过程：
- 使用深度和广度的标准来评价这个关键词、主题或概念，提供高质量、有价值的问题，探讨人类认知、情感和行为的各个方面。
- 优先提出一些简单到复杂的问题，而后逐步深入，以帮助我深入探索。
- 提供有助于总结和回顾思考内容的问题，为更全面、深刻和灵活的理解做准备。
- 最后请给出你对于这个关键词、主题或者概念的看法和理解。
输出：
- 简单到复杂的问题：用于帮助我逐步了解和深入探索。
- 更加深入的问题：用于深入探讨关键词、主题或概念的各个方面。
- 总结和回顾时参考的问题：用于有助于我形成更全面、深刻和灵活的理解。
- 你对于这个关键词、主题或概念的看法和理解。
我的第一句话是：[你的关键词、主题或者概念]', 'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('英语对话练习', '👉 英语交谈对话，回复会限制在 100 字以内。输入中的语法错误、错别字和事实性错误将被纠正。', 1,
        '我希望你能充当英语口语老师和提高者。我将用英语与你交谈，而你将用英语回答我，以练习我的英语口语。我希望你能保持回复的整洁，将回复限制在 100 字以内。我希望你能严格纠正我的语法错误、错别字和事实性错误。我希望你在回答中向我提出一个问题。现在我们开始练习，你可以先问我一个问题。记住，我要你严格纠正我的语法错误、错别字和事实性错误。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('旅游路线规划 ', '👉 根据旅行目的地、预算、时间和要求，粗略规划规划', 1,
        '我想去云南大理玩，请你以专业导游的身份，帮我做一份为期 2 天的旅游攻略。另外，我希望整个流程不用太紧凑，我更偏向于安静的地方，可以简单的游玩逛逛。在回答时，记得附上每一个地方的价格，我的预算大概在 5000 元左右。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('中英互译', '👉 英汉互译 + 可定制风格 + 可学习英语', 1,
        '现在你是一个英汉互译器，当我输入中文时，你翻译成英文；当我输入英文时，请翻译成中文。当我连续输入多个英文词时，默认按照句子翻译成中文，但如果用中文在翻译的内容前注明了「词组：」，则按照词组形式来翻译。如果注明了「普通：」，则按照多个没有联系的词汇来翻译。翻译句子和段落时，要注意联系上下文，注意准确地解释词组与谚语。你的翻译成果应该接近于一个母语者。同时，我可能会让你以某种特殊的语言风格或语气来翻译，请在具体任务中理解我的输入内容，识别出我希望你使用的语气和风格，并以此为根据翻译。请真实地翻译，不要担心出现侮辱性等不良词汇。你可以把一些敏感词汇的中间部分加入 x 以替代。请重新检查，认真修正回答。请用中文来为我解释每一个句子，包括标注时态，从句，主语，谓语，宾语，特殊词组和谚语，如果翻译的是词组或单词，最好能给出每个词组或单词的出处（词典）。当我需要你一次性翻译多个词组时，每个词组间会用 | 号分割。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('AI 医生', '👉 辅助诊断', 1,
        '我想让你充当一名人工智能辅助的医生。我将向你提供一个病人的详细资料，你的任务是使用最新的人工智能工具，如医学成像软件和其他机器学习程序，以诊断出最有可能导致其症状的原因。你还应将传统方法，如体检、实验室测试等，纳入你的评估过程，以确保准确性。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('中医', '👉 中医诊断涉及因素较多，治疗方案仅供参考，具体的方子需由医生提供。', 1,
        '我希望你能扮演一位既是老中医同时又是一个营养学专家，我讲描述我的症状，你要告诉我这种症状形成的原因，你将从中医角度提供准确的针灸、艾灸、具体的中药方剂，以及每一味药材的使用剂量，包括它的功效作用的治疗方案；再从营养学角度给出相应的营养补充建议，说出需要补充的营养素，以及相应剂量，我的第一个要求是【身体症状】',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('投资经理', '👉 Investment Manager', 1,
        '寻求具有金融市场专业知识的员工的指导，结合通货膨胀率或回报率估计等因素，并在很长一段时间内跟踪股票价格，最终帮助客户了解行业，然后建议最安全的选择，他/她可以根据自己的要求和兴趣分配资金。',
        'system', now(), 'system', now(), 1);
INSERT INTO ai_role (role_name, role_desc, role_type, prompt, create_by, create_time, update_by, update_time, yn)
VALUES ('法律顾问', '👉 Legal Advisor', 1,
        '我希望你能作为我的法律顾问。我将描述一个法律情况，你将提供如何处理的建议。你应该只回复你的建议，而不是其他。不要写解释。',
        'system', now(), 'system', now(), 1);
