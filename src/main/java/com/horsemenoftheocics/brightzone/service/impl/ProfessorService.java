package com.horsemenoftheocics.brightzone.service.impl;

import com.horsemenoftheocics.brightzone.entity.*;
import com.horsemenoftheocics.brightzone.enums.EnrollmentStatus;
import com.horsemenoftheocics.brightzone.repository.*;
import com.horsemenoftheocics.brightzone.util.FileUtil;
import io.cucumber.java.it.Ma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ProfessorService {

    @Autowired
    private DeliverableRepository deliverableRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private ClazzRepository clazzRepository;
    @Autowired
    private PersonRepository personRepository;

    public Optional<Deliverable> getDeliverable(int id) {
        return deliverableRepository.findById(id);
    }

    public List<Clazz> getAllClass(int prof_id) {
        return clazzRepository.findByProfId(prof_id);
    }

    public List<Deliverable> getAllDeliverables(int class_id) {
        return deliverableRepository.findByClassId(class_id);
    }

    @Transactional
    public int deleteAllDeliverable(int class_id) {
        int result = -1;
        try {
            List<Deliverable> deliverables = deliverableRepository.findByClassId(class_id);
            if (!deliverables.isEmpty()) {
                for (Deliverable d : deliverables) {
                    this.deleteDeliverable(d.getDeliverableId());
                }
                result = 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return result;
    }

    // ----------------------- Use case: submit a deliverable ----------------------- //
    public int submitDeliverable(Deliverable deliverable) {
        int result = -1;
        // check if deadline is reasonable
        if (deliverable.getDeadLine().before(new Timestamp(System.currentTimeMillis()))) {
            return result;
        }

        List<Deliverable> allDelis = this.getAllDeliverables(deliverable.getClassId());
        for (Deliverable curDeli : allDelis) {
            if (deliverable.getDesc() != null && curDeli.getDesc() !=null && deliverable.getDesc().equals(curDeli.getDesc())) {
                return result;
            }
        }

        try {
            deliverable = deliverableRepository.save(deliverable);
            result = deliverable.getDeliverableId();
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    @Transactional
    public int deleteDeliverable(int deliverable_id) {
        int result = -1;
        try {
            Optional<Deliverable> deliverableOptional = deliverableRepository.findById(deliverable_id);
            if (deliverableOptional.isPresent()) {
                submissionRepository.deleteByDeliverableId(deliverable_id);
                deliverableRepository.deleteById(deliverable_id);
                result = 0;
            }
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    // ----------------------- Use case: submit grade for a submission of a deliverable ----------------------- //
    public int submitDeliverableGrade(int submission_id, float score) {
        int result = -1;
        try {
            Optional<Submission> submissionOptional = submissionRepository.findById(submission_id);
            if (submissionOptional.isEmpty()) {
                result = -1;
            } else {
                Submission curSubmission = submissionOptional.get();
                curSubmission.setGrade(score);
                submissionRepository.save(curSubmission);
                result = 0;
            }
        } catch (Exception ex) {
            return -1;
        }
        return result;
    }

    // ----------------------- Use case: submit final grade for a course of a student ----------------------- //
    public int submitFinalGrade(int class_id, int student_id) {
        int result = -1;
        Optional<Clazz> curClass = clazzRepository.findById(class_id);
        if (curClass.isEmpty()) return result;
        try {
            List<Enrollment> enrollments = enrollmentRepository.findByClassIdAndStudentIdAndStatus(class_id, student_id, EnrollmentStatus.ongoing);
            if (enrollments.isEmpty()) {
                System.out.println("NO SUCH ENROLLMENT");
                result = -1;
            } else {
                float finalGrade = calculateGrade(class_id, student_id);
                Enrollment curEnrollment = enrollments.get(0);
                curEnrollment.setGrade(finalGrade);
                //curEnrollment.setStatus(finalGrade >= 0.5 ? EnrollmentStatus.passed : EnrollmentStatus.failed);
                enrollmentRepository.save(curEnrollment);
                result = 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return result;
    }

    public float calculateGrade(int class_id, int student_id) {
        float grade = 0f;
        float percent_sum = 0f;

        List<Deliverable> deliverables = deliverableRepository.findByClassId(class_id);
        if (deliverables.isEmpty()) {
            System.out.println("NO DELIVERABLES");
        } else {
            for (Deliverable d : deliverables) {
                percent_sum += d.getPercent();
                List<Submission> submissions = submissionRepository.findByDeliverableIdAndStudentIdOrderBySubmitTimeDesc(d.getDeliverableId(), student_id);
                if (!submissions.isEmpty() && submissions.get(0).getSubmitTime().before(d.getDeadLine())) {
                    grade += submissions.get(0).getGrade() * d.getPercent();
                }
            }
        }

        if (percent_sum != 0f) {
            grade = grade / percent_sum;
        }
        return grade;
    }

    public List<Enrollment> getAllEnrollment(Integer id) {
        return enrollmentRepository.findByClassIdAndStatus(id, EnrollmentStatus.ongoing);
    }

    public List<Person> getAllStudent(Integer id) {
        List<Enrollment> enrollments = getAllEnrollment(id);
        List<Person> result = new ArrayList<>();
        for(Enrollment e : enrollments) {
            result.add(personRepository.findById(e.getStudentId()).get());
        }
        return result;
    }

    public List<Submission> getAllSubmission(Integer id) {
        return submissionRepository.findByDeliverableIdOrderBySubmitTimeDesc(id);
    }

    public int uploadClassMaterial(Integer class_id, String directory, MultipartFile file) {
        Optional<Clazz> curClazz = clazzRepository.findById(class_id);
        if (curClazz.isEmpty() || file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return -1;
        }

        String absolutePath = FileUtil.getRootPath() + "/" + class_id + "/course_materials/" + directory;

        File myParentDir = new File(absolutePath);
        File myDir = new File(myParentDir, Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (!myDir.getParentFile().exists()) {
                myDir.getParentFile().mkdirs();
            }
            if (!myDir.exists()) {
                myDir.createNewFile();
            }
            file.transferTo(myDir);
            return 0;
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public List<List<String>> getClassMaterialNames(Integer class_id) {
        List<List<String>> result = new ArrayList<>();

        String absolutePath = FileUtil.getRootPath() + "/" + class_id + "/course_materials";

        File dir = new File(absolutePath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File childDir : directoryListing) {
                if (result.isEmpty()) {
                    result.add(new ArrayList<>());
                }
                result.get(0).add(childDir.getName());

                File[] childFiles = childDir.listFiles();
                List<String> fileNames = new ArrayList<>();
                if (childFiles != null) {
                    for (File f : childFiles) {
                        fileNames.add(f.getName());
                    }
                }
                result.add(fileNames);
            }
        }
        return result;

    }

    public void getClassMaterial(Integer class_id, String dir, String fileName, HttpServletResponse response) {
        String absolutePath = FileUtil.getRootPath() + "/" + class_id + "/course_materials/" + dir + '/' + fileName;
        getFile(fileName, response, absolutePath);
    }

    public int deleteClassMaterial(Integer class_id, String dir, String fileName) {
        String absoluteParentPath = FileUtil.getRootPath() + "/" + class_id + "/course_materials/" + dir;
        String absolutePath = absoluteParentPath + '/' + fileName;

        File myFile = new File(absolutePath);
        File myParentDir = new File(absoluteParentPath);

        if (myFile.exists() && !myFile.isDirectory() && myFile.delete()) {
            if (myParentDir.list() == null || myParentDir.list().length == 0) {
                myParentDir.delete();
            }
            return 0;
        }
        return -1;
    }

    public void getGetSubmissionFile(Integer class_id, Integer deliverable_id, Integer student_id, String submission_time, String fileName, HttpServletResponse response) {
        String close_time = "0";
        Long submission_long = Long.parseLong(submission_time);
        String absoluteParentPath = FileUtil.getRootPath() + "/" + class_id + "/submissions/" + deliverable_id + '/' + student_id;
        File dir = new File(absoluteParentPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File childDir : directoryListing) {
                long curLong = Long.parseLong(childDir.getName());
                if (Math.abs(curLong - submission_long) < Math.abs(Long.parseLong(close_time) - submission_long)) {
                    close_time = childDir.getName();
                }
            }
        }
        String absolutePath = FileUtil.getRootPath() + "/" + class_id + "/submissions/" + deliverable_id + '/' + student_id + '/' + close_time + '/' + fileName;
        getFile(fileName, response, absolutePath);
    }

    private void getFile(String fileName, HttpServletResponse response, String absolutePath) {
        File myFile = new File(absolutePath);

        if(myFile.exists() && !myFile.isDirectory()) {
            MimetypesFileTypeMap mimetypesFileTypeMap=new MimetypesFileTypeMap();
            response.setContentType(mimetypesFileTypeMap.getContentType(myFile));
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            try {
                OutputStream out = response.getOutputStream();
                InputStream in = new FileInputStream(myFile);

                byte[] buffer = new byte[100];
                int len;
                while ((len = in.read(buffer)) !=-1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
