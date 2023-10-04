package sources;

import java.util.Random;

import information.Information;

/**
 * Constructeur qui permet de générer une infomation de manière aléatoire
 * St
 * @author lucasfayolle
 *
 */
public class SourceAleatoire extends Source<Boolean>{
	/**
	 * Constructeur qui permet de générer une infomation de manière aléatoire à partir d'un nombre d'éléments
	 * @param nbElmt : longueur de l'information en bits
	 */
	public SourceAleatoire(int nbElmt) {
		Information<Boolean> info = new Information<Boolean>();
		for (int i = 0; i<nbElmt; i++) {
			Random rd = new Random();
			info.add(rd.nextBoolean());
		}
		this.informationGeneree = info;
		this.informationEmise = info;
	}
	
	/**
	 * Constructeur qui permet de générer une infomation de manière aléatoire à partir d'un nombre d'éléments et d’une semence pour l’initialisation des générateurs aléatoires du simulateur
	 * @param nbElmt : longueur de l'information en bits
	 * @param seed : Semence
	 */
	public SourceAleatoire(int nbElmt, int seed) {
	    Information<Boolean> info = new Information<Boolean>();
	    
	    Random rd = new Random(seed); // Initialisez le générateur de nombres aléatoires avec la graine.

	    for (int i = 0; i < nbElmt; i++) {
	        boolean randomValue = rd.nextBoolean(); // Générez une valeur booléenne.
	        info.add(randomValue); // Ajoutez cette valeur à votre structure d'information.
	    }

	    this.informationGeneree = info;
	    this.informationEmise = this.informationGeneree;
	}
}