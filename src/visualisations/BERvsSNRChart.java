package visualisations;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat; // Importez DecimalFormat pour formater les étiquettes en notation scientifique
import simulateur.Simulateur;

public class BERvsSNRChart {

    private static double[] snrValues = new double[51];
    private static double[] berValues1 = new double[51];
    private static double[] berValues2 = new double[51];
    private static double[] berValues3 = new double[51];
    private static String[] signalTypes = {"RZ", "NRZ", "NRZT"};
    private static String[] xLabels = {"-25", "-20", "-15", "-10", "-5", "0", "5", "10", "15", "20", "25"};
    private static double[] yValues = {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5}; // Puissances de 10 pour l'axe des ordonnées
    private static String[] yLabels = new String[yValues.length]; // Étiquettes pour l'axe des ordonnées

    public static void main(String[] args) throws Exception {
        values();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Taux d'erreur binaire en fonction du SNR en dB");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(new ChartPanel());
            frame.setVisible(true);
        });
    }

    public static void values() throws Exception {
        double maxSnr = 0;
        double maxBer = 0;

        for (int i = -25; i <= 25; i++) { // Couvrir la plage de -25 à 25 inclus
            String[] str1 = {"-mess", "100000", "-snrpb", String.valueOf(i), "-form", "RZ", "-nbEch", "30", "-ampl", "0", "5"};
            String[] str2 = {"-mess", "100000", "-snrpb", String.valueOf(i), "-form", "NRZ", "-nbEch", "30", "-ampl", "0", "5"};
            String[] str3 = {"-mess", "100000", "-snrpb", String.valueOf(i), "-form", "NRZT", "-nbEch", "30", "-ampl", "0", "5"};

            Simulateur sim1 = new Simulateur(str1);
            Simulateur sim2 = new Simulateur(str2);
            Simulateur sim3 = new Simulateur(str3);

            sim1.execute();
            sim2.execute();
            sim3.execute();

            snrValues[i + 25] = i; // Stockez la valeur SNR au lieu de l'indice i
            berValues1[i + 25] = sim1.calculTauxErreurBinaire();
            berValues2[i + 25] = sim2.calculTauxErreurBinaire();
            berValues3[i + 25] = sim3.calculTauxErreurBinaire();

            maxSnr = Math.max(maxSnr, snrValues[i + 25]);
            maxBer = Math.max(maxBer, berValues1[i + 25]);
            maxBer = Math.max(maxBer, berValues2[i + 25]);
            maxBer = Math.max(maxBer, berValues3[i + 25]);
        }

        // Normalisation des valeurs de SNR
        for (int i = 0; i < 51; i++) {
            snrValues[i] = (snrValues[i] + 25) / 50.0;
        }

        // Normalisation des valeurs de BER
        for (int i = 0; i < 51; i++) {
            berValues1[i] /= maxBer;
            berValues2[i] /= maxBer;
            berValues3[i] /= maxBer;
        }

        // Créez des étiquettes pour l'axe des ordonnées en format de notation scientifique
        DecimalFormat formatter = new DecimalFormat("0.0E0");
        for (int i = 0; i < yValues.length; i++) {
            yLabels[i] = formatter.format(Math.pow(10, yValues[i]));
        }
    }

    static class ChartPanel extends JPanel {
        private List<Color> colors = new ArrayList<>();

        public ChartPanel() {
            colors.add(Color.RED);
            colors.add(Color.BLUE);
            colors.add(Color.GREEN);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int padding = 50;
            int width = getWidth();
            int height = getHeight();

            // Dessinez les axes
            g.drawLine(padding, padding, padding, height - padding);
            g.drawLine(padding, height - padding, width - padding, height - padding);

            // Dessinez les lignes du graphique pour les trois simulations
            int numPoints = snrValues.length;

            for (int i = 1; i < numPoints; i++) {
                int x1 = (int) (snrValues[i - 1] * (width - 2 * padding)) + padding;
                int y1_1 = height - (int) (berValues1[i - 1] * (height - 2 * padding)) - padding;
                int y1_2 = height - (int) (berValues2[i - 1] * (height - 2 * padding)) - padding;
                int y1_3 = height - (int) (berValues3[i - 1] * (height - 2 * padding)) - padding;

                int x2 = (int) (snrValues[i] * (width - 2 * padding)) + padding;
                int y2_1 = height - (int) (berValues1[i] * (height - 2 * padding)) - padding;
                int y2_2 = height - (int) (berValues2[i] * (height - 2 * padding)) - padding;
                int y2_3 = height - (int) (berValues3[i] * (height - 2 * padding)) - padding;

                g.setColor(colors.get(0));
                g.drawLine(x1, y1_1, x2, y2_1);
                g.setColor(colors.get(1));
                g.drawLine(x1, y1_2, x2, y2_2);
                g.setColor(colors.get(2));
                g.drawLine(x1, y1_3, x2, y2_3);
            }

            // Ajoutez des légendes aux axes
            g.drawString("SNR (dB)", width / 2 - 20, height - padding + 30);
            g.drawString("BER", padding - 40, height / 2);

            // Ajoutez un titre au graphique
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Graphique de Taux d'erreur binaire en fonction du SNR en dB", width / 4, padding - 20);

            // Ajoutez des légendes pour les types de signal
            int legendX = width - 150;
            int legendY = padding;
            for (int i = 0; i < signalTypes.length; i++) {
                g.setColor(colors.get(i));
                g.fillRect(legendX, legendY + i * 20, 15, 15);
                g.setColor(Color.BLACK);
                g.drawString(signalTypes[i], legendX + 20, legendY + i * 20 + 12);
            }

            // Ajoutez des légendes pour l'échelle en abscisse
            int numLabelsX = xLabels.length;
            int stepX = (width - 2 * padding) / (numLabelsX - 1);
            for (int i = 0; i < numLabelsX; i++) {
                int x = padding + i * stepX;
                int y = height - padding + 15;
                g.drawString(xLabels[i], x - 10, y);
            }

            // Ajoutez des légendes pour l'échelle en ordonnée
            int numLabelsY = yLabels.length;
            int stepY = (height - 2 * padding) / (numLabelsY - 1);
            for (int i = 0; i < numLabelsY; i++) {
                int x = padding - 40;
                int y = height - padding - i * stepY;
                g.drawString(yLabels[i], x, y);
            }
        }
    }
}

