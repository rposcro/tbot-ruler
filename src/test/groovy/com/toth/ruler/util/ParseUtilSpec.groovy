package com.tbot.ruler.util

import spock.lang.Specification

class ParseUtilSpec extends Specification {

    def "parses numbers of different radix" () {
        given:
        String sZero = "0";
        String sDec = "1234";
        String sHex = "0xaf45";
        String sOct = "07345";
        String sBin = "0b010111101";

        when:
        int iZero = ParseUtil.parseInt(sZero);
        int iDec = ParseUtil.parseInt(sDec);
        int iHex = ParseUtil.parseInt(sHex);
        int iOct = ParseUtil.parseInt(sOct);
        int iBin = ParseUtil.parseInt(sBin);

        then:
        iZero == 0;
        iDec == 1234;
        iHex == 0xaf45;
        iOct == 07345;
        iBin == 0b010111101;
    }
}
