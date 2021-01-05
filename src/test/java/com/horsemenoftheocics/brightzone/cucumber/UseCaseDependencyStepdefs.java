package com.horsemenoftheocics.brightzone.cucumber;


import com.horsemenoftheocics.brightzone.controller.account.AccountController;
import com.horsemenoftheocics.brightzone.entity.Account;
import com.horsemenoftheocics.brightzone.entity.Clazz;
import com.horsemenoftheocics.brightzone.entity.Course;
import com.horsemenoftheocics.brightzone.entity.Deliverable;
import com.horsemenoftheocics.brightzone.enums.AccountStatus;
import com.horsemenoftheocics.brightzone.enums.ClassStatus;
import com.horsemenoftheocics.brightzone.repository.*;
import com.horsemenoftheocics.brightzone.service.*;
import com.horsemenoftheocics.brightzone.service.impl.ProfessorService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UseCaseDependencyStepdefs {
    //id
    private final int S1 = 3000195;  // 3000194 is the last student in table <Account> after dbPopulate
    private final int S2 = 3000196;
    private final int S3 = 3000197;

    private final int P1 = 2000090;  // 2000089 is the last professor in table <Account> after dbPopulate
    private final int P2 = 2000091;

    private int C1 = 0;
    private int C2 = 0;
    private int C3 = 0;

    private int project_deliverable_id = 0;
    private int essay_deliverable_id = 0;

    private Course testCourse;
    private Clazz clazzC1;
    private Clazz clazzC2;
    private Clazz clazzC3;

    private final MockHttpServletRequest requestS1 = new MockHttpServletRequest();
    private final MockHttpSession sessionS1 = new MockHttpSession();

    private final MockHttpServletRequest requestS2 = new MockHttpServletRequest();
    private final MockHttpSession sessionS2 = new MockHttpSession();

    private final MockHttpServletRequest requestS3 = new MockHttpServletRequest();
    private final MockHttpSession sessionS3 = new MockHttpSession();

    private final MockHttpServletRequest requestP1 = new MockHttpServletRequest();
    private final MockHttpSession sessionP1 = new MockHttpSession();

    private final MockHttpServletRequest requestP2 = new MockHttpServletRequest();
    private final MockHttpSession sessionP2 = new MockHttpSession();

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DeliverableService deliverableService;

    @Autowired
    private DeliverableRepository deliverableRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountController accountController;

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private AdminCourseService adminCourseService;

    @Autowired
    private AdminClazzService adminClazzService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClazzRepository clazzRepository;

    @Given("Admin creates a term and its deadlines")
    public void admin_creates_a_term_and_its_deadlines() {
        System.out.println("Admin creates a term and its deadlines");
    }

    @Then("S1 requests creation Admin creates C2")
    public void s1_requests_creation_admin_creates_c2() {
        // student 1 request account creation
        Map<String, Object> registerResultS1 = accountService.registerAccount(Integer.toString(S1));
        Assert.assertTrue((Boolean) registerResultS1.get("success"));

        // and admin authorize student1's account
        Optional<Account> optionalStudent1 = accountRepository.findById(S1);
        if (optionalStudent1.isEmpty()) {
            throw new RuntimeException("Can't find student with userId " + S1 + " in database.");
        }
        Account student1 = optionalStudent1.get();
        student1.setAccountStatus(AccountStatus.current);
        int res = adminAccountService.updateAccount(student1);
        Assert.assertEquals(0, res);

        // Admin creates C2
        // add a new test course upper the classes firstly.
        testCourse = createTestCourse();
        // create C2 under the test course
        Clazz newClass = new Clazz();
        newClass.setCourseId(testCourse.getCourseId());
        newClass.setProfId(2000000);
        newClass.setSection(1);
        newClass.setEnrolled(0);
        newClass.setEnrollCapacity(2);
        newClass.setEnrollDeadline(formatString2Timestamp("2021-2-5 20:30:30"));
        newClass.setDropNoPenaltyDeadline(formatString2Timestamp("2021-3-5 20:30:30"));
        newClass.setDropNoFailDeadline(formatString2Timestamp("2021-4-5 20:30:30"));
        newClass.setClassStatus(ClassStatus.open);
        newClass.setClassDesc("Test class 2");
        clazzC2 = adminClazzService.addNewClassInfo(newClass);
        Assert.assertEquals(clazzC2, newClass);
        C2 = clazzC2.getClassId();

    }

    @Then("P1 and P2 simultaneously request creation")
    public void p1_and_p2_simultaneously_request_creation() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // professor 1 request account creation
                Map<String, Object> registerResultP1 = accountService.registerAccount(Integer.toString(P1));
                Assert.assertTrue((Boolean) registerResultP1.get("success"));
                System.out.println("P1 register success");
                // and admin authorize professor1's account
                Optional<Account> optionalProfessor1 = accountRepository.findById(P1);
                if (optionalProfessor1.isEmpty()) {
                    throw new RuntimeException("Can't find professor with userId " + P1 + " in database.");
                }
                Account professor1 = optionalProfessor1.get();
                professor1.setAccountStatus(AccountStatus.current);
                int res1 = adminAccountService.updateAccount(professor1);
                Assert.assertEquals(0, res1);
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                // professor 2 request account creation
                Map<String, Object> registerResultP2 = accountService.registerAccount(Integer.toString(P2));
                Assert.assertTrue((Boolean) registerResultP2.get("success"));
                System.out.println("P2 register success");
                // and admin authorized professor2's account
                Optional<Account> optionalProfessor2 = accountRepository.findById(P2);
                if (optionalProfessor2.isEmpty()) {
                    throw new RuntimeException("Can't find professor with userId " + P2 + " in database.");
                }
                Account professor2 = optionalProfessor2.get();
                professor2.setAccountStatus(AccountStatus.current);
                int res2 = adminAccountService.updateAccount(professor2);
                Assert.assertEquals(0, res2);
            }
        });

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("Admin creates C1 Admin creates C3")
    public void admin_creates_c1_admin_creates_c3() {
        //admin created C1
        Clazz newClass = new Clazz();
        newClass.setCourseId(testCourse.getCourseId());
        newClass.setProfId(2000000);
        newClass.setSection(1);
        newClass.setEnrolled(0);
        newClass.setEnrollCapacity(2);
        newClass.setEnrollDeadline(formatString2Timestamp("2021-2-10 20:30:30"));
        newClass.setDropNoPenaltyDeadline(formatString2Timestamp("2021-3-1 20:30:30"));
        newClass.setDropNoFailDeadline(formatString2Timestamp("2021-4-1 20:30:30"));
        newClass.setClassStatus(ClassStatus.open);
        newClass.setClassDesc("Test class 1");
        clazzC1 = adminClazzService.addNewClassInfo(newClass);
        Assert.assertEquals(clazzC1, newClass);
        C1 = clazzC1.getClassId();
        System.out.println("C1:" + C1);
        //admin created C3
        newClass = new Clazz();
        newClass.setCourseId(testCourse.getCourseId());
        newClass.setProfId(2000000);
        newClass.setSection(1);
        newClass.setEnrolled(0);
        newClass.setEnrollCapacity(2);
        newClass.setEnrollDeadline(formatString2Timestamp("2021-2-10 20:30:30"));
        newClass.setDropNoPenaltyDeadline(formatString2Timestamp("2021-3-1 20:30:30"));
        newClass.setDropNoFailDeadline(formatString2Timestamp("2021-4-1 20:30:30"));
        newClass.setClassStatus(ClassStatus.open);
        newClass.setClassDesc("Test class 3");
        clazzC3 = adminClazzService.addNewClassInfo(newClass);
        Assert.assertEquals(clazzC3, newClass);
        C3 = clazzC3.getClassId();
        System.out.println("C3:" + C3);
    }

    @Then("S2 and S3 simultaneously request creation")
    public void s2_and_s3_simultaneously_request_creation() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // student2 request account creation
                Map<String, Object> registerResultS2 = accountService.registerAccount(Integer.toString(S2));
                Assert.assertTrue((Boolean) registerResultS2.get("success"));
                System.out.println("S2 register success");

                // and admin authorize student2's account
                Optional<Account> optionalStudent2 = accountRepository.findById(S2);
                if (optionalStudent2.isEmpty()) {
                    throw new RuntimeException("Can't find student with userId " + S2 + " in database.");
                }
                Account student2 = optionalStudent2.get();
                student2.setAccountStatus(AccountStatus.current);
                int res1 = adminAccountService.updateAccount(student2);
                Assert.assertEquals(0, res1);
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                // student3 request account creation
                Map<String, Object> registerResultS3 = accountService.registerAccount(Integer.toString(S3));
                Assert.assertTrue((Boolean) registerResultS3.get("success"));
                System.out.println("S3 register success");

                // admin admin authorize student3's account
                Optional<Account> optionalStudent3 = accountRepository.findById(S3);
                if (optionalStudent3.isEmpty()) {
                    throw new RuntimeException("Can't find student with userId " + S3 + " in database.");
                }
                Account student3 = optionalStudent3.get();
                student3.setAccountStatus(AccountStatus.current);
                int res2 = adminAccountService.updateAccount(student3);
                Assert.assertEquals(0, res2);
            }
        });

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("Admin assigns C1 to P1 assigns C3 to P2 assigns C2 to P1")
    public void admin_assigns_c1_to_p1_assigns_c3_to_p2_assigns_c2_to_p1() {
        clazzC1.setProfId(P1);
        adminClazzService.updateClassInfo(clazzC1);
        clazzC3.setProfId(P2);
        adminClazzService.updateClassInfo(clazzC3);
        clazzC2.setProfId(P1);
        adminClazzService.updateClassInfo(clazzC2);
    }

    @Then("S2 logins in, then S3, then S1, then P1, then P2")
    public void s2_logins_in_then_s3_then_s1_then_p1_then_p2() {
        accountService.login(Integer.toString(S2), "123456");
        sessionS2.setAttribute("userId", Integer.toString(S2));
        requestS2.setSession(sessionS2);

        accountService.login(Integer.toString(S3), "123456");
        sessionS3.setAttribute("userId", Integer.toString(S3));
        requestS3.setSession(sessionS3);

        accountService.login(Integer.toString(S1), "123456");
        sessionS1.setAttribute("userId", Integer.toString(S1));
        requestS1.setSession(sessionS1);

        accountService.login(Integer.toString(P1), "123456");
        sessionP1.setAttribute("userId", Integer.toString(P1));
        requestP1.setSession(sessionP1);

        accountService.login(Integer.toString(P2), "123456");
        sessionP2.setAttribute("userId", Integer.toString(P2));
        requestP2.setSession(sessionP2);
    }

    @Then("S2 and S3 simultaneously register in C1")
    public void s2_and_s3_simultaneously_register_in_c1() {
        Thread t1 = new Thread(() -> {
            courseService.registerCourse(S2, C1);
        });
        Thread t2 = new Thread(() -> {
            courseService.registerCourse(S3, C1);
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("S1 registers in C2 S1 registers in C3 S2 registers in C3")
    public void s1_registers_in_c2_s1_registers_in_c3_s2_registers_in_c3() {
        courseService.registerCourse(S1, C2);
        courseService.registerCourse(S1, C3);
        courseService.registerCourse(S2, C3);
    }

    @Then("P1 creates deliverable Project for C1 P2 creates deliverable Essay for C3")
    public void p1_creates_deliverable_project_for_c1_p2_creates_deliverable_essay_for_c3() {
        //First delete all existing deliverables for C1
        professorService.deleteAllDeliverable(C1);
        //P1 creates deliverable Project for C1
        Deliverable project = new Deliverable();
        project.setClassId(C1);
        project.setDead_line(Timestamp.valueOf("2020-12-24 10:10:10.0"));
        project.setDesc("This is a project");
        project.setPercent(0.7f);
        project.setIsNotified(false);
        this.project_deliverable_id = professorService.submitDeliverable(project);

        //First delete all existing deliverables for C3
        professorService.deleteAllDeliverable(C3);
        //P2 creates deliverable Essay for C3
        Deliverable essay = new Deliverable();
        essay.setClassId(C3);
        essay.setDead_line(Timestamp.valueOf("2020-12-25 10:10:10.0"));
        essay.setDesc("This is an Essay");
        essay.setPercent(0.3f);
        essay.setIsNotified(false);
        this.essay_deliverable_id = professorService.submitDeliverable(essay);
    }

    @Then("S1 drops C2")
    public void s1_drops_c2() {
        courseService.dropCourse(S1, C2);
    }

    @Then("S2 and S3 simultaneously submit Project in C1")
    public void s2_and_s3_simultaneously_submit_project_in_c1() {

        List<Deliverable> byClassId = deliverableRepository.findByClassId(C1);

        Thread t1 = new Thread(() -> {
            try {
                deliverableService.submitDeliverable(S2, byClassId.get(0).getDeliverableId(), new MockMultipartFile("file",
                        "myAssignment.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes()), "test");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                deliverableService.submitDeliverable(S3, byClassId.get(0).getDeliverableId(), new MockMultipartFile("file",
                        "myAssignment.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes()), "test");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Then("S1 submits Essay in C3, S2 submits Essay in C3")
    public void s1_submits_essay_in_c3_s2_submits_essay_in_c3() {
        List<Deliverable> byClassId = deliverableRepository.findByClassId(C3);

        try {
            deliverableService.submitDeliverable(S1, byClassId.get(0).getDeliverableId(), new MockMultipartFile("file",
                    "myAssignment.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello, World!".getBytes()), "test");
            deliverableService.submitDeliverable(S2, byClassId.get(0).getDeliverableId(), new MockMultipartFile("file",
                    "myAssignment.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello, World!".getBytes()), "test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Then("P1 submits marks for Project in C1")
    public void p1_submits_marks_for_project_in_c1() {
        //Grade submission from S2
        int s2_project_submission_id = submissionRepository.findByDeliverableIdAndStudentIdOrderBySubmitTimeDesc(project_deliverable_id, S2).get(0).getSubmissionId();
        professorService.submitDeliverableGrade(s2_project_submission_id, 0.77f);
        float S2_grade = submissionRepository.findById(s2_project_submission_id).get().getGrade();
        Assert.assertEquals(0.77, S2_grade, 0.0001);

        //Grade submission from S3
        int s3_project_submission_id = submissionRepository.findByDeliverableIdAndStudentIdOrderBySubmitTimeDesc(project_deliverable_id, S3).get(0).getSubmissionId();
        professorService.submitDeliverableGrade(s3_project_submission_id, 0.66f);
        float S3_grade = submissionRepository.findById(s3_project_submission_id).get().getGrade();
        Assert.assertEquals(0.66, S3_grade, 0.0001);
    }

    @Then("P2 submit marks for Essay in C3")
    public void p2_submit_marks_for_essay_in_c3() {
        //Grade submission from S1
        int s1_essay_submission_id = submissionRepository.findByDeliverableIdAndStudentIdOrderBySubmitTimeDesc(essay_deliverable_id, S1).get(0).getSubmissionId();
        professorService.submitDeliverableGrade(s1_essay_submission_id, 0.77f);
        float S1_grade = submissionRepository.findById(s1_essay_submission_id).get().getGrade();
        Assert.assertEquals(0.77, S1_grade, 0.0001);

        //Grade submission from S2
        int s2_essay_submission_id = submissionRepository.findByDeliverableIdAndStudentIdOrderBySubmitTimeDesc(essay_deliverable_id, S2).get(0).getSubmissionId();
        professorService.submitDeliverableGrade(s2_essay_submission_id, 0.66f);
        float S2_grade = submissionRepository.findById(s2_essay_submission_id).get().getGrade();
        Assert.assertEquals(0.66, S2_grade, 0.0001);
    }

    @Then("Simultaneously P1 and P2 submit final grades respectively for C1 and C3")
    public void simultaneously_p1_and_p2_submit_final_grades_respectively_for_c1_and_c3() {
        Thread P1_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Final grade for S2
                professorService.submitFinalGrade(C1, S2);
                float S2_finalGrade = enrollmentRepository.findByClassIdAndStudentId(C1, S2).get().getFinalGrade();
                Assert.assertEquals(0.77, S2_finalGrade, 0.0001);

                //Final grade for S3
                professorService.submitFinalGrade(C1, S3);
                float S3_finalGrade = enrollmentRepository.findByClassIdAndStudentId(C1, S3).get().getFinalGrade();
                Assert.assertEquals(0.66, S3_finalGrade, 0.0001);
            }
        });

        Thread P2_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Final grade for S1
                professorService.submitFinalGrade(C3, S1);
                float S1_finalGrade = enrollmentRepository.findByClassIdAndStudentId(C3, S1).get().getFinalGrade();
                Assert.assertEquals(0.77, S1_finalGrade, 0.0001);

                //Final grade for S2
                professorService.submitFinalGrade(C3, S2);
                float S2_finalGrade = enrollmentRepository.findByClassIdAndStudentId(C3, S2).get().getFinalGrade();
                Assert.assertEquals(0.66, S2_finalGrade, 0.0001);
            }
        });

        P1_thread.start();
        P2_thread.start();

        try {
            P1_thread.join();
            P2_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("S1, S2, S3, P1 and P2 log out")
    @Transactional
    public void s1_s2_s3_p1_and_p2_log_out() {
        adminCourseService.deleteACourse(testCourse.getCourseId());

//        professorService.deleteDeliverable(essay_deliverable_id);
//        professorService.deleteDeliverable(project_deliverable_id);
//        enrollmentRepository.deleteByStudentId(S1);
//        enrollmentRepository.deleteByStudentId(S2);
//        enrollmentRepository.deleteByStudentId(S3);
//        clazzRepository.deleteById(C1);
//        clazzRepository.deleteById(C2);
//        clazzRepository.deleteById(C3);

        accountRepository.deleteById(S1);
        Map<String, Object> logoutResultS1 = accountController.logout(requestS1);
        Assert.assertTrue((Boolean) logoutResultS1.get("success"));

        accountRepository.deleteById(S2);
        Map<String, Object> logoutResultS2 = accountController.logout(requestS2);
        Assert.assertTrue((Boolean) logoutResultS2.get("success"));

        accountRepository.deleteById(S3);
        Map<String, Object> logoutResultS3 = accountController.logout(requestS3);
        Assert.assertTrue((Boolean) logoutResultS3.get("success"));

        accountRepository.deleteById(P1);
        Map<String, Object> logoutResultP1 = accountController.logout(requestP1);
        Assert.assertTrue((Boolean) logoutResultP1.get("success"));

        accountRepository.deleteById(P2);
        Map<String, Object> logoutResultP2 = accountController.logout(requestP2);
        Assert.assertTrue((Boolean) logoutResultP2.get("success"));
    }

    public Course createTestCourse() {
        //construct a valid course first.
        Course lastCourse = adminCourseService.getLastCourse();
        int newCourseNumber = Integer.parseInt(lastCourse.getCourseNumber()) + 1;
        Course newCourse = new Course();
        newCourse.setCourseName("Test Course " + lastCourse.getCourseName().split(" ")[2] + "I");
        newCourse.setCourseSubject("NE");
        newCourse.setCourseNumber("" + newCourseNumber);
        newCourse.setCourseDesc("xxx");
        newCourse.setCredit(3);

        //try to add this new course to database.
        int status = adminCourseService.addNewCourse(newCourse);
        assertEquals(0, status);
        Course newLastCourse = adminCourseService.getLastCourse();
        Course newAddedCourse = adminCourseService.getCourseById(newLastCourse.getCourseId());
        assertEquals(Integer.parseInt(newAddedCourse.getCourseNumber()), newCourseNumber);

        return newCourse;
    }

    private Timestamp formatString2Timestamp(String tsStr) {
        Timestamp ts = null;
        try {
            ts = Timestamp.valueOf(tsStr);
            System.out.println(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }


}
