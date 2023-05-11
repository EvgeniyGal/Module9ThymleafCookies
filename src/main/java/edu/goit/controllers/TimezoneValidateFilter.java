package edu.goit.controllers;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");
        if (timezone == null) {
            chain.doFilter(req, res);
        } else {
            try {
                ZoneId.of(URLEncoder.encode(timezone, StandardCharsets.UTF_8));
                chain.doFilter(req, res);
            } catch (Exception e) {
                res.setContentType("text/plain");
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().write("Wrong ZoneID format!");
            }
        }
    }
}
