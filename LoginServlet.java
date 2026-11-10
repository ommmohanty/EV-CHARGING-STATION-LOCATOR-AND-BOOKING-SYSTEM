package com.evcharging.controller;

import com.evcharging.dao.UserDAO;
import com.evcharging.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Accept an email address OR a phone number.
        // The form should use name="identifier".
        String identifier = request.getParameter("identifier");

        // Support the old form temporarily if it still uses name="email".
        if (identifier == null) {
            identifier = request.getParameter("email");
        }

        String password = request.getParameter("password");

        if (identifier == null || password == null
                || identifier.trim().isEmpty()
                || password.isEmpty()) {

            request.setAttribute(
                    "error",
                    "Please enter your email/phone number and password."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
            return;
        }

        try {
            User user = userDAO.loginUser(identifier.trim(), password);

            if (user != null) {

                // Create a fresh session after successful authentication.
                HttpSession oldSession = request.getSession(false);

                if (oldSession != null) {
                    oldSession.invalidate();
                }

                HttpSession session = request.getSession(true);

                // Store the complete User object.
                session.setAttribute("user", user);

                // Store individual user details.
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userName", user.getName());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("userRole", user.getRole());

                // Prevent caching of authenticated pages.
                response.setHeader(
                        "Cache-Control",
                        "no-cache, no-store, must-revalidate"
                );
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);

                // Open the dashboard.
                response.sendRedirect(
                        request.getContextPath() + "/dashboard.jsp"
                );

            } else {

                request.setAttribute(
                        "error",
                        "Invalid email/phone number or password."
                );

                request.getRequestDispatcher("/login.jsp")
                        .forward(request, response);
            }

        } catch (SQLException e) {

            throw new ServletException(
                    "Unable to process login. Check the server logs.",
                    e
            );
        }
    }
}
