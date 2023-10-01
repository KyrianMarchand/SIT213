package transmetteurs;

import java.util.Collection;
import java.util.Random;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import visualisations.GenerationCSV;

/**
 * Cette classe représente un transmetteur analogique bruité qui reçoit des signaux analogiques
 * en entrée, ajoute du bruit gaussien blanc aux signaux, et les émet sous forme de signaux analogiques.
 * Le niveau de bruit est déterminé par le rapport signal-sur-bruit par bit (SNRpb) spécifié.
 */
public class TransmetteurAnalogiqueBruite extends Transmetteur<Float, Float> {
    private float snrpb; // Rapport signal-sur-bruit par bit (SNRpb)
    private int nbEch;   // Nombre d'échantillons par bit
    private float[] attenuation;
    private int [] dephasage;
    private Integer seed;
    private Information<Float> informationEmise = new Information<Float>();
    private Information<Float> informationRecue = new Information<Float>();
    private Information<Float> bruit = new Information<Float>();

    /**
     * Constructeur de la classe TransmetteurAnalogiqueBruite.
     *
     * @param snrpb Le rapport signal-sur-bruit par bit (SNRpb).
     * @param nbEch Le nombre d'échantillons par bit.
     * @param attenuation Les atténuation des différents trajets
     * @param dephasage Les déphasage des différents trajets
     * @param seed La seed pour le pseudo aléatoire
     */
    public TransmetteurAnalogiqueBruite(float snrpb, int nbEch, float[] attenuation, int [] dephasage, Integer seed) {
        super();
        this.snrpb = snrpb;
        this.nbEch = nbEch;
        this.attenuation = attenuation;
        this.dephasage = dephasage;
        this.seed = seed;
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
    	Information<Float> info = miseEnFormeSignalPropre();
    	
    	
        float puissanceSignal = 0f;
        float snrpbLin = (float) Math.pow(10, snrpb / 10);

        // Calcul de la puissance du signal
        for (float elmt : info) {
            puissanceSignal += Math.pow(elmt, 2);
        }
        puissanceSignal = puissanceSignal / info.nbElements();

        // Calcul de l'écart-type du bruit
        float sigma = (float) Math.sqrt((puissanceSignal * nbEch) / (2 * snrpbLin));
        
        Random a1;
        Random a2;
        if(this.seed != null) {
        	 a1 = new Random(this.seed);
             a2 = new Random(this.seed);
        }
        else {
        	 a1 = new Random();
             a2 = new Random();
        }
        
        // Ajout du bruit gaussien blanc aux signaux
        for (Float elmt : info) {
        	
            float bruit;
            bruit = (float) (sigma * Math.sqrt(-2 * Math.log(1 - a1.nextDouble())) * Math.cos(2 * Math.PI * a2.nextDouble()));
            this.informationEmise.add(elmt + bruit);
            this.bruit.add(bruit);
        }
        GenerationCSV.generation(this.bruit);

        // Émission des signaux bruités vers les destinations connectées
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }
    
    /**
     * Méthode pour créer un trajet avec un déphasage et une atténuation donnés.
     *
     * @param dephasage   Le déphasage à appliquer au trajet.
     * @param attenuation L'atténuation à appliquer au trajet.
     * @return Un nouvel objet Information<Float> représentant le trajet.
     */
    public Information<Float> creationTrajet(int dephasage, float attenuation) {
    	Information<Float> trajetNouveau = new Information<Float>();
    	for (int i =0; i<dephasage; i++) {
    		trajetNouveau.add(0f);
    	}
    	for (Float elmt : this.informationRecue) {
    		trajetNouveau.add(elmt*attenuation);
    	}
    	for (int i = trajetNouveau.nbElements(); i < maxTrajetLength(); i++) {
    		trajetNouveau.add(0f);
    	}
    	
    	return trajetNouveau;
    }
    
    /**
     * Méthode pour mettre en forme le signal propre en combinant différents trajets.
     *
     * @return Un objet Information représentant le signal propre.
     */
    public Information<Float> miseEnFormeSignalPropre(){
    	Information<Float> info = new Information<Float>();
    	Information<Float> trajetDirect = creationTrajet(0,1);
    	Information<Float> trajet1 = creationTrajet(dephasage[0], attenuation[0]);
    	Information<Float> trajet2 = creationTrajet(dephasage[1], attenuation[1]);
    	Information<Float> trajet3 = creationTrajet(dephasage[2], attenuation[2]);
    	Information<Float> trajet4 = creationTrajet(dephasage[3], attenuation[3]);
    	Information<Float> trajet5 = creationTrajet(dephasage[4], attenuation[4]);
    	for (int i = 0; i<(this.informationRecue.nbElements() + maxDephasage()); i++) {
    		info.add(trajet1.iemeElement(i)+ trajet2.iemeElement(i) + trajet3.iemeElement(i) + trajet4.iemeElement(i) + trajet5.iemeElement(i) + trajetDirect.iemeElement(i));
    	}
    	return info;
    	
    }
    
    /**
     * Méthode pour calculer la longueur maximale du déphasage.
     *
     * @return La longueur maximale du déphasage.
     */
    public int maxDephasage() {
    	int max = 0;
    	for (int elmt : this.dephasage) {
    		if (elmt>max) {
    			max=elmt;
    		}
    	}
    	return max + this.informationEmise.nbElements();
    }
    
    /**
     * Méthode pour calculer la longueur maximale du trajet.
     *
     * @return La longueur maximale du trajet.
     */
    public int maxTrajetLength() {
        int maxLength = this.informationRecue.nbElements();

        for (int i = 0; i < dephasage.length; i++) {
            if (dephasage[i] + informationRecue.nbElements() > maxLength) {
                maxLength = dephasage[i] + informationRecue.nbElements();
            }
        }

        return maxLength;
    }
}
