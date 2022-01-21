package demo;

import java.util.*;

import common.Location;
import common.Machine;

public class Machine_0507 extends Machine 
{

	private int step;
	private Location pos = new Location(0,0);
	private Location dir = new Location(1,0); // using Location as a 2d vector. Bad!

	private int id;

	private Random rand = new Random();//randome variable to be used wherever required
	private int round1_left_count, round1_right_count, round2_right_count, round2_left_count;
	
	private String machine_name;
	private boolean machine_state;//true = correct machine, false = faulty machine
	private boolean leader = false;//true = is the leader in that phase, false = not a leader in that phase
	//leader boolean only turns true when a machine's setLeader() is evoked

	private int no_of_machines, numFaulty;//no. of faulty machines\
	
	private int phase_no = -1;
	private int round_no;

	private ArrayList<Machine> machines;


	public Machine_0507(String name) 
	{
		this.machines = new ArrayList<Machine>();
		this.machine_name = name;

		round1_left_count = round1_right_count = round2_right_count = round2_left_count = 0;
		//setting all the left and right decision resonating machine counters to zero as soon as a machine instance is constructed
	}


	@Override
	public void setMachines(ArrayList<Machine> machines)
	{
		//here we are setting the number of faulty machines and the id of the machine to be the same as its index in the machines' list 
		this.machines = machines;
		no_of_machines = machines.size();

		//We are doing this to ensure that if the total number of machines are 3t, then there are at max t-1 faulty machines
		if(machines.size()%3 == 0)
			numFaulty = machines.size()/3 - 1;//CHECK THIS ONCE
		else
			numFaulty = machines.size()/3;	

		for(int i = 0; i<machines.size(); i++)
		{
			if(machines.get(i)==this)
			{
				this.id = i;
			}
		}
	}


	@Override
	public void setStepSize(int stepSize) 
	{
		step = stepSize;
	}


	@Override
	public void setState(boolean isCorrect) 
	{
		//here we are setting the state of the machine to either correct or faulty as given
		//when we set the state, it indicates that a new phase has been started, hence we increase the phase no., and bring the current round number to 0
		this.machine_state = isCorrect;
		phase_no++;
		round_no = 0;
		round1_left_count = round1_right_count = round2_right_count = round2_left_count = 0;		
	}

	@Override
	public void setLeader() 
	{
		this.leader = true;

		//since only the leader can invoke round 0, hence we make the logic of round 0 in setLeader itself
		int round0_dec = rand.nextInt(2);
		//this is the decision of the leader in round 0, 0 = left, 1 = right
		//System.out.println("Leader decision = " + round0_dec);

		if(machine_state)//if the leader is correct, then it sends a single decision to all the machines, including itself
		{
			for(int i = 0; i < machines.size(); i++)
			{
				machines.get(i).sendMessage(id, phase_no, 0, round0_dec);
			}
		}

		else//if the machine is faulty, then send the same message to at least 2t+1 machines
		{
			int no_of_machines_receiving_message = 2*numFaulty+1 + rand.nextInt(no_of_machines - 2*numFaulty);
			//2t+1, now for the more than 2t+1 machines, add any random number to 2t+1 between [0,t), 
			//so that the best case i.e. 3t machines and the worst case i.e. 2t+1 machines, both are covered 
			//if we have taken the worst case of 3t machines out of which t-1 are faulty

			ArrayList<Integer> machines_receiving_message = new ArrayList<Integer>();

			//Here we keep on adding the indices of the machines randomly to which this leader would send the message to the arraylist
			while(machines_receiving_message.size()!=no_of_machines_receiving_message)
			{
				int i = rand.nextInt(no_of_machines);//index_of_machine_to_be_added
				if(!machines_receiving_message.contains(i))
				{
					machines_receiving_message.add(i);
				}
			}
			for(int i = 0; i < machines.size(); i++)
			{
				if(machines_receiving_message.contains(i))
				{
					machines.get(i).sendMessage(id, phase_no, 0, round0_dec);
				}	
			}	
		}		
	}

