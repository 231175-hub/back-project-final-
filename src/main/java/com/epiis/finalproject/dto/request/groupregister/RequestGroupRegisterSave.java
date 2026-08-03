package com.epiis.finalproject.dto.request.groupregister;

import com.epiis.finalproject.dto.common.UnitScoreBase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestGroupRegisterSave {
    @NotNull(message = "El campo students es obligatorio")
    @Valid
    private List<StudentSaveData> students;

    @Getter
    @Setter
    public static class StudentSaveData {
        @NotBlank(message = "El campo idGroupStudent es obligatorio")
        private String idGroupStudent;

        @NotNull(message = "El campo unitScores es obligatorio")
        @Valid
        private List<UnitScoreSaveData> unitScores;

        @NotNull(message = "El campo attendances es obligatorio")
        @Valid
        private List<AttendanceSaveData> attendances;
    }

    @Getter
    @Setter
    public static class UnitScoreSaveData extends UnitScoreBase {
        private String idUnitScore; // can be null/empty if new
        
        @NotBlank(message = "El campo idUnits es obligatorio")
        private String idUnits;
    }

    @Getter
    @Setter
    public static class AttendanceSaveData {
        private String idAttendance; // can be null/empty if new
        
        @NotBlank(message = "El campo date es obligatorio")
        private String date; // dd-MM-yyyy
        
        @NotBlank(message = "El campo status es obligatorio")
        private String status; // 'A', 'F', 'T', 'J'
    }
}
