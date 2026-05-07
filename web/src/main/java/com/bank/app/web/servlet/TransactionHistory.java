package com.bank.app.web.servlet;

import com.bank.app.core.model.Transaction;
import com.bank.app.core.service.TransactionService;
import com.bank.app.core.service.AccountService;
import com.bank.app.core.model.Account;
import com.bank.app.core.model.Deposit;
import com.bank.app.core.model.Withdrawal;
import com.bank.app.core.service.DepositService;
import com.bank.app.core.service.WithdrawalService;
import com.bank.app.core.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@WebServlet("/admin/TransactionHistory")
public class TransactionHistory extends HttpServlet {
    @PersistenceContext
    private EntityManager em;

    private TransactionService transactionService;
    private AccountService accountService;
    private DepositService depositService;
    private WithdrawalService withdrawalService;

    public static class TransactionView {
        private Transaction tx;
        private String performedBy;
        private String status;
        public TransactionView(Transaction tx, String performedBy, String status) {
            this.tx = tx;
            this.performedBy = performedBy;
            this.status = status;
        }
        public Transaction getTx() { return tx; }
        public String getPerformedBy() { return performedBy; }
        public String getStatus() { return status; }
    }

    private void initServices() throws Exception {
        InitialContext ctx = new InitialContext();
        if (transactionService == null) {
            transactionService = (TransactionService) ctx.lookup("java:global/banking-system-ear/account-module/TransactionSessionBean!com.bank.app.core.service.TransactionService");
        }
        if (accountService == null) {
            accountService = (AccountService) ctx.lookup("java:global/banking-system-ear/account-module/AccountSessionBean!com.bank.app.core.service.AccountService");
        }
        if (depositService == null) {
            depositService = (DepositService) ctx.lookup("java:global/banking-system-ear/account-module/DepositSessionBean!com.bank.app.core.service.DepositService");
        }
        if (withdrawalService == null) {
            withdrawalService = (WithdrawalService) ctx.lookup("java:global/banking-system-ear/account-module/WithdrawalSessionBean!com.bank.app.core.service.WithdrawalService");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            initServices();
            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            boolean isAdmin = user != null && user.getUserType() != null && (user.getUserType().toString().equalsIgnoreCase("ADMIN") || user.getUserType().toString().equalsIgnoreCase("SUPER_ADMIN"));

            String typeParam = req.getParameter("type");
            String accountParam = req.getParameter("accountId");
            String startDateParam = req.getParameter("startDate");
            String endDateParam = req.getParameter("endDate");
            String accountNumberParam = req.getParameter("accountNumber");
            String exportParam = req.getParameter("export");
            List<Transaction> transactions;
            List<Account> allAccounts;

            if (isAdmin) {
                transactions = transactionService.getAllTransactions();
                allAccounts = accountService.getAllAccounts();
            } else if (user != null) {
                transactions = transactionService.getTransactionsByUser(user.getId());
                allAccounts = accountService.getAccountsByUser(user.getId());
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }

            // Filter by type
            if (typeParam != null && !typeParam.isEmpty()) {
                transactions = transactions.stream()
                    .filter(t -> t.getType().toString().equalsIgnoreCase(typeParam))
                    .collect(Collectors.toList());
            }
            // Filter by account
            if (accountParam != null && !accountParam.isEmpty()) {
                try {
                    Long accountId = Long.valueOf(accountParam);
                    transactions = transactions.stream()
                        .filter(t -> t.getAccountId().equals(accountId))
                        .collect(Collectors.toList());
                } catch (NumberFormatException ignored) {}
            }
            // Filter by account number
            if (accountNumberParam != null && !accountNumberParam.isEmpty()) {
                String search = accountNumberParam.trim().toLowerCase();
                List<Long> matchingAccountIds = allAccounts.stream()
                    .filter(acc -> acc.getAccountNumber() != null && acc.getAccountNumber().toLowerCase().contains(search))
                    .map(Account::getId)
                    .collect(Collectors.toList());
                transactions = transactions.stream()
                    .filter(t -> matchingAccountIds.contains(t.getAccountId()))
                    .collect(Collectors.toList());
            }
            // Filter by date range
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (startDateParam != null && !startDateParam.isEmpty()) {
                LocalDate start = LocalDate.parse(startDateParam, dtf);
                transactions = transactions.stream()
                    .filter(t -> t.getDate().toLocalDate().compareTo(start) >= 0)
                    .collect(Collectors.toList());
            }
            if (endDateParam != null && !endDateParam.isEmpty()) {
                LocalDate end = LocalDate.parse(endDateParam, dtf);
                transactions = transactions.stream()
                    .filter(t -> t.getDate().toLocalDate().compareTo(end) <= 0)
                    .collect(Collectors.toList());
            }
            // Build view models
            List<TransactionView> txViews = new ArrayList<>();
            for (Transaction tx : transactions) {
                String performedBy = "-";
                String status = "-";
                switch (tx.getType()) {
                    case DEPOSIT:
                        List<Deposit> deposits = depositService.getDepositsByAccount(tx.getAccountId());
                        Deposit dep = deposits.stream().filter(d -> d.getAmount().compareTo(tx.getAmount()) == 0 && d.getCreatedDate().toLocalDate().equals(tx.getDate().toLocalDate())).findFirst().orElse(null);
                        if (dep != null) {
                            performedBy = dep.getCreatedBy() != null ? dep.getCreatedBy().getName() : "-";
                            status = dep.getStatus() != null ? dep.getStatus().toString() : "-";
                        }
                        break;
                    case WITHDRAWAL:
                        List<Withdrawal> withdrawals = withdrawalService.getWithdrawalsByAccount(tx.getAccountId());
                        Withdrawal wd = withdrawals.stream().filter(w -> w.getAmount().compareTo(tx.getAmount()) == 0 && w.getCreatedDate().toLocalDate().equals(tx.getDate().toLocalDate())).findFirst().orElse(null);
                        if (wd != null) {
                            performedBy = wd.getCreatedBy() != null ? wd.getCreatedBy().getName() : "-";
                            status = wd.getStatus() != null ? wd.getStatus().toString() : "-";
                        }
                        break;
                    case TRANSFER:
                        status = "COMPLETED";
                        break;
                    case INTEREST:
                        performedBy = "System";
                        status = "COMPLETED";
                        break;
                }
                txViews.add(new TransactionView(tx, performedBy, status));
            }
            if (txViews == null) txViews = new ArrayList<>();
            if (allAccounts == null) allAccounts = new ArrayList<>();
            req.setAttribute("transactions", txViews);
            req.setAttribute("allAccounts", allAccounts);
            if (exportParam != null && exportParam.equals("pdf")) {
                // Export PDF
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "attachment; filename=transactions.pdf");
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, resp.getOutputStream());
                    document.open();
                    document.add(new Paragraph("Transaction History"));
                    document.add(new Paragraph(" "));
                    PdfPTable table = new PdfPTable(7);
                    table.addCell("Date/Time");
                    table.addCell("Type");
                    table.addCell("Amount");
                    table.addCell("Account");
                    table.addCell("Description");
                    table.addCell("Performed By");
                    table.addCell("Status");
                    for (TransactionView txView : txViews) {
                        table.addCell(String.valueOf(txView.getTx().getDate()));
                        table.addCell(String.valueOf(txView.getTx().getType()));
                        table.addCell(String.valueOf(txView.getTx().getAmount()));
                        // Find account info
                        String accInfo = "-";
                        for (Account acc : allAccounts) {
                            if (acc.getId().equals(txView.getTx().getAccountId())) {
                                accInfo = acc.getAccountNumber() + " - " + acc.getCustomerName();
                                break;
                            }
                        }
                        table.addCell(accInfo);
                        table.addCell(String.valueOf(txView.getTx().getDescription()));
                        table.addCell(String.valueOf(txView.getPerformedBy()));
                        table.addCell(String.valueOf(txView.getStatus()));
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException e) {
                    throw new IOException("Failed to generate PDF", e);
                }
                return;
            }
            if (isAdmin) {
                req.getRequestDispatcher("/admin/transaction_history.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/user/transaction_history.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getServletContext().log("TransactionHistory error", e);
            resp.sendError(500, "Error loading transactions: " + e.getMessage());
        }
    }
} 