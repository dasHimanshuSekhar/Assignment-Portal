package com.major.assignmentportal_v1_0.controller;

import com.major.assignmentportal_v1_0.entities.Student;
import com.major.assignmentportal_v1_0.entities.StudentAssignment;
import com.major.assignmentportal_v1_0.entities.Teacher;
import com.major.assignmentportal_v1_0.entities.TeacherAssignment;
import com.major.assignmentportal_v1_0.repository.StudentAssignmentRepo;
import com.major.assignmentportal_v1_0.repository.StudentRepo;
import com.major.assignmentportal_v1_0.repository.TeacherAssignmentRepo;
import com.major.assignmentportal_v1_0.repository.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/assignment_portal")
public class StudentController {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private StudentAssignmentRepo studentAssignmentRepo;
    @Autowired
    private TeacherAssignmentRepo teacherAssignmentRepo;

    @GetMapping("load_student_registration_form")
    public String loadRegisterForm(Model model){
        Student student = new Student();
        model.addAttribute("student", student);
        return "html/student_registration";
    }

    @PostMapping("student_registration")
    public String register(@ModelAttribute("teacher") Student student){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        student.setStd_password(encoder.encode(student.getStd_password()));
        studentRepo.save(student);
        return "html/assignment_portal_home";
    }

    @GetMapping("load_student_login_form")
    public String loadLoginForm(Model model){
        Student student = new Student();
        model.addAttribute("student", student);
        return "html/student_login";
    }

    @PostMapping("student_login")
    public String login(@ModelAttribute("student") Student student, Model model_id){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(encoder.matches(student.getStd_password(), studentRepo.findById(student.getStd_id()).orElse(student).getStd_password())){
            System.out.println("credential are correct !!");
            model_id.addAttribute("student_id", student.getStd_id());
            return "html/student_dashboard";
        }
        else{
            System.out.println("credential are not correct !!");
            return "html/assignment_portal_home";
        }
    }

    @GetMapping("view_new_assignment")
    public String viewNewAssignment(Model model, @RequestParam("student_id") String studentId){
        List<TeacherAssignment> teacherAssignmentList = teacherAssignmentRepo.findAll();
        for(int i = 0; i < teacherAssignmentList.size(); i ++){
            String subject_name = studentRepo.findById(studentId).orElse(new Student()).getSubject_name();
            if(teacherAssignmentList.get(i).getSubject_name() != subject_name){
                teacherAssignmentList.remove(i);
            }
        }
        model.addAttribute("assignment", teacherAssignmentList);
        model.addAttribute("student_id", studentId);
        return "html/student_view_new_assignment";
    }

    @GetMapping("student_load_upload_assignment")
    public String uploadAssignment(Model model, @RequestParam("student_id") String student_id, @RequestParam("subject_name") String subject_name){
        StudentAssignment studentAssignment = new StudentAssignment();
        model.addAttribute("studentAssignment", studentAssignment);
        model.addAttribute("student_id", student_id);
        model.addAttribute("subject_name", subject_name);

        System.out.println(subject_name);
        System.out.println(student_id);

        return "html/student_assignment_upload";
    }


    @PostMapping("student_ans_assign")
    public String assign(@ModelAttribute("studentAssignment") StudentAssignment studentAssignment, @RequestParam("file")MultipartFile assignment_file) throws IOException {

        File file = new File("D:\\CODE\\JAVA\\JAVA WEB SERVICE\\AssignmentPortal1.0\\src\\main\\resources\\templates\\file\\temp.pdf");
        assignment_file.transferTo(file);
        byte[] byteFile = new byte[(int) file.length()];
        FileInputStream inputStream = new FileInputStream(file);
        inputStream.read(byteFile);
        inputStream.close();
        studentAssignment.setAssignment_file(convertByteArrayToObjectArray(byteFile));

//        if(studentRepo.findById(studentAssignment.getStd_id()).orElse(new Student()).getSubject_name().equals(studentAssignment.getSubject_name())){
        System.out.println("assigned !!");
        studentAssignmentRepo.save(studentAssignment);
        System.out.println("please fill the correct details !!");
        return "html/student_dashboard";
//        }
//        return "html/student_dashboard";
    }

    @GetMapping("student_assignment_file/{assignment_id}")
    public ResponseEntity<ByteArrayResource> downloadAssgnmentFile(@PathVariable String assignment_id) throws IOException{
        Byte[] assignmentFile_Byte = teacherAssignmentRepo.findById(assignment_id).orElse(new TeacherAssignment()).getAssignment_file();
        byte[] assignmentFile_byte = convertByte_to_byte(assignmentFile_Byte);
        FileSystem fs = FileSystems.getDefault();
        Path tempFilePath = fs.getPath("assignment.pdf");
        Files.write(tempFilePath, assignmentFile_byte);
        ByteArrayResource resource = new ByteArrayResource(assignmentFile_byte);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=assignment.xps");
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

//    upload button for new assignments

//    put a delete method and apply it

//----------------------------------------------------------------------------

    private static Byte[] convertByteArrayToObjectArray(byte[] byteArray) {
        Byte[] objectArray = new Byte[byteArray.length];
        int i = 0;
        for (byte b : byteArray) {
            objectArray[i++] = b;
        }
        return objectArray;
    }

    private static byte[] convertByte_to_byte(Byte[] ByteArray){
        byte[] byteArray = new byte[ByteArray.length];
        for (int i = 0; i < ByteArray.length; i++) {
            byteArray[i] = ByteArray[i];
        }
        return byteArray;
    }
}
