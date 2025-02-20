package com.rakibdevhub.iusping.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class RedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativeUri = uri.substring(contextPath.length());

        if (session != null && session.getAttribute("role") != null && relativeUri.equals("/login")) {
            String role = (String) session.getAttribute("role");
            switch (role) {
                case "admin" -> {
                    httpResponse.sendRedirect(contextPath + "/admin/dashboard");
                    return;
                }
                case "teacher" -> {
                    httpResponse.sendRedirect(contextPath + "/teacher/dashboard");
                    return;
                }
                case "student" -> {
                    httpResponse.sendRedirect(contextPath + "/student/dashboard");
                    return;
                }
                default -> {
                }
            }
        }

        if (session == null || session.getAttribute("role") == null) {
            if (relativeUri.equals("/admin/dashboard") || relativeUri.equals("/teacher/dashboard") || relativeUri.equals("/student/dashboard")) {
                httpResponse.sendRedirect(contextPath + "/login");
                return;
            }
        }

        switch (relativeUri) {
            case "/", "/index", "/index.jsp", "/home.jsp" ->
                httpResponse.sendRedirect(contextPath + "/home");
            case "/login.jsp", "/signin", "/signin.jsp" ->
                httpResponse.sendRedirect(contextPath + "/login");
            default ->
                chain.doFilter(request, response);
        }
    }
}
