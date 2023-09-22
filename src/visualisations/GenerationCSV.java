package visualisations;

import information.Information;

public class GenerationCSV {

    public static void generation(Information<Float> information) {

        Information<String> csv = new Information <String>();
        csv.add("Valeurs");

        for (float val : information) {
            csv.add(String.format("%.1f", val));
        }
        try {
            java.io.FileWriter fw = new java.io.FileWriter("ValeursBruit.csv");
            for (int i = 0; i < csv.nbElements(); i++) {
                fw.write(csv.iemeElement(i) + "\n");
            }
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
