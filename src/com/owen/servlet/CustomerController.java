package com.owen.servlet;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class CustomerController {
    // 展示客户信息
    public void list(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, ServletException, InvocationTargetException, IllegalAccessException {
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
    public void add(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        Customer customer = getParamFromReq(request);
        Connection connection = null;
        PrintWriter out = response.getWriter();
        try {
            connection = JDBCConnection.getConnection();
            connection.setAutoCommit(true);
            String sql = "insert into `hotel_customer` (`name`, `identity`, `telephone`, `roomId`) values('" + customer.getName() + "', '" + customer.getIdentity() + "', '" + customer.getTelephone() + "',0" +")";
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

    private Customer getParamFromReq(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
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
}
