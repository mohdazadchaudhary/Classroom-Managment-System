package com.spe.ClassroomManagementSystem.Config;

import com.spe.ClassroomManagementSystem.Models.Login;
import com.spe.ClassroomManagementSystem.Models.Classroom;
import com.spe.ClassroomManagementSystem.Models.ClassTiming;
import com.spe.ClassroomManagementSystem.Models.Day;
import com.spe.ClassroomManagementSystem.Repository.ClassroomRepository;
import com.spe.ClassroomManagementSystem.Repository.ClassTimingRepository;
import com.spe.ClassroomManagementSystem.Repository.LoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.Time;

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
    private final ClassroomRepository classroomRepository;
    private final ClassTimingRepository classTimingRepository;

    public DevelopmentDataInitializer(LoginRepository loginRepository,
                                      ClassroomRepository classroomRepository,
                                      ClassTimingRepository classTimingRepository) {
        this.loginRepository = loginRepository;
        this.classroomRepository = classroomRepository;
        this.classTimingRepository = classTimingRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Login existingAdmin = loginRepository.findByUserNameAndUserType(ADMIN_USERNAME, "admin");
        if (existingAdmin == null) {
            Login admin = new Login("admin", ADMIN_USERNAME, ADMIN_PASSWORD);
            admin.setStatus("approved");
            loginRepository.save(admin);
            logger.info("Created the local development administrator account");
        }

        Classroom l101 = seedClassroom("L-101", 60, true, 20);
        Classroom l202 = seedClassroom("L-202", 100, true, 35);
        Classroom s301 = seedClassroom("S-301", 30, false, 12);

        if (classTimingRepository.count() == 0) {
            classTimingRepository.save(new ClassTiming(Day.MONDAY,
                    Time.valueOf("09:00:00"), Time.valueOf("10:00:00"), l101));
            classTimingRepository.save(new ClassTiming(Day.WEDNESDAY,
                    Time.valueOf("11:00:00"), Time.valueOf("12:30:00"), l101));
            classTimingRepository.save(new ClassTiming(Day.TUESDAY,
                    Time.valueOf("10:00:00"), Time.valueOf("11:30:00"), l202));
            classTimingRepository.save(new ClassTiming(Day.THURSDAY,
                    Time.valueOf("14:00:00"), Time.valueOf("15:00:00"), s301));
            logger.info("Created local development classroom and timetable sample data");
        }
    }

    private Classroom seedClassroom(String classCode, int capacity, boolean projector, int plugs) {
        Classroom classroom = classroomRepository.findByClassCode(classCode);
        if (classroom != null) {
            return classroom;
        }
        return classroomRepository.save(new Classroom(classCode, capacity, projector, plugs));
    }
}
