package ru.siamoil.siamservice.pvtcalculator.math;

public abstract class Converter {
    public static double cToF(double t) { //from celsius to Fahrenheit degrees (°C to °F)
        return t * 1.8 + 32;
    }

    public static double atmToPSIA(double p) {
        return p * 14.6959;
    }

    public static double fToR(double f) { // °F to °R
        return f + 459.67;
    }

    public static double psiaToAtm(double psia) {
        return psia * 0.0680459639;
    }

    public static double fToC(double f) {
        return (f - 32) / 1.8;
    }

    public static double scfStbToM3M3(double scfStb) {
        return scfStb * 0.178107606679035;
    }

    public static double lbFt3toKgM3(double lbFt3) {
        return lbFt3 * 16.01846337396;
    }

    public static double yoToYAPI(double yo) {
        return 141.5 / yo - 131.5;
    }

    public static double m3m3ToScfStb(double m3m3) {
        return m3m3 / 0.178107606679035;
    }

    public static double cPsiaToCatm(double psia) {
        return psia * 14.6959;
    }

}

