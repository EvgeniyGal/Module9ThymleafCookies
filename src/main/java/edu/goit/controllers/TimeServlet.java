package edu.goit.controllers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@WebServlet("/time/*")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(Objects.requireNonNull(TimeServlet.class.getResource("../../../templates/")).getPath());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> timezone = getTimezoneFromParameter(req);
        ZoneId zoneId;

        if (timezone.isPresent()) {
            zoneId = ZoneId.of(timezone.get());
            resp.addCookie(new Cookie("lastTimezone", timezone.get()));
        } else {
            zoneId = ZoneId.of("UTC+3");
            resp.addCookie(new Cookie("lastTimezone", "UTC+3"));
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd k:m:s z");

        Context simpleContext = new Context(req.getLocale());

        simpleContext.setVariable("time", ZonedDateTime.now(zoneId).format(dateTimeFormatter));

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();

    }

    private Optional<String> getTimezoneFromParameter(HttpServletRequest req) {
        String timezoneFromParam = req.getParameter("timezone");
        if (timezoneFromParam != null) {
            return Optional.of(URLEncoder.encode(timezoneFromParam, StandardCharsets.UTF_8));
        }

        String timezoneFromCookie = null;
        Cookie[] cookies = req.getCookies();
        for (Cookie element : cookies) {
            if (element.getName().equalsIgnoreCase("lastTimezone")) {
                timezoneFromCookie = element.getValue();
            }
        }

        return timezoneFromCookie != null ? Optional.of(timezoneFromCookie) : Optional.empty();
    }

}
