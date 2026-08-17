package com.dlsu.medflow.web.support;

import com.dlsu.medflow.model.Role;
import com.dlsu.medflow.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * Deliberately simple session-based access control - this project does not
 * pull in Spring Security, matching the scope of the original JavaFX
 * edition (an in-memory username/password check, no encryption, no tokens).
 * A production system should use Spring Security instead; for a course
 * capsule project this keeps the auth story easy to follow end-to-end.
 *
 * <p>Two jobs, mirroring what {@code MainShell} used to do by simply never
 * showing a dashboard the logged-in user's role didn't own:</p>
 * <ol>
 *   <li>No session user at all -&gt; bounce to {@code /login}.</li>
 *   <li>Session user's {@link Role} doesn't match the URL prefix they're
 *       trying to reach (e.g. a Patient requesting {@code /admin/...}) -&gt;
 *       bounce to their own {@code /dashboard} instead.</li>
 * </ol>
 */


public class AuthInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(SessionKeys.CURRENT_USER);

        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }
}
