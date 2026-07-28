package com.epiis.finalproject.business;

import com.epiis.finalproject.dto.request.groupregister.RequestGroupRegisterSave;
import com.epiis.finalproject.dto.response.groupregister.ResponseGroupRegisterData;
import com.epiis.finalproject.entity.*;
import com.epiis.finalproject.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessGroupRegister {
    private static final String MSG_GROUP_NOT_FOUND = "Grupo no encontrado";
    private static final String DATE_FORMAT_PATTERN = "dd-MM-yyyy";
    private static final String KEY_SUCCESS = "success";

    private final RepositoryGroup repositoryGroup;
    private final RepositoryGroupStudent repositoryGroupStudent;
    private final RepositoryUnits repositoryUnits;
    private final RepositoryUnitScore repositoryUnitScore;
    private final RepositoryAttendance repositoryAttendance;
    private final RepositoryCourseEnrollment repositoryCourseEnrollment;
    private final RepositoryStudent repositoryStudent;
    private final RepositoryGradeLog repositoryGradeLog;

    public BusinessGroupRegister(
            RepositoryGroup repositoryGroup,
            RepositoryGroupStudent repositoryGroupStudent,
            RepositoryUnits repositoryUnits,
            RepositoryUnitScore repositoryUnitScore,
            RepositoryAttendance repositoryAttendance,
            RepositoryCourseEnrollment repositoryCourseEnrollment,
            RepositoryStudent repositoryStudent,
            RepositoryGradeLog repositoryGradeLog) {
        this.repositoryGroup = repositoryGroup;
        this.repositoryGroupStudent = repositoryGroupStudent;
        this.repositoryUnits = repositoryUnits;
        this.repositoryUnitScore = repositoryUnitScore;
        this.repositoryAttendance = repositoryAttendance;
        this.repositoryCourseEnrollment = repositoryCourseEnrollment;
        this.repositoryStudent = repositoryStudent;
        this.repositoryGradeLog = repositoryGradeLog;
    }

    public ResponseGroupRegisterData getGroupRegisterData(String idGroup) {
        ResponseGroupRegisterData response = new ResponseGroupRegisterData();
        response.setListMessage(new ArrayList<>());

        try {
            EntityGroup group = repositoryGroup.findById(idGroup)
                    .orElseThrow(() -> new IllegalArgumentException(MSG_GROUP_NOT_FOUND));

            EntityCourse course = group.getParentCourse();
            EntityAcademicPeriod period = group.getParentAcademicperiod();
            List<EntityGroupStudent> enrollments = repositoryGroupStudent.findByParentGroup(group);

            // 1. Group Info
            response.setGroupInfo(buildGroupInfo(group, enrollments, course, period));

            // 2. Units
            List<EntityUnits> unitsEntities = repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group);
            response.setUnits(buildUnitsInfo(unitsEntities));

            // 3. Class Dates
            buildWeeksAndClassDates(group, period, response);

            // 4. Students
            response.setStudents(buildStudentsData(enrollments, unitsEntities, response.getClassDates()));

            response.setSuccess(true);
            response.getListMessage().add("Estructura de registro cargada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.getListMessage().add("Error al obtener el registro: " + e.getMessage());
        }

        return response;
    }

    private ResponseGroupRegisterData.GroupInfoData buildGroupInfo(
            EntityGroup group,
            List<EntityGroupStudent> enrollments,
            EntityCourse course,
            EntityAcademicPeriod period) {
        
        ResponseGroupRegisterData.GroupInfoData info = new ResponseGroupRegisterData.GroupInfoData();
        info.setIdGroup(group.getIdGroup());
        info.setNameGroup(group.getNameGroup());
        info.setIdCourse(course.getIdCourse());
        info.setCourseCode(course.getCode());
        info.setCourseName(course.getNameCourse());
        info.setCredits(course.getCredits());
        info.setSchoolName(course.getParentSchool() != null ? course.getParentSchool().getNameSchool() : "");
        
        String semRomano = "";
        if (period != null) {
            String numberStr;
            if (period.getNumberPeriod() == 1) {
                numberStr = "I";
            } else if (period.getNumberPeriod() == 2) {
                numberStr = "II";
            } else {
                numberStr = String.valueOf(period.getNumberPeriod());
            }
            semRomano = period.getYearPeriod() + "-" + numberStr;
        }
        info.setSemesterName(semRomano);
        
        String doc = "SIN DOCENTE";
        if (group.getParentProfessor() != null && group.getParentProfessor().getParentUser() != null) {
            doc = group.getParentProfessor().getParentUser().getSurName() + " " + group.getParentProfessor().getParentUser().getFirstName();
        }
        info.setProfessorName(doc.toUpperCase());
        info.setConceptualWeight(group.getConceptualWeight());
        info.setPracticalWeight(group.getPracticalWeight());
        info.setAttitudinalWeight(group.getAttitudinalWeight());

        // Check if group is closed
        boolean isClosed = false;
        if (!enrollments.isEmpty()) {
            EntityGroupStudent first = enrollments.get(0);
            Optional<EntityCourseEnrollment> enrollmentOpt = repositoryCourseEnrollment.findByStudentCourseAndPeriod(
                    first.getParentStudent().getIdStudent(),
                    course.getIdCourse(),
                    period.getIdPeriod()
            );
            if (enrollmentOpt.isPresent() && enrollmentOpt.get().getFinalScore() != null) {
                isClosed = true;
            }
        }
        info.setClosed(isClosed);
        return info;
    }

    private List<ResponseGroupRegisterData.UnitInfoData> buildUnitsInfo(List<EntityUnits> unitsEntities) {
        return unitsEntities.stream().map(u -> {
            ResponseGroupRegisterData.UnitInfoData ud = new ResponseGroupRegisterData.UnitInfoData();
            ud.setIdUnits(u.getIdUnits());
            ud.setNumberUnit(u.getNumberUnit());
            ud.setNameUnit(u.getNameUnit());
            return ud;
        }).toList();
    }

    private void buildWeeksAndClassDates(
            EntityGroup group,
            EntityAcademicPeriod period,
            ResponseGroupRegisterData response) {
        
        LocalDate startDate = LocalDate.now();
        if (period != null && period.getStartDate() != null) {
            startDate = period.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        List<DayOfWeek> classDays = new ArrayList<>();
        if (group.getChildSchedule() != null) {
            for (EntitySchedule sch : group.getChildSchedule()) {
                DayOfWeek dow = mapStringToDayOfWeek(sch.getDayWeek());
                if (dow != null) {
                    classDays.add(dow);
                }
            }
        }
        if (classDays.isEmpty()) {
            classDays = Arrays.asList(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY);
        }
        List<DayOfWeek> sortedDays = new ArrayList<>(classDays);
        Collections.sort(sortedDays);

        LocalDate weekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

        List<String> generatedDates = new ArrayList<>();
        List<ResponseGroupRegisterData.WeekData> weeksList = new ArrayList<>();

        for (int week = 0; week < 17; week++) {
            ResponseGroupRegisterData.WeekData wd = new ResponseGroupRegisterData.WeekData();
            wd.setName("Semana " + (week + 1));
            wd.setDates(new ArrayList<>());
            for (DayOfWeek day : sortedDays) {
                LocalDate classDate = weekStart.plusWeeks(week).with(day);
                if (!classDate.isBefore(startDate)) {
                    String formatted = classDate.format(formatter);
                    wd.getDates().add(formatted);
                    generatedDates.add(formatted);
                }
            }
            if (!wd.getDates().isEmpty()) {
                weeksList.add(wd);
            }
        }
        response.setWeeks(weeksList);
        response.setClassDates(generatedDates);
    }

    private List<ResponseGroupRegisterData.StudentData> buildStudentsData(
            List<EntityGroupStudent> enrollments,
            List<EntityUnits> unitsEntities,
            List<String> generatedDates) {
        
        List<ResponseGroupRegisterData.StudentData> studentsList = new ArrayList<>();
        
        // Sort by surname and firstname
        enrollments.sort(Comparator.comparing(gs -> 
            (gs.getParentStudent().getParentUser().getSurName() + ", " + gs.getParentStudent().getParentUser().getFirstName()).toUpperCase()
        ));

        // Fetch all attendances for students in this group
        List<EntityAttendance> allAttendances = repositoryAttendance.findByParentGroupStudentIn(enrollments);
        Map<String, List<EntityAttendance>> attendancesMap = allAttendances.stream()
                .collect(Collectors.groupingBy(a -> a.getParentGroupStudent().getIdGroupStudent()));

        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        for (EntityGroupStudent gs : enrollments) {
            ResponseGroupRegisterData.StudentData sd = new ResponseGroupRegisterData.StudentData();
            sd.setIdGroupStudent(gs.getIdGroupStudent());
            sd.setCode(gs.getParentStudent().getCode());
            sd.setFullName(gs.getParentStudent().getParentUser().getSurName() + " " + gs.getParentStudent().getParentUser().getFirstName());

            // Unit Scores
            List<ResponseGroupRegisterData.UnitScoreData> scoresList = new ArrayList<>();
            List<EntityUnitscore> savedScores = gs.getChildUnitscore() != null ? gs.getChildUnitscore() : new ArrayList<>();
            Map<String, EntityUnitscore> scoreByUnitId = savedScores.stream()
                    .collect(Collectors.toMap(us -> us.getParentUnits().getIdUnits(), us -> us, (a, b) -> a));

            for (EntityUnits u : unitsEntities) {
                ResponseGroupRegisterData.UnitScoreData usd = new ResponseGroupRegisterData.UnitScoreData();
                usd.setIdUnits(u.getIdUnits());
                usd.setNumberUnit(u.getNumberUnit());

                EntityUnitscore saved = scoreByUnitId.get(u.getIdUnits());
                if (saved != null) {
                    usd.setIdUnitScore(saved.getIdUnitScore());
                    usd.setConceptualScore(saved.getConceptualScore());
                    usd.setPracticalScore(saved.getPracticalScore());
                    usd.setTest1Score(saved.getTest1Score());
                    usd.setTest2Score(saved.getTest2Score());
                    usd.setAttitudinalScore(saved.getAttitudinalScore());
                    usd.setConceptualGrades(saved.getConceptualGrades());
                    usd.setPracticalGrades(saved.getPracticalGrades());
                    usd.setTestGrades(saved.getTestGrades());
                    usd.setScore(saved.getScore());
                } else {
                    usd.setIdUnitScore(null);
                    usd.setConceptualScore(null);
                    usd.setPracticalScore(null);
                    usd.setTest1Score(null);
                    usd.setTest2Score(null);
                    usd.setAttitudinalScore(null);
                    usd.setConceptualGrades(null);
                    usd.setPracticalGrades(null);
                    usd.setTestGrades(null);
                    usd.setScore(0.0);
                }
                scoresList.add(usd);
            }
            sd.setUnitScores(scoresList);

            // Attendances
            List<EntityAttendance> studentAtts = attendancesMap.getOrDefault(gs.getIdGroupStudent(), new ArrayList<>());
            Map<String, EntityAttendance> attByDateStr = studentAtts.stream()
                    .filter(a -> a.getAttendanceDate() != null)
                    .collect(Collectors.toMap(a -> df.format(a.getAttendanceDate()), a -> a, (a, b) -> a));

            List<ResponseGroupRegisterData.AttendanceData> attList = new ArrayList<>();
            for (String dateStr : generatedDates) {
                ResponseGroupRegisterData.AttendanceData ad = new ResponseGroupRegisterData.AttendanceData();
                ad.setDate(dateStr);

                EntityAttendance savedAtt = attByDateStr.get(dateStr);
                if (savedAtt != null) {
                    ad.setIdAttendance(savedAtt.getIdAttendance());
                    ad.setStatus(savedAtt.getStatus());
                } else {
                    ad.setIdAttendance(null);
                    ad.setStatus("");
                }
                attList.add(ad);
            }
            sd.setAttendances(attList);

            studentsList.add(sd);
        }
        return studentsList;
    }

    private String getCurrentUser() {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                return auth.getName();
            }
        } catch (Exception e) {
            // Fallback to default user if SecurityContext is not available
        }
        return "System";
    }

    private Double parseDoubleSilently(String value) {
        if (value == null || value.trim().isEmpty() || "NSP".equalsIgnoreCase(value.trim())) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Transactional
    public Map<String, Object> saveGroupRegisterData(String idGroup, RequestGroupRegisterSave request) {
        Map<String, Object> response = new HashMap<>();
        try {
            EntityGroup group = repositoryGroup.findById(idGroup)
                    .orElseThrow(() -> new IllegalArgumentException(MSG_GROUP_NOT_FOUND));

            // Check if group is already closed
            List<EntityGroupStudent> enrollments = repositoryGroupStudent.findByParentGroup(group);
            if (!enrollments.isEmpty()) {
                EntityGroupStudent first = enrollments.get(0);
                Optional<EntityCourseEnrollment> enrollmentOpt = repositoryCourseEnrollment.findByStudentCourseAndPeriod(
                        first.getParentStudent().getIdStudent(),
                        group.getParentCourse().getIdCourse(),
                        group.getParentAcademicperiod().getIdPeriod()
                );
                if (enrollmentOpt.isPresent() && enrollmentOpt.get().getFinalScore() != null) {
                    throw new IllegalStateException("El acta de este grupo ya está cerrada y no se pueden realizar modificaciones.");
                }
            }

            Map<String, EntityGroupStudent> studentMap = enrollments.stream()
                    .collect(Collectors.toMap(EntityGroupStudent::getIdGroupStudent, gs -> gs));

            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN);
            Date now = new Date();
            String currentUser = getCurrentUser();
            List<EntityGradeLog> auditLogs = new ArrayList<>();

            for (RequestGroupRegisterSave.StudentSaveData ssd : request.getStudents()) {
                EntityGroupStudent gs = studentMap.get(ssd.getIdGroupStudent());
                if (gs == null) continue;

                saveStudentScores(ssd, gs, currentUser, now, auditLogs);
                saveStudentAttendances(ssd, gs, now, df);
            }

            if (!auditLogs.isEmpty()) {
                repositoryGradeLog.saveAll(auditLogs);
            }

            response.put(KEY_SUCCESS, true);
            response.put("message", "Datos guardados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            response.put(KEY_SUCCESS, false);
            response.put("error", "Error al guardar los datos: " + e.getMessage());
        }

        return response;
    }

    private void saveStudentScores(
            RequestGroupRegisterSave.StudentSaveData ssd,
            EntityGroupStudent gs,
            String currentUser,
            Date now,
            List<EntityGradeLog> auditLogs) {
        
        for (RequestGroupRegisterSave.UnitScoreSaveData ussd : ssd.getUnitScores()) {
            EntityUnitscore scoreEntity;
            if (ussd.getIdUnitScore() != null && !ussd.getIdUnitScore().trim().isEmpty()) {
                scoreEntity = repositoryUnitScore.findById(ussd.getIdUnitScore())
                        .orElse(new EntityUnitscore());
            } else {
                // Check if it already exists in database
                List<EntityUnitscore> existing = gs.getChildUnitscore();
                EntityUnitscore match = null;
                if (existing != null) {
                    for (EntityUnitscore e : existing) {
                        if (e.getParentUnits().getIdUnits().equals(ussd.getIdUnits())) {
                            match = e;
                            break;
                        }
                    }
                }
                if (match != null) {
                    scoreEntity = match;
                } else {
                    scoreEntity = new EntityUnitscore();
                    scoreEntity.setIdUnitScore(UUID.randomUUID().toString());
                    scoreEntity.setParentGroupStudent(gs);
                    EntityUnits u = new EntityUnits();
                    u.setIdUnits(ussd.getIdUnits());
                    scoreEntity.setParentUnits(u);
                    scoreEntity.setCreatedAt(now);
                }
            }

            // Mapeo de sub-notas de exámenes a columnas legacy para compatibilidad
            Double t1 = null;
            Double t2 = null;
            if (ussd.getTestGrades() != null && !ussd.getTestGrades().trim().isEmpty()) {
                String[] split = ussd.getTestGrades().split(",");
                if (split.length > 0) {
                    t1 = parseDoubleSilently(split[0]);
                }
                if (split.length > 1) {
                    t2 = parseDoubleSilently(split[1]);
                }
            }

            // Detect and log changes
            logGradeChangeIfModified(scoreEntity.getIdUnitScore(), "conceptualScore", scoreEntity.getConceptualScore(), ussd.getConceptualScore(), currentUser, now, auditLogs);
            logGradeChangeIfModified(scoreEntity.getIdUnitScore(), "practicalScore", scoreEntity.getPracticalScore(), ussd.getPracticalScore(), currentUser, now, auditLogs);
            logGradeChangeIfModified(scoreEntity.getIdUnitScore(), "test1Score", scoreEntity.getTest1Score(), t1, currentUser, now, auditLogs);
            logGradeChangeIfModified(scoreEntity.getIdUnitScore(), "test2Score", scoreEntity.getTest2Score(), t2, currentUser, now, auditLogs);
            logGradeChangeIfModified(scoreEntity.getIdUnitScore(), "attitudinalScore", scoreEntity.getAttitudinalScore(), ussd.getAttitudinalScore(), currentUser, now, auditLogs);
            logGradeChangeIfModified(scoreEntity.getIdUnitScore(), "score", scoreEntity.getScore(), ussd.getScore(), currentUser, now, auditLogs);

            scoreEntity.setConceptualScore(ussd.getConceptualScore());
            scoreEntity.setPracticalScore(ussd.getPracticalScore());
            scoreEntity.setTest1Score(t1);
            scoreEntity.setTest2Score(t2);
            scoreEntity.setAttitudinalScore(ussd.getAttitudinalScore());
            scoreEntity.setConceptualGrades(ussd.getConceptualGrades());
            scoreEntity.setPracticalGrades(ussd.getPracticalGrades());
            scoreEntity.setTestGrades(ussd.getTestGrades());
            scoreEntity.setScore(ussd.getScore());
            scoreEntity.setUpdatedAt(now);

            repositoryUnitScore.save(scoreEntity);
        }
    }

    private void saveStudentAttendances(
            RequestGroupRegisterSave.StudentSaveData ssd,
            EntityGroupStudent gs,
            Date now,
            SimpleDateFormat df) throws java.text.ParseException {
        
        for (RequestGroupRegisterSave.AttendanceSaveData asd : ssd.getAttendances()) {
            if (asd.getStatus() == null || asd.getStatus().trim().isEmpty()) {
                // If it has id, delete it
                if (asd.getIdAttendance() != null && !asd.getIdAttendance().trim().isEmpty()) {
                    repositoryAttendance.deleteById(asd.getIdAttendance());
                }
                continue;
            }

            EntityAttendance attEntity;
            Date attendanceDate = df.parse(asd.getDate());

            if (asd.getIdAttendance() != null && !asd.getIdAttendance().trim().isEmpty()) {
                attEntity = repositoryAttendance.findById(asd.getIdAttendance())
                        .orElse(new EntityAttendance());
            } else {
                // Check if it already exists for student + date
                List<EntityAttendance> existing = repositoryAttendance.findByParentGroupStudentIn(Arrays.asList(gs));
                EntityAttendance match = null;
                for (EntityAttendance e : existing) {
                    if (e.getAttendanceDate() != null && df.format(e.getAttendanceDate()).equals(asd.getDate())) {
                        match = e;
                        break;
                    }
                }
                if (match != null) {
                    attEntity = match;
                } else {
                    attEntity = new EntityAttendance();
                    attEntity.setIdAttendance(UUID.randomUUID().toString());
                    attEntity.setParentGroupStudent(gs);
                    attEntity.setAttendanceDate(attendanceDate);
                    attEntity.setCreatedAt(now);
                }
            }

            attEntity.setStatus(asd.getStatus().trim().toUpperCase());
            attEntity.setUpdatedAt(now);

            repositoryAttendance.save(attEntity);
        }
    }

    @Transactional
    public Map<String, Object> closeGroupRegister(String idGroup) {
        Map<String, Object> response = new HashMap<>();

        try {
            EntityGroup group = repositoryGroup.findById(idGroup)
                    .orElseThrow(() -> new IllegalArgumentException(MSG_GROUP_NOT_FOUND));

            EntityCourse course = group.getParentCourse();
            EntityAcademicPeriod period = group.getParentAcademicperiod();

            List<EntityGroupStudent> enrollments = repositoryGroupStudent.findByParentGroup(group);
            if (enrollments.isEmpty()) {
                throw new IllegalArgumentException("El grupo no tiene estudiantes inscritos.");
            }

            List<EntityUnits> units = repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group);
            if (units.isEmpty()) {
                throw new IllegalStateException("El curso no tiene unidades académicas configuradas.");
            }

            List<EntityCourseEnrollment> enrollmentsToUpdate = new ArrayList<>();

            for (EntityGroupStudent gs : enrollments) {
                int roundedPpf = calculateStudentFinalScore(gs, units, group);

                // Update official course enrollment history
                EntityCourseEnrollment officialEnrollment = repositoryCourseEnrollment.findByStudentCourseAndPeriod(
                        gs.getParentStudent().getIdStudent(),
                        course.getIdCourse(),
                        period.getIdPeriod()
                ).orElseThrow(() -> new IllegalStateException("No se encontró la matrícula oficial del alumno " + gs.getParentStudent().getCode()));

                officialEnrollment.setFinalScore(roundedPpf);
                officialEnrollment.setStatus(roundedPpf >= 11 ? "APROBADO" : "REPROBADO");
                officialEnrollment.setUpdatedAt(new Date());

                enrollmentsToUpdate.add(officialEnrollment);
            }

            repositoryCourseEnrollment.saveAll(enrollmentsToUpdate);

            // Recalcular y guardar créditos y promedios acumulados en tstudent
            List<EntityStudent> studentsToUpdate = new ArrayList<>();
            for (EntityGroupStudent gs : enrollments) {
                EntityStudent student = gs.getParentStudent();
                
                int totalApprovedCredits = repositoryCourseEnrollment.sumApprovedCreditsByStudent(student.getIdStudent());
                double weightedAverage = repositoryCourseEnrollment.calculateWeightedAverageByStudent(student.getIdStudent());
                
                student.setTotalCredits(totalApprovedCredits);
                student.setAverage(weightedAverage);
                student.setUpdatedAt(new Date());
                
                studentsToUpdate.add(student);
            }
            repositoryStudent.saveAll(studentsToUpdate);

            response.put(KEY_SUCCESS, true);
            response.put("message", "Acta cerrada exitosamente. Las notas finales y créditos se han actualizado en el historial y perfil del estudiante.");

        } catch (Exception e) {
            e.printStackTrace();
            response.put(KEY_SUCCESS, false);
            response.put("error", e.getMessage());
        }

        return response;
    }

    private int calculateStudentFinalScore(EntityGroupStudent gs, List<EntityUnits> units, EntityGroup group) {
        String code = gs.getParentStudent().getCode();
        List<EntityUnitscore> savedScores = gs.getChildUnitscore() != null ? gs.getChildUnitscore() : new ArrayList<>();

        if (savedScores.size() < units.size()) {
            throw new IllegalStateException("Faltan notas para el alumno " + code + ". Aún no se pueden asignar las notas finales.");
        }

        double sumOfUnitScores = 0;

        for (EntityUnits u : units) {
            EntityUnitscore score = null;
            for (EntityUnitscore s : savedScores) {
                if (s.getParentUnits().getIdUnits().equals(u.getIdUnits())) {
                    score = s;
                    break;
                }
            }

            if (score == null || score.getConceptualScore() == null || score.getPracticalScore() == null || score.getAttitudinalScore() == null) {
                throw new IllegalStateException("Faltan notas para el alumno " + code + " en la unidad " + u.getNumberUnit() + ".");
            }

            // Treat NSP (-1.0) as 0.0 for average calculations, and round component grades
            double cc = score.getConceptualScore() < 0 ? 0.0 : Math.round(score.getConceptualScore());
            double cp = score.getPracticalScore() < 0 ? 0.0 : Math.round(score.getPracticalScore());
            double ca = score.getAttitudinalScore() < 0 ? 0.0 : Math.round(score.getAttitudinalScore());

            // Calculate Unit PF
            double unitPf = cc * group.getConceptualWeight() +
                            cp * group.getPracticalWeight() +
                            ca * group.getAttitudinalWeight();
            
            sumOfUnitScores += Math.round(unitPf);
        }

        // PPF (Promedio de Promedios Finales)
        double rawPpf = sumOfUnitScores / units.size();
        return (int) Math.round(rawPpf);
    }

    private DayOfWeek mapStringToDayOfWeek(String dia) {
        if (dia == null) return null;
        switch (dia.trim().toUpperCase()) {
            case "LUNES": return DayOfWeek.MONDAY;
            case "MARTES": return DayOfWeek.TUESDAY;
            case "MIERCOLES", "MIÉRCOLES": return DayOfWeek.WEDNESDAY;
            case "JUEVES": return DayOfWeek.THURSDAY;
            case "VIERNES": return DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO": return DayOfWeek.SATURDAY;
            case "DOMINGO": return DayOfWeek.SUNDAY;
            default: return null;
        }
    }

    private void logGradeChangeIfModified(
            String idUnitScore,
            String fieldName,
            Double oldValue,
            Double newValue,
            String user,
            Date now,
            List<EntityGradeLog> logsList) {
        if (!Objects.equals(oldValue, newValue)) {
            EntityGradeLog log = new EntityGradeLog();
            log.setIdGradeLog(UUID.randomUUID().toString());
            log.setIdUnitScore(idUnitScore);
            log.setFieldName(fieldName);
            log.setPreviousScore(oldValue);
            log.setNewScore(newValue);
            log.setModifiedBy(user);
            log.setCreatedAt(now);
            logsList.add(log);
        }
    }
}
