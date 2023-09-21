package transmetteurs;

import java.util.Random;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class TransmetteurAnalogiqueBruite extends Transmetteur<Float, Float>{
	float snrpb;
	int nbEch;
	//Information<Float> informationEmise;
	//Information<Float> informationRecue;
	
	public TransmetteurAnalogiqueBruite(float snrpb, int nbEch) {
		super();
		this.snrpb = snrpb;
		this.nbEch = nbEch;
		this.informationEmise = new Information<Float>();
		this.informationRecue = new Information<Float>();
	}

	@Override
	public void recevoir(Information<Float> information) throws InformationNonConformeException {
		// TODO Auto-generated method stub
		for (Float i : information) {
			informationRecue.add(i);
		}
		
		emettre();
	}

	@Override
	public void emettre() throws InformationNonConformeException {
		// TODO Auto-generated method stub
		float puissanceSignal =0f;
		float snrpbLin = (float) Math.pow(snrpb/10, 10);
		for (float elmt : this.informationRecue) {
			puissanceSignal += Math.pow(elmt, 2);
		}
		puissanceSignal = puissanceSignal/this.informationRecue.nbElements();
		float sigma = (float) Math.sqrt((puissanceSignal*nbEch)/(2*snrpbLin));
		
		for (Float elmt : this.informationRecue) {
			float bruit;
			Random a1 = new Random();
			Random a2 = new Random();
			bruit = (float) (sigma*Math.sqrt(-2*Math.log(1-a1.nextDouble()))*Math.cos(2 * Math.PI * a2.nextDouble()));
			//System.out.println(bruit);
			this.informationEmise.add(elmt + bruit); 
		}
		
		for (DestinationInterface <Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
		}
	}
}
