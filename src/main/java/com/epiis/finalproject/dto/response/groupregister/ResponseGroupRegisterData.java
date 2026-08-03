package com.epiis.finalproject.dto.response.groupregister;

import com.epiis.finalproject.dto.common.UnitScoreBase;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGroupRegisterData {
    private boolean success;
    private List<String> listMessage;
    private GroupInfoData groupInfo;
    private List<UnitInfoData> units;
    private List<String> classDates; // List of dd-MM-yyyy formatted active class dates
    private List<WeekData> weeks;
    private List<StudentData> students;

    @Getter
    @Setter
    public static class WeekData {
        private String name;
        private List<String> dates;
    }

    @Getter
    @Setter
    public static class GroupInfoData {
        private String idGroup;
        private String nameGroup;
        private String idCourse;
        private String courseCode;
        private String courseName;
        private int credits;
        private String schoolName;
        private String semesterName;
        private String professorName;
        private double conceptualWeight;
        private double practicalWeight;
        private double attitudinalWeight;
        private boolean closed; // if the grades have already been consolidated
    }

    @Getter
    @Setter
    public static class UnitInfoData {
        private String idUnits;
        private int numberUnit;
        private String nameUnit;
    }

    @Getter
    @Setter
    public static class StudentData {
        private String idGroupStudent;
        private String code;
        private String fullName;
        private List<UnitScoreData> unitScores;
        private List<AttendanceData> attendances;
    }

    @Getter
    @Setter
    public static class UnitScoreData extends UnitScoreBase {
        private String idUnitScore;
        private String idUnits;
        private int numberUnit;
    }

    @Getter
    @Setter
    public static class AttendanceData {
        private String idAttendance;
        private String date; // dd-MM-yyyy
        private String status; // 'A', 'F', 'T', 'J'
    }
}
