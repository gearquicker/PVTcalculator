package ru.siamoil.siamservice.pvtcalculator.math;

public abstract class Oil {
    private static class Glaso {
        private static final double[] A1 = {0.816, 0.130, 0.989};
        private static final double[] A2 = {0.816, 0.172, 0.989};

        private static double bob(double t, double yAPI, double yg, double rs) {
            return rs * Math.pow(yg / Converter.yAPItoYo(yAPI), 0.526) + 0.968 * t;
        }
    }

    private static class Standing {
        private static double rsFormula(double t, double p, double yAPI, double yg) {
            return yg * Math.pow((p / 18.2 + 1.4) * Math.pow(10, -x(t, yAPI)), (1 / 0.83));
        }

        private static double x(double t, double yAPI) {
            return 0.00091 * t - 0.0125 * yAPI;
        }
    }

    private static class Beal {
        private static double x(double yAPI) {
            return Math.pow(10, 0.43 + 8.33 / yAPI);
        }
    }

    private static class Converter {
        private static double yAPItoYo(double yAPI) {
            return 141.5 / (yAPI + 131.5);
        }
    }

    public static class Dead {
        public static class Viscosity {
            public static double beal(double t, double yAPI) {
                return (0.32 + 18000000 / Math.pow(yAPI, 4.53)) * Math.pow(360 / (t + 200), Beal.x(yAPI));
            }
        }
    }

    public static class UnderSaturated {
        public static class Compressibility {

            public static double vasquezBeggs(double t, double p, double yAPI, double yg, double rsb) {
                double c;
                c = (-1433 + 5 * rsb + 17.2 * t - 1180 * yg + 12.61 * yAPI) / (100000 * p);
                if (c < 6.89476E-27) {
                    c = 6.89476E-27;
                }
                return c;
            }
        }

        public static class VolumeFactor {
            public static double glaso(double t, double yAPI, double yg, double rsb, double coefC) {
                return coefC * Saturated.VolumeFactor.glaso(t, yAPI, yg, rsb);
            }
        }

        public static class Viscosity {
            public static double beal(double t, double p, double pb, double yAPI, double rsb) {
                return Saturated.Viscosity.chewConnalyConstructor(rsb, Dead.Viscosity.beal(t, yAPI)) + 0.001 *
                        (p - pb) * (0.024 * Math.pow(Saturated.Viscosity.chewConnalyConstructor(rsb,
                        Dead.Viscosity.beal(t, yAPI)), 1.6) + 0.038 *
                        Math.pow(Saturated.Viscosity.chewConnalyConstructor(rsb, Dead.Viscosity.beal(t, yAPI)), 0.56));
            }
        }

        public static class CoefC {
            public static double vasquezBeggs(double t, double p, double pb, double yAPI, double yg, double rsb) {
                double coefC;
                coefC = Math.exp(-Compressibility.vasquezBeggs(t, p, yAPI, yg, rsb) * p *
                        Math.log(p / pb));
                return coefC;
            }
        }
    }

    public static class Saturated {
        public static class GasOilRatio {
            public static double standing(double t, double p, double yAPI, double yg) {
                return Standing.rsFormula(t, p, yAPI, yg);
            }
        }

        public static class VolumeFactor {
            public static double glaso(double t, double yAPI, double yg, double rs) {
                return 1 + Math.pow(10, -6.58511 + 2.91329 * Math.log10(Glaso.bob(t, yAPI, yg, rs)) - 0.27683 *
                        Math.pow(Math.log10(Glaso.bob(t, yAPI, yg, rs)), 2));
            }
        }

        public static class Viscosity {
            public static double chewConnalyConstructor(double rs, double muOD) {
                return (0.2 + 0.8 * Math.pow(10, -0.00081 * rs)) * Math.pow(muOD, 0.43 + 0.57 *
                        Math.pow(10, -0.00072 * rs));
            }


            public static double beal(double t, double yAPI, double rs) {
                return chewConnalyConstructor(rs, Dead.Viscosity.beal(t, yAPI));
            }
        }

        public static class Compressibility {

            public static double internal(double bo, double dBoDP, double bg, double dRsDP) {
                double c;
                c = (-1 / bo) * (dBoDP - (bg / 5.6145835) * dRsDP);
                if (c < 6.89476E-27) {
                    c = 6.89476E-27;
                }
                return c;
            }
        }

        public static class DerivativeRsWrtP {
            private static double dx = 1;

            public static double standing(double t, double p, double yAPI, double yg) {
                return (GasOilRatio.standing(t, p + dx, yAPI, yg) - GasOilRatio.standing(t, p - dx, yAPI, yg)) /
                        (2 * dx);
            }
        }

        public static class derivativeBwrtP {
            public static double glaso(double t, double yAPI, double yg, double rs, double dRsDP) {
                return -0.0000000625054 * Math.pow(10, -0.0522134 * Math.pow(Math.log(Glaso.bob(t, yAPI, yg, rs)), 2)) *
                        Math.pow(yg / Converter.yAPItoYo(yAPI), 0.526) * Math.pow(Glaso.bob(t, yAPI, yg, rs), 1.91329) *
                        (Math.log(Glaso.bob(t, yAPI, yg, rs)) - 12.1159) * dRsDP;
            }
        }
    }

    public static class Live {
        public static class BubblePoint {
            public static double standing(double t, double yAPI, double yg, double rsb) {
                double pb;
                pb = 18.2 * (Math.pow(rsb / yg, 0.83) * Math.pow(10, Standing.x(t, yAPI)) - 1.4);

                if (pb < 0) {
                    pb = 2;
                }
                return pb;
            }
        }

        public static class GasOilRatio {
            public static double standing(double t, double p, double pb, double yAPI, double yg, double rsb) {
                double rs;
                if (p < pb) {
                    rs = Saturated.GasOilRatio.standing(t, p, yAPI, yg);
                } else {
                    rs = rsb;
                }
                return rs;
            }
        }

        public static class Compressibility {
            public static double vasquezBeggs(double t, double p, double pb, double yAPI, double yg, double bo,
                                              double rsb, double dBoDP, double bg, double dRsDP) {
                double c;
                if (p < pb) {
                    c = Saturated.Compressibility.internal(bo, dBoDP, bg, dRsDP);
                } else {
                    c = UnderSaturated.Compressibility.vasquezBeggs(t, p, yAPI, yg, rsb);
                }
                return c;
            }
        }

        public static class VolumeFactor {
            public static double glaso(double t, double p, double pb, double yAPI, double yg, double rs, double rsb,
                                       double coefC) {
                double b;
                if (p < pb) {
                    b = Saturated.VolumeFactor.glaso(t, yAPI, yg, rs);
                } else {
                    b = UnderSaturated.VolumeFactor.glaso(t, yAPI, yg, rsb, coefC);
                }
                return b;
            }
        }

        public static class Viscosity {
            public static double beal(double t, double p, double pb, double yAPI, double rs, double rsb) {
                double mu;
                if (p < pb) {
                    mu = Saturated.Viscosity.beal(t, yAPI, rs);
                } else {
                    mu = UnderSaturated.Viscosity.beal(t, p, pb, yAPI, rsb);
                }
                return mu;
            }
        }

        public static class Density {
            public static double internal(double yAPI, double yg, double rs, double bo) {
                return (62.4 * Converter.yAPItoYo(yAPI) + 0.0136 * rs * yg) / bo;
            }
        }
    }
}

