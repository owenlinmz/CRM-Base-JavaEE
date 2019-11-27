package com.owen;

import com.owen.servlet.RoomController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class BaseServlet extends HttpServlet {

    public static final String SUFFIX = "Controller";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String classPrefix = requestURI.split("/")[3];
        String methodName = requestURI.split("/")[4];
        String className = "com.owen.servlet." + classPrefix + SUFFIX;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Method method = null;
        try {
            assert clazz != null;
            method = clazz.getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            Class<?>[] parameterTypes = method.getParameterTypes();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            assert method != null;
            method.invoke(clazz.newInstance(), req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(("方法调用异常"));
        }
    }
}
