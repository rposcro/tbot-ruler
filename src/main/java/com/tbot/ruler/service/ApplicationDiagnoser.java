package com.tbot.ruler.service;

import com.tbot.ruler.util.ApplicationDiagnosis;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ApplicationDiagnoser {

    private List<ApplicationDiagnosis> diagnoses = new LinkedList<>();

    public void storeDiagnosis(ApplicationDiagnosis diagnosis) {
        diagnoses.add(diagnosis);
    }

    public List<ApplicationDiagnosis> getDiagnoses() {
        return this.diagnoses;
    }
}
