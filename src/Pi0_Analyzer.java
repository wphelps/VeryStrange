import java.awt.BorderLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

//import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.physics.EventFilter;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.clas.physics.RecEvent;
import org.jlab.clas.physics.Vector3;
import org.jlab.detector.base.DetectorType;
import org.jlab.geom.prim.Vector3D;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

public class Pi0_Analyzer {
	static H2F hsixsectors = new H2F("hsixsectors", 360, -180, 180, 120, 0, 40);
	static H1F hsixsectors2;
	static H1F hsector1;
	static H1F hsector2;
	static H1F hsector3;
	static H1F hsector4;
	static H1F hsector5;
	static H1F hsector6;
	static H2F h2sector1;
	static H2F h2sector2;
	static H2F h2sector3;
	static H2F h2sector4;
	static H2F h2sector5;
	static H2F h2sector6;
	static H2F h2sixsectors;
	static H1F hsixsectors_mass;

	static ParticleSelectorKPP selector = new ParticleSelectorKPP(10.6);
	static EventFilter filter2 = new EventFilter("22:22:X+:X-");
	// static EventFilter filter2 = new EventFilter("22:22");
	// static String directory = "/Users/latifa/Downloads/coatjava/pass1";
	//static String directory = "/Users/wphelps/Desktop/clas12-kpp/pass1/dst_ftof_2";
	static String directory = "/Users/wphelps/Desktop/rga/temp/";

	static void processEvent(DataEvent event) {
       selector.setMinimumPhotonEnergy(0.2);
       //selector.setMaximumPhotonEnergy(1.0);
       selector.setMinimumFTOFMatchAngle(0.0);
       RecEvent recevent = selector.getRecEvent(event);
       PhysicsEvent pevent = recevent.getReconstructed();
       if (filter2.isValid(pevent)) {
           Particle pi0 = pevent.getParticle("[22,0]+[22,1]");
           LorentzVector gamma14V = pevent.getParticle("[22,0]").vector();
           LorentzVector gamma24V = pevent.getParticle("[22,1]").vector();
           Vector3 gamma1 = pevent.getParticle("[22,0]").vector().vect();
           Vector3 gamma2 = pevent.getParticle("[22,1]").vector().vect();
           double phig1 = Math.toDegrees(gamma1.phi());
           double phig2 = Math.toDegrees(gamma2.phi());
           double energyProduct = gamma14V.e()*gamma24V.e();
           double openingAngle = Math.toDegrees(Math.acos(gamma1.dot(gamma2) / (gamma1.mag() * gamma2.mag())));
           //double openingAngle = Math.toDegrees(gamma1.theta())+ Math.toDegrees(gamma2.theta());
           List<DetectorResponse> hits = DetectorResponse.readHipoEvent(event, 
   	            "FTOF::hits", DetectorType.FTOF);
			boolean ftofHit = false;
			double closestg1 = 1000;
			double closestg2 = 1000;

   		for(DetectorResponse hit : hits){
   			Vector3D hitPosition = hit.getPosition();
   			Vector3 ftofHitVector = new Vector3();
   			//assuming target is at 0,0,0....
   			ftofHitVector.setXYZ(hitPosition.x(), hitPosition.y(), hitPosition.z());
   			double distanceg1 = Math.toDegrees(Math.acos(gamma1.dot(ftofHitVector)/(gamma1.mag()*ftofHitVector.mag())));
   			double distanceg2 = Math.toDegrees(Math.acos(gamma2.dot(ftofHitVector)/(gamma2.mag()*ftofHitVector.mag())));
   			if(distanceg1<closestg1){
   				closestg1 = distanceg1;
   			}
   			if(distanceg2<closestg2){
   				closestg2 = distanceg2;
   			}	
   		}
           h2sixsectors.fill(openingAngle,energyProduct);
           if(openingAngle > 0.0){
           hsixsectors_mass.fill(pi0.mass());
           }
           if (phig1 > -30 && phig1 < 30 && phig2 > -30 && phig2 < 30) {
               hsector1.fill(pi0.mass());
               h2sector1.fill(openingAngle,energyProduct);
           }
           //if (phig1 > 30 && phig1 < 90 && phig2 > 30 && phig2 < 90 ) {
               if (phig1 > 30 && phig1 < 90 && phig2 > 30 && phig2 < 90 ) {
               hsector2.fill(pi0.mass());
               h2sector2.fill(openingAngle,energyProduct);

           }
           if (phig1 > 90 && phig1 < 150 && phig2 > 90 && phig2 < 150) {
               hsector3.fill(pi0.mass());
               h2sector3.fill(openingAngle,energyProduct);
           }
           if ((phig1 > 150 || phig1 < -150) && (phig2 > 150 || phig2 < -150)) {
               hsector4.fill(pi0.mass());
               h2sector4.fill(openingAngle,energyProduct);
           }
           if (phig1 > -150 && phig1 < -90 && phig2 > -150 && phig2 < -90) {
               hsector5.fill(pi0.mass());
               h2sector5.fill(openingAngle,energyProduct);
           }
           if (phig1 > -90 && phig1 < -30 && phig2 > -90 && phig2 < -30) {
               hsector6.fill(pi0.mass());
               h2sector6.fill(openingAngle,energyProduct);
           }
           hsixsectors.fill(Math.toDegrees(gamma1.phi()), Math.toDegrees(gamma1.theta()));
           hsixsectors.fill(Math.toDegrees(gamma2.phi()), Math.toDegrees(gamma2.theta()));
           hsixsectors2.fill(Math.toDegrees(gamma1.phi()));
           hsixsectors2.fill(Math.toDegrees(gamma2.phi()));

       }
   }

