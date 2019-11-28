package com.owen.servlet;

import com.owen.page.Page;
import com.owen.entity.Room;
import com.owen.utils.JDBCConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class RoomController extends HttpServlet {

    // 展示客房信息
    public void list(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Page<Room> page = new Page<>(1, 5);
        // 获取查询客房的参数
        Enumeration<String> parameterNames = request.getParameterNames();
        ArrayList<String> whereList = new ArrayList<String>();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            if (name.equals("page")) {
                page.setPage(Integer.parseInt(request.getParameter(name)));
                continue;
            } else if (name.equals("rows")) {
                page.setSize(Integer.parseInt(request.getParameter(name)));
                continue;
            }
            String value = request.getParameter(name);
            byte[] buf = value.getBytes("iso8859-1");
            value = new String(buf, "utf-8");
            if (!value.equals("")) {
                whereList.add(String.format("`%s` = '%s'", name, value));
            }
        }
        // 组装where参数
        String whereStr = "";
        if (!whereList.isEmpty()) {
            whereStr = joinListToStr(whereList, " and ");
        }
        if (whereStr != "") {
            whereStr = " where " + whereStr;
        }

        Connection connection = null;
        List<Room> list = new ArrayList<>();
        int count = 0;
        int startRows = (page.getPage() - 1) * page.getSize();
        try {
            connection = JDBCConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("select * from hotel%s limit %d, %d", whereStr, startRows, page.getSize()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String roomNumber = resultSet.getString(2);
                String type = resultSet.getString(7);
                String status = resultSet.getString(5);
                int floor = resultSet.getInt(3);
                int bed = resultSet.getInt(4);
                int price = resultSet.getInt(6);
                Room room = new Room(id, roomNumber, type, status, floor, bed, price);
                list.add(room);
            }
            preparedStatement = connection.prepareStatement(String.format("select count(1) from hotel%s", whereStr));
            ResultSet countResult = preparedStatement.executeQuery();
            while (countResult.next()) {
                count = countResult.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(connection);
        }
        ServletContext servletContext = request.getServletContext();

        page.setSize(5);
        page.setTotal(count);
        page.setRows(list);
        servletContext.setAttribute("page", page);
        request.getRequestDispatcher("/jsp/room.jsp").forward(request, response);
    }

    // 添加客房
    public void add(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InvocationTargetException, IOException {
        Room room = getParamFromReq(request);
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String sql = "insert into `hotel` (`roomNumber`, `floor`, `bed`, `status`, `price`, `type`) values('" + room.getRoomNumber() + "', '" + room.getFloor() + "', '" + room.getBed() + "', '" + "正常" + "', '" + room.getPrice() + "', '" + room.getType() + "')";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("false");
            return;
        } finally {
            JDBCConnection.close(connection);
        }
        out.println("true");
    }

    // 获取单个客房信息
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 解决返回中文乱码
        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            String sql = String.format("select * from hotel where id = %d", id);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String roomNumber = resultSet.getString(2);
                String type = resultSet.getString(7);
                String status = resultSet.getString(5);
                int floor = resultSet.getInt(3);
                int bed = resultSet.getInt(4);
                int price = resultSet.getInt(6);
                Room room = new Room(id, roomNumber, type, status, floor, bed, price);
                out.println(room.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("false");
        } finally {
            JDBCConnection.close(connection);
        }
    }

    // 修改客房
    public void update(HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException, IOException {
        Room room = getParamFromReq(request);
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String sql = "update hotel set  " + " `floor` = " + room.getFloor() + ", `bed` = " + room.getBed() + ", price = " + room.getPrice() + ", `type` = " + "\"" + room.getType() + "\"" + " where `id` = " + room.getId();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("false");
            return;
        } finally {
            JDBCConnection.close(connection);
        }
        out.println(true);
    }

    // 变更客房状态
    public void changeStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status");
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String checkSql = "select count(1) from hotel_customer where id = " + id;
            PreparedStatement preparedStatement = connection.prepareStatement(checkSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt("count(1)") != 0) {
                out.println("false");
                return;
            }

            String updateSql = "update hotel set `status` = " + "\"" + status + "\"" + " where `id` = " + id;
            preparedStatement = connection.prepareStatement(updateSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("false");
            return;
        } finally {
            JDBCConnection.close(connection);
        }
        out.println(true);
    }

    // 删除客房
    public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String checkSql = "select count(1) from hotel_customer where id = " + id;
            PreparedStatement preparedStatement = connection.prepareStatement(checkSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt("count(1)") != 0) {
                out.println("false");
                return;
            }
            preparedStatement = connection.prepareStatement("delete from hotel where `id` = " + id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("false");
            return;
        } finally {
            JDBCConnection.close(connection);
        }
        out.println(true);
    }

    private String buildGetterMethod(String fieldName) {
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String suffix = fieldName.substring(1);
        return "set" + firstLetter + suffix;
    }

    private String joinListToStr(ArrayList<String> strList, String str) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < strList.size(); i++) {
            if (i != strList.size() - 1) {
                result.append(strList.get(i) + str);
            } else {
                result.append(strList.get(i));
            }
        }
        return result + "";
    }

    private Room getParamFromReq(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        Enumeration<String> parameterNames = request.getParameterNames();
        Room room = new Room();
        Class<? extends Room> clazz = room.getClass();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            Method method = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(it -> it.getName().equals(this.buildGetterMethod(name)))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            if (method.getParameterTypes()[0].getName().equals("int")) {
                method.invoke(room, Integer.parseInt(value));
            } else {
                method.invoke(room, value);
            }
        }
        return room;
    }
}

