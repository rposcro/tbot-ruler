package com.tbot.ruler.subjects;

import lombok.Getter;

@Getter
public class BasicSubjectState<T> implements SubjectState {

    protected String subjectUuid;
    protected T payload;
}
