
import java.awt.BorderLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import org.jlab.clas.physics.EventFilter;
import org.jlab.clas.physics.GenericKinematicFitter;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.clas.physics.RecEvent;
import org.jlab.clas.physics.Vector3;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.groot.ui.TCanvas;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;
import org.jlab.clas12.tools.*;

public class MultiPhoton {
	H1F blah;

	static H1F hpair1;
	static H1F hpair1_precut;
	static H1F hpair2;
	static H1F hpair3;
	static H1F htriple;
	static H1F htriple2;
	static H1F htriple2_60;
	static H1F htriple2_40;
	static H1F htriple2_120;
	static H1F htriple2_180;
	static H1F hmm2;
	static H2F hpairs;
	static H2F hpi0opening;
	static H2F hpairgammaopening;

	static H1F hmgamma2;
	static H1F hmgamma21;
	static H1F hmgamma22;

	static H1F hmgamma3;
	static H1F hmgamma3_60;
	static H1F hmgamma3_120;
	static H1F hmgamma3_180;
	static H1F hmgamma3_480;

	static H1F hmgamma4;
	static H1F hmgamma5;
	static H1F hpipi;
	static H1F hpipigammagamma;

	static H2F hsixsectors = new H2F("hsixsectors", 360, -180, 180, 120, 0, 40);
	static H1F hsixsectors2;
	static H1F hsector1;
	static H1F hsector2;
	static H1F hsector3;
	static H1F hsector4;
	static H1F hsector5;
	static H1F hsector6;

	static ParticleSelectorKPP selector = new ParticleSelectorKPP(6.423);
	static EventFilter filter2 = new EventFilter("22:22:X+:X-:Xn");
	static EventFilter filter3 = new EventFilter("22:22:22:X+:X-:Xn");
	static EventFilter filter4 = new EventFilter("22:22:22:22:X+:X-:Xn");
	static EventFilter filter5 = new EventFilter("22:22:22:22:22:X+:X-:Xn");

	static EventFilter filter6 = new EventFilter("211:-211:X+:X-:Xn");
	static EventFilter filter7 = new EventFilter("211:-211:22:22");

