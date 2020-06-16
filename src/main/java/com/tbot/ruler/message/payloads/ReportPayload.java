package com.tbot.ruler.message.payloads;

import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.model.ReportEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPayload implements MessagePayload {

    private ReportEntry reportEntry;
}
