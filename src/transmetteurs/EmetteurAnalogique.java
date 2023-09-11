package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class EmetteurAnalogique extends Transmetteur<Boolean, Float>{
	private float minimumAmp = 0f;
	private float maximumAmp = 0f;
	private int nbEchantillon = 0;
	private String typeSignal = "";
	
	public EmetteurAnalogique(float minimumAmp, float maximumAmp, int nbEchantillon, String typeSignal) throws InformationNonConformeException {
		super();
		
		if (!typeSignal.equals("RZ") || !typeSignal.equals("NRZ") || !typeSignal.equals("NRZT"))
			throw new InformationNonConformeException("Le type du signal est inconnue");
		
		if (typeSignal.equals("RZ") && minimumAmp != 0)
			throw new InformationNonConformeException("Minimum different de 0 pour RZ");
		
		
		
		this.minimumAmp = minimumAmp;
		this.maximumAmp = maximumAmp;
		this.nbEchantillon = nbEchantillon;
		this.typeSignal = typeSignal;
		
	}
	
	public void translationNRZ(Information<Boolean> info) {
		for(Boolean tempBool : informationRecue) {
			for(int i=0;i<nbEchantillon; i++) {
				if(tempBool) {
					informationEmise.add(maximumAmp);
				}
				else {
					informationEmise.add(minimumAmp);
				}
			}
		}
	}
	
	public void translationNRZT(Information<Boolean> info) {
		// Calcul de la période tierce
		int tierPeriodeFloat = (int) Math.ceil(nbEchantillon / 3);
		int diff = nbEchantillon % 3;

		// Création d'une copie du tableau d'informations
		Information<Boolean> tempArray = new Information<Boolean>();
		
		tempArray = informationRecue;

		// Initialisation d'un tableau pour stocker les informations émises
		Information<Float> informationEmise = new Information<Float>();

		// Parcours des éléments du tableau d'informations
		for (int j = 0; j < tempArray.nbElements(); j++) {
		    Boolean tempBool = tempArray.iemeElement(j);

		    // Parcours des échantillons
		    for (int i = 0; i < nbEchantillon; i++) {
		        if (i < tierPeriodeFloat) {
		            if (j != 0 && tempArray.iemeElement(j) == tempArray.iemeElement(j - 1)) {
		                informationEmise.add(tempBool ? maximumAmp : minimumAmp);
		            } else {
		                informationEmise.add(tempBool ? (maximumAmp / tierPeriodeFloat) * i : (minimumAmp / tierPeriodeFloat) * i);
		            }
		        } else if (i >= tierPeriodeFloat && i <= 2 * tierPeriodeFloat + diff) {
		            informationEmise.add(tempBool ? maximumAmp : minimumAmp);
		        } else {
		            if (j != tempArray.nbElements() - 1 && tempArray.iemeElement(j) == tempArray.iemeElement(j + 1)) {
		                informationEmise.add(tempBool ? maximumAmp : minimumAmp);
		            } else {
		                informationEmise.add(tempBool ? ((maximumAmp / tierPeriodeFloat) * (-i + diff)) + (3 * maximumAmp) : ((minimumAmp / tierPeriodeFloat) * (-i + diff)) + (3 * minimumAmp));
		            }
		        }
		    }
		}

		// Ajout de la dernière valeur
		informationEmise.add((maximumAmp + minimumAmp) / 2);

	}
	
	public void translationRZ(Information<Boolean> info) {
		float tierPeriode = nbEchantillon/3;
		for(Boolean tempBool : informationRecue) {
			for(int i=0;i<nbEchantillon; i++) {
				if (i<tierPeriode || i>2*tierPeriode) {
					informationEmise.add(minimumAmp);
				} else {
					if(tempBool) {
						informationEmise.add(maximumAmp);
					}
					else {
						informationEmise.add(minimumAmp);
					}
				}				
			}
		}
	}

	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (DestinationInterface <Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
		
		
		
	}

	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		switch (typeSignal){
		case "NRZ" :
			translationNRZ(this.informationRecue);
			break;
		
		case "RZ" :
			translationRZ(this.informationRecue);
			break;
			
		case "NRZT" :
			translationNRZT(this.informationRecue);
			break;
		}
	}
}
