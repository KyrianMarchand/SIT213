package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * Cette classe représente un émetteur analogique qui prend des informations
 * booléennes en entrée et les transforme en signaux analogiques en fonction
 * du type de signal spécifié.
 * 
 */
public class EmetteurAnalogique extends Transmetteur<Boolean, Float>{
	private float minimumAmp = 0f;
	private float maximumAmp = 0f;
	private int nbEchantillon = 0;
	private String typeSignal = "";
	
	/**
     * Constructeur de la classe EmetteurAnalogique.
     * 
     * @param minimumAmp     L'amplitude minimale du signal.
     * @param maximumAmp     L'amplitude maximale du signal.
     * @param nbEchantillon  Le nombre d'échantillons par bit.
     * @param typeSignal     Le type de signal ("RZ", "NRZ" ou "NRZT").
     * @throws InformationNonConformeException Si le type de signal est inconnu
     *                                          ou si le minimumAmp est différent
     *                                          de 0 pour le signal "RZ".
     */
	public EmetteurAnalogique(float minimumAmp, float maximumAmp, int nbEchantillon, String typeSignal) throws InformationNonConformeException {
		super();
		
		if (!typeSignal.equals("RZ") && !typeSignal.equals("NRZ") && !typeSignal.equals("NRZT"))
			throw new InformationNonConformeException("Le type du signal est inconnue");
		
		if (typeSignal.equals("RZ") && minimumAmp != 0)
			throw new InformationNonConformeException("Minimum different de 0 pour RZ");
		
		//this.informationEmise= new Information<Float>();
		//this.informationRecue = new Information<Boolean>();
		
		this.minimumAmp = minimumAmp;
		this.maximumAmp = maximumAmp;
		this.nbEchantillon = nbEchantillon;
		this.typeSignal = typeSignal;
		this.informationRecue = new Information<Boolean>();
		this.informationEmise = new Information<Float>();
		
	}
	
	public void setInformationRecue(Information<Boolean> info) {
		this.informationRecue = info;
	}
	public float getMinimumAmp() {
        return minimumAmp;
    }

    public void setMinimumAmp(float minimumAmp) {
        this.minimumAmp = minimumAmp;
    }

    public float getMaximumAmp() {
        return maximumAmp;
    }

    public void setMaximumAmp(float maximumAmp) {
        this.maximumAmp = maximumAmp;
    }

    public int getNbEchantillon() {
        return nbEchantillon;
    }

    public void setNbEchantillon(int nbEchantillon) {
        this.nbEchantillon = nbEchantillon;
    }

    public String getTypeSignal() {
        return typeSignal;
    }

    public void setTypeSignal(String typeSignal) {
        this.typeSignal = typeSignal;
    }
	
    /**
     * Méthode de transformation du signal en NRZ.
     * 
     * @throws InformationNonConformeException Si l'information reçue est incorrecte.
     */
	public void translationNRZ() throws InformationNonConformeException {
		for(Boolean tempBool : informationRecue) {
			if (tempBool == null) throw new InformationNonConformeException();
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
	
    /**
     * Méthode de transformation du signal en NRZT.
     * 
     * @throws InformationNonConformeException Si l'information reçue est incorrecte.
     */
	public void translationNRZT() throws InformationNonConformeException {
		// Calcul de la période tierce
		int tierPeriode = (int) Math.ceil(nbEchantillon / 3);
		int reste = nbEchantillon % 3;

		// Création d'une copie du tableau d'informations
		Information<Boolean> tempArray = new Information<Boolean>();
		
		tempArray = informationRecue;

		for (int indiceInfo = 0; indiceInfo < informationRecue.nbElements(); indiceInfo++) {
		    Boolean bitCourant = tempArray.iemeElement(indiceInfo);
		    
		    if (bitCourant == null) throw new InformationNonConformeException();
		    
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
		informationEmise.add(informationEmise.iemeElement(0));

	}
	
    /**
     * Méthode de transformation du signal en RZ.
     * 
     * @throws InformationNonConformeException Si l'information reçue est incorrecte.
     */
	public void translationRZ() throws InformationNonConformeException {
		float tierPeriode = nbEchantillon/3;
		for(Boolean tempBool : informationRecue) {
			if (tempBool == null) throw new InformationNonConformeException();
			for(int i=0;i<nbEchantillon; i++) {
				if (i<tierPeriode || i>=2*tierPeriode) {
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
	
    /**
     * Méthode pour recevoir des informations booléennes en entrée.
     * 
     * @param information Les informations booléennes à recevoir.
     * @throws InformationNonConformeException Si les informations reçues sont
     *                                          incorrectes.
     */
	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (Boolean i : information) {
			informationRecue.add(i);
		}
		emettre();
		
		
		
	}
	
	/**
     * Méthode pour émettre le signal transformé aux destinations connectées.
     * 
     * @throws InformationNonConformeException Si les informations émises sont
     *                                          incorrectes.
     */
	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		switch (typeSignal){
		case "NRZ" :
			translationNRZ();
			break;
		
		case "RZ" :
			translationRZ();
			break;
			
		case "NRZT" :
			translationNRZT();
			break;
		}
		//System.out.println(this.informationRecue);
		for (DestinationInterface <Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
		}
		
	}
}
