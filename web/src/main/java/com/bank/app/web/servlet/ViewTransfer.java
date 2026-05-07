package com.bank.app.web.servlet;

import com.bank.app.core.model.Transfer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;

@WebServlet("/admin/view_transfer")
public class ViewTransfer extends HttpServlet {
    @PersistenceContext
    private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Transfer transfer = null;
        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                Long id = Long.parseLong(idParam);
                transfer = em.find(Transfer.class, id);
            } catch (Exception ignored) {}
        }
        req.setAttribute("transfer", transfer);
        req.getRequestDispatcher("/admin/single_view_transfer.jsp").forward(req, resp);
    }
} 