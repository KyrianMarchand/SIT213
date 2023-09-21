package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * Cette classe représente un récepteur analogique qui reçoit des signaux analogiques
 * en entrée, les décode en signaux binaires (booléens) en fonction d'un seuil,
 * et les émet sous forme de signaux booléens.
 * 
 */
public class RecepteurAnalogique extends Transmetteur<Float, Boolean> {
	float seuil;
	int nbEchantillon;
	
    /**
     * Constructeur de la classe RecepteurAnalogique.
     * 
     * @param minimumAmp   L'amplitude minimale du signal.
     * @param maximumAmp   L'amplitude maximale du signal.
     * @param nbEchantillon Le nombre d'échantillons par bit.
     */
	public RecepteurAnalogique(float minimumAmp, float maximumAmp, int nbEchantillon) {
		super();
		this.informationEmise = new Information<Boolean>();
		this.informationRecue = new Information<Float>();
		this.seuil = (minimumAmp + maximumAmp)/2;
		this.nbEchantillon = nbEchantillon;
	}

    /**
     * Méthode pour recevoir des signaux analogiques en entrée.
     * 
     * @param information Les signaux analogiques à recevoir.
     * @throws InformationNonConformeException Si les signaux reçus sont incorrects.
     */
	@Override
	public void recevoir(Information<Float> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (Float i : information) {
			informationRecue.add(i);
		}
		decodage(this.informationRecue);
		emettre();
		
	}
	
	/**
     * Méthode de décodage des signaux analogiques en signaux booléens en fonction
     * du seuil.
     * 
     * @param information Les signaux analogiques à décoder.
     * @throws InformationNonConformeException Si les signaux reçus sont incorrects.
     */
	public void decodage(Information<Float> information) throws InformationNonConformeException {
	    for (int compteur = (int) nbEchantillon/2; compteur<(information.nbElements()); compteur+=nbEchantillon) {
	        if(information.iemeElement(compteur) >= seuil) {
	            this.informationEmise.add(true);
	        }
	        else {
	            this.informationEmise.add(false);
	        }
	    }
	    
	}

	
	/**
     * Méthode pour émettre les signaux booléens aux destinations connectées.
     * 
     * @throws InformationNonConformeException Si les signaux émis sont incorrects.
     */
	@Override
	public void emettre() throws InformationNonConformeException {
		
		// TODO Auto-generated method stub
		for (DestinationInterface <Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}

}
