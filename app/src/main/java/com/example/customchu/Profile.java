package com.example.customchu;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Profile {
    // stores the user's student id
    public Integer student_id;

    public Profile() {
        // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    }
    public Profile(Integer student_id) {
        this.student_id = student_id;
    }

    public void setStudentId(Integer studentId) {
        this.student_id = studentId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("student_id", student_id);

        return result;
    }
}
