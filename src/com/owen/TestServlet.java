package com.owen;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class TestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext servletContext = request.getServletContext();
        OutputStream out = response.getOutputStream();
        InputStream in = servletContext.getResourceAsStream("/image/1.jpg");
        byte[] buf = new byte[1024];
        int length = 0;

        while ((length = in.read(buf)) != -1) {
            out.write(buf, 0, length);
        }
        in.close();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
