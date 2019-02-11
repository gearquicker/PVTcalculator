package ru.siamoil.siamservice.pvtcalculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ru.siamoil.siamservice.pvtcalculator.math.Converter;
import ru.siamoil.siamservice.pvtcalculator.math.Gas;
import ru.siamoil.siamservice.pvtcalculator.math.Oil;

public class InputFragment extends Fragment {

    private PlotFragment plotFragment;

    private EditText tvPressure;
    private EditText tvTemperature;
    private EditText tvGasGravity;
    private EditText tvOilGravity;
    private EditText tvGor;
    private Spinner spinner;
    private TextView tvResult;
    private int pointsAmountRoot = 50;

    private double[] pX = new double[pointsAmountRoot];
    private double[] tY = new double[pointsAmountRoot];

    private NumberFormat expForm = new DecimalFormat("0.####E0");
    private NumberFormat decForm = new DecimalFormat("###.####");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_fragment, container, false);
        setHasOptionsMenu(true);
        initView(view);
        return view;
    }

    private void initView(View view) {
        initXY();
        tvPressure = view.findViewById(R.id.tv_pressure);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvGasGravity = view.findViewById(R.id.tv_gas_gravity);
        tvOilGravity = view.findViewById(R.id.tv_oil_gravity);
        tvGor = view.findViewById(R.id.tv_gor);
        spinner = view.findViewById(R.id.spinner);
        tvResult = view.findViewById(R.id.tv_result);
        Button btn_calc = view.findViewById(R.id.btn_calc);

        String[] strings = new String[]{
                getString(R.string.spinner_pb),
                getString(R.string.spinner_rs),
                getString(R.string.spinner_bo),
                getString(R.string.spinner_co),
                getString(R.string.spinner_rho),
                getString(R.string.spinner_mu)
        };
        final ArrayAdapter<String> adapterPeriod1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, strings);
        adapterPeriod1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterPeriod1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calcCurrent();
                refresh3d();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcCurrent();
                refresh3d();
            }
        });
    }

    private void calcCurrent() {
        String text = "";
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                text = getString(R.string.unit_atm);
                break;
            case 1:
                text = getString(R.string.unit_m3_m3);
                break;
            case 2:
                text =  getString(R.string.unit_m3_m3);
                break;
            case 3:
                text =  getString(R.string.unit_atm1);
                break;
            case 4:
                text =  getString(R.string.unit_kg_m3);
                break;
            case 5:
                text =  getString(R.string.unit_cp);
                break;
        }

        text = formatDoubleToString(getParamValue()) + " " + text;

        tvResult.setText(text);
    }

    private String formatDoubleToString(double d) {

        String ans;
        if ((Math.abs(d) >= 0.001 & Math.abs(d) <= 1000) || d == 0) {
            ans = decForm.format(d);
        } else {
            ans = expForm.format(d);
        }
        ans = ans.replace(",", ".");
        return ans;
    }

    private double[][] getZ() {
        double[][] z = new double[pointsAmountRoot][pointsAmountRoot];
        double yg = Double.parseDouble(tvGasGravity.getText().toString());
        double yAPI = Converter.yoToYAPI(Double.parseDouble(tvOilGravity.getText().toString()));
        double rsb = Converter.m3m3ToScfStb(Double.parseDouble(tvGor.getText().toString()));

        switch (spinner.getSelectedItemPosition()) {
            case 0:
                for (int p = 0; p < pointsAmountRoot; p++) {
                    for (int t = 0; t < pointsAmountRoot; t++) {
                        z[t][p] = Converter.psiaToAtm(getPb(Converter.cToF(tY[t]), yg, yAPI, rsb));
                    }
                }
                break;
            case 1:
                for (int p = 0; p < pointsAmountRoot; p++) {
                    for (int t = 0; t < pointsAmountRoot; t++) {
                        z[t][p] = Converter.scfStbToM3M3(getRs(Converter.cToF(tY[t]), Converter.atmToPSIA(pX[p]), yg, yAPI, rsb));
                    }
                }
                break;
            case 2:
                for (int p = 0; p < pointsAmountRoot; p++) {
                    for (int t = 0; t < pointsAmountRoot; t++) {
                        z[t][p] = getBo(Converter.cToF(tY[t]), Converter.atmToPSIA(pX[p]), yg, yAPI, rsb);
                    }
                }
                break;
            case 3:
                for (int p = 0; p < pointsAmountRoot; p++) {
                    for (int t = 0; t < pointsAmountRoot; t++) {
                        z[t][p] = Converter.cPsiaToCatm(getCo(Converter.cToF(tY[t]), Converter.atmToPSIA(pX[p]), yg, yAPI, rsb));
                    }
                }
                break;
            case 4:
                for (int p = 0; p < pointsAmountRoot; p++) {
                    for (int t = 0; t < pointsAmountRoot; t++) {
                        z[t][p] = Converter.lbFt3toKgM3(getRho(Converter.cToF(tY[t]), Converter.atmToPSIA(pX[p]), yg, yAPI, rsb));
                    }
                }
                break;
            case 5:
                for (int p = 0; p < pointsAmountRoot; p++) {
                    for (int t = 0; t < pointsAmountRoot; t++) {
                        z[t][p] = getMu(Converter.cToF(tY[t]), Converter.atmToPSIA(pX[p]), yg, yAPI, rsb);
                    }
                }
                break;
        }

        return z;
    }

    private double getParamValue() {
        double yg = Double.parseDouble(tvGasGravity.getText().toString());
        double yAPI = Converter.yoToYAPI(Double.parseDouble(tvOilGravity.getText().toString()));
        double rsb = Converter.m3m3ToScfStb(Double.parseDouble(tvGor.getText().toString()));
        double currentT = Double.parseDouble(tvTemperature.getText().toString());
        double currentP = Double.parseDouble(tvPressure.getText().toString());
        double value = 0;

        switch (spinner.getSelectedItemPosition()) {
            case 0:
                value = Converter.psiaToAtm(getPb(Converter.cToF(currentT), yg, yAPI, rsb));
                break;
            case 1:
                value = Converter.scfStbToM3M3(getRs(Converter.cToF(currentT), Converter.atmToPSIA(currentP), yg, yAPI, rsb));
                break;
            case 2:
                value = getBo(Converter.cToF(currentT), Converter.atmToPSIA(currentP), yg, yAPI, rsb);
                break;
            case 3:
                value = Converter.cPsiaToCatm(getCo(Converter.cToF(currentT), Converter.atmToPSIA(currentP), yg, yAPI, rsb));
                break;
            case 4:
                value = Converter.lbFt3toKgM3(getRho(Converter.cToF(currentT), Converter.atmToPSIA(currentP), yg, yAPI, rsb));
                break;
            case 5:
                value = getMu(Converter.cToF(currentT), Converter.atmToPSIA(currentP), yg, yAPI, rsb);
                break;
        }

        return value;
    }

    private void refresh3d() {
        Gson gson = new GsonBuilder().create();
        double currentT = Double.parseDouble(tvTemperature.getText().toString());
        double currentP = Double.parseDouble(tvPressure.getText().toString());
        plotFragment.updateWebView(gson.toJson(pX), gson.toJson(tY), gson.toJson(getZ()), currentP, currentT, getParamValue());
    }

    private double getPb(double t, double yg, double yAPI, double rsb) {
        return Oil.Live.BubblePoint.standing(t, yAPI, yg, rsb);
    }

    private double getRs(double t, double p, double yg, double yAPI, double rsb) {
        double pb = getPb(t, yg, yAPI, rsb);
        return Oil.Live.GasOilRatio.standing(t, p, pb, yAPI, yg, rsb);
    }

    private double getBo(double t, double p, double yg, double yAPI, double rsb) {
        double pb = getPb(t, yg, yAPI, rsb);
        double rs = getRs(t, p, yg, yAPI, rsb);
        double coefC = Oil.UnderSaturated.CoefC.vasquezBeggs(t, p, pb, yAPI, yg, rsb);
        return Oil.Live.VolumeFactor.glaso(t, p, pb, yAPI, yg, rs, rsb, coefC);
    }

    private double getCo(double t, double p, double yg, double yAPI, double rsb) {
        double pb = getPb(t, yg, yAPI, rsb);
        double rs = getRs(t, p, yg, yAPI, rsb);
        double bo = getBo(t, p, yg, yAPI, rsb);
        double dRsDP = Oil.Saturated.DerivativeRsWrtP.standing(t, p, yAPI, yg);
        double dBoDP = Oil.Saturated.derivativeBwrtP.glaso(t, yAPI, yg, rs, dRsDP);

        double tc = Gas.PseudoCriticalTemperature.sutton(yg, 0, 0, 0);
        double pc = Gas.PseudoCriticalPressure.sutton(yg, 0, 0, 0);
        double zFactor = Gas.ZFactor.dranchuk(Converter.fToR(t), tc, p, pc);
        double bg = Gas.VolumeFactor.internal(zFactor, Converter.fToR(t), p);

        return Oil.Live.Compressibility.vasquezBeggs(t, p, pb, yAPI, yg, bo, rsb, dBoDP, bg, dRsDP);
    }

    private double getRho(double t, double p, double yg, double yAPI, double rsb) {
        double rs = getRs(t, p, yg, yAPI, rsb);
        double bo = getBo(t, p, yg, yAPI, rsb);

        return Oil.Live.Density.internal(yAPI, yg, rs, bo);
    }

    private double getMu(double t, double p, double yg, double yAPI, double rsb) {
        double pb = getPb(t, yg, yAPI, rsb);
        double rs = getRs(t, p, yg, yAPI, rsb);

        return  Oil.Live.Viscosity.beal(t, p, pb, yAPI, rs, rsb);
    }

    private void initXY() {
        double minX = Converter.psiaToAtm(14.6959);
        double maxX = Converter.psiaToAtm(10050);
        double minY = Converter.fToC(32);
        double maxY = Converter.fToC(752);
        double stepX = (maxX - minX) / (pointsAmountRoot - 1);
        double stepY = (maxY - minY) / (pointsAmountRoot - 1);
        double currentX = minX;
        double currentY = minY;

        for (int i = 0; i < pointsAmountRoot; i++) {
            pX[i] = currentX;
            tY[i] = currentY;
            currentX += stepX;
            currentY += stepY;
        }
    }

    public void setPlotFragment(PlotFragment plotFragment) {
        this.plotFragment = plotFragment;
    }
}
