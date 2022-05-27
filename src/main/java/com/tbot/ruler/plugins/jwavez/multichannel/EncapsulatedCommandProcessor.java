package com.tbot.ruler.plugins.jwavez.multichannel;

import com.rposcro.jwavez.core.commands.supported.multichannel.MultiChannelCommandEncapsulation;

public interface EncapsulatedCommandProcessor {

    void handle(MultiChannelCommandEncapsulation commandEncapsulation);
}
