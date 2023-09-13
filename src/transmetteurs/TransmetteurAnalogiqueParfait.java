package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
/**
 * Cette classe représente un transmetteur analogique parfait qui reçoit des
 * signaux analogiques en entrée et les émet tels qu'ils sont, sans aucune
 * modification.
 * 
 */
public class TransmetteurAnalogiqueParfait extends Transmetteur<Float,Float> {
	
	/**
     * Constructeur de la classe TransmetteurAnalogiqueParfait.
     * Il initialise les informations reçues en tant que nouvelle instance d'Information Float.
     */
	public TransmetteurAnalogiqueParfait() {
		super();
		this.informationRecue = new Information<Float>();
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
		emettre();
	}
	
	/**
     * Méthode pour émettre les signaux analogiques aux destinations connectées
     * sans aucune modification.
     * 
     * @throws InformationNonConformeException Si les signaux émis sont incorrects.
     */
	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		this.informationEmise = informationRecue;
		for (DestinationInterface <Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}

}
