package com.bank.app.web.servlet;

import com.bank.app.core.model.ScheduledTransfer;
import com.bank.app.core.service.ScheduledTransferService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/admin/view_scheduled_transfer")
public class ViewScheduledTransfer extends HttpServlet {
    private ScheduledTransferService scheduledTransferService;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            scheduledTransferService = (ScheduledTransferService) ctx.lookup("java:global/banking-system-ear/account-module/ScheduledTransferSessionBean!com.bank.app.core.service.ScheduledTransferService");
        } catch (NamingException e) {
            throw new ServletException("Failed to lookup ScheduledTransferService", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        ScheduledTransfer transfer = null;
        if (idParam != null) {
            try {
                Long id = Long.parseLong(idParam);
                transfer = scheduledTransferService.getAllScheduledTransfers().stream()
                        .filter(t -> t.getId().equals(id))
                        .findFirst().orElse(null);
            } catch (NumberFormatException ignored) {}
        }
        if (transfer != null) {
            request.setAttribute("scheduledTransfer", transfer);
        }

        request.getRequestDispatcher("/admin/view_scheduled_transfer.jsp").forward(request, response);
    }
} 