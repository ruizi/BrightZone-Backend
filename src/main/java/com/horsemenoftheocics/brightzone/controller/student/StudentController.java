package com.horsemenoftheocics.brightzone.controller.student;

import com.horsemenoftheocics.brightzone.controller.BaseController;
import com.horsemenoftheocics.brightzone.enums.DropStatus;
import com.horsemenoftheocics.brightzone.enums.RegisterStatus;
import com.horsemenoftheocics.brightzone.service.CourseService;
import com.horsemenoftheocics.brightzone.service.DeliverableService;
import com.horsemenoftheocics.brightzone.service.impl.ProfessorService;
import com.horsemenoftheocics.brightzone.vo.CourseVo;
import com.horsemenoftheocics.brightzone.vo.DeliverableVo;
import com.horsemenoftheocics.brightzone.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
public class StudentController extends BaseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private DeliverableService deliverableService;

    @Autowired
    private ProfessorService professorService;

    @PostMapping("/submitDeliverable")
    public @ResponseBody
    boolean submitDeliverable(int deliverableId, MultipartFile file) {
        try {
            return deliverableService.submitDeliverable(getUserId(), deliverableId, file, null);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/getAllMaterial")
    public @ResponseBody
    List<List<String>> getAllMaterial(int clazzId) {
        List<List<String>> classMaterialNames = professorService.getClassMaterialNames(clazzId);
        return classMaterialNames;
    }

    @GetMapping("/registerCourse")
    public @ResponseBody
    ResultVo registerCourse(int clazzId) {
        RegisterStatus registerStatus = courseService.registerCourse(getUserId(), clazzId);
        return getSuccessResult(registerStatus.isStatus(), registerStatus.getReason());
    }

    @GetMapping("/getAllOpenedCourse")
    public @ResponseBody
    List<CourseVo> getAllOpenedCourse() {
        return courseService.getAllOpenedCourse(getUserId());
    }

    @GetMapping("/getAllRegisteredCourse")
    public @ResponseBody
    Set<CourseVo> getAllRegisteredCourse() {
        return courseService.getAllRegisteredCourse(getUserId());
    }

    @GetMapping("/getAllDeliverable")
    public @ResponseBody
    Set<DeliverableVo> getAllDeliverable(int clazzId) {
        return deliverableService.getAllCourseAssignment(clazzId, getUserId());
    }

    @GetMapping("/dropCourse")
    public @ResponseBody
    ResultVo dropCourse(int clazzId) {
        DropStatus dropStatus = courseService.dropCourse(getUserId(), clazzId);
        return getSuccessResult(dropStatus.isStatus(), dropStatus.getReason());
    }

    @GetMapping("/getCourseVo")
    @ResponseBody
    public CourseVo getCourseVo(int clazzId) {
        return courseService.getCourse(clazzId);
    }
}
