package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import transmetteurs.RecepteurAnalogique;
import information.Information;
import information.InformationNonConformeException;

public class RecepteurAnalogiqueTest {
    private RecepteurAnalogique recepteur;

    @Before
    public void setUp() throws Exception {
        // Initialisation du récepteur avant chaque test
        recepteur = new RecepteurAnalogique(0.0f, 1.0f, 8);
    }

    @Test
    public void testDecodage() throws InformationNonConformeException {
        // Créez une Information<Float> simulée pour tester le décodage
        Information<Float> information = new Information<>();
        for(int nbE = 0; nbE < 8; nbE++) {
        	information.add(0.0f);
        }
        for(int nbE = 0; nbE < 8; nbE++) {
        	information.add(1.0f);
        }
        for(int nbE = 0; nbE < 8; nbE++) {
        	information.add(1.0f);
        }
        for(int nbE = 0; nbE < 8; nbE++) {
        	information.add(0.0f);
        }
        for(int nbE = 0; nbE < 8; nbE++) {
        	information.add(1.0f);
        }



        // Appelez la méthode de décodage
        recepteur.decodage(information);

        // Créez une Information<Boolean> attendue
        Information<Boolean> attendu = new Information<>();
        attendu.add(false);
        attendu.add(true);
        attendu.add(true);
        attendu.add(false);
        attendu.add(true);

        // Vérifiez si la sortie du récepteur correspond à l'attendu
        assertEquals(attendu, recepteur.getInformationEmise());
    }
}
