package com.spe.ClassroomManagementSystem.Service;

import com.spe.ClassroomManagementSystem.Models.*;
import com.spe.ClassroomManagementSystem.Repository.LoginRepository;
import com.spe.ClassroomManagementSystem.Repository.ProfessorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Table;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Override
    public boolean save(Login login, HttpSession session){
        List<Login> loginList = loginRepository.findAll();
        for (Login l:loginList) {
            if (login.getUserName().equals(l.getUserName()) && login.getUserType().equals(l.getUserType())){
                logger.error("User Already Exists");
                session.setAttribute("msg", "User Already Exists");
                return false;
            }
        }

        loginRepository.save(login);
        logger.info("User saved successfully");
        return true;

    }

    @Override
    public  Login findByUsernameAndPassword(String username,String password){
        return loginRepository.findByUserNameAndPassword(username,password);
    }

    @Override
    public boolean checkCredentials(String username, String password, String userType, HttpSession session) {
        session.setAttribute("userType", userType);
        if (userType.equals("admin")){
            if (username.equals("admin") && password.equals("admin")){
                return true;
            }
        }
        Login user = loginRepository.findByUserNameAndUserType(username, userType);
        System.out.println(user);

        if (user == null) {
            return false;
        } else {
            // Block accounts that are not yet approved by admin
            if (!"approved".equals(user.getStatus())) {
                logger.warn("Login blocked for user '{}': account status is '{}'", username, user.getStatus());
                return false;
            }
            if (user.getPassword().equals(password)) {
                switch (userType) {
                    case "professor":
                        session.setAttribute("login", true);
                        Professor professor = user.getProfessor();
                        session.setAttribute("professor", professor);
                        break;
                    case "ta":
                        session.setAttribute("login", true);
                        TA ta = user.getTa();
                        session.setAttribute("ta", ta);
                        break;
                    case "committee":
                        session.setAttribute("login", true);
                        Committee committee = user.getCommittee();
                        session.setAttribute("committee", committee);
                        break;
                    case "sac":
                        session.setAttribute("login", true);
                        Sac sac = user.getSac();
                        session.setAttribute("sac", sac);
                        break;

                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public Login findByLoginId(Long loginId)
    {
        return loginRepository.findByLoginId(loginId);
    }

    /**
     * Saves a self-registered user with status set to "pending".
     * The user cannot log in until an admin approves the account.
     *
     * @param login   Login entity with status already set to "pending"
     * @param session HTTP session for flash messages
     * @return true if registration succeeded, false if username is already taken
     */
    @Override
    public boolean selfRegister(Login login, HttpSession session) {
        List<Login> existing = loginRepository.findAll();
        for (Login l : existing) {
            if (login.getUserName().equals(l.getUserName()) && login.getUserType().equals(l.getUserType())) {
                logger.warn("Self-registration rejected: username '{}' already exists for role '{}'",
                        login.getUserName(), login.getUserType());
                session.setAttribute("signup_error", "Username already exists for this role. Please choose another.");
                return false;
            }
        }
        login.setStatus("pending");
        loginRepository.save(login);
        logger.info("Self-registration saved for user '{}' (pending approval)", login.getUserName());
        return true;
    }

    /**
     * Returns all login accounts in "pending" state awaiting admin review.
     *
     * @return list of pending Login entities
     */
    @Override
    public List<Login> getPendingUsers() {
        return loginRepository.findAllByStatus("pending");
    }

    /**
     * Approves a pending user by updating their status to "approved".
     *
     * @param loginId the ID of the Login record to approve
     * @throws IllegalArgumentException if no Login is found with the given ID
     */
    @Override
    public void approveUser(Long loginId) {
        Login login = loginRepository.findByLoginId(loginId);
        if (login == null) {
            logger.error("approveUser: no Login found with id {}", loginId);
            throw new IllegalArgumentException("User not found: " + loginId);
        }
        login.setStatus("approved");
        loginRepository.save(login);
        logger.info("User '{}' approved by admin", login.getUserName());
    }

    /**
     * Rejects a pending user by deleting their Login (and cascaded profile) record.
     *
     * @param loginId the ID of the Login record to reject
     * @throws IllegalArgumentException if no Login is found with the given ID
     */
    @Override
    public void rejectUser(Long loginId) {
        Login login = loginRepository.findByLoginId(loginId);
        if (login == null) {
            logger.error("rejectUser: no Login found with id {}", loginId);
            throw new IllegalArgumentException("User not found: " + loginId);
        }
        loginRepository.delete(login);
        logger.info("Pending user '{}' rejected and removed by admin", login.getUserName());
    }
}
