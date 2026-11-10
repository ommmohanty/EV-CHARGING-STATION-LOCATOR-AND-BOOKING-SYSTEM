package com.evcharging.controller;

import java.sql.SQLException;

import com.evcharging.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        // Validate required fields
        if (name == null || email == null
                || phone == null || password == null
                || name.isBlank() || email.isBlank()
                || phone.isBlank() || password.isBlank()) {

            request.setAttribute(
                    "error",
                    "All fields are required."
            );

            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
            return;
        }

        // Validate phone number
        if (!phone.trim().matches("[0-9]{10}")) {

            request.setAttribute(
                    "error",
                    "Enter a valid 10-digit phone number."
            );

            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
            return;
        }

        // Validate password
        if (password.length() < 8) {

            request.setAttribute(
                    "error",
                    "Password must contain at least 8 characters."
            );

            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);
            return;
        }

        try {

            boolean registered = userDAO.registerUser(
                    name.trim(),
                    email.trim(),
                    phone.trim(),
                    password
            );

            if (registered) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/login.jsp?registered=1"
                );

            } else {

                request.setAttribute(
                        "error",
                        "This email or phone number is already registered."
                );

                request.getRequestDispatcher("/register.jsp")
                        .forward(request, response);
            }

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

            request.getRequestDispatcher("/register.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Database error during registration.", e
            );
        }
    }
}

