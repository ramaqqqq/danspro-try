package com.micro.demo.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class Helpers {

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static boolean verifyPassword(String hashedPassword, String password) {
        return encoder.matches(password, hashedPassword);
    }

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    public class H extends HashMap<String, Object> {}

    public static Map<String, Object> MsgOk(int status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", status);
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> MsgErr(int status, String message, String err) {
        Map<String, Object> response  = new HashMap<>();
        response.put("code", status);
        response.put("message", message);
        response.put("error", err);
        return response;
    }

    public static void rMSG(HttpServletResponse response, int status, Map<String, Object> data) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(data);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(jsonData);
    }

     public static void Response(HttpServletResponse w, int status, Map<String, Object> data) throws IOException {
        w.setHeader("Content-Type", "application/json");
        w.setStatus(status);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("body", data);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(responseBody);

        PrintWriter writer = w.getWriter();
        writer.write(jsonData);
        writer.flush();
    }

    public static void log(String logType, String message) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(logType);

        logger.setLevel(Level.DEBUG);

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setPattern("[%level]: %date{dd-MM-yyyy HH:mm:ss} - %msg%n");
        patternLayout.setContext(loggerContext);
        patternLayout.start();

        LayoutWrappingEncoder<ch.qos.logback.classic.spi.ILoggingEvent> layoutEncoder = new LayoutWrappingEncoder<>();
        layoutEncoder.setLayout(patternLayout);
        layoutEncoder.setContext(loggerContext);
        layoutEncoder.start();

        ConsoleAppender<ch.qos.logback.classic.spi.ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(layoutEncoder);
        consoleAppender.setContext(loggerContext);
        consoleAppender.start();

        logger.addAppender(consoleAppender);

        switch (logType) {
            case "info":
                logger.info(message);
                break;
            case "error":
                logger.error(message);
                break;
        }
    }

}
