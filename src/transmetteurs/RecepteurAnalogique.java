package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class RecepteurAnalogique extends Transmetteur<Float, Boolean> {
	float seuil;
	int nbEchantillon;
	
	public RecepteurAnalogique(float minimumAmp, float maximumAmp, int nbEchantillon) {
		super();
		this.informationEmise = new Information<Boolean>();
		this.informationRecue = new Information<Float>();
		this.seuil = (minimumAmp + maximumAmp)/2;
		this.nbEchantillon = nbEchantillon;
	}

	@Override
	public void recevoir(Information<Float> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (Float i : information) {
			informationRecue.add(i);
		}
		decodage(this.informationRecue);
		emettre();
		
		
		
	}
	
	public void decodage(Information<Float> information) throws InformationNonConformeException {
		for (int compteur = (int) nbEchantillon/2; compteur<(information.nbElements()); compteur+=nbEchantillon) {
			this.informationRecue.add(information.iemeElement(compteur));
			if(information.iemeElement(compteur) >= seuil) {
				this.informationEmise.add(true);
			}
			else {
				this.informationEmise.add(false);
			}
		}
		emettre();
	}

	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (DestinationInterface <Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}

}
