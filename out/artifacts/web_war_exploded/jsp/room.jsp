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
                    <li><a href="${pageContext.request.contextPath}/customer/list"
                           class="active"><i class="fa fa-edit fa-fw"></i> 客户管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/room"
                           class="active"><i class="fa fa-dashboard fa-fw"></i> 客房管理</a></li>
                    <li><a href="${pageContext.request.contextPath}/record/list"
                           class="active"><i class="fa fa-dashboard fa-fw"></i> 入住信息管理</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- 查询筛选条件 -->
    <div id="page-wrapper">
        <div class="row">
            <div class="col-lg-12">
                <h1 class="page-header">客房管理</h1>
            </div>
            <!-- /.col-lg-12 -->
        </div>

        <!-- /.row -->
        <div class="panel panel-default">
            <div class="panel-body">
                <form class="form-inline"
                      action="${pageContext.request.contextPath}/api/Room/getRooms"
                      method="post">
                    <div class="form-group">
                        <label for="roomNumber">房间号</label> <input type="text"
                                                              class="form-control" id="roomNumber" value="${roomNumber}"
                                                              name="roomNumber">
                    </div>
                    <div class="form-group">
                        <label for="floor">楼层</label> <input type="number"
                                                                   class="form-control" id="floor"
                                                                   value="${floor}"
                                                                   name="floor" min="1" max="10">
                    </div>
                    <div class="form-group">
                        <label for="bed">床位数量</label> <input type="number"
                                                                   class="form-control" id="bed"
                                                                   value="${bed}"
                                                                   name="bed" min="1" max="3">
                    </div>
                    <div class="form-group">
                        <label for="type">类型</label> <input type="text"
                                                                   class="form-control" id="type"
                                                                   value="${type}"
                                                                   name="type">
                    </div>
                    <div class="form-group">
                        <label for="status">状态</label> <input type="status"
                                                                   class="form-control" id="status"
                                                                   value="${status}"
                                                                   name="status">
                    </div>
                    &nbsp;&nbsp;
                    <button type="submit" class="btn btn-primary">查询</button>
                    &nbsp;&nbsp;
                    <!-- data-toggle指以什么事件触发  data-target指事件的目标-->
                    <a href="#" class="btn btn-primary" data-toggle="modal"
                       data-target="#roomAddDialog" )">添加客房</a>
                </form>

            </div>
        </div>

        <!-- 客房信息列表 -->
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading">客房信息列表</div>
                    <!-- /.panel-heading -->
                    <table class="table table-bordered table-striped" id="roomList">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>房间号</th>
                            <th>楼层</th>
                            <th>床位数量</th>
							<th>类型</th>
							<th>每晚房间价格</th>
							<th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${page.rows}" var="row">
                            <tr>
                                <td>${row.id}</td>
                                <td>${row.roomNumber}</td>
								<td>${row.floor}</td>
								<td>${row.bed}</td>
								<td>${row.type}</td>
								<td>${row.price}</td>
								<td>${row.status}</td>
                                <td><a href="#" class="btn btn-primary btn-xs"
                                       data-toggle="modal" data-target="#roomEditDialog"
                                       onclick="editRoom(${row.id})">修改</a> <a href="#"
                                                                                    class="btn btn-danger btn-xs"
                                                                                    onclick="deleteRoom(${row.id})">删除</a>
                                    <a href="#" class="btn btn-warning btn-xs" onclick="changeStatus(${row.id}, '${row.status}')">进入维修 / 恢复正常</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                    <div class="col-md-12 text-right">
                        <owen:page
                                url="${pageContext.request.contextPath}/api/Room/getRooms"/>
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
<div class="modal fade" id="roomEditDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改客房信息</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="edit_room_form">
                    <input type="hidden" id="edit_id" name="id"/>
                    <div class="form-group">
                        <label for="edit_roomNumber" class="col-sm-2 control-label">房间号</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="房间号"
                                   name="roomNumber" id="edit_roomNumber" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="edit_type" class="col-sm-2 control-label">房间类型</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="房间类型"
                                   name="type" id="edit_type" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="edit_floor" class="col-sm-2 control-label">所在楼层</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" placeholder="所在楼层"
                                   name="floor" id="edit_floor" required min="1" max="10">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="edit_bed" class="col-sm-2 control-label">床位数量</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" placeholder="床位数量"
                                   name="bed" id="edit_bed" required min="1" max="3">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit_price" class="col-sm-2 control-label">每睌房间价格</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" placeholder="每睌房间价格"
                                   name="price" id="edit_price" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="edit_status" class="col-sm-2 control-label">房间状态</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="房间状态"
                                   name="status" id="edit_status" disabled>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary"
                        onclick="updateRoom()">修改客房
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>
<!-- /#wrapper -->

