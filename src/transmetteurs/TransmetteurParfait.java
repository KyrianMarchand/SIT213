package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class TransmetteurParfait  extends Transmetteur<Boolean,Boolean>{
	public TransmetteurParfait(){
		super();
		informationRecue =  new Information<Boolean>();
		informationEmise = new Information<Boolean>();
	}

	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (Boolean i : information) {
			informationRecue.add(i);
		}
		emettre();
	}

	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		this.informationEmise = informationRecue;
		for (DestinationInterface <Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}
}
