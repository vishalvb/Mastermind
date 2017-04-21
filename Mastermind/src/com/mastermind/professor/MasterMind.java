package com.mastermind.professor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MasterMind
{
	
	private static int[] answer_code;
	private static int[] guess_result;
	private static int[] guess;
	private static Vector<int[]> S;
	
	// generate the random secret code with 8 color
	public static int[] generate_code(int numberOfColors, int numberOfPits)
	{
		int[] result = new int[numberOfPits];
		Random random = new Random();
		result[0] = random.nextInt(numberOfColors) + 1;
		result[1] = random.nextInt(numberOfColors) + 1;
		result[2] = random.nextInt(numberOfColors) + 1;
		result[3] = random.nextInt(numberOfColors) + 1;
		return result;
	}
	
	// generate all the possibilities of the 4 bit code
	public static Vector<int[]> generate_S(int numberOfColors)
	{
		Vector<int[]> vector = new Vector<int[]>();
		for(int i1=1; i1<=numberOfColors; i1++)
		{
			for(int i2=1; i2<=numberOfColors; i2++)
			{
				for(int i3=1; i3<=numberOfColors; i3++)
				{
					for(int i4=1; i4<=numberOfColors; i4++)
					{
						int[] a = {i1,i2,i3,i4};
						vector.add(a);
					}
				}
			}
		}
		return vector;
	}
	
	// remove the element of S that did not give the same response of the guess
	public static void elimination(int[] result, int[] guess, int numberOfColors, int numberOfPits)
	{
		int i=0;
		while(i<S.size())
		{
			// get the result from guess and element of S
			int[] check_case = check_result(guess, S.get(i),numberOfColors,numberOfPits);
			int flag = 0;
			
			// compare the result if not same, remove it
			if(result[0] != check_case[0] || result[1] != check_case[1])
			{
				flag = 1;
				S.remove(i);
			}
			if(flag == 0)
			{
				i++;
			}
		}
	}
	
	//compare the guess with the code.
	public static int[] check_result(int[] guess, int[] s, int numberOfColors, int numberOfPits)
	{
		int[] result = new int[2];
		
		int count_black = 0;
		int count_white = 0;
		//mark the color
		int[] color_check = new int[numberOfPits];
		//mark the guess
		int[] check_use = new int[numberOfPits];
		
		//check how many black there will be
		for(int i=0; i < numberOfPits; i++)
		{
			if(guess[i] == s[i])
			{
				count_black++;
				color_check[i]++;
				check_use[i]++;
			}
		}
		
		//check how many white there will be
		for(int i=0; i<numberOfPits; i++)
		{
			if(check_use[i]!=0)
			{
				continue;
			}
			for(int j=0; j<numberOfPits; j++)
			{
				if(guess[i] == s[j] && (i != j) && color_check[j] == 0)
				{
					count_white++;
					color_check[j]++;
				}
			}
		}
		
		result[0] = count_black;
		result[1] = count_white;
		
		return result;
	}
	
	//getting the min possibility of the guess as the score
	public static int min_number_of_elimination(int[] guess, int numberOfColors, int numberOfPits)
	{
		int sum = 0;
		int min = 99999;
		//14 possible results
		int[][] possibleResult = {{4,0},{3,0},{2,2},{2,1},{2,0},{1,3},{1,2},{1,1},{1,0},{0,4},{0,3},{0,2},{0,1},{0,0}};
		
		//List<Integer> possibleResult = new ArrayList<Integer>();
		/*List<ArrayList<Integer>> possibleResult = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> arg0;
		for(int i = 0; i < numberOfPits; i++){
			for(int j = 0; j < numberOfPits; j++){
				arg0.add(j);
				//possibleResult[i][j] = 1;
			}
			possibleResult.add(arg0);		
		}*/
		
		//calculate the min score
		for(int i=0; i<14; i++)
		{
			if(possibleResult[i][0] < guess_result[0] || possibleResult[i][1] < guess_result[1])
			{
				continue;
			}
			
			sum=0;
			for(int j=0; j<S.size(); j++)
			{
				int[] check_case = check_result(guess, S.get(j),numberOfColors,numberOfPits);
				if(possibleResult[i][0]!=check_case[0] || possibleResult[i][1]!=check_case[1])
				{
					sum++;
				}
			}
			if(min>sum)
			{
				min = sum;
			}
		}
		return min;
	}
	
	// take the highest score from S
	public static int[] get_next_guess(int numberOfColors, int numberOfPits)
	{
		int[] result = new int[4];
		
		int max = 0;
		for(int i=0; i<S.size(); i++)
		{
			int score = min_number_of_elimination(S.get(i),numberOfColors,numberOfPits);
			if(score>=max)
			{
				max = score;
				result = S.get(i);
			}
		}
		return result;
	}
	
	//show guess
	public static void output_guess(int[] guess, int numberOfPits)
	{
		
		for(int i = 0; i < numberOfPits;i++){
			System.out.print(" | ");
			System.out.print(guess[i]);
		}
		/*System.out.print(" | ");
		System.out.print(guess[0]);
		System.out.print(" ");
		System.out.print(guess[1]);
		System.out.print(" ");
		System.out.print(guess[2]);
		System.out.print(" ");
		System.out.print(guess[3]);
		System.out.print(" | ");*/
	}
	
	//show result
	public static void output_result(int[] result)
	{
		for(int i=0; i<result[0]; i++)
		{
			System.out.print(" black ");
		}
		for(int i=0; i<result[1]; i++)
		{
			System.out.print(" white ");
		}
		System.out.println(" ");
	}
	
	public static void playGame(int numberOfColors, int numberOfPits)
	{
		
		long average = 0;
		for(int i = 0; i < 10; i++){
			average += playGameOnce(numberOfColors,numberOfPits);
		}
		System.out.println("Average time :"+average/10);
			
	}
	
	public static long playGameOnce(int numberOfColors, int numberOfPits) 
	{
		//record the start time
		long startTime=System.nanoTime(); 
		
		answer_code = generate_code(numberOfColors,numberOfPits);
		
		solveMasterMindProblem(numberOfColors,numberOfPits);
		
		// record the end time 
		long endTime=System.nanoTime();
		
		//print time it takes
		System.out.println("The algorithm take "+(endTime-startTime)+" ns");
		
		return endTime - startTime;
	}

	private static void solveMasterMindProblem(int numberOfColors,int numberOfPits)
	{
		S = generate_S(numberOfColors);
		
		// the First guess is 1122
		guess = new int[numberOfPits];
		for(int i = 0; i<numberOfPits;i++){
			if(i+1 <= numberOfPits/2){
				guess[i] = 1;
			}
			else{
				guess[i] = 2; 
			}
		}
		
		
		
		
		guess_result = check_result(guess,answer_code,numberOfColors,numberOfPits);
		
		output_guess(guess,numberOfPits);
		output_result(guess_result);
		
		//eliminate if 0 white and 0 black
		
		/*if(guess_result[0] == 0 && guess_result[1] == 0){
			S = newset(guess);
		}*/
		//end elimination
		
		elimination(guess_result, guess,numberOfColors,numberOfPits);
		
		//loop to guess, win to break
		while(guess_result[0] != numberOfPits)
		{
			guess = get_next_guess(numberOfColors,numberOfPits);
			guess_result = check_result(guess,answer_code,numberOfColors,numberOfPits);
			
			output_guess(guess,numberOfPits);
			output_result(guess_result);
			
			elimination(guess_result, guess,numberOfColors,numberOfPits);
		}
	}
	
	/*private static Vector<int[]> newset(int[] guess2) {
		
		
		
		return S;
		
	}*/

	public static void main(String args[])
	{
		playGame(8,4);
	}
}