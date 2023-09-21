package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * Cette classe représente un émetteur analogique qui prend des informations
 * booléennes en entrée et les transforme en signaux analogiques en fonction
 * du type de signal spécifié.
 * 
 */
public class EmetteurAnalogique extends Transmetteur<Boolean, Float> {
    private float minimumAmp = 0f;
    private float maximumAmp = 0f;
    private int nbEchantillon = 0;
    private String typeSignal = "";

    /**
     * Constructeur de la classe EmetteurAnalogique.
     * 
     * @param minimumAmp     L'amplitude minimale du signal.
     * @param maximumAmp     L'amplitude maximale du signal.
     * @param nbEchantillon  Le nombre d'échantillons par bit.
     * @param typeSignal     Le type de signal ("RZ", "NRZ" ou "NRZT").
     * @throws InformationNonConformeException Si le type du signal est inconnu
     *                                          ou si le minimumAmp est différent
     *                                          de 0 pour le signal "RZ".
     */
    public EmetteurAnalogique(float minimumAmp, float maximumAmp, int nbEchantillon, String typeSignal)
            throws InformationNonConformeException {
        super();

        if (!typeSignal.equals("RZ") && !typeSignal.equals("NRZ") && !typeSignal.equals("NRZT"))
            throw new InformationNonConformeException("Le type du signal est inconnu");

        if (typeSignal.equals("RZ") && minimumAmp != 0)
            throw new InformationNonConformeException("Minimum différent de 0 pour RZ");

        this.minimumAmp = minimumAmp;
        this.maximumAmp = maximumAmp;
        this.nbEchantillon = nbEchantillon;
        this.typeSignal = typeSignal;
        this.informationRecue = new Information<Boolean>();
        this.informationEmise = new Information<Float>();
    }

    /**
     * Méthode pour recevoir des informations booléennes en entrée.
     * 
     * @param information Les informations booléennes à recevoir.
     * @throws InformationNonConformeException Si les informations reçues sont
     *                                          incorrectes.
     */
    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        for (Boolean bit : information) {
            informationRecue.add(bit);
        }
        emettre();
    }

    /**
     * Méthode pour émettre le signal transformé aux destinations connectées.
     * 
     * @throws InformationNonConformeException Si les informations émises sont
     *                                          incorrectes.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        switch (typeSignal) {
        case "NRZ":
            translationNRZ();
            break;

        case "RZ":
            translationRZ();
            break;

        case "NRZT":
            translationNRZT();
            break;
        }

        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    /**
     * Méthode de transformation du signal en NRZ.
     * 
     * @throws InformationNonConformeException Si l'information reçue est incorrecte.
     */
    private void translationNRZ() throws InformationNonConformeException {
        for (Boolean bitCourant : informationRecue) {
            if (bitCourant == null)
                throw new InformationNonConformeException();

            for (int i = 0; i < nbEchantillon; i++) {
                informationEmise.add(bitCourant ? maximumAmp : minimumAmp);
            }
        }
    }

    /**
     * Méthode de transformation du signal en NRZT.
     * 
     * @throws InformationNonConformeException Si l'information reçue est incorrecte.
     */
    private void translationNRZT() throws InformationNonConformeException {
        int tierPeriode = (int) Math.ceil(nbEchantillon / 3);
        int reste = nbEchantillon % 3;

        // Création d'une copie de l'information reçue pour éviter la modification de la liste originale
        Information<Boolean> tempInfo = new Information<Boolean>();
        tempInfo = this.informationRecue;

        for (int indiceElementCourant = 0; indiceElementCourant < tempInfo.nbElements(); indiceElementCourant++) {
            Boolean bitCourant = tempInfo.iemeElement(indiceElementCourant);

            if (bitCourant == null)
                throw new InformationNonConformeException();

            for (int indiceEchantillon = 0; indiceEchantillon < nbEchantillon; indiceEchantillon++) {
                float valeurEchantillon;

                if (indiceEchantillon < tierPeriode) {
                    Boolean bitPrecedent = (indiceElementCourant > 0)
                            ? tempInfo.iemeElement(indiceElementCourant - 1)
                            : null;

                    if (bitPrecedent != null && bitPrecedent.equals(bitCourant)) {
                        valeurEchantillon = bitCourant ? maximumAmp : minimumAmp;
                    } else {
                        valeurEchantillon = bitCourant ? ((maximumAmp / tierPeriode) * indiceEchantillon)
                                : ((minimumAmp / tierPeriode) * indiceEchantillon);
                    }
                } else if (indiceEchantillon >= tierPeriode && indiceEchantillon <= 2 * tierPeriode + reste) {
                    valeurEchantillon = bitCourant ? maximumAmp : minimumAmp;
                } else {
                    Boolean bitSuivant = (indiceElementCourant < tempInfo.nbElements() - 1)
                            ? tempInfo.iemeElement(indiceElementCourant + 1)
                            : null;

                    if (bitSuivant != null && bitSuivant.equals(bitCourant)) {
                        valeurEchantillon = bitCourant ? maximumAmp : minimumAmp;
                    } else {
                        valeurEchantillon = bitCourant
                                ? ((maximumAmp / tierPeriode) * (-indiceEchantillon + reste) + (3 * maximumAmp))
                                : ((minimumAmp / tierPeriode) * (-indiceEchantillon + reste) + (3 * minimumAmp));
                    }
                }
                informationEmise.add(valeurEchantillon);
            }
        }

        informationEmise.add(informationEmise.iemeElement(0));
    }

    /**
     * Méthode de transformation du signal en RZ.
     * 
     * @throws InformationNonConformeException Si l'information reçue est incorrecte.
     */
    private void translationRZ() throws InformationNonConformeException {
        float tierPeriode = nbEchantillon / 3;
        for (Boolean bitCourant : informationRecue) {
            if (bitCourant == null)
                throw new InformationNonConformeException();

            for (int i = 0; i < nbEchantillon; i++) {
                if (i < tierPeriode || i >= 2 * tierPeriode) {
                    informationEmise.add(minimumAmp);
                } else {
                    informationEmise.add(bitCourant ? maximumAmp : minimumAmp);
                }
            }
        }
    }
}
