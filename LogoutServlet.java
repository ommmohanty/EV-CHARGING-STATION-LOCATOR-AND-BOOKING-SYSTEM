package com.evcharging.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        // Get the current session without creating a new one.
        HttpSession session = request.getSession(false);

        // End the user's session.
        if (session != null) {
            session.invalidate();
        }

        // Prevent the browser from reusing a cached page.
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Return to the home page.
        response.sendRedirect(
                request.getContextPath() + "/index.jsp"
        );
    }
}
