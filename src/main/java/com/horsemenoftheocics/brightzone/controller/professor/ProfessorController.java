package com.horsemenoftheocics.brightzone.controller.professor;

import com.horsemenoftheocics.brightzone.entity.*;
import com.horsemenoftheocics.brightzone.service.impl.ProfessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Slf4j
@RestController
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping(path = "/getFileNames/{class_id}")
    public @ResponseBody List<List<String>> getClassMaterialNames(@PathVariable Integer class_id) {
        return professorService.getClassMaterialNames(class_id);
    }

    @PostMapping("/uploadClassMaterial/{class_id}/{dir}")
    public @ResponseBody String uploadClassMaterial(@PathVariable Integer class_id, @PathVariable String dir, MultipartFile file) {
        int code = professorService.uploadClassMaterial(class_id, dir, file);
        if (code != -1) {
            return ("SUCCEED");
        } else {
            return ("FAIL");
        }
    }

    @GetMapping(path = "/getAllEnrollment/{id}")
    public @ResponseBody List<Enrollment> getAllEnrollment(@PathVariable Integer id) {
        return professorService.getAllEnrollment(id);
    }

    @GetMapping(path = "/getAllStudent/{id}")
    public @ResponseBody List<Person> getAllStudent(@PathVariable Integer id) {
        return professorService.getAllStudent(id);
    }

    @GetMapping(path = "/getAllSubmission/{id}")
    public @ResponseBody List<Submission> getAllSubmission(@PathVariable Integer id) {
        return professorService.getAllSubmission(id);
    }


    @GetMapping(path = "/getAllClass/{id}")
    public @ResponseBody List<Clazz> getAllClass(@PathVariable String id) {
        int i = Integer.parseInt(id);
        return professorService.getAllClass(i);
    }

    @GetMapping(path = "/getAllDeliverables/{class_id}")
    public @ResponseBody List<Deliverable> getAllDeliverables(@PathVariable String class_id) {
        int i = Integer.parseInt(class_id);
        return professorService.getAllDeliverables(i);
    }

    @PostMapping("/createDeliverable/")
    public @ResponseBody String createDeliverable(@RequestBody Deliverable deliverable) {
        int code = professorService.submitDeliverable(deliverable);
        if (code != -1) {
            return ("SUCCEED");
        } else {
            return ("FAIL");
        }
    }

    @PostMapping("/submitGrade/{id}/{grade}")
    public @ResponseBody String submitDeliverableGrade(@PathVariable String id, @PathVariable String grade) {
        int i = Integer.parseInt(id);
        float g = Float.parseFloat(grade);
        int code = professorService.submitDeliverableGrade(i, g);
        if (code == 0) {
            return ("SUCCEED");
        } else {
            return ("FAIL");
        }
    }

    @PostMapping("/submitFinalGrade/{class_id}/{student_id}")
    public @ResponseBody String submitFinalGrade(@PathVariable String class_id, @PathVariable String student_id) {
        int cid = Integer.parseInt(class_id);
        int sid = Integer.parseInt(student_id);
        int code = professorService.submitFinalGrade(cid, sid);
        if (code == 0) {
            return ("SUCCEED");
        } else {
            return ("FAIL");
        }
    }

    @Transactional
    @DeleteMapping("/deleteDeliverable/{id}")
    public @ResponseBody String deleteDeliverable(@PathVariable String id) {
        int deliverableId = Integer.parseInt(id);
        int code = professorService.deleteDeliverable(deliverableId);
        if (code == 0) {
            return ("SUCCEED");
        } else {
            return ("FAIL");
        }
    }

    @Transactional
    @DeleteMapping("/deleteAllDeliverableFromClass/{class_id}")
    public @ResponseBody String deleteAllDeliverableFromClass(@PathVariable String class_id) {
        int cid = Integer.parseInt(class_id);
        int code = professorService.deleteAllDeliverable(cid);
        if (code == 0) {
            return ("SUCCEED");
        } else {
            return ("FAIL");
        }
    }

    @GetMapping("/getClassMaterial/{class_id}/{dir}/{fileName}")
    public void getClassMaterial(@PathVariable Integer class_id,
                     @PathVariable String dir,
                     @PathVariable String fileName,
                     HttpServletResponse response) {
        professorService.getClassMaterial(class_id, dir, fileName, response);
    }

    @Transactional
    @DeleteMapping("/deleteClassMaterial/{class_id}/{dir}/{fileName}")
    public String deleteClassMaterial(@PathVariable Integer class_id, @PathVariable String dir,
                                 @PathVariable String fileName) {
        int code = professorService.deleteClassMaterial(class_id, dir, fileName);
        if (code == 0) {
            return ("SUCCEED");
        } else {
            return ("FAIL");
        }
    }

    @GetMapping("/getGetSubmissionFile/{class_id}/{deliverable_id}/{student_id}/{submission_time}/{fileName}")
    public void getGetSubmissionFile(@PathVariable Integer class_id,
                                     @PathVariable Integer deliverable_id,
                                     @PathVariable Integer student_id,
                                     @PathVariable String submission_time,
                                     @PathVariable String fileName,
                                 HttpServletResponse response) {
        professorService.getGetSubmissionFile(class_id, deliverable_id, student_id, submission_time, fileName, response);
    }
}
