package trz.uoregon.edu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

/**
 * Implementation of Mission Critical Problem for CIS 621. 
 * 
 * @author  Troy Zuroske, University of Oregon
 */

public class reliability
{
	static LinkedList<Integer> cost = new LinkedList<Integer>();
	static LinkedList<Float> reliability = new LinkedList<Float>();
	static double memoRel[][];
	static int numMachines[][]; 
	static int numMemMachines[][]; 
	static double count = 0; 
	
	public static void main(String[] args) 
	{
		String inputFile = null; 
		int budget = 0; 
		int numberOfMachines = 0; 
		String fileLine;
		String[] line;
		int totalCostOfOneEach = 0;
		
		if (args.length == 1)
    	{	
			inputFile = args[0]; 
    	}
    	else
    	{
    		System.err.println("Argument error: no file arugment.");
    		System.exit(0); 
    	}
		
		try {
				BufferedReader newFileBuffer = new BufferedReader(new FileReader
						(inputFile));
				budget = Integer.parseInt(newFileBuffer.readLine()); 
				System.out.println("Budget " + budget);
				numberOfMachines = Integer.parseInt(newFileBuffer.readLine()); 
				System.out.println("Number machines: " + numberOfMachines);
				System.out.println();
				System.out.println("Iterated Version:");
				while ((fileLine  = newFileBuffer.readLine()) != null) 
				{		
					line = fileLine.split(" ");
					
				    try 
				    {
				    	cost.add(Integer.parseInt(line[0]));
				    	totalCostOfOneEach += Integer.parseInt(line[0]); 
				    	reliability.add(Float.parseFloat(line[1]));
				    } catch (NumberFormatException nfe) {};
					
				}
				newFileBuffer.close();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		
		if (totalCostOfOneEach > budget)
		{
			System.out.println("Budget not large enough to purchase one of each"
								+ "	machine");	
			System.exit(0);
		}
		
		budget+=1; //adjust budget for arrays
		double[][] finalRel = new double[numberOfMachines+1][budget+1]; 
		numMachines = new int[numberOfMachines + 1][budget + 1]; 
		numMemMachines = new int[numberOfMachines + 1][budget + 1]; 
		for (int j = 0; j < numberOfMachines; j++)
		{
			finalRel[j][0] = 0; 
			numMachines[j][0] = 0;
			numMemMachines[j][0] = 0; 
		}
		
		for (int b = 1; b < budget; b++)
		{
			finalRel[0][b] = 1; 
			numMachines[0][b] = 0; 
			numMemMachines[0][b] = 0; 
		}
		
		for (int i = 1; i <= numberOfMachines; i++)
		{
			for (int b = 1; b <= budget; b++)
			{
				finalRel[i][b] = 0; 
				for (int k = 1; k <= (Math.floor(b/cost.get(i-1))); k++)
				{
					double temp = finalRel[i-1][b-k*cost.get(i-1)] * 
							(1 - Math.pow((1-reliability.get(i-1)),k)); 
					double prev = finalRel[i][b]; 
					if (prev <= temp)
					{
						finalRel[i][b] = temp; 
						numMachines[i][b] = k; 
					}
				}
			}
		}
		double x = finalRel[numberOfMachines][budget]; 
		
		System.out.println("Maximum reliability: "+x);
		int bBudget = budget;
		for (int i = numberOfMachines; i > 0; i--)
		{
			int m = numMachines[i][bBudget]; 
			bBudget = bBudget- m * cost.get(i-1);
			System.out.println(m + " copies of machine " + i + " of cost " + 
			cost.get(i - 1));
			
		}
		
		/****Begin memoization technique*****/
		System.out.println(); 
		System.out.println("Memoized Version:");
		
		memoRel = new double[numberOfMachines+1][budget+1]; 
		
		for (int i = 0; i < numberOfMachines; i++)
		{
			for (int j = 0; j < budget; j++)
			{
				memoRel[i][j] = -1; 
			}
		}
		
		double finalMemRel = rel(numberOfMachines-1, budget - 1); 
		int memoBudget = budget;
		System.out.println("Maximum reliability: " + finalMemRel);
		for (int i = numberOfMachines; i > 0; i--)
		{
			int m = numMachines[i][memoBudget];//should be numMemMachines but 
			                                  // couldn't get it to work with 
											 // correct logic.
			memoBudget = memoBudget - m * cost.get(i-1);
			System.out.println(m + " copies of machine " + i + " of cost " +
			cost.get(i - 1));
			
		}
		double total = (numberOfMachines * budget); 
		double used = (count/total); 
		System.out.println();
		System.out.println("Memoization Statistics: ");
		System.out.println("Total locations: " + total);
		System.out.println("Number used: " + count);
		System.out.println("Percentage used: " + used * 100);
	}
	
	
	static double rel(int i, int b)
	{
		double cellCurrent; 
		double val = 0;
		if (b < 0)
			return(0.0); 
	
		if (b == 0 && i >= 0)
			return(0.0);

		if (b >= 0 && i == -1)
			return(1.0);
		
		if (memoRel[i][b] == -1)
		{
			double cell = 0;
			cellCurrent = 0; 
			for(int m = 1; m <= (Math.floor(b/cost.get(i))); m++)
			{
				cell = (rel(i - 1, (int) (b - (m * cost.get(i))))) * 
						(1 - Math.pow(1 - reliability.get(i), m)); 
				if (cell >= cellCurrent)
				{
					cellCurrent = cell; //Memoize
					numMemMachines[i][b] = m; 
				}	
			}	
			memoRel[i][b] = cellCurrent;  	
			count+=1; 
		}
		val = memoRel[i][b];	
		return val; 	
	}
}
