package com.spe.ClassroomManagementSystem.Config;

import com.spe.ClassroomManagementSystem.Models.Login;
import com.spe.ClassroomManagementSystem.Repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the local development administrator once so a new database is usable
 * without manually inserting credentials. Existing accounts are never changed.
 */
@Component
public class DevelopmentDataInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DevelopmentDataInitializer.class);
    private static final String ADMIN_USERNAME = "azadchaudhary03@gmail.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private final LoginRepository loginRepository;

    public DevelopmentDataInitializer(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Login existingAdmin = loginRepository.findByUserNameAndUserType(ADMIN_USERNAME, "admin");
        if (existingAdmin != null) {
            return;
        }

        Login admin = new Login("admin", ADMIN_USERNAME, ADMIN_PASSWORD);
        admin.setStatus("approved");
        loginRepository.save(admin);
        logger.info("Created the local development administrator account");
    }
}
