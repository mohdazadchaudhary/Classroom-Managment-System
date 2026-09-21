package com.spe.ClassroomManagementSystem.Service;

import com.spe.ClassroomManagementSystem.Models.Login;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface LoginService {
    boolean save(Login login, HttpSession session);

    Login findByUsernameAndPassword(String username, String password);

    boolean checkCredentials(String username, String password, String userType, HttpSession session);

    Login findByLoginId(Long loginId);

    /**
     * Saves a self-registered user with status = "pending".
     * Validates that the username is not already taken.
     *
     * @param login   the Login entity populated from the signup form
     * @param session the current HTTP session (used for flash messages)
     * @return true if saved successfully, false if username already exists
     */
    boolean selfRegister(Login login, HttpSession session);

    /**
     * Returns all Login records in "pending" status awaiting admin approval.
     *
     * @return list of pending Login entities
     */
    List<Login> getPendingUsers();

    /**
     * Approves a pending user account by setting its status to "approved".
     *
     * @param loginId the ID of the Login record to approve
     */
    void approveUser(Long loginId);

    /**
     * Rejects and removes a pending user account.
     *
     * @param loginId the ID of the Login record to reject/delete
     */
    void rejectUser(Long loginId);
}
