package Model;

import AgentGridMin.SqList;

import java.util.Arrays;

/**
 * Created by rafael on 11/9/16.
 */
public class CONST_AND_FUNCTIONS {

    static final boolean NORMAL_CELLS_ACTIVE=true;
    static final boolean TUMOR_CELLS_ACTIVE=true;
    static final boolean PDL1_CELLS_ACTIVE=true;
    static final boolean ACIDIC_CELLS_ACTIVE=false;
    static final boolean NECRO_CELLS_ACTIVE=false;
    static final boolean VESSELS_ACTIVE=true;
    static final boolean T_CELLS_ACTIVE=true;
    static final boolean OXYGEN_ACTIVE=true;
    static final boolean GLUCOSE_ACTIVE=true;
    static final boolean ACID_ACTIVE=true;
    static final boolean DRUG_ACTIVE=true;

    static final int SEED_TIME = 500;
    static final int IMMUNE_TIME = 700;
    static final int DRUG_TIME = 2000;
    static final int STOP_MODEL_TIME=200000;

    static final double BIN_LENGTH = 10.0; //microns
    static final double BIN_VOLUME = BIN_LENGTH*BIN_LENGTH; //n.b. 2D
    static final int NUM_BINS_1D = 300;
    static final double DIFFUSE_TIME_LENGTH = 0.01;
    static final double DIFFUSE_DT = 0.010;

    //cell constants
    static final public double MAX_POP=BIN_LENGTH*BIN_LENGTH*NUM_BINS_1D;
    static final double TIME_STEP=0.2; //days
    static final double TUMOR_PROLIF_RATE=0.7*TIME_STEP;
    static final double PDL1_PROLIF_RATE=TUMOR_PROLIF_RATE*0.995;
    static final double NECROTIC_DECAY_RATE=0.05*TIME_STEP;
    static final double NORMAL_PROLIF_RATE=0.4*TIME_STEP;
    static final double NORMAL_DEATH_RATE=0.35*TIME_STEP;

    static final double TUMOR_DEATH_RATE = 0.02*TIME_STEP;
    static final double PDL1_WEAKNESS = 0.80; //max = 1, min = 0

    static final double TUMOR_LOW_OXYGEN_DEATH_THRESHOLD = 0.1;
    static final double TUMOR_HIGH_ACID_DEATH_THRESHOLD = 0.1;
//    static final double IMMUNE_CELL_MAX_HYPOXIC_KILL_RATE_REDUCTION = 0.1;

    static final double DRUG_EFFICACY=100000.0; //DRUG_EFFICACY*DRUG_CONC represents the modification to the immune kill rate. 1% of drug is useful, and the other factor represents reduction due to PD-L1.
    static final double IMMUNE_KILL_RATE_SHAPE_FACTOR=1;
    static final double IMMUNE_KILL_RATE=5.8*TIME_STEP;
    static final double NORMAL_HYPOXIC_THRESHOLD=1;
    static final double NORMAL_HYPOXIC_SCALE_FACTOR=0.01;
    static final double TUMOR_HYPOXIC_THRESHOLD=NORMAL_HYPOXIC_THRESHOLD*0.8;
    static final double MAX_HYPOXIC_DEATH_RATE=0.5;
    static final double VESSELS_TO_TCELLS=1*TIME_STEP;
    static final double TCELL_MOVE_RATE=0.4*TIME_STEP;
    static final double TCELL_DEATH_RATE=0.01*TIME_STEP;
    static final int TCELL_TURNS= 2;

    //diffusible constants
    static final double OXYGEN_DIFFUSION_RATE = 0.1;
    static final double GLUCOSE_DIFFUSION_RATE = 0.1;
    static final double ACID_DIFFUSION_RATE = 0.1;
    static final double DRUG_DIFFUSION_RATE = 0.2;

    static final double OXYGEN_PRODUCTION_RATE=0.2; //per dt, per unit density
    static final double GLUCOSE_PRODUCTION_RATE=0.2; //per dt, per unit density
    static final double DRUG_PRODUCTION_RATE = 0.2;
    static final double ACID_PRODUCTION_RATE=0.01; //per dt, per unit density

    //Govern gluc/oxy on
    static final double GLUCOSE_THRESHOLD = 0.05;
    static final double OXYGEN_THRESHOLD = 0.05;
    static final double CONSUMPTION_SCALE=0.04;
    static final double TUMOR_OXYGEN_CONSUMPTION=0.0008*CONSUMPTION_SCALE;
    static final double TUMOR_GLUCOSE_CONSUMPTION=0.0008*CONSUMPTION_SCALE;
    static final double NORMAL_OXYGEN_CONSUMPTION = 0.0004*CONSUMPTION_SCALE;
    static final double NORMAL_GLUCOSE_CONSUMPTION = 0.0004*CONSUMPTION_SCALE;
    static final double NORMAL_DRUG_CONSUMPTION = 0.0015*CONSUMPTION_SCALE;
    static final double PDL1_DRUG_CONSUMPTION = 0;//0.0015*CONSUMPTION_SCALE;

    static double DiffusibleDeath(double cellPop, double diffusible,double threshold,double scaleConst){
        return diffusible>threshold?0:cellPop*(1-diffusible/threshold)*scaleConst;
    }

    static double modifiedBirthRate(double birthRate, double oxy, double gluc) {
        double oxyPenalty = 1.0;
        double glucPenalty = 1.0;
        if (gluc < GLUCOSE_THRESHOLD)
        {
            glucPenalty = 0.1;
        }
        if (oxy < OXYGEN_THRESHOLD)
        {
            oxyPenalty = 0.1;
        }
        return birthRate * glucPenalty * oxyPenalty;
    }

