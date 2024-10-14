package thymleaftime;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(value = "/time")
public class timeHandler extends HttpServlet {
    private TemplateEngine engine;


    @Override
    public void init(ServletConfig config) throws ServletException {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setOrder(engine.getTemplateResolvers().size());
        templateResolver.setCacheable(false);

        engine.addTemplateResolver(templateResolver);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        Context context = new Context();

        context.setVariable("currentTime", getTime(req, resp));

        engine.process("showTime", context, resp.getWriter());

        resp.getWriter().close();

    }


    private String getDefaultTime(HttpServletRequest request) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        if(request.getHeader("Cookie")!=null){
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("lastTimezone")){
                    ZonedDateTime utc = ZonedDateTime.now(ZoneId.of(cookie.getValue()));
                    return utc.format(dateTimeFormatter);
                }
            }
        }
        ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
        return utc.format(dateTimeFormatter);
    }

    private String getTime(HttpServletRequest request, HttpServletResponse response) {
        String timeZoneValue = request.getParameter("timezone");

        if (timeZoneValue == null) {
            return getDefaultTime(request);
        }

        return getDefaultTimeWithTimeZone(timeZoneValue, response);
    }


    private String getDefaultTimeWithTimeZone(String UTC, HttpServletResponse response) {
        ZonedDateTime utc = ZonedDateTime.now(ZoneId.of(UTC));
        response.addCookie(new Cookie("lastTimezone", UTC));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return utc.format(dateTimeFormatter);
    }


}
