package jc27g15;
import robocode.*;
import java.awt.Color;
import java.util.Random;
import java.lang.Math;

/**
 * WTFIDunno - a robot by James Curran
 */
public class WTFIDunno extends Robot
{
	private Random random;
	private enum ScanMode
	{
		SEARCHING (360),
		TARGETING (120),
		DEFENSE (0);
		
		private final int degrees;
		
		ScanMode(int degrees)
		{
			this.degrees = degrees;
		}
		
		public int getDegrees(){return this.degrees;}
	};
	private ScanMode mode;
	private byte sweeps;
	private boolean gunSweepingLeft;
	private double targetHeading;
	private int hits;
		
	/**
	 * Default behavior
	 */
	public void run()
	{
		this.mode = ScanMode.SEARCHING;
		this.hits = 0;
		this.out.println("GET REKT");
		this.random = new Random();
		setColors(Color.green,Color.orange,Color.pink); // body,gun,radar
		
		while(true)
		{
			if(this.mode == ScanMode.SEARCHING)
			{
				if(this.random.nextBoolean())
				{
					this.gunSweepingLeft = true;
					this.turnGunLeft(this.mode.getDegrees());
				}
				else
				{
					this.gunSweepingLeft = false;
					this.turnGunRight(this.mode.getDegrees());
				}
			}		
			else if(this.mode == ScanMode.TARGETING)
			{
				if(this.sweeps == 0)
				{
					this.turnGunRight(this.targetHeading - this.getGunHeading());
				}
				if(this.gunSweepingLeft)
				{
					this.turnGunRight(this.mode.getDegrees());
				}
				else
				{
					this.turnGunLeft(this.mode.getDegrees());
				}
				this.gunSweepingLeft = !this.gunSweepingLeft;
				this.sweeps++;
				if(this.sweeps > 1)
				{	
					this.out.println("Well, back to searching.");
					this.mode = ScanMode.SEARCHING;
				}
			}
			//MOVES BODY
			if(this.random.nextInt(3) == 2)
			{
				this.ahead(this.random.nextInt(160) + 40);
			}
			else
			{
				this.back(this.random.nextInt(160) + 40);
			}
		}
	}

	/**
	 * What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e)
	{
		this.sweeps = 0;
		if(this.mode == ScanMode.DEFENSE)
		{
			this.turnRight(this.getGunHeading() - this.getHeading());
			fire(3.0);
		}
		else
		{
			if(this.mode == ScanMode.SEARCHING)
			{
				this.targetHeading = this.getGunHeading() + this.nextMaybeNegativeDouble(10);
				if(e.getDistance() <= this.getBattleFieldWidth() * 2 / 3)
				{
					this.out.println("Got one in the sights. Scrub's going down.");
					this.mode = ScanMode.TARGETING;
					fire(3.0);
				}
			}
			else
			{
				fire(3.0);
			}
		}
	}

	public void onHitRobot(HitRobotEvent e)
	{
		if(this.mode != ScanMode.DEFENSE)
		{
			this.out.println("AAAAAAAAAAARGH!");
			this.turnRight(e.getBearing());
			this.turnGunLeft(this.getGunHeading() - this.getHeading());
			this.mode = ScanMode.DEFENSE;
		}
	}
	
	/**
	 * What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e)
	{
		if(this.mode != ScanMode.DEFENSE)
		{
			this.hits++;
			if(hits % 3 == 0)
			{	
				turnRight(e.getBearing() + this.nextMaybeNegativeDouble(45));
				this.ahead(this.random.nextInt(100) + 40);
			}
		}
		else
		{
			if(Math.abs(e.getBearing()) < 2)
			{
				this.mode = ScanMode.SEARCHING;
			}
		}
	}
	
	/**
	 * What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e)
	{
		if(this.mode != ScanMode.DEFENSE)
		{
			if(Math.abs(e.getBearing()) <= 90)
			{
				this.back(80);
			}
			else
			{
				this.ahead(80);
			}
			this.turnRight(this.nextMaybeNegativeDouble(60));
		}
	}
	
	public void onBulletMissed(BulletMissedEvent e)
	{
		if(this.mode == ScanMode.DEFENSE)
		{
			this.mode = ScanMode.SEARCHING;
		}
	}
	
	/**
	 * Returns a double between scale and -scale.
	 */ 
	private double nextMaybeNegativeDouble(double scale)
	{
		int multiplier = 1;
		if(this.random.nextBoolean())
		{
			multiplier = -1;
		}
		return this.random.nextFloat() * multiplier * scale;
	}
}
