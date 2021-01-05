package com.horsemenoftheocics.brightzone.service.impl;

import com.horsemenoftheocics.brightzone.entity.Deliverable;
import com.horsemenoftheocics.brightzone.entity.Submission;
import com.horsemenoftheocics.brightzone.repository.DeliverableRepository;
import com.horsemenoftheocics.brightzone.repository.SubmissionRepository;
import com.horsemenoftheocics.brightzone.service.DeliverableService;
import com.horsemenoftheocics.brightzone.util.FileUtil;
import com.horsemenoftheocics.brightzone.vo.DeliverableVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
            public class DeliverableServiceImpl implements DeliverableService {

    @Autowired
    private DeliverableRepository deliverableRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    @Transactional
    public boolean submitDeliverable(int studentId, int deliverableId, MultipartFile file, String desc) throws IOException {
        Optional<Deliverable> deliverable = deliverableRepository.findById(deliverableId);
        if (!deliverable.isPresent()) {
            return false;
        }

        if (System.currentTimeMillis() > deliverable.get().getDeadLine().getTime()) {
            return false;
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (file != null) {
            String absolutePath = FileUtil.getRootPath() + "/" + deliverable.get().getClassId() + "/submissions/" + deliverableId + "/" + studentId + "/" + timestamp.getTime();

            File dest0 = new File(absolutePath);
            File dest = new File(dest0, file.getOriginalFilename());

            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
                //检测文件是否存在
            }
            if (!dest.exists()) {
                dest.createNewFile();
            }
            file.transferTo(dest);

            dest.getAbsolutePath();

            Submission submission = new Submission();
            submission.setDeliverableId(deliverableId);
            submission.setStudentId(studentId);
            submission.setSubmitTime(timestamp);
            submission.setSubmissionDesc(desc);
            submission.setFileName(dest.getName());
            submissionRepository.save(submission);
            return true;
        }
        return false;


    }

    @Override
    public Set<DeliverableVo> getAllCourseAssignment(int clazzId, int studentId) {
        List<Deliverable> byClassId = deliverableRepository.findByClassId(clazzId);

        List<Submission> submissionList = submissionRepository.findByStudentId(studentId);
        Map<Integer, List<Submission>> submissionMap = submissionList.stream().collect(Collectors.groupingBy(Submission::getDeliverableId));


        Set<DeliverableVo> collect = byClassId.stream().map(d -> {
            DeliverableVo deliverableVo = new DeliverableVo();
            deliverableVo.setDeliverableId(d.getDeliverableId());
            deliverableVo.setDeliverableDesc(d.getDeliverableDesc());
            deliverableVo.setDeadline(d.getDeadLine());

            List<Submission> submissions = submissionMap.get(d.getDeliverableId());
            if (submissions != null) {
                submissions.stream().sorted(Comparator.comparing(Submission::getSubmitTime)).findFirst().ifPresent(f -> {
                    deliverableVo.setSubmitTime(f.getSubmitTime());
                    deliverableVo.setScore(f.getGrade());
                });
            }
            return deliverableVo;
        }).collect(Collectors.toSet());
        return collect;
    }

    @Override
    @Transactional
    public void deleteAssignment(int deliverableId, int studentId) {
        submissionRepository.deleteByDeliverableIdAndStudentId(deliverableId,studentId);
    }
}
