<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="owen" uri="owenLin" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>酒店管理系统</title>

    <!-- Bootstrap Core CSS -->
    <link href="<%=basePath%>css/bootstrap.min.css" rel="stylesheet">

    <!-- MetisMenu CSS -->
    <link href="<%=basePath%>css/metisMenu.min.css" rel="stylesheet">

    <!-- DataTables CSS -->
    <link href="<%=basePath%>css/dataTables.bootstrap.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="<%=basePath%>css/sb-admin-2.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="<%=basePath%>css/font-awesome.min.css" rel="stylesheet"
          type="text/css">
    <link href="<%=basePath%>css/boot-crm.css" rel="stylesheet"
          type="text/css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

<div id="wrapper">
    <!-- Navigation -->
    <nav class="navbar navbar-default navbar-static-top" role="navigation"
         style="margin-bottom: 0">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse"
                    data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span> <span
                    class="icon-bar"></span> <span class="icon-bar"></span> <span
                    class="icon-bar"></span>
            </button>
            <a class="navbar-brand">酒店管理系统 v1.0</a>
        </div>

        <!-- /.navbar-header -->
        <ul class="nav navbar-top-links navbar-right">
            <li class="dropdown"><a class="dropdown-toggle"
                                    data-toggle="dropdown" href="#"> <i class="fa fa-user fa-fw"></i>
                <i class="fa fa-caret-down"></i></a>
                <ul class="dropdown-menu dropdown-user">
                    <li><a href="${pageContext.request.contextPath}/user/login"
                           onclick="logout()"> <i class="fa fa-sign-out fa-fw"></i>退出登录
                    </a></li>
                </ul>
            </li>
        </ul>

        <!-- /.navbar-top-links -->
        <div class="navbar-default sidebar" role="navigation">
            <div class="sidebar-nav navbar-collapse">
                <ul class="nav" id="side-menu">
                    <li><a href="${pageContext.request.contextPath}/api/Customer/list"
                           class="active"><i class="fa fa-edit fa-fw"></i> 客户管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/api/Room/list"
                           class="active"><i class="fa fa-dashboard fa-fw"></i> 客房管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/api/Record/list"
                           class="active"><i class="fa fa-dashboard fa-fw"></i> 入住信息管理</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- 查询筛选条件 -->
    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header">入住信息管理</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <form class="form-inline"
                      action="${pageContext.request.contextPath}/api/Record/list"
                      method="post">
                    <div class="form-group">
                        <label for="nameFilter">客户姓名</label> <input type="text"
                                                                    class="form-control" id="nameFilter" value="${name}"
                                                                    name="name">
                    </div>
                    <div class="form-group">
                        <label for="roomNumberFilter">房间号</label> <input type="text"
                                                                         class="form-control" id="roomNumberFilter"
                                                                         value="${roomNumber}"
                                                                         name="roomNumber">
                    </div>
                    <div class="form-group">
                        <label for="typeFilter">房间类型</label> <input type="text"
                                                                    class="form-control" id="typeFilter"
                                                                    value="${type}"
                                                                    name="type">
                    </div>
                    <button type="submit" class="btn btn-primary">查询</button>
                </form>

            </div>
        </div>

        <!-- 客户信息列表 -->
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading">入住信息列表</div>
                    <!-- /.panel-heading -->
                    <table class="table table-bordered table-striped">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>客户名称</th>
                            <th>电话号码</th>
                            <th>房间号码</th>
                            <th>类型</th>
                            <th>入住时间</th>
                            <th>离开时间</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${page.rows}" var="row">
                            <tr>
                                <td>${row.id}</td>
                                <td>${row.name}</td>
                                <td>${row.telephone}</td>
                                <td>${row.roomNumber}</td>
                                <td>${row.type}</td>
                                <td>${row.inTime}</td>
                                <td>${row.outTime}</td>
                                <td><a href="#" class="btn btn-primary btn-xs"
                                       data-toggle="modal" data-target="#recordDetail"
                                       onclick="getDetail(${row.id}, ${row.roomNumber})">详情</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="col-md-12 text-right">
                        <owen:page
                                url="${pageContext.request.contextPath }/api/Record/list"/>
                    </div>
                    <!-- /.panel-body -->
                </div>
                <!-- /.panel -->
            </div>
            <!-- /.col-lg-12 -->
        </div>
    </div>
    <!-- /#page-wrapper -->
</div>

<!-- 客户编辑对话框 -->
<div class="modal fade" id="recordDetail" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">入住记录详情</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="edit_room_form">
                    <input type="hidden" id="edit_id" name="id"/>
                    <div class="form-group">
                        <label for="detail_roomNumber" class="col-sm-2 control-label">房间号</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="房间号"
                                   name="roomNumber" id="detail_roomNumber" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="detail_type" class="col-sm-2 control-label">房间类型</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="房间类型"
                                   name="type" id="detail_type" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="detail_nameList" class="col-sm-2 control-label">入住客户</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="入住客户"
                                   name="nameList" id="detail_nameList" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="detail_inTime" class="col-sm-2 control-label">入住时间</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="入住时间"
                                   name="inTime" id="detail_inTime" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="detail_outTime" class="col-sm-2 control-label">离开时间</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="离开时间"
                                   name="outTime" id="detail_outTime" disabled>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>

<!-- jQuery -->
<script src="<%=basePath%>js/jquery.min.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="<%=basePath%>js/bootstrap.min.js"></script>

<!-- Metis Menu Plugin JavaScript -->
<script src="<%=basePath%>js/metisMenu.min.js"></script>

<!-- DataTables JavaScript -->
<script src="<%=basePath%>js/jquery.dataTables.min.js"></script>
<script src="<%=basePath%>js/dataTables.bootstrap.min.js"></script>

<!-- Custom Theme JavaScript -->
<script src="<%=basePath%>js/sb-admin-2.js"></script>

<script type="text/javascript">
    function getDetail(id, roomNumber) {
        $.ajax({
            type: "get",
            url: "<%=basePath%>api/Record/get",
            data: {"id": id, "roomNumber": roomNumber},
            success: function (data) {
                $("#detail_inTime").val(data.inTime);
                $("#detail_outTime").val(data.outTime);
                $("#detail_nameList").val(data.nameList);
                $("#detail_roomNumber").val(data.roomNumber);
                $("#detail_type").val(data.type);
            }
        });
    }

    function logout() {
        $.post("<%=basePath%>user/logout.action", function (data) {
        });
    }

</script>
</body>

</html>
