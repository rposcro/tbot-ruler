package com.tbot.ruler.broker.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryClaimTest {

    @Test
    public void testIsToggle() {
        assertTrue(BinaryClaim.TOGGLE.isToggle());
        assertFalse(BinaryClaim.SET_ON.isToggle());
        assertFalse(BinaryClaim.SET_OFF.isToggle());
    }

    @Test
    public void testIsSetOn() {
        assertTrue(BinaryClaim.SET_ON.isSetOn());
        assertFalse(BinaryClaim.TOGGLE.isSetOn());
        assertFalse(BinaryClaim.SET_OFF.isSetOn());
    }

    @Test
    public void testIsSetOff() {
        assertTrue(BinaryClaim.SET_OFF.isSetOff());
        assertFalse(BinaryClaim.TOGGLE.isSetOff());
        assertFalse(BinaryClaim.SET_ON.isSetOff());
    }

    @Test
    public void testOf() {
        assertTrue(BinaryClaim.of(true).isSetOn());
        assertFalse(BinaryClaim.of(true).isSetOff());
        assertFalse(BinaryClaim.of(false).isSetOn());
        assertTrue(BinaryClaim.of(false).isSetOff());
    }

    @Test
    public void testResolveState() {
        assertTrue(BinaryClaim.SET_ON.resolveState(null).isOn());
        assertTrue(BinaryClaim.SET_ON.resolveState(BinaryState.ON).isOn());
        assertTrue(BinaryClaim.SET_ON.resolveState(BinaryState.OFF).isOn());
        assertTrue(BinaryClaim.TOGGLE.resolveState(null).isOn());
        assertFalse(BinaryClaim.TOGGLE.resolveState(BinaryState.ON).isOn());
        assertTrue(BinaryClaim.TOGGLE.resolveState(BinaryState.OFF).isOn());
        assertFalse(BinaryClaim.SET_OFF.resolveState(null).isOn());
        assertFalse(BinaryClaim.SET_OFF.resolveState(BinaryState.ON).isOn());
        assertFalse(BinaryClaim.SET_OFF.resolveState(BinaryState.OFF).isOn());
    }
}