<!-- 客房添加对话框 -->
<div class="modal fade" id="roomAddDialog" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">添加客房信息</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal" id="add_room_form">
                    <div class="form-group">
                        <label for="add_roomNumber" class="col-sm-2 control-label">房间号</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="房间号"
                                   name="roomNumber" id="add_roomNumber" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="add_type" class="col-sm-2 control-label">房间类型</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" placeholder="房间类型"
                                   name="type" id="add_type" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="add_floor" class="col-sm-2 control-label">所在楼层</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" placeholder="所在楼层"
                                   name="floor" id="add_floor" required min="1" max="10">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="add_bed" class="col-sm-2 control-label">床位数量</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" placeholder="床位数量"
                                   name="bed" id="add_bed" min="1" max="3">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="add_price" class="col-sm-2 control-label">每睌房间价格</label>
                        <div class="col-sm-10">
                            <input type="number" class="form-control" placeholder="每睌房间价格"
                                   name="price" id="add_price">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="addRoom()">添加客房</button>
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
    function editRoom(id) {
        $.ajax({
            type: "get",
            url: "<%=basePath%>room/edit.action",
            data: {"id": id},
            success: function (data) {
                $("#edit_id").val(data.id);
                $("#edit_roomNumber").val(data.roomNumber);
                $("#edit_type").val(data.type);
                $("#edit_floor").val(data.floor);
                $("#edit_bed").val(data.bed);
                $("#edit_price").val(data.price);
                $("#edit_status").val(data.status)
            }
        });
    }

    function updateRoom() {
        $.post("<%=basePath%>room/update.action", $("#edit_room_form").serialize(), function (data) {
            alert("客房信息更新成功！");
            window.location.reload();
        });
    }

    function deleteRoom(id) {
        if (confirm('确实要删除该客房吗?')) {
            $.post("<%=basePath%>room/delete.action", {"id": id}, function (data) {
                alert("客房删除成功！");
                window.location.reload();
            });
        }
    }

    function addRoom() {
        $.post("<%=basePath%>room/add.action", $("#add_room_form").serialize(), function (data) {
            alert("客房信息添加成功！");
            window.location.reload();
        });
    }

    function logout() {
        $.post("<%=basePath%>user/logout.action", function (data) {
        });
    }

    function changeStatus(id, status) {
        var msg = '';
        var successMsg = '';
        if (status === '正常') {
            msg = '是否维修该客房？';
            successMsg = '客房进入维修状态！';
            newStatus = '维修'
        }else {
            msg = '是否恢复该客房？';
            successMsg = '客房恢复正常状态！';
            newStatus = '正常'
        }
        if (confirm(msg)) {
            $.post("<%=basePath%>room/changeStatus.action", {"id": id, "status": newStatus}, function (data) {
                if (data){
                    alert(successMsg);
                    window.location.reload();
                } else {
                    alert("该房间有客人正在入住，无法进入维修");
                }

            });
        }
    }

    $(document).ready(function () {
        $.get("<%=basePath%>api/Room/getRooms", function (data) {
            // console.log(data)
        });
    })
</script>
</body>

</html>
