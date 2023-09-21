package simulateur;
import destinations.Destination;
import destinations.DestinationFinale;
import information.Information;
import information.InformationNonConformeException;
import sources.Source;
import sources.SourceAleatoire;
import sources.SourceFixe;
import transmetteurs.EmetteurAnalogique;
import transmetteurs.RecepteurAnalogique;
import transmetteurs.Transmetteur;
import transmetteurs.TransmetteurAnalogiqueBruite;
import transmetteurs.TransmetteurAnalogiqueParfait;
import transmetteurs.TransmetteurParfait;
import visualisations.Sonde;
import visualisations.SondeAnalogique;
import visualisations.SondeLogique;
import visualisations.VueCourbe;


/** La classe Simulateur permet de construire et simuler une chaîne de
 * transmission composée d'une Source, d'un nombre variable de
 * Transmetteur(s) et d'une Destination.
 * @author cousin
 * @author prou
 *
 */
public class Simulateur {
      	
    /** indique si le Simulateur utilise des sondes d'affichage */
    private boolean affichage = false;
    
    /** indique si le Simulateur utilise un message généré de manière aléatoire (message imposé sinon) */
    private boolean messageAleatoire = true;
    
    /** indique si le Simulateur utilise un germe pour initialiser les générateurs aléatoires */
    private boolean aleatoireAvecGerme = false;
    
    /** la valeur de la semence utilisée pour les générateurs aléatoires */
    private Integer seed = null; // pas de semence par défaut
    
    /** la longueur du message aléatoire à transmettre si un message n'est pas imposé */
    private int nbBitsMess = 100; 
    
    /** la chaîne de caractères correspondant à m dans l'argument -mess m */
    private String messageString = "100";
   
   	
    /** le  composant Source de la chaine de transmission */
    private Source <Boolean>  source = null;
    
    /** le  composant Transmetteur parfait logique de la chaine de transmission */
    private Transmetteur <Boolean, Boolean>  transmetteurLogique = null;
    
    /** le  composant Destination de la chaine de transmission */
    private Destination <Boolean>  destination = null;
    
    private String form = "RZ";
    
    private int nbEch = 30;
    
    private float maxAmp = 1f;
    
    private float minAmp = 0f;
    
    private boolean analogique = false;
    
    private float snrpb = 10000000000000000000000000000000000000f;
   	
   
    /** Le constructeur de Simulateur construit une chaîne de
     * transmission composée d'une Source, d'une Destination
     * et de Transmetteur(s) [voir la méthode
     * analyseArguments]...  <br> Les différents composants de la
     * chaîne de transmission (Source, Transmetteur(s), Destination,
     * Sonde(s) de visualisation) sont créés et connectés.
     * @param args le tableau des différents arguments.
     *
     * @throws ArgumentsException si un des arguments est incorrect
     * @throws InformationNonConformeException 
     *
     */   
    
    
    public  Simulateur(String [] args) throws ArgumentsException, InformationNonConformeException {
    	// analyser et récupérer les arguments   	
    	analyseArguments(args);
    	if (messageAleatoire && aleatoireAvecGerme) {
    		this.source = new SourceAleatoire(Integer.parseInt(messageString), seed);
    	}
    	else if (messageAleatoire && !aleatoireAvecGerme) {
    		this.source = new SourceAleatoire(Integer.parseInt(messageString));
    	}
    	else {
    		this.source = new SourceFixe(messageString);
    	}
    	this.destination = new DestinationFinale();
    	if (analogique) {
    		EmetteurAnalogique ea = new EmetteurAnalogique(this.minAmp, this.maxAmp, this.nbEch, this.form);
        	source.connecter(ea);
        	Transmetteur tap;
        	if (snrpb == 10000000000000000000000000000000000000f) {
        		tap = new TransmetteurAnalogiqueParfait();
        	}
        	else {
        		
        		tap = new TransmetteurAnalogiqueBruite(snrpb, nbEch);
        	}
        	
        	ea.connecter(tap);
        	RecepteurAnalogique ra = new RecepteurAnalogique(this.minAmp, this.maxAmp, this.nbEch);
        	tap.connecter(ra);
        	
        	ra.connecter(destination);
        	
        	if (affichage) {
        		source.connecter(new SondeLogique("Source", 200));
        		ea.connecter(new SondeAnalogique("EmetteurAnalogique"));
        		tap.connecter(new SondeAnalogique("TransmetteurAnalogique"));
        		ra.connecter(new SondeLogique("Recepteur analogique",200));
        	}
        	
        	//if (true) {
        	//	double[] tab = new double[25];
        	//	for (int i =0; i<25;i++) {
        			
        	//	}
        	//}
    	}
    	else {
    		this.transmetteurLogique = new TransmetteurParfait();
    		source.connecter(transmetteurLogique);
			transmetteurLogique.connecter(destination);
    		if (affichage) {
    			source.connecter(new SondeLogique("Source", 200));
    			this.transmetteurLogique.connecter(new SondeLogique("Transmetteur",200));
    		}
    	}
    }
   
