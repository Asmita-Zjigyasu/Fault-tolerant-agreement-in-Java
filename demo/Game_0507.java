package demo;

import java.util.*;

import common.Game;
import common.Machine;

public class Game_0507 extends Game 
{
	private ArrayList<Machine> machines;
	private int phase_no;//phase no.
	private int total_machines;//total no. of machines
	private int numFaulty;// no. of faulty machines

	public Game_0507()
	{
		this.machines = new ArrayList<Machine>();
		this.phase_no = 0;//when the game begins, the phase number is set to 0
		this.numFaulty = 0;
	}
   
	@Override
	public void addMachines(ArrayList<Machine> machines, int numFaulty) 
	{
		for(int i = 0; i < machines.size(); i++)
		{
			this.machines.add(machines.get(i));
		}
		this.total_machines = machines.size();
		this.numFaulty = numFaulty;

		// TODO Auto-generated method stub
	}

	@Override
	public void startPhase() 
	{
		Random rand = new Random();
		System.out.println("\n!!!!!\n" + "Phase number " + phase_no + " started" );
		phase_no++;//after printing that x phase has started, just increment the phase counter

		ArrayList<Integer> faulty_machines = new ArrayList<Integer>();

		//Here we keep randomly choosing the indices of faulty machines
		while(faulty_machines.size()!=numFaulty)
		{
			int i = rand.nextInt(total_machines);//index_of_machine_to_be_added
			if(!faulty_machines.contains(i))
			{
				faulty_machines.add(i);
			}
		}

		//Now set the state of all the given machines
		for(int i = 0; i < total_machines; i++)
		{
			if(faulty_machines.contains(i))
			{
				machines.get(i).setState(false);
			}
			else
			{
				machines.get(i).setState(true);
			}
		}

		//Now set each machine
		for(int i = 0; i < machines.size(); i++)
		{
			machines.get(i).setMachines(this.machines);
		}

		//Now set the leader for this phase_no
		int curr_leader = rand.nextInt(total_machines);
		machines.get(curr_leader).setLeader();
		//And now the entire phase has started as per the set leader function
		// TODO Auto-generated method stub
	}



	@Override
	public void startPhase(int leaderId, ArrayList<Boolean> areCorrect) 
	{
		System.out.println("\n!!!!!\n" + "Phase number " + phase_no + " started" );
		phase_no++;//after printing that x phase has started, just increment the phase counter

		for(int i = 0; i < total_machines; i++)
		{
			if(areCorrect.get(i) == true)
			{
				machines.get(i).setState(true);
			}

			else
				machines.get(i).setState(false);

		}

		//Now set each machine
		for(int i = 0; i < machines.size(); i++)
		{
			machines.get(i).setMachines(this.machines);
		}

		//Now set the leader for this phase_no
		machines.get(leaderId).setLeader();
		//And now the entire phase has started as per the set leader function
		
		// TODO Auto-generated method stub		
	}
}
