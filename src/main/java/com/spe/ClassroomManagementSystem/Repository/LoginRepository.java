package com.spe.ClassroomManagementSystem.Repository;

import com.spe.ClassroomManagementSystem.Models.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login,Long> {

    Login findByUserNameAndPassword(String username,String password);

    Login findByUserNameAndUserType(String username, String userType);

    Login findByLoginId(Long loginId);

    /** Returns all Login records with the given status (e.g., "pending"). */
    java.util.List<Login> findAllByStatus(String status);
}