	public static void main(String[] args) {
		GStyle.getGraphErrorsAttributes().setMarkerStyle(0);
		GStyle.getGraphErrorsAttributes().setMarkerColor(3);
		GStyle.getGraphErrorsAttributes().setMarkerSize(7);
		GStyle.getGraphErrorsAttributes().setLineColor(3);
		GStyle.getGraphErrorsAttributes().setLineWidth(3);
		GStyle.getAxisAttributesX().setTitleFontSize(32);
		GStyle.getAxisAttributesX().setLabelFontSize(28);
		GStyle.getAxisAttributesY().setTitleFontSize(32);
		GStyle.getAxisAttributesY().setLabelFontSize(28);
		GStyle.getH1FAttributes().setLineWidth(2);
		GStyle.getH1FAttributes().setLineColor(21);
		GStyle.getH1FAttributes().setFillColor(34);
		GStyle.getH1FAttributes().setOptStat("10");
		GStyle.getAxisAttributesZ().setLog(true);

		HipoDataSource reader = new HipoDataSource();
		int eventCounter = 0;

		hsixsectors = new H2F("hsixsectors", 360, -180, 180, 120, 0, 40);
		hsixsectors2 = new H1F("hsixsectors2", 360, -180, 180);
		hsector1 = new H1F("hsector1", 200, 0, .30);
		hsector2 = new H1F("hsector2", 200, 0, .30);
		hsector3 = new H1F("hsector3", 200, 0, .30);
		hsector4 = new H1F("hsector4", 200, 0, .30);
		hsector5 = new H1F("hsector5", 200, 0, .30);
		hsector6 = new H1F("hsector6", 200, 0, .30);
		h2sector1 = new H2F("hsector1", 100, 0, 40, 100, 0, 6.0);
		h2sector2 = new H2F("hsector2", 100, 0, 40, 100, 0, 6.0);
		h2sector3 = new H2F("hsector3", 100, 0, 40, 100, 0, 6.0);
		h2sector4 = new H2F("hsector4", 100, 0, 40, 100, 0, 6.0);
		h2sector5 = new H2F("hsector5", 100, 0, 40, 100, 0, 6.0);
		h2sector6 = new H2F("hsector6", 100, 0, 40, 100, 0, 6.0);
		h2sixsectors = new H2F("hsixsectors", 100, 0, 60, 100, 0, 6.0);

		hsixsectors_mass = new H1F("SixSectorMass", 100, 0, 1.0);

		EmbeddedCanvas can5 = new EmbeddedCanvas();
		can5.setTitleSize(32);
		hsixsectors.setTitleX("Photon #phi [Deg.]");
		hsixsectors.setTitleY("Photon #theta [Deg.]");
		hsixsectors2.setTitleX("Photon #phi [Deg.]");
		hsixsectors2.setTitleY("Counts");
		hsixsectors2.setFillColor(38);
		hsector1.setTitleX("M(#gamma#gamma) [GeV/c^2]");
		hsector2.setTitleX("M(#gamma#gamma) [GeV/c^2]");
		hsector3.setTitleX("M(#gamma#gamma) [GeV/c^2]");
		hsector4.setTitleX("M(#gamma#gamma) [GeV/c^2]");
		hsector5.setTitleX("M(#gamma#gamma) [GeV/c^2]");
		hsector6.setTitleX("M(#gamma#gamma) [GeV/c^2]");
		hsector1.setTitleY("Counts");
		hsector2.setTitleY("Counts");
		hsector3.setTitleY("Counts");
		hsector4.setTitleY("Counts");
		hsector5.setTitleY("Counts");
		hsector6.setTitleY("Counts");
		hsector1.setTitle("Sector 1");
		hsector2.setTitle("Sector 2");
		hsector3.setTitle("Sector 3");
		hsector4.setTitle("Sector 4");
		hsector5.setTitle("Sector 5");
		hsector6.setTitle("Sector 6");

		h2sector1.setTitleX("Opening Angle [degrees]");
		h2sector2.setTitleX("Opening Angle [degrees]");
		h2sector3.setTitleX("Opening Angle [degrees]");
		h2sector4.setTitleX("Opening Angle [degrees]");
		h2sector5.setTitleX("Opening Angle [degrees]");
		h2sector6.setTitleX("Opening Angle [degrees]");
		h2sector1.setTitleY("E1*E2 [GeV^2]");
		h2sector2.setTitleY("E1*E2 [GeV^2]");
		h2sector3.setTitleY("E1*E2 [GeV^2]");
		h2sector4.setTitleY("E1*E2 [GeV^2]");
		h2sector5.setTitleY("E1*E2 [GeV^2]");
		h2sector6.setTitleY("E1*E2 [GeV^2]");
		h2sector1.setTitle("Sector 1");
		h2sector2.setTitle("Sector 2");
		h2sector3.setTitle("Sector 3");
		h2sector4.setTitle("Sector 4");
		h2sector5.setTitle("Sector 5");
		h2sector6.setTitle("Sector 6");
		h2sixsectors.setTitleY("E1*E2 [GeV^2]");
		h2sixsectors.setTitleX("Opening Angle [degrees]");
		h2sixsectors.setTitle("All Six Sectors");

		EmbeddedCanvas can2 = new EmbeddedCanvas();
		can2.divide(4, 2);
		can2.cd(0);
		can2.draw(h2sixsectors);
		can2.cd(1);
		can2.draw(h2sector1);
		can2.cd(2);
		can2.draw(h2sector2);
		can2.cd(3);
		can2.draw(h2sector3);
		can2.cd(4);
		can2.draw(hsixsectors_mass);
		can2.cd(5);
		can2.draw(h2sector4);
		can2.cd(6);
		can2.draw(h2sector5);
		can2.cd(7);
		can2.draw(h2sector6);

		can5.divide(4, 2);
		can5.cd(0);
		can5.draw(hsixsectors);
		can5.cd(1);
		can5.draw(hsector1);
		can5.cd(2);
		can5.draw(hsector2);
		can5.cd(3);
		can5.draw(hsector3);
		can5.cd(4);
		can5.draw(hsixsectors2);
		can5.cd(5);
		can5.draw(hsector4);
		can5.cd(6);
		can5.draw(hsector5);
		can5.cd(7);
		can5.draw(hsector6);

		JFrame frame = new JFrame("KPP Data Analysis");
		frame.setSize(1400, 800);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(can5, "Pi0 Six Sectors");
		tabbedPane.add(can2, "Pi0 Energy Product vs Opening Angle");

		frame.setLayout(new BorderLayout());
		frame.add(tabbedPane, BorderLayout.CENTER);
		JProgressBar bar = new JProgressBar();
		bar.setBorder(new TitledBorder("Progress"));
		frame.add(bar, BorderLayout.PAGE_END);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		int totalNevents = 0;
		System.out.println("Number of files:["+(getHipoFiles(directory).length)+"]");
		for (File file : getHipoFiles(directory)) {
			System.out.println("Opening File:"+file);
			reader.open(file);
			totalNevents += reader.getSize();
			reader.close();
		}
		bar.setMaximum(totalNevents);
		bar.setValue(0);
		int inFileCounter = 0;
		for (File file : getHipoFiles(directory)) {
			reader = new HipoDataSource();
			inFileCounter = 0;
			reader.open(file);
			while (reader.hasEvent()) {
				eventCounter++;
				inFileCounter++;
				try {
					processEvent(reader.getNextEvent());
				} catch (Exception e) {
					System.out.println("Hipo died at:" + file.getName() + " at event:" + inFileCounter);
					e.printStackTrace();
				}
				if (eventCounter % 5000 == 0) {
					can2.update();
					can5.update();
					bar.setValue(eventCounter);
					bar.repaint();
				}
			}
			reader.close();
			reader = null;
		}
		can5.update();
		can2.update();
		bar.setValue(eventCounter);
		bar.repaint();

	}

	public static File[] getHipoFiles(String dirName) {
		File dir = new File(dirName);

		/*return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".hipo");
			}
		});*/
		return dir.listFiles();

	}

}