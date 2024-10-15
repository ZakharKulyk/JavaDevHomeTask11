package thymleaftime;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(value = "/time")
public class TimezoneFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String regex = "^UTC([+-](0[0-9]|1[0-4]):([0-5][0-9])|([+-](0[0-9]|1[0-4]))|)$";
        String parameter = req.getParameter("timezone");
        if(parameter==null){
            chain.doFilter(req,res);
        }
        if(parameter.matches(regex)){
            chain.doFilter(req,res);
        }
        res.setContentType("text/html; charset=utf-8");
        res.setStatus(400);
        res.getWriter().write("Invalid timezone");

    }
}
