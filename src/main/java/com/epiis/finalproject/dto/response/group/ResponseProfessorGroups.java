package com.epiis.finalproject.dto.response.group;

import com.epiis.finalproject.generic.ResponseGeneric;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class ResponseProfessorGroups extends ResponseGeneric {
    private List<ProfessorGroupProjection> data;
}