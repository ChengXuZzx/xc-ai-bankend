package com.xc.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 天气查询工具
 * 与 weather-query skill 绑定使用
 */
@Slf4j
@Component
public class WeatherQueryTool {

    @Value("${weather.api.key:}")
    private String weatherApiKey;

    private static final String WEATHER_API_URL = "https://restapi.amap.com/v3/weather/weatherInfo";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 查询指定城市的天气信息
     * 此工具与 weather-query skill 绑定
     *
     * @param city 城市名称
     * @return 天气信息描述
     */
    @Tool(name = "weather_query", description = "查询指定城市的当前天气信息，包括温度、天气状况、湿度、风向等")
    public String queryWeather(
            @ToolParam(description = "城市名称，如：成都、北京、上海、广州等") String city) {

        log.info("天气查询工具被调用, 城市: {}", city);

        if (!StringUtils.hasText(city)) {
            return "请提供要查询的城市名称";
        }

        if (!StringUtils.hasText(weatherApiKey)) {
            log.error("天气API密钥未配置");
            return "天气服务暂时不可用，请联系管理员配置API密钥";
        }

        String url = String.format("%s?key=%s&city=%s&extensions=base", WEATHER_API_URL, weatherApiKey, city);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return parseWeatherResponse(response.getBody(), city);
        } catch (Exception e) {
            log.error("查询天气失败, 城市: {}, 错误: {}", city, e.getMessage(), e);
            return "查询天气失败，请稍后重试";
        }
    }

    @SuppressWarnings("unchecked")
    private String parseWeatherResponse(Map<String, Object> response, String city) {
        if (response == null) {
            return "天气服务返回空数据";
        }

        String status = String.valueOf(response.get("status"));
        if (!"1".equals(status)) {
            String info = String.valueOf(response.get("info"));
            log.warn("天气API调用失败: {}", info);
            return String.format("查询%s天气失败: %s", city, info);
        }

        List<Map<String, Object>> lives = (List<Map<String, Object>>) response.get("lives");
        if (lives == null || lives.isEmpty()) {
            return String.format("未找到%s的天气信息", city);
        }

        Map<String, Object> live = lives.get(0);
        String actualCity = getStringValue(live, "city", city);
        String temperature = getStringValue(live, "temperature", "");
        String weather = getStringValue(live, "weather", "");
        String humidity = getStringValue(live, "humidity", "");
        String windDirection = getStringValue(live, "winddirection", "");
        String windPower = getStringValue(live, "windpower", "");
        String reportTime = getStringValue(live, "reporttime", "");

        return String.format(
            "%s当前天气：%s，温度%s℃，湿度%s%%，%s%s，数据更新时间：%s",
            actualCity, weather, temperature, humidity, windDirection, windPower, reportTime
        );
    }

    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
