package com.spe.ClassroomManagementSystem.Controller;

import com.spe.ClassroomManagementSystem.Models.Day;
import com.spe.ClassroomManagementSystem.Service.ClassTimingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.sql.Time;

@RestController
public class TimeTableController {
    private static final Logger logger = LoggerFactory.getLogger(TimeTableController.class);

    @Autowired
    private ClassTimingService classTimingService;


    @RequestMapping("/saveInClassTimings")
    public RedirectView saveInClassTimings(
            @RequestParam("classCode") String classCode,
            @RequestParam("day") String day,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            HttpSession session){
        logger.trace("saveInClassTimings called");
        RedirectView rv = new RedirectView("AddTimetable.jsp");
        if (session.getAttribute("admin_login") == null) {
            session.setAttribute("save_message", "Administrator login is required.");
            rv.setUrl("LoginFirst.jsp");
            return rv;
        }
        try {
            Day day1 = Day.valueOf(day.trim().toUpperCase());
            Time startTimeFormat = Time.valueOf(startTime + ":00");
            Time endTimeFormat = Time.valueOf(endTime + ":00");
            if (!startTimeFormat.before(endTimeFormat)) {
                session.setAttribute("save_message", "Start time must be before end time.");
                return rv;
            }
            classTimingService.saveInClassTiming(classCode, startTimeFormat, endTimeFormat, day1, session);
        } catch (IllegalArgumentException e) {
            session.setAttribute("save_message", "Enter a valid day and time in HH:mm format.");
        }
        return rv;

    }
}
