package com.spe.ClassroomManagementSystem.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

/** Routes an authenticated user back to the dashboard for their own role. */
@RestController
public class DashboardController {
    @RequestMapping("/dashboard")
    public RedirectView dashboard(HttpSession session) {
        if (session.getAttribute("admin_login") != null) {
            return new RedirectView("/AdminDashboard.jsp");
        }
        if (session.getAttribute("professor_login") != null) {
            return new RedirectView("/ProfessorDashboard.jsp");
        }
        if (session.getAttribute("ta_login") != null) {
            return new RedirectView("/TADashboard.jsp");
        }
        if (session.getAttribute("committee_login") != null) {
            return new RedirectView("/CommitteeDashboard.jsp");
        }
        if (session.getAttribute("sac_login") != null) {
            return new RedirectView("/SACDashboard.jsp");
        }
        return new RedirectView("/LoginFirst.jsp");
    }
}
