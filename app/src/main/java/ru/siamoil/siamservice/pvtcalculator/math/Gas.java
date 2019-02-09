package ru.siamoil.siamservice.pvtcalculator.math;

public abstract class Gas {

    private static class PseudoReduced {

        private static double tPr(double t, double tc) {
            return t / tc;
        }

        private static double pPr(double p, double pc) {
            return p / pc;
        }
    }

    public static class ZFactor {
        private static double e = 0.000000000001;

        public static double dranchuk(double t, double tc, double p, double pc) {
            int i = 0;
            double x1;
            double x0 = 0.5;

            do {
                x1 = x0 - f(x0, t, tc, p, pc) / dfdz(x0, t, tc, p, pc);
                if (Math.abs(x1 - x0) < e) {
                    break;
                }
                x0 = Math.abs(x1) + e / 10;
                i++;
            } while (i < 100);
            return (x1 + x0) / 2;
        }

        private final static double A[] = {0.3265, -1.07, -0.5339, 0.01569, -0.05165, 0.5475, -0.7361, 0.1844,
                0.1056, 0.6134, 0.721};

        private static double rho(double z, double t, double tc, double p, double pc) {
            return 0.27 * PseudoReduced.pPr(p, pc) / (z * PseudoReduced.tPr(t, tc));
        }

        private static double c1(double t, double tc) {
            return A[0] + A[1] / PseudoReduced.tPr(t, tc) + A[2] / Math.pow(PseudoReduced.tPr(t, tc), 3.0) + A[3] /
                    Math.pow(PseudoReduced.tPr(t, tc), 4.0) + A[4] / Math.pow(PseudoReduced.tPr(t, tc), 5.0);
        }

        private static double c2(double t, double tc) {
            return A[5] + A[6] / PseudoReduced.tPr(t, tc) + A[7] / Math.pow(PseudoReduced.tPr(t, tc), 2);
        }

        private static double c3(double t, double tc) {
            return A[8] * (A[6] / PseudoReduced.tPr(t, tc) + A[7] / Math.pow(PseudoReduced.tPr(t, tc), 2));
        }

        private static double c4(double z, double t, double tc, double p, double pc) {
            return A[9] * (1 + A[10] * Math.pow(rho(z, t, tc, p, pc), 2)) * Math.pow(rho(z, t, tc, p, pc), 2) /
                    Math.pow(PseudoReduced.tPr(t, tc), 3) * Math.exp(-A[10] * Math.pow(rho(z, t, tc, p, pc), 2));
        }

        private static double f(double z, double t, double tc, double p, double pc) {
            return z - (1 + c1(t, tc) * rho(z, t, tc, p, pc) + c2(t, tc) * Math.pow(rho(z, t, tc, p, pc), 2) -
                    c3(t, tc) * Math.pow(rho(z, t, tc, p, pc), 5) + c4(z, t, tc, p, pc));
        }

        private static double dfdz(double z, double t, double tc, double p, double pc) {
            return 1 + c1(t, tc) * rho(z, t, tc, p, pc) / z + 2 * c2(t, tc) * Math.pow(rho(z, t, tc, p, pc), 2) / z -
                    5 * c3(t, tc) * Math.pow(rho(z, t, tc, p, pc), 5) / z + 2 * A[9] *
                    Math.pow(rho(z, t, tc, p, pc), 2) / (Math.pow(PseudoReduced.tPr(t, tc), 3) * z) *
                    (1 + A[10] * Math.pow(rho(z, t, tc, p, pc), 2) - Math.pow(A[10] *
                            Math.pow(rho(z, t, tc, p, pc), 2), 2)) * Math.exp(-A[10] * Math.pow(rho(z, t, tc, p, pc), 2));
        }

    }

    public static class VolumeFactor {
        public static double internal(double z, double t, double p) {
            return 0.02826136 * z * t / p;
        }
    }


    public static class PseudoCriticalTemperature {
        private static double suttonTc(double yg) {
            return 169.2 + 349.4 * yg - 74 * yg * yg;
        }

        public static double sutton(double yg, double nCO2, double nN2, double nH2S) {
            return WichertAziz.tC(WichertAziz.tPCM(suttonTc(WichertAziz.ygHC(yg, nCO2, nN2, nH2S)), nCO2, nN2, nH2S),
                    nCO2, nH2S);
        }
    }

    public static class PseudoCriticalPressure {
        private static double suttonPc(double yg) {
            return 756.8 - 131 * yg - 3.6 * yg * yg;
        }

        public static double sutton(double yg, double nCO2, double nN2, double nH2S) {
            return WichertAziz.pC(WichertAziz.pPCM(suttonPc(WichertAziz.ygHC(yg, nCO2, nN2, nH2S)), nCO2, nN2, nH2S),
                    WichertAziz.tPCM(PseudoCriticalTemperature.suttonTc(WichertAziz.ygHC(yg, nCO2, nN2, nH2S)),
                            nCO2, nN2, nH2S), nCO2, nH2S);
        }
    }

    private static class WichertAziz {

        private static double nHC(double nCO2, double nN2, double nH2S) {
            return 1 - (nCO2 + nN2 + nH2S);
        }

        private static double e(double nCO2, double nH2S) {
            return 120 * (Math.pow(nCO2 + nH2S, 0.9) - Math.pow(nCO2 + nH2S, 1.6)) + 15 * (Math.pow(nCO2, 0.5) -
                    Math.pow(nH2S, 4));
        }

        private static double ygHC(double yg, double nCO2, double nN2, double nH2S) {
            return (yg - (nH2S * PVTconstants.M_H2S + nCO2 * PVTconstants.M_CO2 + nN2 * PVTconstants.M_N2) /
                    PVTconstants.M_AIR) / nHC(nCO2, nN2, nH2S);
        }

        private static double tPCM(double tPCHC, double nCO2, double nN2, double nH2S) {
            return nHC(nCO2, nN2, nH2S) * tPCHC + (nH2S * PVTconstants.TC_H2S + nCO2 * PVTconstants.TC_CO2 + nN2 *
                    PVTconstants.TC_N2);
        }

        private static double pPCM(double pPCHC, double nCO2, double nN2, double nH2S) {
            return nHC(nCO2, nN2, nH2S) * pPCHC + (nH2S * PVTconstants.PC_H2S + nCO2 *
                    PVTconstants.PC_CO2 + nN2 * PVTconstants.PC_N2);
        }

        private static double tC(double tPCM, double nCO2, double nH2S) {
            return tPCM - e(nCO2, nH2S);
        }

        private static double pC(double pPCM, double tPCM, double nCO2, double nH2S) {
            return pPCM * (tPCM - e(nCO2, nH2S)) / (tPCM + nH2S * (1 - nH2S) * e(nCO2, nH2S));
        }
    }
}
