package com.tbot.ruler.messages.payloads;

import com.tbot.ruler.model.ReportEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPayload {

    private ReportEntry reportEntry;
}
