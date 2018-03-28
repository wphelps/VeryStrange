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
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

public class DeltaAnalyzer {
	static H1F hpid;
	static H1F hnpp;
	static H1F hnnp;
	static H1F hpipi;
	static H1F hpi0;
	static H1F hppip;
	static H1F hppim;
	static H1F hppi0;
	static H1F hmissmass;

	static H2F hpos_beta_p;
	static H2F hneg_beta_p;

	static ParticleSelectorKPP selector = new ParticleSelectorKPP(6.4);
	static EventFilter chargedParticles = new EventFilter("X+:X-");
	static EventFilter ppi0Filter = new EventFilter("22:22:2212");
	static EventFilter ppi0eFilter = new EventFilter("22:22:2212:11");
	static EventFilter ppimFilter = new EventFilter("-211:2212");
	static EventFilter ppipFilter = new EventFilter("211:2212");

	static void processEvent(DataEvent event) {
		RecEvent recevent = selector.getRecEvent(event);
		PhysicsEvent pevent = recevent.getReconstructed();
		if (chargedParticles.isValid(pevent)) {
			hnnp.fill(pevent.countByCharge(-1));
			hnpp.fill(pevent.countByCharge(1));
			for(int i=0; i<pevent.count();i++){
				hpid.fill(pevent.getParticle(i).pid());
				//hpos_beta_p.fill(pevent.getParticle(i));
			}
			
		}
		if (ppi0Filter.isValid(pevent)||ppi0eFilter.isValid(pevent)) {
			Particle pipi = pevent.getParticle("[22,0]+[22,1]+[2212]");
			Particle pi0 = pevent.getParticle("[22,0]+[22,1]");
			Particle missmass = pevent.getParticle("[b]+[t]-[22,0]-[22,1]-[2212]");
			if(pipi.mass2()>0.0&&pi0.mass2()>0.0){
				hpipi.fill(Math.sqrt(pipi.mass2()));
				hpi0.fill(pi0.mass());
				if(Math.abs(pi0.mass()-.137)<.075) {
					hppi0.fill(pipi.mass());
					hmissmass.fill(missmass.mass());
				}
			}
		}
		if(ppimFilter.isValid(pevent)) {
			Particle ppim = pevent.getParticle("[-211]+[2212]");
			if(ppim.mass2()>0.0) {
				hppim.fill(ppim.mass());
			}
		}
		if(ppipFilter.isValid(pevent)) {
			Particle ppip = pevent.getParticle("[211]+[2212]");
			if(ppip.mass2()>0.0) {
				hppip.fill(ppip.mass());
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
		GStyle.getH1FAttributes().setOptStat("110");
		GStyle.getAxisAttributesZ().setLog(true);

		HipoDataSource reader = new HipoDataSource();
		int eventCounter = 0;
		selector.setMinimumPhotonEnergy(.02);
		String directory = "/Users/wphelps/Desktop/rga/phys/";
		hpid = new H1F("hpid", "", 4600, -2300, 2300);
		hnnp = new H1F("hnnp", "", 1000, 0, 10);
		hnpp = new H1F("hnpp", "", 1000, 0, 10);
		hpipi = new H1F("hpipi","",100,.8,2.0);
		hpi0 = new H1F("hpi0","",100,0,1.0);
		hppi0 = new H1F("hppi0","",100,.8,2.0);
		hppip = new H1F("hppip","",100,.8,2.0);
		hppim = new H1F("hppim","",100,.8,2.0);
		hmissmass = new H1F("hmissmass","",200,-1.0,2.0);

		hpos_beta_p = new H2F("hpos_beta_p",50,0,1.5,50,0,8);
		hneg_beta_p = new H2F("hneg_beta_p",50,0,1.5,50,0,8);
		hpid.setTitleX("PID");
		hnpp.setTitleX("# Positive Tracks");
		hnnp.setTitleX("# Negative Tracks");
		hpid.setTitleY("Counts");
		hnnp.setTitleY("Counts");
		hnpp.setTitleY("Counts");
		hpipi.setTitleX("M(p#gamma#gamma)");
		hpi0.setTitleX("M(#gamma#gamma)");
		hppi0.setTitleX("M(p#pi^0)");
		hmissmass.setTitleX("MM(p#pi^0)");
		hppip.setTitleX("M(p#pi^+)");
		hppim.setTitleX("M(p#pi^-)");

		EmbeddedCanvas can = new EmbeddedCanvas();
		can.divide(3, 3);
		can.cd(0);
		can.draw(hpid);
		can.cd(1);
		can.draw(hnnp);
		can.cd(2);
		can.draw(hnpp);
		can.cd(3);
		can.draw(hpipi);
		can.cd(4);
		can.draw(hpi0);
		can.cd(5);
		can.draw(hppi0);
		can.cd(6);
		can.draw(hmissmass);
		can.cd(7);
		can.draw(hppip);
		can.cd(8);
		can.draw(hppim);	
		
		JFrame frame = new JFrame("RGA Data Analysis");
		frame.setSize(1400, 800);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(can, "Delta Analyzer");
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
