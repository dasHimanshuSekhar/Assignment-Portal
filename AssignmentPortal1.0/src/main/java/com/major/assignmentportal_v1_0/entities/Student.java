package com.major.assignmentportal_v1_0.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_credential")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    private String std_id;
    private String std_name;
    private String std_password;
    private String subject_name;
}