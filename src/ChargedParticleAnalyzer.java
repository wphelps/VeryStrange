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

public class ChargedParticleAnalyzer {
	static H1F hpid;
	static H1F hnpp;
	static H1F hnnp;
	static H1F hpipi;
	static H2F hpos_beta_p;
	static H2F hneg_beta_p;

	static ParticleSelectorKPP x = new ParticleSelectorKPP(6.4);
	static EventFilter chargedParticles = new EventFilter("X+:X-");
	static EventFilter ppim = new EventFilter("-211:2212:X+:X-:Xn");

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
		if (ppim.isValid(pevent)) {
			Particle pipi = pevent.getParticle("[-211]+[2212]");
			if(pipi.mass()>0.0){
				hpipi.fill(Math.sqrt(pipi.mass2()));
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
		selector.setMinimumPhotonEnergy(.05);
		String directory = "/Users/wphelps/Desktop/rga/temp/";
		hpid = new H1F("hpid", "", 4600, -2300, 2300);
		hnnp = new H1F("hnnp", "", 1000, 0, 10);
		hnpp = new H1F("hnpp", "", 1000, 0, 10);
		hpipi = new H1F("hpipi","",200,0,2.0);
		hpos_beta_p = new H2F("hpos_beta_p",50,0,1.5,50,0,8);
		hneg_beta_p = new H2F("hneg_beta_p",50,0,1.5,50,0,8);
		hpid.setTitleX("PID");
		hnpp.setTitleX("# Positive Tracks");
		hnnp.setTitleX("# Negative Tracks");
		hpid.setTitleY("Counts");
		hnnp.setTitleY("Counts");
		hnpp.setTitleY("Counts");
		hpipi.setTitleX("M(#pi^+#pi^-)");

		EmbeddedCanvas can = new EmbeddedCanvas();
		can.divide(2, 2);
		can.cd(0);
		can.draw(hpid);
		can.cd(1);
		can.draw(hnnp);
		can.cd(2);
		can.draw(hnpp);
		can.cd(3);
		can.draw(hpipi);
			
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
