import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.detector.DetectorTrack;
import org.jlab.clas.pdg.PDGDatabase;
import org.jlab.clas.physics.EventFilter;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.clas.physics.RecEvent;
import org.jlab.clas.physics.Vector3;
import org.jlab.detector.base.DetectorType;
import org.jlab.geom.prim.Vector3D;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.evio.EvioDataBank;
import org.jlab.io.evio.EvioDataEvent;
import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoNode;

/**
 *
 * @author gavalian
 */
public class ParticleSelectorKPP {
    
    private final   EventFilter filter = new EventFilter();
    protected Double  beamEnergy  = 11.0;
    double minimumPhotonEnergy = 0.0;
    double minimumFTOFMatchAngle = -100.0;
    
    
    public double getMinimumFTOFMatchAngle() {
		return minimumFTOFMatchAngle;
	}

	public void setMinimumFTOFMatchAngle(double minimumFTOFMatchAngle) {
		this.minimumFTOFMatchAngle = minimumFTOFMatchAngle;
	}

	public ParticleSelectorKPP(double beam){
        this.beamEnergy = beam;
        this.filter.setFilter("X+:X-:Xn");
    }
    
    /**
     * Returns PhysicsEvent object with reconstructed particles.
     * @param event - DataEvent object
     * @return PhysicsEvent : event containing particles.
     */
    public PhysicsEvent  getPhysicsEvent(DataEvent  event){
        //if(event instanceof EvioDataEvent){
            //System.out.println("   CHECK FOR  PARTICLE = " + event.hasBank("EVENT::particle"));
        if(event.hasBank("REC::Particle")){
            DataBank evntBank = (DataBank) event.getBank("REC::Particle");
            int nrows = evntBank.rows();
            PhysicsEvent  physEvent = new PhysicsEvent();
                physEvent.setBeam(this.beamEnergy);
                for(int loop = 0; loop < nrows; loop++){
                    
                    int pid    = evntBank.getInt("pid", loop);
                    int status = evntBank.getInt("status", loop);
                    
                    if(PDGDatabase.isValidPid(pid)==true){
                        Particle part = new Particle(
                                evntBank.getInt("pid", loop),
                                evntBank.getFloat("px", loop),
                                evntBank.getFloat("py", loop),
                                evntBank.getFloat("pz", loop),
                                evntBank.getFloat("vx", loop),
                                evntBank.getFloat("vy", loop),
                                evntBank.getFloat("vz", loop));
                        
                        if(status>0){
                        	
                        	if(part.pid()==22&&part.p()>minimumPhotonEnergy){
                        		List<DetectorResponse> hits = DetectorResponse.readHipoEvent(event, 
                        	            "FTOF::hits", DetectorType.FTOF);
                    			Vector3 photonVector = part.vector().vect();
                    			boolean ftofHit = false;
                        		for(DetectorResponse hit : hits){
                        			Vector3D hitPosition = hit.getPosition();
                        			Vector3 ftofHitVector = new Vector3();
                        			//assuming target is at 0,0,0....
                        			ftofHitVector.setXYZ(hitPosition.x(), hitPosition.y(), hitPosition.z());
                        			if(Math.toDegrees(Math.acos(photonVector.dot(ftofHitVector)/(photonVector.mag()*ftofHitVector.mag())))<minimumFTOFMatchAngle){
                        				//ftofHit = true;
                        				//System.out.println("Ftof hit!:"+Math.acos(photonVector.dot(ftofHitVector)/(photonVector.mag()*ftofHitVector.mag())));
                        			}
                        		}
                        		if(!ftofHit){
                        			physEvent.addParticle(part);
                        		}
                        	}else if(part.pid()!=22){
                        		physEvent.addParticle(part);
                        	}
                        }
                    } else {
                    	/*
                       // Particle part = new Particle();
                        int charge = evntBank.getInt("charge", loop);
                        part.setParticleWithMass(evntBank.getFloat("mass", loop),
                                (byte) charge,
                                evntBank.getFloat("px", loop),
                                evntBank.getFloat("py", loop),
                                evntBank.getFloat("pz", loop),
                                evntBank.getFloat("vx", loop),
                                evntBank.getFloat("vy", loop),
                                evntBank.getFloat("vz", loop)
                        );
                        Particle part = new Particle(
                                evntBank.getInt("pid", loop),
                                evntBank.getFloat("px", loop),
                                evntBank.getFloat("py", loop),
                                evntBank.getFloat("pz", loop),
                                evntBank.getFloat("vx", loop),
                                evntBank.getFloat("vy", loop),
                                evntBank.getFloat("vz", loop));
                        
                        if(status>0){
                        	if(part.pid()==22&&part.p()>minimumPhotonEnergy){
                        		physEvent.addParticle(part);
                        	}else if(part.pid()!=22){
                        		physEvent.addParticle(part);
                        	}
                        }
                        */
                    }
                   
                }
                return physEvent;
            }
            
        //}
        return new PhysicsEvent(this.beamEnergy);
    }
    
