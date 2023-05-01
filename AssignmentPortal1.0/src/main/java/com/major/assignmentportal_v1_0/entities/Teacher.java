package com.major.assignmentportal_v1_0.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teacher_credential")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    private String tch_id;
    private String tch_name;
    private String tch_password;
    private String subject_name;
}