	private void round1(int decision_from_round0)
	{
		round_no = 1;//this would be executed if this machine is not the leader
		if(machine_state)//if the machine is correct, then it sends a single decision to all the machines, including itself
		{
			for(int i = 0; i < machines.size(); i++)
			{
				machines.get(i).sendMessage(id, phase_no, 1, decision_from_round0);
			}
		}

		else//if the machine is faulty, it can give a random decision to all the other machines or choose to stay silent 
		{
			int not_silent = rand.nextInt(2);//1 = pass a random decision, 0 = stay silent
			decision_from_round0 = rand.nextInt(2);//select a random decision from 0 or 1, i.e. to move left or right 

			if(not_silent == 1)
			{
				for(int i = 0; i < machines.size(); i++)
				{
					machines.get(i).sendMessage(id, phase_no, 1, decision_from_round0);
				}
			}
		}
	}


	private void round2_begin(int decision_from_round1)
	{
		if(machine_state)//if the machine is correct, then it sends a single decision to all the machines, including itself
		{
			for(int i = 0; i < machines.size(); i++)
			{
				machines.get(i).sendMessage(id, phase_no, 2, decision_from_round1);
			}
		}

		else//if the machine is faulty, it can give a random decision to all the other machines or choose to stay silent 
		{
			int not_silent = rand.nextInt(2);//1 = pass a random decision, 0 = stay silent
			decision_from_round1 = rand.nextInt(2);//select a random decision from 0 or 1, i.e. to move left or right 

			if(not_silent == 1)
			{
				for(int i = 0; i < machines.size(); i++)
				{
					machines.get(i).sendMessage(id, phase_no, 2, decision_from_round1);
				}
			}
		}
	}


	private void round2_end(int decision_from_round2)//decision_from_round2 is the final decision
	{
		if(decision_from_round2 == 0)//turn left
		{
			if(dir.getY() == 0)
				dir.setLoc(0, dir.getX());
			else
				dir.setLoc(-1*dir.getY(), 0);
	
			move();
		}
		//All the left turns have come from the following matrix
		/*
			X = x1*0 + y1*1    Matrix = 0 1 
			Y = x1*-1 + y1*0           -1 0   
		*/

		else//turn right
		{
			if(dir.getY() == 0)
				dir.setLoc(0, -1*dir.getX());

			else
				dir.setLoc(dir.getY(), 0);

			move();
		}
		//All the right turns have come from the following matrix
		/*
			X = x1*0 + y1*-1    Matrix = 0 -1 
			Y = x1*1 + y1*0              1 0   
		*/

		System.out.println("Final decision of " + machine_name + " is " + decision_from_round2);
		round1_left_count = round1_right_count = round2_right_count = round2_left_count = 0;
		//reset all the counter variables now
	}


	@Override
	public void sendMessage(int sourceId, int phaseNum, int roundNum, int decision) 
	{
		if(roundNum == 0)
		{
			//here we will set the round no. to 1, i.e. we move to the next round number
			//and we pass the decision received by the machine in round 0 to round 1.
			round_no = 1;
			round1(decision);
		}

		else if(roundNum == 1 && (round_no < 2))
		//round_num == 1 when the machine had received a message from the leader 
		//and round_num == 0 when the machine has not received a message from the leader 
		{
			if(decision == 0)
				round1_left_count++;
			else
				round1_right_count++;

			//now if the machine has received more that 2t+1 messages, t = faulty machines, then only will it pass a decision to round 2
			if((round1_left_count+round1_right_count) >= (2*numFaulty+1))
			{
				round_no = 2;//set the current round no. to round 2 
				int decision_from_round1 = 0;
				if(round1_left_count >= (numFaulty+1))//left count >= t+1
					round2_begin(decision_from_round1);
				else
				{
					decision_from_round1 = 1;
					round2_begin(decision_from_round1);
				}	
			}	
		} 

		else if(roundNum == 2 && round_no!= -1)
		{
			if(decision == 0)
				round2_left_count++;
			else
				round2_right_count++;

			if(round2_left_count > 2*numFaulty+1)
			{
				round_no = -1;
				round2_end(0);
			}
			
			else if(round2_right_count > 2*numFaulty+1)
			{
				round_no = -1;
				round2_end(1);
			}
		}	
	}


	@Override
	public void move() 
	{
		pos.setLoc(pos.getX() + dir.getX()*step, pos.getY() + dir.getY()*step);	
	}


	@Override
	public String name() 
	{
		//return "demo_"+id;
		return machine_name;
	}


	@Override
	public Location getPosition() 
	{	
		return new Location(pos.getX(), pos.getY());
	}	
}
