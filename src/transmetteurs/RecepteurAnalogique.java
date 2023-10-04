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
	String form;
	boolean codeur;
	
    /**
     * Constructeur de la classe RecepteurAnalogique.
     * 
     * @param minimumAmp   L'amplitude minimale du signal.
     * @param maximumAmp   L'amplitude maximale du signal.
     * @param nbEchantillon Le nombre d'échantillons par bit.
     * @param form La forme du signal
     */
	public RecepteurAnalogique(float minimumAmp, float maximumAmp, int nbEchantillon, String form, boolean codeur) {
		super();
		this.informationEmise = new Information<Boolean>();
		this.informationRecue = new Information<Float>();
		this.seuil = (minimumAmp + maximumAmp)/2;
		this.nbEchantillon = nbEchantillon;
		this.form = form;
		this.codeur = codeur;
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
     */
	
	public Information<Boolean> decodage(Information<Float> information) {
		Information<Boolean> infoBoolean = new Information<Boolean>();
		if (this.form.equals("NRZ")) {
			
			for (int compteur = 0 ; compteur<information.nbElements() - nbEchantillon/2; compteur+=nbEchantillon){
				float moyenne=0f;
				for (int elmt=0 ; elmt < nbEchantillon; elmt++) {
					moyenne += information.iemeElement(compteur + elmt);
				}
				moyenne = moyenne/nbEchantillon;
				if(moyenne >= seuil) {
					infoBoolean.add(true);
		        }
		        else {
		        	infoBoolean.add(false);
		        }
			}
			
		}
		else {
			int tierPeriode = (int) Math.ceil(nbEchantillon / 3);
			//System.out.println(this.informationRecue.nbElements()+ "reception");
			for (int compteur = tierPeriode ; compteur<information.nbElements()  - nbEchantillon/2 ; compteur+=nbEchantillon){
				float moyenne=0f;
				for (int elmt=0 ; elmt < tierPeriode; elmt++) {
					moyenne += information.iemeElement(compteur + elmt);
				}
				moyenne = moyenne/tierPeriode;
				if(moyenne >= seuil) {
					infoBoolean.add(true);
		        }
		        else {
		        	infoBoolean.add(false);
		        }
			}
		}
		return infoBoolean;
	}
	
	public Information<Boolean> decodeurCanal(Information<Boolean> info){
		Information<Boolean> infoDecodee = new Information<Boolean>();
		for (int symbole = 0; symbole<info.nbElements()-2; symbole+=3) {
			if((info.iemeElement(symbole) && !info.iemeElement(symbole+1) && info.iemeElement(symbole+2)) 
					|| (!info.iemeElement(symbole) && !info.iemeElement(symbole+1) && info.iemeElement(symbole+2))
					|| (info.iemeElement(symbole) && info.iemeElement(symbole+1) && info.iemeElement(symbole+2))
					|| (info.iemeElement(symbole) && !info.iemeElement(symbole+1) && !info.iemeElement(symbole+2))) {
				infoDecodee.add(true);
			}
			else if((!info.iemeElement(symbole) && info.iemeElement(symbole+1) && !info.iemeElement(symbole+2))
					|| (info.iemeElement(symbole) && info.iemeElement(symbole+1) && !info.iemeElement(symbole+2))
					|| (!info.iemeElement(symbole) && !info.iemeElement(symbole+1) && !info.iemeElement(symbole+2))
					|| (!info.iemeElement(symbole) && info.iemeElement(symbole+1) && info.iemeElement(symbole+2))) {
				infoDecodee.add(false);
			}
		}
		return infoDecodee;
	}

	
	/**
     * Méthode pour émettre les signaux booléens aux destinations connectées.
     * 
     * @throws InformationNonConformeException Si les signaux émis sont incorrects.
     */
	@Override
	public void emettre() throws InformationNonConformeException {
		if (!codeur) {
			this.informationEmise = decodage(this.informationRecue);
		}
		else {
			this.informationEmise = decodeurCanal(decodage(this.informationRecue));
		}
		
		// TODO Auto-generated method stub
		for (DestinationInterface <Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}

}
