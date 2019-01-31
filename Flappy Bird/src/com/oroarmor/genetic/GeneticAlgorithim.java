package com.oroarmor.genetic;

import java.io.*;
import java.util.*;

public class GeneticAlgorithim<T extends GeneticCreature> {

	public ArrayList<T> currentGen;
	public ArrayList<T> nextGen;
	public int genNum;
	int numCreatures;
	public T bestCreature;

	public GeneticAlgorithim(int _numCreatures, T creatureType) {
		currentGen = new ArrayList<T>();
		nextGen = new ArrayList<T>();
		numCreatures = _numCreatures;
		for (int i = 0; i < _numCreatures; i++) {
			add(creatureType, i);
		}
		setup();
		genNum = 0;
		bestCreature = null;
		File f = new File("data/bestCreature.ser");
		if(f.exists() && !f.isDirectory()) {
		  importBest();
		}
	}

	private void setup() {
		for (T creature : currentGen) {
			creature.setup();
		}

	}

	@SuppressWarnings("unchecked")
	private void add(T t, int iding) {
		t.randomize();
		if (t.parentNum == -1) {
			t.setParent(iding);
		}
		T newCreature = (T) t.copy();
		newCreature.randomize();
		currentGen.add(newCreature);
	}

	public void run(float[] inputs) {
		for (T creature : currentGen) {
			creature.run(inputs.clone());
		}
		for (T creature : nextGen) {
			creature.run(inputs.clone());
		}
	}

	public void check(Object o, float[] addlFitnessComponents) {
		// TODO Auto-generated method stub
		for (T creature : currentGen) {
			if (creature.check(o)) {
				creature.calculateFitness(o, addlFitnessComponents);
				nextGen.add(creature);
			}
		}
		currentGen.removeAll(nextGen);
		if (currentGen.size() == 0) {
			evolve(o, addlFitnessComponents);
		}
	}

	@SuppressWarnings("unchecked")
	public void evolve(Object o, float[] addlFitnessComponents) {

		if (currentGen.size() != 0) {
			for (T creature : currentGen) {
				creature.calculateFitness(o, addlFitnessComponents);
				nextGen.add(creature);
			}
		}

		nextGen.sort(Comparator.comparing(GeneticCreature::getFitness));

		if (bestCreature == null) {
			bestCreature = nextGen.get(numCreatures - 1);
		} else if (bestCreature.getFitness() < nextGen.get(numCreatures - 1).getFitness()) {
			bestCreature = nextGen.get(numCreatures - 1);
		}

		float totalFitness = 0;

		for (int i = 0; i < nextGen.size(); i++) {
			totalFitness += nextGen.get(i).getFitness();
		}

		float[] fitnessPercent = new float[nextGen.size()];

		for (int i = 0; i < nextGen.size(); i++) {
			fitnessPercent[i] = nextGen.get(i).getFitness() / totalFitness;
		}

		currentGen.clear();

		for (int i = 0; i < numCreatures; i++) {
			currentGen.add((T) nextGen.get(0).cross((ArrayList<GeneticCreature>) nextGen, fitnessPercent,
					addlFitnessComponents));
		}
		genNum++;
		nextGen.clear();
		serializeBest();
	}

	private void serializeBest() {
		try {
			FileOutputStream fileOut = new FileOutputStream("src/data/bestCreature.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(bestCreature);
			out.close();
			fileOut.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void importBest() {
		try {
			FileInputStream fileIn = new FileInputStream("/bestCreature.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			bestCreature = (T) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("T class not found");
			c.printStackTrace();
			return;
		}
	}

}
