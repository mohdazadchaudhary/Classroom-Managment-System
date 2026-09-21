package com.spe.ClassroomManagementSystem.Controller;


import com.spe.ClassroomManagementSystem.Models.*;
import com.spe.ClassroomManagementSystem.Service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private CommitteeService committeeService;
    @Autowired
    private ProfessorService professorService;
    @Autowired
    private SacService sacService;
    @Autowired
    private TaService taService;
    @Autowired
    private LoginService loginService;

    // -------------------------------------------------------------------------
    // Admin-only: register a user directly (immediately approved)
    // -------------------------------------------------------------------------

    /**
     * Handles admin-created user registration.
     * Accounts created here are immediately active (status = "approved").
     *
     * @param usertype  the role of the new user
     * @param name      full name / display name
     * @param username  login username
     * @param email     contact email
     * @param password  login password
     * @param session   HTTP session (must contain "admin_login" attribute)
     * @return redirect back to RegisterUser.jsp
     */
    @PostMapping("/register")
    public RedirectView registerUser(@RequestParam("usertype") String usertype,
                                     @RequestParam("name") String name,
                                     @RequestParam("username") String username,
                                     @RequestParam("email") String email,
                                     @RequestParam("password") String password,
                                     HttpSession session) {
        logger.trace("registerUser called");
        Login login = new Login();
        login.setUserType(usertype);
        login.setPassword(password);
        login.setUserName(username);
        login.setStatus("approved"); // Admin-created accounts are immediately active
        boolean saved = loginService.save(login, session);
        if (saved) {
            logger.info("user added by admin");
            String msg = "";
            switch (usertype) {
                case "professor":
                    Professor professor = new Professor();
                    professor.setProfessorName(name);
                    professor.setUserName(username);
                    professor.setProfessorEmail(email);
                    professor.setForeignId(login);
                    msg = professorService.saveProfessor(professor);
                    session.setAttribute("msg", msg);
                    break;
                case "ta":
                    TA ta = new TA();
                    ta.setTaName(name);
                    ta.setTaEmail(email);
                    ta.setUserName(username);
                    ta.setForeignId(login);
                    msg = taService.saveTa(ta);
                    session.setAttribute("msg", msg);
                    break;
                case "committee":
                    Committee committee = new Committee();
                    committee.setUserName(username);
                    committee.setCommitteeName(name);
                    committee.setCommitteeEmail(email);
                    committee.setForeignId(login);
                    msg = committeeService.saveCommittee(committee);
                    session.setAttribute("msg", msg);
                    break;
                case "sac":
                    Sac sac = new Sac();
                    sac.setSacName(name);
                    sac.setUserName(username);
                    sac.setSacEmail(email);
                    sac.setForeignId(login);
                    msg = sacService.saveSac(sac);
                    session.setAttribute("msg", msg);
                    break;
            }
        }
        RedirectView rv = new RedirectView();
        rv.setUrl("/RegisterUser.jsp");
        return rv;
    }

    // -------------------------------------------------------------------------
    // Public: self-signup (account starts as "pending", awaits admin approval)
    // -------------------------------------------------------------------------

    /**
     * Handles public self-registration.
     * The created account has status = "pending" and cannot be used to log in
     * until an admin approves it via {@code /approveUser}.
     *
     * @param usertype  the role the user is requesting
     * @param name      full name / display name
     * @param username  desired login username
     * @param email     contact email
     * @param password  desired password
     * @param session   HTTP session (used for flash messages)
     * @return redirect to index.html on success, back to Signup.jsp on failure
     */
    @PostMapping("/signup")
    public RedirectView selfSignup(@RequestParam("usertype") String usertype,
                                   @RequestParam("name") String name,
                                   @RequestParam("username") String username,
                                   @RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   HttpSession session) {
        logger.trace("selfSignup called for username='{}', role='{}'", username, usertype);
        RedirectView rv = new RedirectView();

        Login login = new Login();
        login.setUserType(usertype);
        login.setPassword(password);
        login.setUserName(username);
        login.setStatus("pending");

        boolean saved = loginService.selfRegister(login, session);

        if (!saved) {
            // Username conflict: selfRegister already set "signup_error" on session
            rv.setUrl("/Signup.jsp");
            return rv;
        }

        // Save the role-specific profile linked to the pending Login
        switch (usertype) {
            case "professor":
                Professor professor = new Professor();
                professor.setProfessorName(name);
                professor.setUserName(username);
                professor.setProfessorEmail(email);
                professor.setForeignId(login);
                professorService.saveProfessor(professor);
                break;
            case "ta":
                TA ta = new TA();
                ta.setTaName(name);
                ta.setTaEmail(email);
                ta.setUserName(username);
                ta.setForeignId(login);
                taService.saveTa(ta);
                break;
            case "committee":
                Committee committee = new Committee();
                committee.setUserName(username);
                committee.setCommitteeName(name);
                committee.setCommitteeEmail(email);
                committee.setForeignId(login);
                committeeService.saveCommittee(committee);
                break;
            case "sac":
                Sac sac = new Sac();
                sac.setSacName(name);
                sac.setUserName(username);
                sac.setSacEmail(email);
                sac.setForeignId(login);
                sacService.saveSac(sac);
                break;
        }

        logger.info("Self-registration complete for '{}' — awaiting admin approval", username);
        session.setAttribute("signup_success",
                "Registration submitted! Your account is pending admin approval. You will be able to log in once approved.");
        rv.setUrl("/index.jsp");
        return rv;
    }

    // -------------------------------------------------------------------------
    // Admin-only: view, approve, and reject pending signups
    // -------------------------------------------------------------------------

    /**
     * Returns the AdminPendingUsers view populated with all pending signup requests.
     * Redirects unauthenticated requests to LoginFirst.jsp.
     *
     * @param session HTTP session (must contain "admin_login")
     * @return ModelAndView for AdminPendingUsers.jsp or redirect
     */
    @GetMapping("/getPendingUsers")
    public ModelAndView getPendingUsers(HttpSession session) {
        if (session.getAttribute("admin_login") == null) {
            return new ModelAndView("redirect:/LoginFirst.jsp");
        }
        List<Login> pendingUsers = loginService.getPendingUsers();
        ModelAndView mv = new ModelAndView("AdminPendingUsers");
        mv.addObject("pendingUsers", pendingUsers);
        return mv;
    }

    /**
     * Approves a pending user account.
     * Admin-only — redirects unauthenticated requests to LoginFirst.jsp.
     *
     * @param loginId the ID of the Login record to approve
     * @param session HTTP session (must contain "admin_login")
     * @return redirect to /getPendingUsers
     */
    @PostMapping("/approveUser")
    public RedirectView approveUser(@RequestParam("loginId") Long loginId,
                                    HttpSession session) {
        if (session.getAttribute("admin_login") == null) {
            return new RedirectView("/LoginFirst.jsp");
        }
        try {
            loginService.approveUser(loginId);
            session.setAttribute("admin_msg", "User approved successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("approveUser failed: {}", e.getMessage());
            session.setAttribute("admin_msg", "Error: " + e.getMessage());
        }
        return new RedirectView("/getPendingUsers");
    }

    /**
     * Rejects (deletes) a pending user account.
     * Admin-only — redirects unauthenticated requests to LoginFirst.jsp.
     *
     * @param loginId the ID of the Login record to reject
     * @param session HTTP session (must contain "admin_login")
     * @return redirect to /getPendingUsers
     */
    @PostMapping("/rejectUser")
    public RedirectView rejectUser(@RequestParam("loginId") Long loginId,
                                   HttpSession session) {
        if (session.getAttribute("admin_login") == null) {
            return new RedirectView("/LoginFirst.jsp");
        }
        try {
            loginService.rejectUser(loginId);
            session.setAttribute("admin_msg", "User rejected and removed.");
        } catch (IllegalArgumentException e) {
            logger.error("rejectUser failed: {}", e.getMessage());
            session.setAttribute("admin_msg", "Error: " + e.getMessage());
        }
        return new RedirectView("/getPendingUsers");
    }
}