import java.awt.BorderLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import org.jlab.clas.physics.EventFilter;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.clas.physics.RecEvent;
import org.jlab.clas.physics.Vector3;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

public class OmegaAnalyzer2 {
	static H1F hpair1;
	static H1F hpair1_precut;
	static H1F hpair1_bg;
	static H1F hpair2;
	static H1F htriple;
	static H1F htriple_pi0mass;
	static H1F htriple_pi0massbg;
	static H1F htriple_pi0massbgsub;
	static H1F htriple2;
	static H2F hpi0opening;
	static H2F hpairgammaopening;

	static ParticleSelectorKPP selector = new ParticleSelectorKPP(10.60);
	static EventFilter filter2 = new EventFilter("22:22:X+:X-");
	static EventFilter filter3 = new EventFilter("22:22:22:X+:X-");
	
	static String directory = "/Users/wphelps/Desktop/rga/phys/";

	static void processEvent(DataEvent event) {
		selector.setMinimumPhotonEnergy(.05);
		selector.setMinimumFTOFMatchAngle(0.0);
		RecEvent recevent = selector.getRecEvent(event);
		PhysicsEvent pevent = recevent.getReconstructed();
		if (filter3.isValid(pevent)) {
			Particle threeGamma = pevent.getParticle("[22,0]+[22,1]+[22,2]");
			
			if(threeGamma.e()>.5){
			Particle pair1 = pevent.getParticle("[22,0]+[22,1]");
			Particle pair2 = pevent.getParticle("[22,1]+[22,2]");
			Particle pair3 = pevent.getParticle("[22,0]+[22,2]");
			double mpi0 = .140;
			double closest = 1000;
			int pair = 0;
			if((pair1.mass()-mpi0)<closest){
				closest = (pair1.mass()-mpi0);
				pair = 0;
			}
			if((pair2.mass()-mpi0)<closest){
				closest = (pair2.mass()-mpi0);
				pair = 1;
			}
			if((pair3.mass()-mpi0)<closest){
				closest = (pair3.mass()-mpi0);
				pair = 2;
			}
			Particle gamma1 = null;
			Particle gamma2 = null;
			Particle gamma3 = null;
			Particle pi0 = null;
			Particle otherpair1 = null;
			Particle otherpair2 = null;
			if(pair==0){
				pi0 = pair1;
				otherpair1 = pair2;
				otherpair2 = pair3;
				gamma1 = pevent.getParticle("[22,0]");
				gamma2 = pevent.getParticle("[22,1]");
				gamma3 = pevent.getParticle("[22,2]");
			}
			if(pair==1){
				pi0 = pair2;
				otherpair1 = pair1;
				otherpair2 = pair3;
				gamma3 = pevent.getParticle("[22,0]");
				gamma1 = pevent.getParticle("[22,1]");
				gamma2 = pevent.getParticle("[22,2]");
			}
			if(pair==2){
				pi0 = pair3;
				otherpair1 = pair1;
				otherpair2 = pair2;
				gamma1 = pevent.getParticle("[22,0]");
				gamma3 = pevent.getParticle("[22,1]");
				gamma2 = pevent.getParticle("[22,2]");
			}
			
			
			double openingAnglePi0 = Math.toDegrees(Math.acos(gamma1.vector().vect().dot(gamma2.vector().vect()) / (gamma1.vector().vect().mag() * gamma2.vector().vect().mag())));
			double openingAnglepair1 = Math.toDegrees(
					Math.acos(pi0.vector().vect().dot(gamma3.vector().vect()) / (pi0.vector().vect().mag() * gamma3.vector().vect().mag())));
			double mpi0_low = .127-.015;
			double mpi0_high = .157+.015;
			hpi0opening.fill(pair1.mass(), openingAnglePi0);

			double phig1 = Math.toDegrees(gamma1.phi());
			double phig2 = Math.toDegrees(gamma2.phi());
			double phig3 = Math.toDegrees(gamma3.phi());

			if (phig1 > 30 && phig1 < 150 && phig2 > 30 && phig2 < 150) {
			//if(true){
			if (openingAnglePi0 > 2.5 ) {
				hpair1_precut.fill(pi0.mass());
				if (pi0.mass() > mpi0_low && pi0.mass() < mpi0_high) {
					hpair1.fill(pi0.mass());
					hpair2.fill(otherpair1.mass());
					hpair2.fill(otherpair2.mass());
					htriple.fill(threeGamma.mass());
					htriple2.fill(threeGamma.mass());
					hpairgammaopening.fill(threeGamma.mass(), openingAnglepair1);
					
					Particle pi0_mass = new Particle();
					pi0_mass.setParticleWithMass(.1349766, (byte) 0, pi0.px(), pi0.py(), pi0.pz(), pi0.vx(),
							pi0.vy(), pi0.vz());
					LorentzVector pi0_4vec = pi0_mass.vector();
					pi0_4vec.add(gamma3.vector());
					htriple_pi0mass.fill(pi0_4vec.mass());
				}else if (((pi0.mass() > mpi0_low-Math.abs(mpi0_low-mpi0_high)/2.0)&&pi0.mass() < mpi0_low) || (pi0.mass() < mpi0_high+Math.abs(mpi0_low-mpi0_high)/2.0&&pi0.mass() > mpi0_high)) {
					htriple_pi0massbg.fill(threeGamma.mass());
					hpair1_bg.fill(pi0.mass());
				}
			} 
			float data1[] = htriple2.getData();
			float data2[] = htriple_pi0massbg.getData();
			for(int i=0; i< data1.length; i++){
				htriple_pi0massbgsub.setBinContent(i, data1[i]-data2[i]);
			}
			}
			}
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
		hpair1 = new H1F("hpair1", "", 100, 0.0, .3);
		hpair1_bg = new H1F("hpair1bg", "", 100, 0.0, .3);
		hpair1_precut = new H1F("hpair1_precut", "", 100, 0.0, .3);
		hpair2 = new H1F("hpair2", "", 200, 0.0, 1.1);
		htriple = new H1F("htriple", "", 120, 0.6, 1.2);
		htriple2 = new H1F("htriple", "", 50, 0.0, 1.8);
		htriple_pi0mass = new H1F("htriplepoorkinfit", "", 50, 0.0, 1.8);
	    hpairgammaopening = new H2F("hpairgammaopening",120,0.0,1.2,100,0,70);
	    hpi0opening = new H2F("hpi0opening",120,0.0,.6,100,0,70);
	    htriple_pi0massbg = new H1F("htriplebg", "", 50, 0., 1.8);
	    htriple_pi0massbgsub = new H1F("htriplebg", "", 50, 0., 1.8);
	    hpairgammaopening.setTitleY("#pi^0 photon opening angle");
	    hpairgammaopening.setTitleX("M(3#gamma)");
	    hpi0opening.setTitleY("Two photon opening angle");
	    hpi0opening.setTitleX("M(#gamma#gamma)");
		htriple.setTitleX("M(#pi^0#gamma)");
		htriple.setTitleY("Counts");
		htriple_pi0massbgsub.setTitleX("M(#pi^0#gamma)");
		htriple_pi0massbgsub.setTitleY("Counts");
		htriple2.setTitleX("M(#pi^0#gamma)");
		htriple2.setTitleY("Counts");
		hpair1_precut.setTitleX("M(#gamma#gamma)");
		hpair1_precut.setTitleY("Counts");
		hpair1.setTitleX("M(#gamma#gamma)");
		hpair1.setTitleY("Counts");
		hpair1.setFillColor(38);
		hpair2.setTitleX("M(#gamma#gamma) (Other Pairs)");
		hpair2.setTitleY("Counts");
		hpair2.setFillColor(38);
		
		EmbeddedCanvas can = new EmbeddedCanvas();
		can.divide(3, 2);
		//can.cd(0);
		//can.draw(htriple);
		can.cd(0);
		can.draw(htriple2);
		htriple_pi0mass.setFillColor(38);
		can.draw(htriple_pi0mass,"same");
		htriple_pi0massbg.setFillColor(32);
		can.draw(htriple_pi0massbg,"same");
		can.cd(1);
		can.draw(htriple_pi0massbgsub);
		can.cd(2);
		can.draw(hpair1_precut);
		can.draw(hpair1, "same");
		hpair1_bg.setFillColor(32);
		can.draw(hpair1_bg,"same");
		can.cd(3);
		can.draw(hpair2);
		can.cd(4);
		can.draw(hpi0opening);
		can.cd(5);
		can.draw(hpairgammaopening);

		JFrame frame = new JFrame("KPP Data Analysis");
		frame.setSize(1400, 800);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(can, "Three Photons");
		frame.setLayout(new BorderLayout());
		frame.add(tabbedPane, BorderLayout.CENTER);
		JProgressBar bar = new JProgressBar();
		bar.setBorder(new TitledBorder("Progress"));
		frame.add(bar, BorderLayout.PAGE_END);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		int totalNevents = 0;
		for (File file : getHipoFiles(directory)) {
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
					can.update();
					bar.setValue(eventCounter);
					bar.repaint();
				}
			}
			reader.close();
			reader = null;
		}
		can.update();
		bar.setValue(eventCounter);
		bar.repaint();

	}

	public static File[] getHipoFiles(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".hipo");
			}
		});

	}

}
