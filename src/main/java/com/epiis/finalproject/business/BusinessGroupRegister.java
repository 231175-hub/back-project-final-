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
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            EntityCourse course = group.getParentCourse();
            EntityAcademicPeriod period = group.getParentAcademicperiod();

            // 1. Group Info
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
                semRomano = period.getYearPeriod() + "-" + (period.getNumberPeriod() == 1 ? "I" : period.getNumberPeriod() == 2 ? "II" : String.valueOf(period.getNumberPeriod()));
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
            List<EntityGroupStudent> enrollments = repositoryGroupStudent.findByParentGroup(group);
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
            response.setGroupInfo(info);

            // 2. Units
            List<EntityUnits> unitsEntities = repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group);
            List<ResponseGroupRegisterData.UnitInfoData> unitsList = unitsEntities.stream().map(u -> {
                ResponseGroupRegisterData.UnitInfoData ud = new ResponseGroupRegisterData.UnitInfoData();
                ud.setIdUnits(u.getIdUnits());
                ud.setNumberUnit(u.getNumberUnit());
                ud.setNameUnit(u.getNameUnit());
                return ud;
            }).collect(Collectors.toList());
            response.setUnits(unitsList);

            // 3. Class Dates
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
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

            // 4. Students
            List<ResponseGroupRegisterData.StudentData> studentsList = new ArrayList<>();
            
            // Sort by surname and firstname
            enrollments.sort(Comparator.comparing(gs -> 
                (gs.getParentStudent().getParentUser().getSurName() + ", " + gs.getParentStudent().getParentUser().getFirstName()).toUpperCase()
            ));

            // Fetch all attendances for students in this group
            List<EntityAttendance> allAttendances = repositoryAttendance.findByParentGroupStudentIn(enrollments);
            Map<String, List<EntityAttendance>> attendancesMap = allAttendances.stream()
                    .collect(Collectors.groupingBy(a -> a.getParentGroupStudent().getIdGroupStudent()));

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

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
            response.setStudents(studentsList);
            response.setSuccess(true);
            response.getListMessage().add("Estructura de registro cargada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.getListMessage().add("Error al obtener el registro: " + e.getMessage());
        }

        return response;
    }

    @Transactional
    public Map<String, Object> saveGroupRegisterData(String idGroup, RequestGroupRegisterSave request) {
        Map<String, Object> response = new HashMap<>();
        try {
            EntityGroup group = repositoryGroup.findById(idGroup)
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

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
                    throw new RuntimeException("El acta de este grupo ya está cerrada y no se pueden realizar modificaciones.");
                }
            }

            Map<String, EntityGroupStudent> studentMap = enrollments.stream()
                    .collect(Collectors.toMap(EntityGroupStudent::getIdGroupStudent, gs -> gs));

            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date now = new Date();

            String currentUser = "System";
            try {
                org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getName() != null) {
                    currentUser = auth.getName();
                }
            } catch (Exception e) {}

            List<EntityGradeLog> auditLogs = new ArrayList<>();

            for (RequestGroupRegisterSave.StudentSaveData ssd : request.getStudents()) {
                EntityGroupStudent gs = studentMap.get(ssd.getIdGroupStudent());
                if (gs == null) continue;

                // Save/update scores
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
                        if (split.length > 0 && !split[0].trim().isEmpty() && !split[0].equalsIgnoreCase("NSP")) {
                            try { t1 = Double.parseDouble(split[0].trim()); } catch (Exception e) {}
                        }
                        if (split.length > 1 && !split[1].trim().isEmpty() && !split[1].equalsIgnoreCase("NSP")) {
                            try { t2 = Double.parseDouble(split[1].trim()); } catch (Exception e) {}
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

                // Save/update attendances
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

            if (!auditLogs.isEmpty()) {
                repositoryGradeLog.saveAll(auditLogs);
            }

            response.put("success", true);
            response.put("message", "Datos guardados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("error", "Error al guardar los datos: " + e.getMessage());
        }

        return response;
    }

    @Transactional
    public Map<String, Object> closeGroupRegister(String idGroup) {
        Map<String, Object> response = new HashMap<>();

        try {
            EntityGroup group = repositoryGroup.findById(idGroup)
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            EntityCourse course = group.getParentCourse();
            EntityAcademicPeriod period = group.getParentAcademicperiod();

            List<EntityGroupStudent> enrollments = repositoryGroupStudent.findByParentGroup(group);
            if (enrollments.isEmpty()) {
                throw new RuntimeException("El grupo no tiene estudiantes inscritos.");
            }

            List<EntityUnits> units = repositoryUnits.findByParentGroupOrderByNumberUnitAsc(group);
            if (units.isEmpty()) {
                throw new RuntimeException("El curso no tiene unidades académicas configuradas.");
            }

            List<EntityCourseEnrollment> enrollmentsToUpdate = new ArrayList<>();

            for (EntityGroupStudent gs : enrollments) {
                String code = gs.getParentStudent().getCode();
                List<EntityUnitscore> savedScores = gs.getChildUnitscore() != null ? gs.getChildUnitscore() : new ArrayList<>();

                if (savedScores.size() < units.size()) {
                    throw new RuntimeException("Faltan notas para el alumno " + code + ". Aún no se pueden asignar las notas finales.");
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
                        throw new RuntimeException("Faltan notas para el alumno " + code + " en la unidad " + u.getNumberUnit() + ".");
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
                int roundedPpf = (int) Math.round(rawPpf);

                // Update official course enrollment history
                EntityCourseEnrollment officialEnrollment = repositoryCourseEnrollment.findByStudentCourseAndPeriod(
                        gs.getParentStudent().getIdStudent(),
                        course.getIdCourse(),
                        period.getIdPeriod()
                ).orElseThrow(() -> new RuntimeException("No se encontró la matrícula oficial del alumno " + code));

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

            response.put("success", true);
            response.put("message", "Acta cerrada exitosamente. Las notas finales y créditos se han actualizado en el historial y perfil del estudiante.");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("error", e.getMessage());
        }

        return response;
    }

    private DayOfWeek mapStringToDayOfWeek(String dia) {
        if (dia == null) return null;
        switch (dia.trim().toUpperCase()) {
            case "LUNES": return DayOfWeek.MONDAY;
            case "MARTES": return DayOfWeek.TUESDAY;
            case "MIERCOLES":
            case "MIÉRCOLES": return DayOfWeek.WEDNESDAY;
            case "JUEVES": return DayOfWeek.THURSDAY;
            case "VIERNES": return DayOfWeek.FRIDAY;
            case "SABADO":
            case "SÁBADO": return DayOfWeek.SATURDAY;
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
