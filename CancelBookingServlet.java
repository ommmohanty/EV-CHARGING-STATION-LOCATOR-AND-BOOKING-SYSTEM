package com.evcharging.controller;

import com.evcharging.dao.BookingDAO;
import com.evcharging.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cancel-booking")
public class CancelBookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            int bookingId = Integer.parseInt(
                    request.getParameter("bookingId"));

            boolean cancelled = bookingDAO.cancelBooking(
                    bookingId, user.getUserId());

            if (cancelled) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/booking-history?cancelled=1");
            } else {
                response.sendRedirect(
                        request.getContextPath()
                                + "/booking-history?error=cancel");
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid booking ID.");
        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to cancel booking.", e);
        }
    }
}