    /** La méthode analyseArguments extrait d'un tableau de chaînes de
     * caractères les différentes options de la simulation.  <br>Elle met
     * à jour les attributs correspondants du Simulateur.
     *
     * @param args le tableau des différents arguments.
     * <br>
     * <br>Les arguments autorisés sont : 
     * <br> 
     * <dl>
     * <dt> -mess m  </dt><dd> m (String) constitué de 7 ou plus digits à 0 | 1, le message à transmettre</dd>
     * <dt> -mess m  </dt><dd> m (int) constitué de 1 à 6 digits, le nombre de bits du message "aléatoire" à transmettre</dd> 
     * <dt> -s </dt><dd> pour demander l'utilisation des sondes d'affichage</dd>
     * <dt> -seed v </dt><dd> v (int) d'initialisation pour les générateurs aléatoires</dd> 
     * </dl>
     *
     * @throws ArgumentsException si un des arguments est incorrect.
     * @throws InformationNonConformeException 
     *
     */   
    public  void analyseArguments(String[] args)  throws  ArgumentsException, InformationNonConformeException {

    	for (int i=0;i<args.length;i++){ // traiter les arguments 1 par 1

    		if (args[i].matches("-s")){
    			affichage = true;
    		}
    		
    		else if (args[i].matches("-seed")) {
    			aleatoireAvecGerme = true;
    			i++; 
    			// traiter la valeur associee
    			try { 
    				seed = Integer.valueOf(args[i]);
    			}
    			catch (Exception e) {
    				throw new ArgumentsException("Valeur du parametre -seed  invalide :" + args[i]);
    			}           		
    		}

    		else if (args[i].matches("-mess")){
    			i++; 
    			// traiter la valeur associee
    			messageString = args[i];
    			if (args[i].matches("[0,1]{7,}")) { // au moins 7 digits
    				messageAleatoire = false;
    				nbBitsMess = args[i].length();
    			} 
    			else if (args[i].matches("[0-9]{1,6}")) { // de 1 à 6 chiffres
    				messageAleatoire = true;
    				nbBitsMess = Integer.valueOf(args[i]);
    				if (nbBitsMess < 1) 
    					throw new ArgumentsException ("Valeur du parametre -mess invalide : " + nbBitsMess);
    			}
    			else 
    				throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
    		}
    		
    		else if (args[i].matches("-form")) {
    			i++;
    			if (String.valueOf(args[i]).equals("RZ") || String.valueOf(args[i]).equals("NRZ") || String.valueOf(args[i]).equals("NRZT")) {
    				form = String.valueOf(args[i]);
    				analogique = true;
    			}
    			else throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
    			
    		}
    		
    		else if (args[i].matches("-nbEch")) {
    			i++;
    			if ( Integer.valueOf(args[i]) > 0 ){
    				nbEch=Integer.valueOf(args[i]);
    				analogique = true;
    			}
    			else throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
    		}
    		
    		else if (args[i].matches("-ampl")) {
    			i++;
				minAmp = Float.valueOf(args[i]);
				i++;
				maxAmp = Float.valueOf(args[i]);
				analogique = true;
				if (maxAmp < minAmp) {
					throw new InformationNonConformeException("MAX inferieur à MIN");
				}
    		}
    		
    		else if (args[i].matches("-snrpb")) {
    			i++;
    			snrpb=Float.valueOf(args[i]);
    			analogique = true;
    			this.snrpb = snrpb;
    		}
    		
    		
    		//TODO : ajouter ci-après le traitement des nouvelles options

    		else throw new ArgumentsException("Option invalide :"+ args[i]);
    	}
    	
      
    }
     
    
   	
    /** La méthode execute effectue un envoi de message par la source
     * de la chaîne de transmission du Simulateur.
     *
     * @throws Exception si un problème survient lors de l'exécution
     *
     */ 
    public void execute() throws Exception {      
         
    	// TODO : typiquement source.emettre(); 
    	
    	source.emettre();
    	
    }
   
   	   	
   	
    /** La méthode qui calcule le taux d'erreur binaire en comparant
     * les bits du message émis avec ceux du message reçu.
     *
     * @return  La valeur du Taux dErreur Binaire.
     */   	   
    public float  calculTauxErreurBinaire() {
    	
    	int nbErreur = 0;
    	Information<Boolean> infoDestination = destination.getInformationRecue();
    	Information<Boolean> infoSource = source.getInformationEmise();
    	System.out.println("Src" + infoSource.nbElements());
    	System.out.println("Dst" + infoDestination.nbElements());
    	if (infoSource.nbElements() == 0) {
    		return 0.0f;
    	}
    	for (int i =0; i< infoSource.nbElements(); i++) {
    		//System.out.println("Destination" + infoDestination);
    		//System.out.println("Source" + infoSource);
    		if (infoDestination.iemeElement(i) != infoSource.iemeElement(i)) {
    			nbErreur += 1;
    		}
    	}
    	return ((float) nbErreur/( (float) infoSource.nbElements()));
    }
   
   
   
   
    /** La fonction main instancie un Simulateur à l'aide des
     *  arguments paramètres et affiche le résultat de l'exécution
     *  d'une transmission.
     *  @param args les différents arguments qui serviront à l'instanciation du Simulateur.
     */
    public static void main(String [] args) { 

    	Simulateur simulateur = null;

    	try {
    		simulateur = new Simulateur(args);
    	}
    	catch (Exception e) {
    		System.out.println(e); 
    		System.exit(-1);
    	} 

    	try {
    		simulateur.execute();
    		String s = "java  Simulateur  ";
    		for (int i = 0; i < args.length; i++) { //copier tous les paramètres de simulation
    			s += args[i] + "  ";
    		}
    		System.out.println(s + "  =>   TEB : " + simulateur.calculTauxErreurBinaire());
    	}
    	catch (Exception e) {
    		System.out.println(e);
    		e.printStackTrace();
    		System.exit(-2);
    	}              	
    }
}

