<%-- 
    Document   : edit
    Created on : Jul 9, 2021, 5:01:19 PM
    Author     : Dell 7450
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="./Allproduct/css/edit.css">

    </head>

    <body>
        <header>
            <%@include file="../product/header.jsp" %> 
        </header>           
        <h1>Thông Tin User</h1>
        <form id="editproduct" action="./Save.html" method="POST">
            <input hidden="id" type="text" name="id" value="${u.id}">
            <div>
                <label ><b>Tên: </b></label>
                <input type="text" name="name" value="${u.name}" required>
            </div>
            <div >
                <label ><b>Password:  </b></label>
                <input type="text" name="password" value="${u.password}" required>
            </div>
            <div>
                <label ><b>Số Điên Thoại: </b></label>
                <input type="text" name="phone" value="${u.phone}"></input>                    
            </div>
            <div>
                <label ><b>Số tài khoản: </b>${u.money}</label>                    
            </div>
            <div>
                <button style="margin-left: 250px" type="submit">Cập nhật</button>
            </div>
        </form>
        <footer>
            <%@include file="../footer.jsp" %>
        </footer>
    </body>
</html>
