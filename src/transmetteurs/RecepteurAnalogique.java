package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class RecepteurAnalogique extends Transmetteur<Float, Boolean> {
	float seuil;
	int nbEchantillon;
	
	public RecepteurAnalogique(float minimumAmp, float maximumAmp, int nbEchantillon) {
		super();
		this.seuil = (minimumAmp + maximumAmp)/2;
		this.nbEchantillon = nbEchantillon;
	}

	@Override
	public void recevoir(Information<Float> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		int compteur = (int) nbEchantillon/2;
		Information<Boolean> infoLogique = new Information<Boolean>();
		for (int i =0; i<(information.nbElements()/nbEchantillon-1); i++) {
			if(information.iemeElement(i) >= seuil) {
				infoLogique.add(true);
			}
			else {
				infoLogique.add(false);
			}
		}
	}

	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (DestinationInterface <Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}

}
