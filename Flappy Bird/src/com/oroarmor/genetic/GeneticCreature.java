package com.oroarmor.genetic;

import java.io.Serializable;
import java.util.ArrayList;

import com.oroarmor.network.NeurNet;

public abstract class GeneticCreature implements Serializable {

	private static final long serialVersionUID = 1L;

	public int[] brainConfig;
	public NeurNet brain;
	public int parentNum;
	public float fitness;

	public GeneticCreature(int[] _brainConfig, int _parentNum) {
		brainConfig = _brainConfig;
		parentNum = _parentNum;
		fitness = 0;
		brain = new NeurNet(brainConfig);
	}

	public abstract void randomize();

	public abstract void setParent(int iding);

	public abstract void run(float[] inputs);

	public abstract void setup();

	public abstract GeneticCreature copy();

	public abstract boolean check(Object o);

	public abstract void calculateFitness(Object o, float[] addlFitnessComponents);

	public abstract GeneticCreature cross(ArrayList<GeneticCreature> nextGen, float[] fitnessPercent,
			float[] evolvingConditions);

	public abstract float getFitness();

}
