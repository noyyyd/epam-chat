<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="../../static/chat.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
    <title>Chat with friends</title>
</head>
<body>
<div class="greeting">
    <div class="exit__btn">
        <form action="/login" method="post">
            <input type="hidden" name="cmd" value="logout">
            <button class="exit__btn__stl" type="submit">Exit</button>
        </form>
    </div>
</div>
<div class="msg__and__users">
    <div class="chat__window">
        <c:forEach var="message" items="${messages}">
            <c:out value="${message.timeStamp}"/>
            <c:out value="${message.userFrom}"/>:
            <c:out value="${message.text}"/>
            <br>
        </c:forEach>
    </div>
    <div class="users__window">
        <ul>
        <c:forEach var="user" items="${users}">
            <form action="/login" method="post">
                <li>
                <c:out value="${user.nick}"/>
                <c:if test="${adminRole == true}">
                    <input type="hidden" name="cmd" value="kick">
                    <button type="submit" name="nickKickUser" value="${user.nick}">Kick</button>
                </c:if>
                </li>
            </form>
            <br>
        </c:forEach>
        </ul>
        <ul>
            <c:forEach var="kickUser" items="${kickUsers}">
                <form action="/login" method="post">
                    <li>
                        <c:out value="${kickUser.nick}"/>
                        <c:if test="${adminRole == true}">
                            <input type="hidden" name="cmd" value="unkick">
                            <button type="submit" name="nickKickUser" value="${kickUser.nick}">Unkick</button>
                        </c:if>
                    </li>
                </form>
                <br>
            </c:forEach>
        </ul>
    </div>
</div>
<div class="container__users__field">
    <div class="users__field">
        <form action="/login" method="post" class="container__form">
            <input type="text" name="text">
            <input type="hidden" name="cmd" value="message">
            <button class="text__btn" type="submit">Send</button>
        </form>
    </div>
</div>
</body>
</html>