    //returns number of births from metabolic birth rate
    static double MetabolicBirth(double cellPop, double totalPop, double maxProlifRate, double oxygen, double glucose, double glucoseIn, double oxygenIn, double acidRate) {
        // determine cells that legally proliferate with oxygen and glucose (=perfect proliferation)
        double legals_1 = 0;
        double legals_2 = 0;
        //double hypoxics = 0;
        //double acid = 0;
        if (oxygenIn * cellPop < oxygen && glucoseIn * cellPop < glucose) { // all cells can proliferate perfectly
            legals_1 = cellPop;
        }
        else if (oxygenIn * cellPop > oxygen && glucoseIn * cellPop > glucose) { //only some cells proliferate perfectly
            legals_1 = Math.min(oxygen / oxygenIn, glucose / glucoseIn);
        }
        // update oxygen, glucose and cellPop values
        oxygen =+ -oxygenIn*legals_1;
        glucose =+ -glucoseIn*legals_1;
        cellPop =+ - legals_1;
        //determine cells that legally proliferate with only glucose(acidic proliferation)
        if (cellPop < glucose/glucoseIn) {
            legals_2 = cellPop;
        }
        else if (cellPop > glucose/glucoseIn) {
            legals_2 = glucose/glucoseIn;
        }
        //acid = acidRate*legals_2;
        return (legals_1+legals_2) * maxProlifRate * (1 - totalPop / MAX_POP);
    }


    //returns pop to be born
    static double Birth(double cellPop,double totalPop,double maxProlifRate) {
        return cellPop*(maxProlifRate * (1 - totalPop / MAX_POP));
    }
    //returns pop to be killed
    static double Death(double cellPop,double deathRate){
        return cellPop*deathRate;
    }

    //add hypoxic death to Death function

    static void NecroDeath(double[] NecroSwap,int i,double deadCellPop){
        NecroSwap[i]+=deadCellPop;
    }
    //gets the population that will migrate, based on the number of cells born
    static double MigrantPop(double totalPop,double numBorn){
        return numBorn * (totalPop / MAX_POP);
    }

    //executes migration, returns number of migrants
    static double Migrate( TumorModel myModel, double[] swap,int x, int y, double popToMigrate, SqList neighborhood, double[] storePops){
        Arrays.fill(storePops,0);
        double myTotal = myModel.totalPops[myModel.I(x, y)];
        double surroundingDiff = 0;
        for (int i = 0; i < neighborhood.length; i++) {
            int checkX = neighborhood.Xsq(i) + x;
            int checkY = neighborhood.Ysq(i) + y;
            if (myModel.WithinGrid(checkX, checkY)) {
                double neighborDelta = myTotal-myModel.totalPops[myModel.I(checkX, checkY)];
                if(neighborDelta>=0) {
                    storePops[i] = neighborDelta;
                    surroundingDiff += neighborDelta;
                }
            }
        }
        if(surroundingDiff>0) {
            for (int i = 0; i < storePops.length; i++) {
                if (storePops[i] > 0) {
                    swap[myModel.I(neighborhood.Xsq(i) + x, neighborhood.Ysq(i) + y)] += popToMigrate * (storePops[i] / surroundingDiff);
                }
            }
        }
        return surroundingDiff>0?popToMigrate:0;
    }

    static double HypoxicDeath(double cellPop, double oxygen, double gluc, double acid){
            double hypDeath = 0.0;
            if (oxygen < TUMOR_LOW_OXYGEN_DEATH_THRESHOLD)
            {
                hypDeath += (1-oxygen/ TUMOR_LOW_OXYGEN_DEATH_THRESHOLD)*.01;
            }
            if (acid > TUMOR_HIGH_ACID_DEATH_THRESHOLD)
            {
                hypDeath += 0.0;
            }
            return  cellPop*hypDeath;
    }

//    static void TCellDiffusion(TumorModel myModel,ImmuneCells tCells, int x,int y,double popToMigrate){
//
//    }

//    static void MigrateTCells(TumorModel myModel,double[] swap,int x,int y,double popToMigrate,SqList neighborhood,double[] storePops,double cellSize){
//        double totalPop=popToMigrate;
//        double movedPop=0;
//        Arrays.fill(storePops,0);
//        double totSpace=0;
//        for(int i=0;i<neighborhood.length;i++){
//            int checkX = neighborhood.Xsq(i) + x;
//            int checkY = neighborhood.Ysq(i) + y;
//            if (myModel.WithinGrid(checkX, checkY)) {
//                double space = MAX_POP - myModel.totalPops[myModel.I(checkX, checkY)];
//                if(space>0) {
//                    storePops[i] = space;
//                    totSpace += space;
//                }
//            }
//        }
//        popToMigrate=totSpace/cellSize>popToMigrate?popToMigrate:totSpace/cellSize;
//        for(int i=0;i<neighborhood.length;i++){
//            if(storePops[i]>0) {
//                swap[myModel.I(neighborhood.Xsq(i) + x, neighborhood.Ysq(i) + y)] += popToMigrate * (storePops[i] / totSpace);
//                movedPop+=popToMigrate * (storePops[i] / totSpace);
//            }
//        }
//        if(Math.abs(totalPop-movedPop)>1){
//            throw new RuntimeException("tcell movement changed the pop"+(totalPop-movedPop));
//        }
//    }
}
