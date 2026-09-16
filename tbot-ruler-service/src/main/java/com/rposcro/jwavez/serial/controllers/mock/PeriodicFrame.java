package com.rposcro.jwavez.serial.controllers.mock;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PeriodicFrame implements Comparable<PeriodicFrame> {

    private String rawFrame;
    private ImmutableBuffer frameBuffer;
    private long periodMilliseconds;

    @Setter
    private long nextTriggerTime;

    public PeriodicFrame(String rawFrame, long periodMilliseconds) {
        rawFrame = rawFrame.replace(" ", "");

        if (rawFrame.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid frame length " + rawFrame.length() + " for " + rawFrame);
        }

        int bytesCnt = rawFrame.length() / 2;
        byte[] bytes = new byte[bytesCnt];

        for (int i = 0; i < bytesCnt; i++) {
            bytes[i] = (byte) Integer.parseInt(rawFrame.substring(i * 2, i * 2 + 2), 16);
        }

        this.rawFrame = rawFrame;
        this.frameBuffer = ImmutableBuffer.overBuffer(bytes);
        this.periodMilliseconds = periodMilliseconds;
        this.nextTriggerTime = System.currentTimeMillis();
    }

    @Override
    public int compareTo(PeriodicFrame another) {
        if (this.nextTriggerTime > another.nextTriggerTime) {
            return 1;
        } else if (this.nextTriggerTime < another.nextTriggerTime) {
            return -1;
        } else {
            return this.rawFrame.compareTo(another.rawFrame);
        }
    }
}
