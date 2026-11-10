package com.evcharging.controller;

import com.evcharging.dao.ChargingStationDAO;
import com.evcharging.model.ChargingStation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/stations")
public class StationListServlet extends HttpServlet {

    private ChargingStationDAO stationDAO;

    @Override
    public void init() {
        stationDAO = new ChargingStationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<ChargingStation> stations =
                    stationDAO.getAllStations();

            request.setAttribute("stations", stations);

            request.getRequestDispatcher("/stations.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load charging stations.", e);
        }
    }
}