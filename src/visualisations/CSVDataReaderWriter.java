package visualisations;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import simulateur.Simulateur;

public class CSVDataReaderWriter {
    public static void main(String[] args) throws Exception {
        String outputFilePath = "output.csv";

        List<DataPoint> dataPoints = new ArrayList<>();

        for (float i = 0; i <= 1; i+=0.1) {
        	System.out.println(i);
            String[] str1 = {"-mess", "100000", "-snrpb", "8", "-form", "RZ", "-nbEch", "30", "-ampl", "0", "5", "-ti", "25", String.valueOf(i)};
            String[] str2 = {"-mess", "100000", "-snrpb", "8", "-form", "NRZ", "-nbEch", "30", "-ampl", "-5", "5", "-ti", "25", String.valueOf(i)};
            String[] str3 = {"-mess", "100000", "-snrpb", "8", "-form", "NRZT", "-nbEch", "30", "-ampl", "-5", "5", "-ti", "25", String.valueOf(i)};
            
            System.out.println("RZ");
            Simulateur sim1 = new Simulateur(str1);
            System.out.println("NRZ");
            Simulateur sim2 = new Simulateur(str2);
            System.out.println("NRZT");
            Simulateur sim3 = new Simulateur(str3);

            sim1.execute();
            sim2.execute();
            sim3.execute();

            double snr = i;
            double ber1 = sim1.calculTauxErreurBinaire();
            double ber2 = sim2.calculTauxErreurBinaire();
            double ber3 = sim3.calculTauxErreurBinaire();

            dataPoints.add(new DataPoint(snr, ber1, ber2, ber3));
        }

        writeCSV(dataPoints, outputFilePath);
    }

    public static void writeCSV(List<DataPoint> dataPoints, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("SNR (dB),BER (RZ),BER (NRZ),BER (NRZT)\n"); // Écrire l'en-tête

            for (DataPoint dataPoint : dataPoints) {
                writer.write(dataPoint.getSnr() + "," + dataPoint.getBer1() + "," + dataPoint.getBer2() + "," + dataPoint.getBer3() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class DataPoint {
        private double snr;
        private double ber1;
        private double ber2;
        private double ber3;

        public DataPoint(double snr, double ber1, double ber2, double ber3) {
            this.snr = snr;
            this.ber1 = ber1;
            this.ber2 = ber2;
            this.ber3 = ber3;
        }

        public double getSnr() {
            return snr;
        }

        public double getBer1() {
            return ber1;
        }

        public double getBer2() {
            return ber2;
        }

        public double getBer3() {
            return ber3;
        }
    }
}

