<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Trouble Ticket Page</title>
    </head>	
    <body>
        <table>
            <tr>
                <th>ObjectID</th>
                <th>Title</th>
                <th>Status</th>
                <th>Severity</th>
                <th>Created Time</th>
                <th>Closed Time</th>
                <th>External TT</th>
            </tr>
            <c:forEach items="${listTT}" var="ticket">
                <tr>
                    <td><c:out value="${ticket.internalTicketId}"/></td>
                    <td><c:out value="${ticket.title}"/></td>  
                    <td><c:out value="${ticket.status}"/></td>  
                    <td><c:out value="${ticket.severity}"/></td> 
                    <td><c:out value="${ticket.timeCreation}"/></td>  
                    <td><c:out value="${ticket.timeClosed}"/></td>  
                    <td><c:out value="${ticket.externalTicketID}"/></td>  
                </tr>
            </c:forEach>
        </table>
    </body>	
</html>