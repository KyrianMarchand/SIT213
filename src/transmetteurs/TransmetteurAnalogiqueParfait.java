package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class TransmetteurAnalogiqueParfait extends Transmetteur<Float,Float>{

	@Override
	public void recevoir(Information<Float> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (Float i : information) {
			informationRecue.add(i);
		}
		emettre();
	}

	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		this.informationEmise = informationRecue;
		for (DestinationInterface <Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}

}
