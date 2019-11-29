package com.owen.servlet;

import java.io.PrintWriter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.owen.entity.Record;
import com.owen.page.Page;
import com.owen.utils.JDBCConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
                whereList.add(String.format("`%s` like '%%%s%%'", name, value));
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
                Record record = new Record(id, inTime, outTime, breakfast, price, type, name, roomNumber, telephone);
                list.add(record);
            }
            preparedStatement = connection.prepareStatement(String.format("select count(1), hc.name as name, h.roomNumber as roomNumber, h.type as `type` from ((hotel_record as hr left join hotel_customer as hc on hr.customerId = hc.id) left join hotel as h on hr.roomId = h.id)%s", whereStr));
            ResultSet countResult = preparedStatement.executeQuery();
            while (countResult.next()) {
                count = countResult.getInt("count(1)");
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
        request.getRequestDispatcher("/WEB-INF/jsp/record.jsp").forward(request, response);
    }

    // 入住详情
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CustomerController.resolveChinese(response);
        String roomNumber = request.getParameter("roomNumber");
        PrintWriter out = response.getWriter();
        int id = Integer.parseInt(request.getParameter("id"));
        ArrayList<JSONObject> recordList = getRecordsByRoomNumber(roomNumber);
        Date realInTime = null;
        Date realOutTime = null;
        for (JSONObject record : recordList) {
            if (record.getIntValue("id") == id) {
                realInTime = record.getTimestamp("inTime");
                realOutTime = record.getTimestamp("outTime");
                break;
            }
        }

        JSONObject result = new JSONObject();
        ArrayList<JSONObject> finalList = new ArrayList<>();
        if (realInTime == null) {
            out.println(result.toString());
            return;
        }
        // 计算入住时间和离开时间
        Date finalInTime = null;
        Date finalOutTime = null;
        for (JSONObject record : recordList) {
            if (record.getTimestamp("outTime") != null && record.getTimestamp("outTime").before(realInTime)) {
                continue;
            } else if (realOutTime != null && record.getTimestamp("inTime").after(realOutTime)) {
                continue;
            }
            if (record.getTimestamp("inTime") != null) {
                finalInTime = record.getTimestamp("inTime");
            }
            if (record.getTimestamp("outTime") != null) {
                finalOutTime = record.getTimestamp("outTime");
            }
            finalList.add(record);
        }
        if (finalList.isEmpty() || finalInTime == null) {
            out.println(result.toString());
            return;
        }
        ArrayList<String> nameList = new ArrayList<>();
        for (JSONObject record : finalList) {
            if (finalInTime.after(record.getTimestamp("inTime"))) {
                finalInTime = record.getTimestamp("inTime");
            }

            // 如果房间内仍有客户未离开，则离开时间为空
            if (record.getTimestamp("outTime") == null) {
                finalOutTime = null;
            }
            if (finalOutTime != null && record.getTimestamp("outTime") != null && record.getTimestamp("outTime").after(finalOutTime)) {
                finalOutTime = record.getTimestamp("outTime");
            }
            nameList.add(record.getString("name"));
        }
        result.put("nameList", joinListToStr(nameList, ", "));
        result.put("inTime", CustomerController.getTimeString(finalInTime));
        result.put("roomNumber", finalList.get(0).getString("roomNumber"));
        result.put("type", finalList.get(0).getString("roomNumber"));
        if (finalOutTime != null) {
            result.put("outTime", CustomerController.getTimeString(finalOutTime));
        } else {
            result.put("outTime", "空");
        }
        out.println(result.toString());
    }

    // 通过房间号获取入住记录
    private ArrayList getRecordsByRoomNumber(String roomNumber) {
        Connection connection = null;
        ArrayList<JSONObject> recordList = new ArrayList<>();
        try {
            connection = JDBCConnection.getConnection();
            String sql = "select hr.*, h.roomNumber as roomNumber, hc.name as name, h.type as `type` from ((hotel_record as hr inner  join  hotel as h on h.roomNumber = " + roomNumber + " and h.id = hr.roomId inner join hotel_customer as hc on hr.customerId = hc.id))";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                JSONObject record = new JSONObject();
                record.put("inTime", resultSet.getTimestamp("inTime"));
                record.put("outTime", resultSet.getTimestamp("outTime"));
                record.put("roomNumber", resultSet.getString("roomNumber"));
                record.put("name", resultSet.getString("name"));
                record.put("type", resultSet.getString("type"));
                record.put("id", resultSet.getInt("id"));
                recordList.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(connection);
        }
        return recordList;
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
