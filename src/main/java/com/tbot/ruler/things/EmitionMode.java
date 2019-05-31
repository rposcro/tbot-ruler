package com.tbot.ruler.things;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmitionMode {
    private boolean periodic;
    private long period;
    
    public static EmitionMode periodic(long period) {
        return new EmitionMode(true, period);
    }
}
