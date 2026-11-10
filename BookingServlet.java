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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@WebServlet("/book")
public class BookingServlet extends HttpServlet {


    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check whether the user is logged in
        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            // Read booking form values
            int stationId = Integer.parseInt(
                    request.getParameter("stationId"));

            LocalDate bookingDate = LocalDate.parse(
                    request.getParameter("bookingDate"));

            LocalTime startTime = LocalTime.parse(
                    request.getParameter("startTime"));

            LocalTime endTime = LocalTime.parse(
                    request.getParameter("endTime"));

            // Validate booking details
            if (stationId <= 0) {
                throw new IllegalArgumentException(
                        "Please select a valid charging station.");
            }
            // End time must be later than start time
            if (!endTime.isAfter(startTime)) {
                throw new IllegalArgumentException(
                        "End time must be later than start time.");
            }

            if (bookingDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "You cannot book a date in the past.");
            }

            if (bookingDate.equals(LocalDate.now())
                    && !startTime.isAfter(LocalTime.now())) {
                throw new IllegalArgumentException(
                        "For today's booking, please select a future start time.");
            }

            if (!endTime.isAfter(startTime)) {
                throw new IllegalArgumentException(
                        "End time must be later than start time.");
            }

            // Create booking object
            Booking booking = new Booking();

            booking.setUserId(user.getUserId());
            booking.setStationId(stationId);
            booking.setBookingDate(bookingDate);
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);
            booking.setStatus("BOOKED");

            // Save booking to database
            boolean created = bookingDAO.createBooking(booking);

            if (created) {
                // Load the booking history page
                response.sendRedirect(
                        request.getContextPath()
                                + "/booking-history?success=1");
            } else {
                response.sendRedirect(
                        request.getContextPath()
                                + "/booking.jsp?stationId=" + stationId
                                + "&error=booking"
                );
            }

        } catch (NumberFormatException |
                 DateTimeParseException e) {

            request.setAttribute(
                    "error",
                    "Please enter valid booking details.");

            request.getRequestDispatcher("/booking.jsp")
                    .forward(request, response);

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

            request.getRequestDispatcher("/booking.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Database error: " + e.getMessage()
            );

            request.getRequestDispatcher("/booking.jsp")
                    .forward(request, response);
        }
    }


}
