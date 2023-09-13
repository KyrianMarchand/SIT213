package test; // Your test class is in the "Test" package

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import transmetteurs.EmetteurAnalogique;
import information.Information;
import information.InformationNonConformeException;

public class EmetteurAnalogiqueTest {
	
    private EmetteurAnalogique emetteur;

    @Before
    public void setUp() throws Exception {
        // Initialization of the transmitter before each test
        emetteur = new EmetteurAnalogique(0.0f, 1.0f, 8, "NRZ");
    }

    @Test
    public void testTranslationNRZ() throws InformationNonConformeException {
        Information<Boolean> info = new Information<>();
        info.add(true);
        info.add(false);
        info.add(true);
        emetteur.recevoir(info);

        // Test if the NRZ translation works correctly
        Information<Float> resultatAttendu = new Information<>();
        for(int nbE = 0; nbE < 8; nbE++) {
            resultatAttendu.add(1.0f);
        }
        for(int nbE = 0; nbE < 8; nbE++) {
            resultatAttendu.add(0.0f);
        }
        for(int nbE = 0; nbE < 8; nbE++) {
            resultatAttendu.add(1.0f);
        }

        assertEquals(resultatAttendu, emetteur.getInformationEmise());
    }

    @Test
    public void testTranslationRZ() throws InformationNonConformeException {
        Information<Boolean> info = new Information<>();
        info.add(true);
        info.add(false);
        info.add(true);
        emetteur = new EmetteurAnalogique(0.0f, 1.0f, 9, "RZ");
        emetteur.recevoir(info);

        // Test if the RZ translation works correctly
        Information<Float> resultatAttendu = new Information<>();
        for(int nbE = 0; nbE < 3; nbE++) {
            resultatAttendu.add(0.0f);
        }
        for(int nbE = 0; nbE < 3; nbE++) {
            resultatAttendu.add(1.0f);
        }
        for(int nbE = 0; nbE < 3; nbE++) {
            resultatAttendu.add(0.0f);
        }
        for(int nbE = 0; nbE < 9; nbE++) {
            resultatAttendu.add(0.0f);
        }
        for(int nbE = 0; nbE < 3; nbE++) {
            resultatAttendu.add(0.0f);
        }
        for(int nbE = 0; nbE < 3; nbE++) {
            resultatAttendu.add(1.0f);
        }
        for(int nbE = 0; nbE < 3; nbE++) {
            resultatAttendu.add(0.0f);
        }

        assertEquals(resultatAttendu, emetteur.getInformationEmise());
    }

    @Test
    public void testTranslationNRZT() throws InformationNonConformeException {
        Information<Boolean> info = new Information<>();
        info.add(true);
        info.add(false);
        info.add(true);
        emetteur = new EmetteurAnalogique(0.0f, 1.0f, 8, "NRZT");
        emetteur.recevoir(info);

        // Test if the NRZT translation works correctly
        Information<Float> resultatAttendu = new Information<>();
        resultatAttendu.add(0.0f);
        resultatAttendu.add(0.5f);
        for(int nbE = 0; nbE < 5; nbE++) {
            resultatAttendu.add(1.0f);
        }
        resultatAttendu.add(0.5f);
        for(int nbE = 0; nbE < 7; nbE++) {
            resultatAttendu.add(0.0f);
        }
        resultatAttendu.add(0.5f);
        for(int nbE = 0; nbE < 5; nbE++) {
            resultatAttendu.add(1.0f);
        }
        resultatAttendu.add(0.5f);
        resultatAttendu.add(0.0f);

        assertEquals(resultatAttendu, emetteur.getInformationEmise());
    }
}
