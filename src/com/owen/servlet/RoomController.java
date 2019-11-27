package com.owen.servlet;

import com.owen.page.Page;
import jdk.nashorn.internal.scripts.JD;
import com.owen.BaseServlet;
import com.owen.entity.Room;
import com.owen.utils.JDBCConnection;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            getRooms(req, resp);
        } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void getRooms(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ServletException, InvocationTargetException, IllegalAccessException {
        Enumeration<String> parameterNames = request.getParameterNames();
        ArrayList<String> whereList = new ArrayList<String>();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            byte[] buf = value.getBytes("iso8859-1");
            value = new String(buf,"utf-8");
            if (!value.equals("")) {
                whereList.add(String.format("%s = '%s'", name, value));
            }
        }
        String whereStr = "";
        if (!whereList.isEmpty()) {
            whereStr = joinListToStr(whereList, " and ");
        }
        if (whereStr != ""){
            whereStr = " where " + whereStr;
        }
        Connection connection = null;
        List<Room> list = new ArrayList<>();
        int count = 0;

        try {
            connection = JDBCConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("select * from hotel%s", whereStr));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String roomNumber = resultSet.getString(2);
                String type = resultSet.getString(7);
                String status = resultSet.getString(5);
                int floor = resultSet.getInt(3);
                int bed = resultSet.getInt(4);
                int price = resultSet.getInt(6);
                Room room = new Room(id, roomNumber, type, status, floor, bed, price);
                list.add(room);
            }
            preparedStatement = connection.prepareStatement("select count(1) from hotel");
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
        Page<Room> page = new Page<Room>();
        page.setSize(5);
        page.setTotal(count);
        page.setRows(list);
        servletContext.setAttribute("page", page);
        request.getRequestDispatcher("/jsp/room.jsp").forward(request, response);
    }

    public void addRoom(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InstantiationException, InvocationTargetException {
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
            method.invoke(room, value);
        }

        Connection connection = null;
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String sql = "insert into T_ROOM(ROOM_NUM, TYPE, STATUS) values('" + room.getRoomNumber() + "', '" + room.getType() + "', '" + room.getStatus() + "')";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(connection);
        }


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
            }else {
                result.append(strList.get(i));
            }
        }
        return result + "";
    }
}
