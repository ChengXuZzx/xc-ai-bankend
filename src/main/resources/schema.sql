-- 修改 ai_log 表结构以支持会话隔离
ALTER TABLE ai_log 
CHANGE COLUMN prompt question VARCHAR(500) COMMENT '用户问题或 AI 回复';

ALTER TABLE ai_log 
ADD COLUMN chat_id VARCHAR(64) COMMENT '会话 ID' AFTER id;

ALTER TABLE ai_log 
ADD COLUMN message_type VARCHAR(20) COMMENT '消息类型：user/assistant' AFTER question;

-- 添加索引提高查询性能
CREATE INDEX idx_chat_id ON ai_log(chat_id);
