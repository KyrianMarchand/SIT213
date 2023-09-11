package sources;

import information.Information;

/**
 * 
 * @author lucasfayolle
 *
 */
public class SourceFixe extends Source<Boolean>{
	/**
	 * Constructeur qui permet de recuperer une chaine de caractères composé de 0 et de 1 et de convertire celui-ci en une information que l'on peut transmettre.
     * Cette information est stockée dans les attributs informationGeneree et informationEmise
	 * @param message : Chaine de caractère composée de 0 et de 1 à traduire en information
	 */
	public SourceFixe (String message) {
		Information<Boolean> info = new Information<Boolean>();
        for (int i = 0; i<message.length(); i++) {
        	if (String.valueOf(message.charAt(i)).equals("1")) {
        		info.add(true);
        	}
        	else {
        		info.add(false);
        	}
        } 
        this.informationGeneree=info;
        this.informationEmise=this.informationGeneree;
    }

}
