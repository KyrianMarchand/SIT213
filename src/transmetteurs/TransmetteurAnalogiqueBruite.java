package transmetteurs;

import java.util.Random;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * Cette classe représente un transmetteur analogique bruité qui reçoit des signaux analogiques
 * en entrée, ajoute du bruit gaussien blanc aux signaux, et les émet sous forme de signaux analogiques.
 * Le niveau de bruit est déterminé par le rapport signal-sur-bruit par bit (SNRpb) spécifié.
 */
public class TransmetteurAnalogiqueBruite extends Transmetteur<Float, Float> {
    float snrpb; // Rapport signal-sur-bruit par bit (SNRpb)
    int nbEch;   // Nombre d'échantillons par bit
    Information<Float> informationEmise = new Information<Float>();
    Information<Float> informationRecue = new Information<Float>();

    /**
     * Constructeur de la classe TransmetteurAnalogiqueBruite.
     *
     * @param snrpb Le rapport signal-sur-bruit par bit (SNRpb).
     * @param nbEch Le nombre d'échantillons par bit.
     */
    public TransmetteurAnalogiqueBruite(float snrpb, int nbEch) {
        super();
        this.snrpb = snrpb;
        this.nbEch = nbEch;
    }

    /**
     * Méthode pour recevoir des signaux analogiques en entrée.
     *
     * @param information Les signaux analogiques à recevoir.
     * @throws InformationNonConformeException Si les signaux reçus sont incorrects.
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        for (Float i : information) {
            informationRecue.add(i);
        }

        emettre();
    }

    /**
     * Méthode pour émettre les signaux analogiques bruités aux destinations connectées.
     *
     * @throws InformationNonConformeException Si les signaux émis sont incorrects.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        float puissanceSignal = 0f;
        float snrpbLin = (float) Math.pow(10, snrpb / 10);

        // Calcul de la puissance du signal
        for (float elmt : this.informationRecue) {
            puissanceSignal += Math.pow(elmt, 2);
        }
        puissanceSignal = puissanceSignal / this.informationRecue.nbElements();

        // Calcul de l'écart-type du bruit
        float sigma = (float) Math.sqrt((puissanceSignal * nbEch) / (2 * snrpbLin));

        // Ajout du bruit gaussien blanc aux signaux
        for (Float elmt : this.informationRecue) {
            float bruit;
            Random a1 = new Random();
            Random a2 = new Random();
            bruit = (float) (sigma * Math.sqrt(-2 * Math.log(1 - a1.nextDouble())) * Math.cos(2 * Math.PI * a2.nextDouble()));
            this.informationEmise.add(elmt + bruit);
        }

        // Émission des signaux bruités vers les destinations connectées
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }
}