    public PhysicsEvent  getGeneratedEvent(DataEvent event){
        PhysicsEvent physEvent = new PhysicsEvent();
        physEvent.setBeam(this.beamEnergy);
        if(event.hasBank("GenPart::true")){
            EvioDataBank evntBank = (EvioDataBank) event.getBank("GenPart::true");
            int nrows = evntBank.rows();
            for(int loop = 0; loop < nrows; loop++){
                Particle genParticle = new Particle(
                        evntBank.getInt("pid", loop),
                        evntBank.getDouble("px", loop)*0.001,
                        evntBank.getDouble("py", loop)*0.001,
                        evntBank.getDouble("pz", loop)*0.001,
                        evntBank.getDouble("vx", loop),
                        evntBank.getDouble("vy", loop),
                        evntBank.getDouble("vz", loop));
                if(genParticle.p()<10.999&&
                        Math.toDegrees(genParticle.theta())>2.0){
                    physEvent.addParticle(genParticle);    
                }
            }
        }
        return physEvent;
    }
        
    public PhysicsEvent createEvent(DataEvent event){
        PhysicsEvent  recEvent = this.getPhysicsEvent(event);
        PhysicsEvent  genEvent = this.getGeneratedEvent(event);
        for(int i = 0; i < genEvent.count();i++){
            recEvent.mc().add(genEvent.getParticle(i));
        }
        return recEvent;
    }
    
    public PhysicsEvent createEvent(HipoEvent event){
        PhysicsEvent physEvent = new PhysicsEvent();
        physEvent.setBeam(this.beamEnergy);
        if(event.hasGroup(20)==true){
            Map<Integer,HipoNode>  items = event.getGroup(20);
            int nentries = items.get(1).getDataSize();
            for(int i = 0; i < nentries; i++){
                int pid = items.get(1).getInt(i);
                double px = items.get(2).getFloat(i*3+0);
                double py = items.get(2).getFloat(i*3+1);
                double pz = items.get(2).getFloat(i*3+2);
                Particle particle = new Particle(pid,px,py,pz);
                double vx = items.get(3).getShort(i*3+0)*100.0;
                double vy = items.get(3).getShort(i*3+1)*100.0;
                double vz = items.get(3).getShort(i*3+2)*100.0;
                particle.vertex().setXYZ(vx, vy, vz);
                physEvent.mc().add(particle);
            }
        }
        return physEvent;
    }
    
    public RecEvent getRecEvent(DataEvent event){
        
        PhysicsEvent rev = getPhysicsEvent(event);
        PhysicsEvent gev = getGeneratedEvent(event);
        RecEvent  recEvent = new RecEvent(this.beamEnergy);
        
        for(int i = 0; i < rev.count();i++){
            recEvent.getReconstructed().addParticle(rev.getParticle(i));
        }
        
        for(int i = 0; i < gev.count();i++){
            recEvent.getGenerated().addParticle(gev.getParticle(i));
        }
        
        return recEvent;
    }

	public double getMinimumPhotonEnergy() {
		return minimumPhotonEnergy;
	}

	public void setMinimumPhotonEnergy(double minimumPhotonEnergy) {
		this.minimumPhotonEnergy = minimumPhotonEnergy;
	}
}