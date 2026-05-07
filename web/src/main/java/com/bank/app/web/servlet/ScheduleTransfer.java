package com.bank.app.web.servlet;

import com.bank.app.core.model.ScheduledTransfer;
import com.bank.app.core.model.TransferStatus;
import com.bank.app.core.service.ScheduledTransferService;
import com.bank.app.core.model.Account;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.model.User;
import com.bank.app.core.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.naming.InitialContext;

@WebServlet("/admin/ScheduleTransfer")
public class ScheduleTransfer extends HttpServlet {
    private ScheduledTransferService scheduledTransferService;
    private AccountService accountService;
    private UserService userService;

    private void initService() throws Exception {
        if (scheduledTransferService == null) {
            InitialContext context = new InitialContext();
            scheduledTransferService = (ScheduledTransferService) context.lookup(
                "java:global/banking-system-ear/account-module/ScheduledTransferSessionBean!com.bank.app.core.service.ScheduledTransferService");
        }
    }

    private void initAccountService() throws Exception {
        if (accountService == null) {
            InitialContext context = new InitialContext();
            accountService = (AccountService) context.lookup(
                "java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        }
    }

    private void initUserService() throws Exception {
        if (userService == null) {
            InitialContext context = new InitialContext();
            userService = (UserService) context.lookup(
                "java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initService();
            String action = req.getParameter("action");
            if ("verify".equals(action)) {
                // Verify account numbers
                initAccountService();
                String fromAccountNumber = req.getParameter("fromAccountNumber");
                String toAccountNumber = req.getParameter("toAccountNumber");
                Account fromAccount = null;
                Account toAccount = null;
                if (fromAccountNumber != null && toAccountNumber != null) {
                    fromAccount = accountService.getAccountByAccountNumber(fromAccountNumber);
                    toAccount = accountService.getAccountByAccountNumber(toAccountNumber);
                    if (fromAccount != null && toAccount != null) {
                        req.setAttribute("fromAccount", fromAccount);
                        req.setAttribute("toAccount", toAccount);
                        req.getRequestDispatcher("/admin/schedule_transfer.jsp").forward(req, resp);
                        return;
                    }
                }
                // If not found, show error and keep entered values
                req.setAttribute("error", "Invalid account number(s). Please try again.");
                req.setAttribute("fromAccountNumber", fromAccountNumber);
                req.setAttribute("toAccountNumber", toAccountNumber);
                req.getRequestDispatcher("/admin/schedule_transfer.jsp").forward(req, resp);
                return;
            }
            if (action == null || action.equals("add")) {
                // Create new scheduled transfer
        Long fromAccountId = Long.valueOf(req.getParameter("fromAccountId"));
        Long toAccountId = Long.valueOf(req.getParameter("toAccountId"));
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));
        LocalDateTime scheduledTime = LocalDateTime.parse(req.getParameter("scheduledTime"));
        ScheduledTransfer transfer = new ScheduledTransfer(fromAccountId, toAccountId, amount, scheduledTime);
                // Set createdBy
                try {
                    initUserService();
                    String email = req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : null;
                    if (email != null) {
                        User user = userService.getUserByEmail(email);
                        if (user != null) {
                            transfer.setCreatedBy(user);
                        }
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                scheduledTransferService.createScheduledTransfer(transfer);
                resp.sendRedirect(req.getContextPath() + "/admin/scheduled_transfers.jsp?success=created");
            } else if (action.equals("edit")) {
                // Edit existing scheduled transfer
                Long id = Long.valueOf(req.getParameter("id"));
                ScheduledTransfer transfer = findTransferById(id);
                if (transfer != null && transfer.getStatus() == TransferStatus.PENDING) {
                    transfer.setFromAccountId(Long.valueOf(req.getParameter("fromAccountId")));
                    transfer.setToAccountId(Long.valueOf(req.getParameter("toAccountId")));
                    transfer.setAmount(new BigDecimal(req.getParameter("amount")));
                    transfer.setScheduledDate(LocalDateTime.parse(req.getParameter("scheduledTime")));
                    scheduledTransferService.updateScheduledTransfer(transfer);
                    resp.sendRedirect(req.getContextPath() + "/admin/scheduled_transfers.jsp?success=updated");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/scheduled_transfers.jsp?error=not_found_or_not_pending");
                }
            } else if (action.equals("cancel")) {
                // Cancel scheduled transfer
                Long id = Long.valueOf(req.getParameter("id"));
                ScheduledTransfer transfer = findTransferById(id);
                if (transfer != null && transfer.getStatus() == TransferStatus.PENDING) {
                    transfer.setStatus(TransferStatus.CANCELLED);
                    transfer.setCancelledDate(java.time.LocalDateTime.now()); // Set cancelledDate
                    scheduledTransferService.updateScheduledTransfer(transfer);
                    resp.sendRedirect(req.getContextPath() + "/admin/scheduled_transfers.jsp?success=cancelled");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/admin/scheduled_transfers.jsp?error=not_found_or_not_pending");
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/scheduled_transfers.jsp?error=invalid_action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin/scheduled_transfers.jsp?error=exception");
        }
    }

    private ScheduledTransfer findTransferById(Long id) throws Exception {
        // Helper to fetch a single transfer by ID
        initService();
        for (ScheduledTransfer st : scheduledTransferService.getAllScheduledTransfers()) {
            if (st.getId().equals(id)) return st;
        }
        return null;
    }
} 