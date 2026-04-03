package com.xc.constant;

public enum ChatType {
    CHAT("chat", "聊天"),
    HONGHONG("honghong", "哄哄模拟聊天"),
    PDF("pdf", "PDF聊天"),
    CUSTOMER_SERVICE("customer_service", "智能客服");

    private final String name;
    private final String description;

    ChatType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
