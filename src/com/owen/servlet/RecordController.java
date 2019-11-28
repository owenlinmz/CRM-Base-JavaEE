package com.owen.servlet;

import com.owen.entity.Record;
import com.owen.page.Page;
import com.owen.utils.JDBCConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RecordController {

    // 展示入住记录信息
    public void list(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Page<Record> page = new Page<>(1, 5);
        // 获取查询入住记录的参数
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
                whereList.add(String.format("%s like '%%%s%%'", name, value));
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
        List<Record> list = new ArrayList<>();
        int count = 0;
        int startRows = (page.getPage() - 1) * page.getSize();
        try {
            connection = JDBCConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("select hr.*, hc.name as name, hc.telephone as telephone, h.roomNumber as roomNumber, h.type as type from ((hotel_record as hr left join hotel_customer as hc on hr.customerId = hc.id) left join hotel as h on hr.roomId = h.id)%s limit %d, %d", whereStr, startRows, page.getSize()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String roomNumber = resultSet.getString("roomNumber");
                String type = resultSet.getString("type");
                Date inTime = resultSet.getTimestamp("inTime");
                Date outTime = resultSet.getTimestamp("outTime");
                String name = resultSet.getString("name");
                String breakfast = resultSet.getString("breakfast");
                int price = resultSet.getInt("price");
                String telephone = resultSet.getString("telephone");
                Record record = new Record(id, inTime, outTime, breakfast, price, type, name,  roomNumber, telephone);
                list.add(record);
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
        request.getRequestDispatcher("/jsp/record.jsp").forward(request, response);
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

}
