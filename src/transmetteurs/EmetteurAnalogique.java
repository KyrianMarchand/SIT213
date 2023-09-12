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
		
		if (!typeSignal.equals("RZ") && !typeSignal.equals("NRZ") && !typeSignal.equals("NRZT"))
			throw new InformationNonConformeException("Le type du signal est inconnue");
		
		if (typeSignal.equals("RZ") && minimumAmp != 0)
			throw new InformationNonConformeException("Minimum different de 0 pour RZ");
		
		this.informationEmise= new Information<Float>();
		this.informationRecue = new Information<Boolean>();
		
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
		int tierPeriode = (int) Math.ceil(nbEchantillon / 3);
		int reste = nbEchantillon % 3;

		// Création d'une copie du tableau d'informations
		Information<Boolean> tempArray = new Information<Boolean>();
		
		tempArray = informationRecue;

		for (int indiceInfo = 0; indiceInfo < informationRecue.nbElements(); indiceInfo++) {
		    Boolean bitCourant = tempArray.iemeElement(indiceInfo);

		    // Parcours de chaque échantillon
		    for (int indiceEchantillon = 0; indiceEchantillon < nbEchantillon; indiceEchantillon++) {
		        if (indiceEchantillon < tierPeriode) {
		            if (indiceInfo != 0 && bitCourant == tempArray.iemeElement(indiceInfo - 1)) {
		                // Cas où le bit est identique au précédent
		                informationEmise.add(bitCourant ? maximumAmp : minimumAmp);
		            } else {
		                // Cas où le bit change par rapport au précédent
		                float valeurEchantillon;

		                if (bitCourant) {
		                    valeurEchantillon = (maximumAmp / tierPeriode) * indiceEchantillon;
		                } else {
		                    valeurEchantillon = (minimumAmp / tierPeriode) * indiceEchantillon;
		                }

		                informationEmise.add(valeurEchantillon);
		            }
		        } else if (indiceEchantillon >= tierPeriode && indiceEchantillon <= 2 * tierPeriode + reste) {
		            // Cas où l'échantillon est dans la deuxième période
		            informationEmise.add(bitCourant ? maximumAmp : minimumAmp);
		        } else {
		            if (indiceInfo != informationRecue.nbElements() - 1 && bitCourant == tempArray.iemeElement(indiceInfo + 1)) {
		                // Cas où le bit est identique au suivant
		                informationEmise.add(bitCourant ? maximumAmp : minimumAmp);
		            } else {
		                // Cas où le bit change par rapport au suivant
		                float valeurEchantillon;

		                if (bitCourant) {
		                    valeurEchantillon = ((maximumAmp / tierPeriode) * (-indiceEchantillon + reste)) + (3 * maximumAmp);
		                } else {
		                    valeurEchantillon = ((minimumAmp / tierPeriode) * (-indiceEchantillon + reste)) + (3 * minimumAmp);
		                }

		                informationEmise.add(valeurEchantillon);
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
		for (Boolean i : information) {
			informationRecue.add(i);
		}
		emettre();
		
		
		
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
		//System.out.println(this.informationRecue);
		for (DestinationInterface <Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
		}
	}
}
