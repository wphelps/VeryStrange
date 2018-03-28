import java.awt.BorderLayout;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import org.jlab.clas.physics.EventFilter;
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

public class OmegaAnalyzer {
	static H1F hpair1;
	static H1F hpair1_precut;
	static H1F hpair2;
	static H1F htriple;
	static H1F htriple2;
	static H2F hpi0opening;
	static H2F hpairgammaopening;

	static ParticleSelectorKPP selector = new ParticleSelectorKPP(6.423);
	static EventFilter filter2 = new EventFilter("22:22:X+:X-");
	static EventFilter filter3 = new EventFilter("22:22:22:X+:X-");

	static void processEvent(DataEvent event) {
		selector.setMinimumPhotonEnergy(.05);
		RecEvent recevent = selector.getRecEvent(event);
		PhysicsEvent pevent = recevent.getReconstructed();
		if (filter3.isValid(pevent)) {
			Particle threeGamma = pevent.getParticle("[22,0]+[22,1]+[22,2]");
			Particle pair1 = pevent.getParticle("[22,0]+[22,1]");
			Particle pair2 = pevent.getParticle("[22,1]+[22,2]");
			Particle pair3 = pevent.getParticle("[22,0]+[22,2]");
			Vector3 gamma1 = pevent.getParticle("[22,0]").vector().vect();
			Vector3 gamma2 = pevent.getParticle("[22,1]").vector().vect();
			Vector3 gamma3 = pevent.getParticle("[22,2]").vector().vect();
			double openingAngle12 = Math.toDegrees(Math.acos(gamma1.dot(gamma2) / (gamma1.mag() * gamma2.mag())));
			double openingAngle23 = Math.toDegrees(Math.acos(gamma2.dot(gamma3) / (gamma2.mag() * gamma3.mag())));
			double openingAngle13 = Math.toDegrees(Math.acos(gamma1.dot(gamma3) / (gamma1.mag() * gamma3.mag())));
			double openingAnglepair1 = Math.toDegrees(
					Math.acos(pair1.vector().vect().dot(gamma3) / (pair1.vector().vect().mag() * gamma3.mag())));
			double openingAnglepair2 = Math.toDegrees(
					Math.acos(pair2.vector().vect().dot(gamma1) / (pair2.vector().vect().mag() * gamma1.mag())));
			double openingAnglepair3 = Math.toDegrees(
					Math.acos(pair3.vector().vect().dot(gamma2) / (pair3.vector().vect().mag() * gamma2.mag())));
			double mpi0_low = .09;
			double mpi0_high = .18;
			hpi0opening.fill(pair1.mass(), openingAngle12);
			hpi0opening.fill(pair2.mass(), openingAngle23);
			hpi0opening.fill(pair3.mass(), openingAngle13);
			if (openingAngle12 > 4.0 && openingAngle12 < 18.0 && openingAnglepair1 > 15.0) {
				hpair1_precut.fill(pair1.mass());
				if (pair1.mass() > mpi0_low && pair1.mass() < mpi0_high) {
					hpair1.fill(pair1.mass());
					hpair2.fill(pair2.mass());
					hpair2.fill(pair3.mass());
					htriple.fill(threeGamma.mass());
					htriple2.fill(threeGamma.mass());
					hpairgammaopening.fill(threeGamma.mass(), openingAnglepair1);
				}
			} else if (openingAngle23 > 4.0 && openingAngle23 < 18.0 && openingAnglepair2 > 15.0) {
				hpair1_precut.fill(pair2.mass());
				if (pair2.mass() > mpi0_low && pair2.mass() < mpi0_high) {
					hpair1.fill(pair2.mass());
					hpair2.fill(pair1.mass());
					hpair2.fill(pair3.mass());
					htriple.fill(threeGamma.mass());
					htriple2.fill(threeGamma.mass());
					hpairgammaopening.fill(threeGamma.mass(), openingAnglepair1);

				}
			} else if (openingAngle13 > 4.0 && openingAngle13 < 18.0 && openingAnglepair3 > 15.0) {
				hpair1_precut.fill(pair3.mass());
				if (pair3.mass() > mpi0_low && pair3.mass() < mpi0_high) {
					hpair1.fill(pair3.mass());
					hpair2.fill(pair1.mass());
					hpair2.fill(pair2.mass());
					htriple.fill(threeGamma.mass());
					htriple2.fill(threeGamma.mass());
					hpairgammaopening.fill(threeGamma.mass(), openingAnglepair1);

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
		//String directory = "/Users/wphelps/Desktop/clas12-kpp/dst/";
		//String directory = "/Users/wphelps/Desktop/clas12-kpp/pass1/dst_ftof_2/";
		String directory = "/Users/wphelps/Desktop/rga/phys/";
		//String directory = args[0];
		hpair1 = new H1F("hpair1", "", 500, 0.0, 1.1);
		hpair1_precut = new H1F("hpair1_precut", "", 500, 0.0, 1.1);
		hpair2 = new H1F("hpair2", "", 500, 0.0, 1.1);
		htriple = new H1F("htriple", "", 120, 0.6, 1.2);
		htriple2 = new H1F("htriple", "", 240, 0.0, 1.2);
	    hpairgammaopening = new H2F("hpairgammaopening",120,0.0,1.2,100,0,70);
	    hpi0opening = new H2F("hpi0opening",120,0.0,.6,100,0,70);


	    hpairgammaopening.setTitleY("#pi^0 photon opening angle");
	    hpairgammaopening.setTitleX("M(3#gamma)");
	    hpi0opening.setTitleY("Two photon opening angle");
	    hpi0opening.setTitleX("M(#gamma#gamma)");
		htriple.setTitleX("M(#pi^0#gamma)");
		htriple.setTitleY("Counts");
		htriple2.setTitleX("M(#pi^0#gamma)");
		htriple2.setTitleY("Counts");
		hpair1_precut.setTitleX("M(#gamma#gamma) (5 deg.<#theta<30 deg.)");
		hpair1_precut.setTitleY("Counts");
		hpair1.setTitleX("M(#gamma#gamma) (5 deg.<#theta<30 deg.)");
		hpair1.setTitleY("Counts");
		hpair1.setFillColor(32);
		hpair2.setTitleX("M(#gamma#gamma) (Other Pairs)");
		hpair2.setTitleY("Counts");
		hpair2.setFillColor(38);
		
		EmbeddedCanvas can = new EmbeddedCanvas();
		can.divide(3, 2);
		can.cd(0);
		can.draw(htriple);
		can.cd(1);
		can.draw(htriple2);
		can.cd(2);
		can.draw(hpair1_precut);
		can.draw(hpair1, "same");
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
