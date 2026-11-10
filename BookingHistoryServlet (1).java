package com.evcharging.controller;

import com.evcharging.dao.BookingDAO;
import com.evcharging.model.Booking;
import com.evcharging.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/booking-history")
public class BookingHistoryServlet extends HttpServlet {


    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Ensure the user is logged in
        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            // Retrieve bookings for the logged-in user
            List<Booking> bookings =
                    bookingDAO.getBookingsByUser(user.getUserId());

            request.setAttribute("bookings", bookings);

            // Display booking-history.jsp
            request.getRequestDispatcher("/booking-history.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to load booking history.", e);
        }
    }

}