	static void processEvent(DataEvent event) {
		selector.setMinimumPhotonEnergy(.5);
		RecEvent recevent = selector.getRecEvent(event);
		PhysicsEvent pevent = recevent.getReconstructed();
		// System.out.println(pevent);
		if (filter3.isValid(pevent)) {
			Particle threeGamma = pevent.getParticle("[22,0]+[22,1]+[22,2]");
			Particle pair1 = pevent.getParticle("[22,0]+[22,1]");
			Particle pair2 = pevent.getParticle("[22,1]+[22,2]");
			Particle pair3 = pevent.getParticle("[22,0]+[22,2]");
			LorentzVector gamma14v = pevent.getParticle("[22,0]").vector();
			LorentzVector gamma24v = pevent.getParticle("[22,1]").vector();
			LorentzVector gamma34v = pevent.getParticle("[22,2]").vector();
			Vector3 gamma1 = pevent.getParticle("[22,0]").vector().vect();
			Vector3 gamma2 = pevent.getParticle("[22,1]").vector().vect();
			Vector3 gamma3 = pevent.getParticle("[22,2]").vector().vect();
			double phig1 = Math.toDegrees(gamma1.phi());
			double phig2 = Math.toDegrees(gamma2.phi());
			double phig3 = Math.toDegrees(gamma3.phi());
			// if ((phig1 > 30 || phig1 < -150) && (phig2 > 30 || phig2 < -150)
			// &&(phig3>30||phig3<-150)) {
			LorentzVector sum = new LorentzVector();
			sum.add(gamma14v);
			sum.add(gamma24v);
			sum.add(gamma34v);

			//if ((phig1 > -30 && phig1 < 150) && (phig2 > -30 && phig2 < 150) &&sum.e()>.5){
					if(sum.e() > 1.0) {

				double openingAngle12 = Math.toDegrees(Math.acos(gamma1.dot(gamma2) / (gamma1.mag() * gamma2.mag())));
				double openingAngle23 = Math.toDegrees(Math.acos(gamma2.dot(gamma3) / (gamma2.mag() * gamma3.mag())));
				double openingAngle13 = Math.toDegrees(Math.acos(gamma1.dot(gamma3) / (gamma1.mag() * gamma3.mag())));
				double openingAnglepair1 = Math.toDegrees(
						Math.acos(pair1.vector().vect().dot(gamma3) / (pair1.vector().vect().mag() * gamma3.mag())));
				double openingAnglepair2 = Math.toDegrees(
						Math.acos(pair2.vector().vect().dot(gamma1) / (pair2.vector().vect().mag() * gamma1.mag())));
				double openingAnglepair3 = Math.toDegrees(
						Math.acos(pair3.vector().vect().dot(gamma2) / (pair3.vector().vect().mag() * gamma2.mag())));
				double mpi0_low = .137-2*.021;
				double mpi0_high = .137+2*.021;
				double opa1 = 2.5;
				double opa2 = 5;

				hpi0opening.fill(pair1.mass(), openingAngle12);
				hpi0opening.fill(pair2.mass(), openingAngle23);
				hpi0opening.fill(pair3.mass(), openingAngle13);

				hsixsectors.fill(Math.toDegrees(gamma1.phi()), Math.toDegrees(gamma1.theta()));
				hsixsectors.fill(Math.toDegrees(gamma2.phi()), Math.toDegrees(gamma2.theta()));
				hsixsectors.fill(Math.toDegrees(gamma3.phi()), Math.toDegrees(gamma3.theta()));
				hsixsectors2.fill(Math.toDegrees(gamma1.phi()));
				hsixsectors2.fill(Math.toDegrees(gamma2.phi()));
				hsixsectors2.fill(Math.toDegrees(gamma3.phi()));

				if (openingAngle12 > opa1 && openingAnglepair1 > opa2) {
					// if(openingAngle12>4.0&&openingAngle12<30.0){
					Particle pi0 = pair1;
					Vector3 g1 = gamma1;
					Vector3 g2 = gamma2;
					if (phig1 > -30 && phig1 < 30 && phig2 > -30 && phig2 < 30) {
						hsector1.fill(pi0.mass());
					}
					if (phig1 > 30 && phig1 < 90 && phig2 > 30 && phig2 < 90) {
						hsector2.fill(pi0.mass());
					}
					if (phig1 > 90 && phig1 < 150 && phig2 > 90 && phig2 < 150) {
						hsector3.fill(pi0.mass());
					}
					if ((phig1 > 150 || phig1 < -150) && (phig2 > 150 || phig2 < -150)) {
						hsector4.fill(pi0.mass());
					}
					if (phig1 > -150 && phig1 < -90 && phig2 > -150 && phig2 < -90) {
						hsector5.fill(pi0.mass());
					}
					if (phig1 > -90 && phig1 < -30 && phig2 > -90 && phig2 < -30) {
						hsector6.fill(pi0.mass());
					}
					hpair1_precut.fill(pair1.mass());
					if (pair1.mass() > mpi0_low && pair1.mass() < mpi0_high) {
						hpair1.fill(pair1.mass());
						hpair2.fill(pair2.mass());
						hpair2.fill(pair3.mass());

						hpairs.fill(pair1.mass(), pair2.mass());
						hpairgammaopening.fill(threeGamma.mass(), openingAnglepair1);
						htriple.fill(threeGamma.mass());
						htriple2.fill(threeGamma.mass());
						Particle pi0_mass = new Particle();
						pi0_mass.setParticleWithMass(.1349766, (byte) 0, pi0.px(), pi0.py(), pi0.pz(), pi0.vx(),
								pi0.vy(), pi0.vz());
						LorentzVector pi0_4vec = pi0_mass.vector();
						pi0_4vec.add(gamma34v);
						htriple2_40.fill(pi0_4vec.mass());
						htriple2_60.fill(pi0_4vec.mass());
						htriple2_120.fill(pi0_4vec.mass());
						htriple2_180.fill(pi0_4vec.mass());

					}
				} else if (openingAngle23 > opa1 && openingAnglepair2 > opa2) {
					// }else if(openingAngle23>4.0&&openingAngle23<30.0){
					Particle pi0 = pair2;
					Vector3 g1 = gamma2;
					Vector3 g2 = gamma3;
					phig1 = Math.toDegrees(g1.phi());
					phig2 = Math.toDegrees(g2.phi());
					if (phig1 > -30 && phig1 < 30 && phig2 > -30 && phig2 < 30) {
						hsector1.fill(pi0.mass());
					}
					if (phig1 > 30 && phig1 < 90 && phig2 > 30 && phig2 < 90) {
						hsector2.fill(pi0.mass());
					}
					if (phig1 > 90 && phig1 < 150 && phig2 > 90 && phig2 < 150) {
						hsector3.fill(pi0.mass());
					}
					if ((phig1 > 150 || phig1 < -150) && (phig2 > 150 || phig2 < -150)) {
						hsector4.fill(pi0.mass());
					}
					if (phig1 > -150 && phig1 < -90 && phig2 > -150 && phig2 < -90) {
						hsector5.fill(pi0.mass());
					}
					if (phig1 > -90 && phig1 < -30 && phig2 > -90 && phig2 < -30) {
						hsector6.fill(pi0.mass());
					}
					hpair1_precut.fill(pair2.mass());
					if (pair2.mass() > mpi0_low && pair2.mass() < mpi0_high) {
						hpair1.fill(pair2.mass());
						hpair2.fill(pair1.mass());
						hpair2.fill(pair3.mass());
						htriple.fill(threeGamma.mass());
						htriple2.fill(threeGamma.mass());
						hpairs.fill(pair1.mass(), pair2.mass());
						hpairgammaopening.fill(threeGamma.mass(), openingAnglepair2);
						Particle pi0_mass = new Particle();
						pi0_mass.setParticleWithMass(.1349766, (byte) 0, pi0.px(), pi0.py(), pi0.pz(), pi0.vx(),
								pi0.vy(), pi0.vz());
						LorentzVector pi0_4vec = pi0_mass.vector();
						pi0_4vec.add(gamma14v);
						htriple2_40.fill(pi0_4vec.mass());
						htriple2_60.fill(pi0_4vec.mass());
						htriple2_120.fill(pi0_4vec.mass());
						htriple2_180.fill(pi0_4vec.mass());

					}
				} else if (openingAngle13 > opa1 && openingAnglepair3 > opa2) {
					// }else if(openingAngle13>4.0&&openingAngle13<30.0){
					hpair1_precut.fill(pair3.mass());
					Particle pi0 = pair3;
					Vector3 g1 = gamma1;
					Vector3 g2 = gamma3;
					phig1 = Math.toDegrees(g1.phi());
					phig2 = Math.toDegrees(g2.phi());
					if (phig1 > -30 && phig1 < 30 && phig2 > -30 && phig2 < 30) {
						hsector1.fill(pi0.mass());
					}
					if (phig1 > 30 && phig1 < 90 && phig2 > 30 && phig2 < 90) {
						hsector2.fill(pi0.mass());
					}
					if (phig1 > 90 && phig1 < 150 && phig2 > 90 && phig2 < 150) {
						hsector3.fill(pi0.mass());
					}
					if ((phig1 > 150 || phig1 < -150) && (phig2 > 150 || phig2 < -150)) {
						hsector4.fill(pi0.mass());
					}
					if (phig1 > -150 && phig1 < -90 && phig2 > -150 && phig2 < -90) {
						hsector5.fill(pi0.mass());
					}
					if (phig1 > -90 && phig1 < -30 && phig2 > -90 && phig2 < -30) {
						hsector6.fill(pi0.mass());
					}
					if (pair3.mass() > mpi0_low && pair3.mass() < mpi0_high) {
						hpair1.fill(pair3.mass());
						hpair2.fill(pair1.mass());
						hpair2.fill(pair2.mass());
						htriple.fill(threeGamma.mass());
						htriple2.fill(threeGamma.mass());
						hpairs.fill(pair1.mass(), pair2.mass());
						hpairgammaopening.fill(threeGamma.mass(), openingAnglepair3);

						Particle pi0_mass = new Particle();
						pi0_mass.setParticleWithMass(.1349766, (byte) 0, pi0.px(), pi0.py(), pi0.pz(), pi0.vx(),
								pi0.vy(), pi0.vz());
						LorentzVector pi0_4vec = pi0_mass.vector();
						pi0_4vec.add(gamma24v);
						htriple2_40.fill(pi0_4vec.mass());
						htriple2_60.fill(pi0_4vec.mass());
						htriple2_120.fill(pi0_4vec.mass());
						htriple2_180.fill(pi0_4vec.mass());

					}
				}
			}
		}
			if (filter2.isValid(pevent)) {
				Particle gamma21 = pevent.getParticle("[22,0]+[22,1]");
				Particle gamma_1 = pevent.getParticle("[22,0]");
				Particle gamma_2 = pevent.getParticle("[22,1]");
				Vector3 gamma1_vec = gamma_1.vector().vect();
				Vector3 gamma2_vec = gamma_2.vector().vect();
				double openingAngleDegrees = Math
						.toDegrees(Math.acos(gamma1_vec.dot(gamma2_vec) / (gamma1_vec.mag() * gamma2_vec.mag())));
				hmgamma2.fill(gamma21.mass());
				if(openingAngleDegrees>2.0){
					hmgamma21.fill(gamma21.mass());
				}
				LorentzVector sum2 = new LorentzVector();
				sum2.add(gamma_1.vector());
				sum2.add(gamma_2.vector());
				if(openingAngleDegrees>2.0&&sum2.e()>4.0){
					hmgamma22.fill(gamma21.mass());
				}

			}

			if (filter3.isValid(pevent)) {
				Particle gamma31 = pevent.getParticle("[22,0]+[22,1]+[22,2]");
				hmgamma3.fill(gamma31.mass());
				hmgamma3_60.fill(gamma31.mass());
				hmgamma3_120.fill(gamma31.mass());
				hmgamma3_180.fill(gamma31.mass());
				hmgamma3_480.fill(gamma31.mass());

			}

			if (filter4.isValid(pevent)) {
				Particle gamma4 = pevent.getParticle("[22,0]+[22,1]+[22,2]+[22,3]");
				hmgamma4.fill(gamma4.mass());
			}

			if (filter5.isValid(pevent)) {
				Particle gamma5 = pevent.getParticle("[22,0]+[22,1]+[22,2]+[22,3]+[22,4]");
				hmgamma5.fill(gamma5.mass());
			}

			if (filter6.isValid(pevent)) {
				Particle mpipi = pevent.getParticle("[211]+[-211]");
				hmgamma2.fill(mpipi.mass());
			}

			if (filter7.isValid(pevent)) {
				Particle gamma21 = pevent.getParticle("[211]+[-211]+[22,0]+[22,1]");
				hmgamma2.fill(gamma21.mass());
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

		int nEvents = reader.getSize();
		System.out.println("NEvents" + nEvents);
		JFrame frame = new JFrame("KPP Data Analysis");
		frame.setSize(1400, 800);

		int eventCounter = 0;
		EmbeddedCanvas can = new EmbeddedCanvas();
		hpair1 = new H1F("hpair1", "", 500, 0.0, 1.1);
		hpair1_precut = new H1F("hpair1_precut", "", 500, 0.0, 1.1);
		hpair2 = new H1F("hpair2", "", 500, 0.0, 1.1);
		htriple = new H1F("htriple", "", 120, 0.6, 1.2);
		htriple2 = new H1F("htriple", "", 240, 0.0, 1.2);

		hpairs = new H2F("hpairs", "", 400, 0.0, 1.1, 400, 0.0, 1.1);
		hmm2 = new H1F("hm2", "", 400, -.5, 1.1);
		hpairgammaopening = new H2F("hpairgammaopening", 120, 0.0, 1.2, 100, 0, 70);
		hpi0opening = new H2F("hpi0opening", 120, 0.0, .6, 100, 0, 70);

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
		hpairs.setTitleX("M(#gamma#gamma) (pair1)");
		hpairs.setTitleY("M(#gamma#gamma) (pair2)");

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

		EmbeddedCanvas can2 = new EmbeddedCanvas();
		hmgamma2 = new H1F("hg3", "", 120, 0.0, 1.1);
		hmgamma2.setTitleX("M(#gamma#gamma)");
		hmgamma2.setTitleY("Counts");
		hmgamma21 = new H1F("hg3", "", 120, 0.0, 1.1);
		hmgamma21.setTitleX("M(#gamma#gamma)");
		hmgamma21.setTitleY("Counts");
		hmgamma21.setFillColor(38);
		hmgamma22 = new H1F("hg3", "", 120, 0.0, 1.1);
		hmgamma22.setTitleX("M(#gamma#gamma)");
		hmgamma22.setTitleY("Counts");
		hmgamma22.setFillColor(32);
		hmgamma3 = new H1F("hg3", "", 120, 0.0, 1.2);
		hmgamma3.setTitleX("M(#gamma#gamma#gamma)");
		hmgamma3.setTitleY("Counts");
		hmgamma4 = new H1F("hg4", "", 120, 0.0, 1.1);
		hmgamma4.setTitleX("M(#gamma#gamma#gamma#gamma)");
		hmgamma4.setTitleY("Counts");
		hmgamma5 = new H1F("hg5", "", 120, 0.0, 1.1);
		hmgamma5.setTitleX("M(#gamma#gamma#gamma#gamma#gamma)");
		hmgamma5.setTitleY("Counts");

		EmbeddedCanvas can3 = new EmbeddedCanvas();
		hmgamma3_60 = new H1F("hmgamma3_60", "", 60, 0.0, 1.2);
		hmgamma3_60.setTitleX("M(#gamma#gamma#gamma)");
		hmgamma3_60.setTitleY("Counts");
		hmgamma3_120 = new H1F("hmgamma3_120", "", 120, 0.0, 1.2);
		hmgamma3_120.setTitleX("M(#gamma#gamma#gamma)");
		hmgamma3_120.setTitleY("Counts");
		hmgamma3_180 = new H1F("hmgamma3_180", "", 180, 0.0, 1.2);
		hmgamma3_180.setTitleX("M(#gamma#gamma#gamma)");
		hmgamma3_180.setTitleY("Counts");
		hmgamma3_480 = new H1F("hmgamma3_480", "", 480, 0.0, 1.2);
		hmgamma3_480.setTitleX("M(#gamma#gamma#gamma)");
		hmgamma3_480.setTitleY("Counts");

		can3.divide(2, 2);
		can3.cd(0);
		can3.draw(hmgamma3_60);
		can3.cd(1);
		can3.draw(hmgamma3_120);
		can3.cd(2);
		can3.draw(hmgamma3_180);
		can3.cd(3);
		can3.draw(hmgamma3_480);

		EmbeddedCanvas can4 = new EmbeddedCanvas();
		htriple2_40 = new H1F("htriple2_40", "", 40, 0.0, 1.2);
		htriple2_40.setTitleX("M(#gamma#gamma#gamma)");
		htriple2_40.setTitleY("Counts");
		htriple2_60 = new H1F("htriple2_60", "", 60, 0.0, 1.2);
		htriple2_60.setTitleX("M(#gamma#gamma#gamma)");
		htriple2_60.setTitleY("Counts");
		htriple2_120 = new H1F("htriple2_120", "", 120, 0.0, 1.2);
		htriple2_120.setTitleX("M(#gamma#gamma#gamma)");
		htriple2_120.setTitleY("Counts");
		htriple2_180 = new H1F("htriple2_180", "", 180, 0.0, 1.2);
		htriple2_180.setTitleX("M(#gamma#gamma#gamma)");
		htriple2_180.setTitleY("Counts");

		can4.divide(2, 2);
		can4.cd(0);
		can4.draw(htriple2_40);
		can4.cd(1);
		can4.draw(htriple2_60);
		can4.cd(2);
		can4.draw(htriple2_120);
		can4.cd(3);
		can4.draw(htriple2_180);
		hsixsectors = new H2F("hsixsectors", 360, -180, 180, 120, 0, 40);
		hsixsectors2 = new H1F("hsixsectors2", 360, -180, 180);
		hsector1 = new H1F("hsector1", 200, 0, .40);
		hsector2 = new H1F("hsector2", 200, 0, .40);
		hsector3 = new H1F("hsector3", 200, 0, .40);
		hsector4 = new H1F("hsector4", 200, 0, .40);
		hsector5 = new H1F("hsector5", 200, 0, .40);
		hsector6 = new H1F("hsector6", 200, 0, .40);
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
		/*
		 * can4.cd(1); can4.draw(htriple2_60); can4.cd(2);
		 * can4.draw(htriple2_120); can4.cd(3); can4.draw(htriple2_180);
		 */
		/*
		 * hpipi = new H1F("hpipi", "", 500, 0.0, 1.1);
		 * hpipi.setTitleX("M(#pi^+#pi^-)"); hpipi.setTitleY("Counts");
		 * hpipigammagamma = new H1F("hpipigg", "", 500, 0.0, 1.1);
		 * hpipigammagamma.setTitleX("M(#pi^+#pi^-#gamma#gamma)");
		 * hpipigammagamma.setTitleY("Counts");
		 */

		can2.divide(2, 2);
		can2.cd(0);
		can2.draw(hmgamma2);
		can2.draw(hmgamma21,"same");
		can2.draw(hmgamma22,"same");
		can2.cd(1);
		can2.draw(hmgamma3);
		can2.cd(2);
		can2.draw(hmgamma4);
		can2.cd(3);
		can2.draw(hmgamma5);
		// can2.cd(4);
		// can2.draw(hpipi);
		// can2.cd(5);
		// can2.draw(hpipigammagamma);
		selector.setMinimumPhotonEnergy(.5);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(can, "Three Photons");
		tabbedPane.add(can4, "Three Photons Rebinned w/cuts");
		tabbedPane.add(can3, "Three Photons Rebinned");
		tabbedPane.add(can2, "Multi Photons");
		tabbedPane.add(can5, "Pi0 angular distribution");
		frame.setLayout(new BorderLayout());
		frame.add(tabbedPane, BorderLayout.CENTER);
		JProgressBar bar = new JProgressBar();
		bar.setBorder(new TitledBorder("Progress"));
		frame.add(bar, BorderLayout.PAGE_END);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		int totalNevents = 0;
		String directory = "/Users/wphelps/Desktop/rga/temp/";
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
					System.out.println("Hipo shit the bed on file:" + file.getName() + " at event:" + inFileCounter);
					System.out.println(e.getStackTrace());
				}
				if (eventCounter % 5000 == 0) {
					can.update();
					can2.update();
					can3.update();
					can4.update();
					can5.update();
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
