package com.owen.servlet;

import com.alibaba.fastjson.JSONObject;
import com.owen.entity.Customer;
import com.owen.page.Page;
import com.owen.utils.JDBCConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerController {
    // 展示客户信息
    public void list(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Page<Customer> page = new Page<>(1, 5);
        // 获取查询客户的参数
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
        List<Customer> list = new ArrayList<>();
        int count = 0;
        int startRows = (page.getPage() - 1) * page.getSize();
        try {
            connection = JDBCConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(String.format("select hc.*, h.roomNumber as roomNumber from hotel_customer as hc left join hotel as h on hc.roomId = h.id%s limit %d, %d", whereStr, startRows, page.getSize()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String identity = resultSet.getString(3);
                String telephone = resultSet.getString(4);
                int roomId = resultSet.getInt(5);
                String roomNumber = resultSet.getString(6);
                Customer customer = new Customer(id, name, identity, telephone, roomId, roomNumber);
                list.add(customer);
            }
            preparedStatement = connection.prepareStatement(String.format("select count(1) from hotel_customer%s", whereStr));
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
        request.getRequestDispatcher("/jsp/customer.jsp").forward(request, response);
    }

    // 添加客户
    public void add(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InvocationTargetException, IOException {
        Customer customer = getParamFromReq(request);
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String sql = "insert into `hotel_customer` (`name`, `identity`, `telephone`, `roomId`) values('" + customer.getName() + "', '" + customer.getIdentity() + "', '" + customer.getTelephone() + "',0" + ")";
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

    // 获取单个客户信息
    public void get(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 解决返回中文乱码
        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            String sql = String.format("select * from hotel_customer where id = %d", id);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(2);
                String identity = resultSet.getString(3);
                String telephone = resultSet.getString(4);
                int roomId = resultSet.getInt(5);
                Customer customer = new Customer(id, name, identity, telephone, roomId, null);
                out.println(customer.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("false");
        } finally {
            JDBCConnection.close(connection);
        }
    }

    // 修改客户
    public void update(HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException, IOException {
        Customer customer = getParamFromReq(request);
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String sql = "update hotel_customer set  " + " `name` = " + "\"" + customer.getName() + "\"" + ", `telephone` = " + "\"" + customer.getTelephone() + "\"" + " where `id` = " + customer.getId();
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

    // 删除客户
    public void delete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String checkSql = "select roomId from hotel_customer where id = " + id;
            PreparedStatement preparedStatement = connection.prepareStatement(checkSql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt("roomId") != 0) {
                out.println("false");
                return;
            }
            preparedStatement = connection.prepareStatement("delete from hotel_customer where `id` = " + id);
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

    // 获取当前入住信息
    public void getLiveIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 解决返回中文乱码
        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(request.getParameter("id"));
        PrintWriter out = response.getWriter();
        out.println(getLiveInfo(id));
    }

    // 更新入住信息
    public void updateLiveIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 解决返回中文乱码
        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        String inTime = request.getParameter("inTime");
        String outTime = request.getParameter("outTime");
        String breakfast = request.getParameter("breakfast");
        String roomNumber = request.getParameter("roomNumber");
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        int recordId = Integer.parseInt(request.getParameter("recordId"));

        // 时间处理
        if (outTime.equals("")) {
            outTime = null;
        } else {
            outTime = "\"" + outTime + "\"";
        }
        inTime = "\"" + inTime + "\"";

        Connection connection = null;
        PrintWriter out = response.getWriter();

        try {
            connection = JDBCConnection.getConnection();
            ArrayList<Object> roomData;
            roomData = getRoomDateByRoomNumber(connection, roomNumber);
            int newRoomId = (int) roomData.get(0);
            int price = (int) roomData.get(1);
            String status = (String) roomData.get(2);
            if (newRoomId == 0) {
                out.println("{\"result\":false, \"msg\": \"入住失败！不存在的房间号！\"}");
                return;
            } else if (!status.equals("正常")) {
                out.println("{\"result\":false, \"msg\": \"入住失败！该房间正在维修！\"}");
                return;
            }
            // 启动对多个表操作启用事务
            connection.setAutoCommit(false);
            // 客户新入住
            if (roomId == 0) {
                String insertSql = "insert into `hotel_record` (`customerId`, `roomId`, `inTime`, `outTime`, `breakfast`, `price`) values (" + id + "," + roomId + "," + inTime + "," + outTime + ",\"" + breakfast + "\"," + price + ")";
                PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                preparedStatement.execute();
                preparedStatement = connection.prepareStatement("update hotel_customer set `roomId` = " + roomId + " where `id` = " + id);
                preparedStatement.execute();
            } else {
                // 旧客户更新入住信息
                String recordSql = "update `hotel_record` set `inTime` = " + inTime + ", `outTime` = " + outTime + ", `breakfast` = " + "\"" + breakfast + "\"" + ", `roomId` = " + newRoomId + " where `id` = " + recordId;
                PreparedStatement preparedStatement = connection.prepareStatement(recordSql);
                preparedStatement.execute();
                if (roomId != newRoomId) {
                    preparedStatement = connection.prepareStatement("update `hotel_customer set `roomId` = `" + newRoomId + " where `id` = " + id);
                    preparedStatement.execute();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("{\"result\":false, \"msg\": \"入住失败！请联系系统管理员！\"}");
            return;
        } finally {
            JDBCConnection.close(connection);
        }
        out.println("{\"result\":true, \"msg\": \"入住更新成功！\"}");
    }

    // 办理退房
    public void getOutOfRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 解决返回中文乱码
        response.setContentType("text/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        PrintWriter out = response.getWriter();
        JSONObject liveInInfo = JSONObject.parseObject(getLiveInfo(id));
        Connection connection = null;
        boolean needUpdateOutTime = false;
        Date now = new Date();
        double days = 0;
        if (liveInInfo.getTimestamp("outTime") != null) {
            days = (liveInInfo.getTimestamp("outTime").getTime() - liveInInfo.getTimestamp("inTime").getTime()) / (float) (1000 * 60 * 60 * 24);
        } else {
            days = (now.getTime() - liveInInfo.getTimestamp("inTime").getTime()) / (float) (1000 * 60 * 60 * 24);
            needUpdateOutTime = true;
        }
        JSONObject resultObj = new JSONObject();
        if (days <= 0) {
            resultObj.put("result", false);
            resultObj.put("message", "退房失败：出错！入住时间大于或等于离开时间！");
            out.println(resultObj.toString());
        }
        double money = 0;
        if (days > Math.floor(days)) {
            money = Math.floor(days) * liveInInfo.getIntValue("price");
        } else {
            money = days * liveInInfo.getIntValue("price");
        }
        resultObj.put("message", "退房成功：总消费：" + money + "元！");
        resultObj.put("result", true);

        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("update `hotel_customer` set `roomId` = 0 where `id` = " + id);
            preparedStatement.execute();
            if (needUpdateOutTime) {
                preparedStatement = connection.prepareStatement("update `hotel_record` set `outTime` = " + "\"" + getTimeString(now) + "\"" + " where id = " + liveInInfo.getIntValue("recordId"));
                preparedStatement.execute();
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            resultObj.put("result", false);
            resultObj.put("message", "退房失败，请联系系统管理员");
            out.println(resultObj.toString());
            return;
        } finally {
            JDBCConnection.close(connection);
        }
        out.println(resultObj.toString());
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
                result.append(strList.get(i)).append(str);
            } else {
                result.append(strList.get(i));
            }
        }
        return result + "";
    }

    private Customer getParamFromReq(HttpServletRequest request) throws
            InvocationTargetException, IllegalAccessException {
        Enumeration<String> parameterNames = request.getParameterNames();
        Customer customer = new Customer();
        Class<? extends Customer> clazz = customer.getClass();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            Method method = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(it -> it.getName().equals(this.buildGetterMethod(name)))
                    .findFirst()
                    .orElseThrow(RuntimeException::new);
            if (method.getParameterTypes()[0].getName().equals("int")) {
                method.invoke(customer, Integer.parseInt(value));
            } else {
                method.invoke(customer, value);
            }
        }
        return customer;
    }

    private ArrayList<Object> getRoomDateByRoomNumber(Connection connection, String roomNumber) throws SQLException {
        String getRoomSql = "select `id`, `price`, `status` from `hotel` where `roomNumber` = " + roomNumber;
        PreparedStatement preparedStatement = connection.prepareStatement(getRoomSql);
        ResultSet roomResult = preparedStatement.executeQuery();
        int price = 0;
        String status = "正常";
        int roomId = 0;
        while (roomResult.next()) {
            roomId = roomResult.getInt("id");
            price = roomResult.getInt("price");
            status = roomResult.getString("status");
        }
        ArrayList<Object> resultList = new ArrayList<>();
        resultList.add(roomId);
        resultList.add(price);
        resultList.add(status);
        return resultList;
    }

    private String getLiveInfo(int id) {
        Connection connection = null;
        String data = "{\"roomId\":" + "0" + ",\"recordId\":" + "0" + ",\"roomNumber\":" + "\"" + "" + "\"" + ",\"inTime\":" + "\"" + null + "\"" + ",\"outTime\":" + "\"" + null + "\"" + ",\"breakfast\":" + "\"" + "" + "\"" + ",\"id\":" + id + "}";
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String sql = "select hc.id as id, hr.id as recordId, hr.inTime as inTime, hr.outTime as outTime, hr.breakfast as breakfast, h.roomNumber as roomNumber, hr.price as price, h.id as roomId from" +
                    "((hotel_customer as hc inner join hotel as h on hc.id = " + id + " and hc.roomId = h.id) left join hotel_record as hr on hr.customerId = hc.id and hr.roomId = h.id) order by recordId desc limit 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String roomNumber = resultSet.getString("roomNumber");
                Date inTime = resultSet.getTimestamp("inTime");
                Date outTime = resultSet.getTimestamp("outTime");
                String breakfast = resultSet.getString("breakfast");
                int roomId = resultSet.getInt("roomId");
                int recordId = resultSet.getInt("recordId");
                int price = resultSet.getInt("price");
                data = "{\"roomId\":" + roomId + ",\"recordId\":" + recordId + ",\"roomNumber\":" + "\"" + roomNumber + "\"" + ",\"inTime\":" + "\"" + inTime + "\"" + ",\"outTime\":" + "\"" + outTime + "\"" + ",\"breakfast\":" + "\"" + breakfast + "\"" + ",\"id\":" + id + ",\"price\":" + price + "}";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(connection);
        }
        return data;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }

}
