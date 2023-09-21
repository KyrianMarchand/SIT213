package destinations;

import information.Information;
import information.InformationNonConformeException;

public class DestinationFinale extends Destination <Boolean>{
	/**
	 * Constructeur de DestinationFinale() qui ajoute l'attribut information recue 
	 */
	public DestinationFinale() {
		super();
		informationRecue = new Information<Boolean>();
	}
	
	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub

		for (Boolean i : information) {
			//System.out.println(this.informationRecue.nbElements());
			this.informationRecue.add(i);
		}
		//System.out.println(informationRecue);
	}
}
