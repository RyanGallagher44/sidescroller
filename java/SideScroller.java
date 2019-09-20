import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.image.*;
import java.applet.*;
import javax.swing.border.*;
import java.applet.*;
import java.io.*;
import sun.audio.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.imageio.ImageIO;
import java.util.*;

public class SideScroller extends JPanel implements KeyListener,Runnable
{
	private JFrame frame;
	Thread t;
	private boolean gameOn;
	
	//hero stuff
	Hero hero;
	int imgCount = 0;
	BufferedImage[] guysRight = new BufferedImage[16];
	Image[] guyImageRight = new Image[16];
	BufferedImage[] guysLeft = new BufferedImage[16];
	Image[] guyImageLeft = new Image[16];
	boolean canJump = true;
	int fallCount = 0,jumpCount = 0;
	boolean movingLeft = false;
	boolean movingRight = true;
	boolean onMovingBlockHorizontal = false;
	boolean onMovingBlockVertical = false;
	MovingBlock movingBlockHorizontalHeroIsOn;
	MovingBlock movingBlockVerticalHeroIsOn;
	boolean canShoot = true;
	int shootingCounter = 0;
	boolean canWalk = true;
	int walkingCounter = 0;
	
	//background stuff
	int count = 5;
	BufferedImage[] backgrounds = new BufferedImage[5];
	Image[] backgrounds2 = new Image[5];
	int[] bgX = {0,0,0,0,0,0,0,0,0,0,0};
	
	boolean right = false,left = false,down = false,up = false,fall = false,restart = false,shot = false;
	
	//weapons
	ArrayList<Bullet> bullets;
	boolean bulletCollision = false;
	
	//images
	BufferedImage origSand;
	Image sand;
	BufferedImage origWood1;
	Image wood1;
	BufferedImage origWood2;
	Image wood2;
	BufferedImage origBullet;
	Image bullet;
	
	//blocks
	ArrayList<Block> blocks;
	ArrayList<MovingBlock> movingBlocksHorizontal;
	ArrayList<MovingBlock> movingBlocksVertical;
	ArrayList<Enemy> enemies;
	
	//levels
	int [][] levelOneLocs;
	int rows;
	int cols;
	
	//sounds
	
	public SideScroller()
	{
		frame = new JFrame();
		
		//initializing hero
		hero = new Hero(250,0);
		
		//initializing lists
		blocks = new ArrayList<Block>();
		movingBlocksHorizontal = new ArrayList<MovingBlock>();
		movingBlocksVertical = new ArrayList<MovingBlock>();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();

		gameOn = true;
		
		AudioClip clip = Applet.newAudioClip(getClass().getResource("theme.mp3"));
		
		//initializing images
		try {
			origSand = ImageIO.read(getClass().getResource("sand.png"));
			sand = origSand.getScaledInstance(75, 75, Image.SCALE_DEFAULT);
			origWood1 = ImageIO.read(getClass().getResource("wood1.jpg"));
			wood1 = origWood1.getScaledInstance(75, 75, Image.SCALE_DEFAULT);
			origWood2 = ImageIO.read(getClass().getResource("wood2.jpg"));
			wood2 = origWood2.getScaledInstance(75, 75, Image.SCALE_DEFAULT);
			origBullet = ImageIO.read(getClass().getResource("bullet.png"));
			bullet = origBullet.getScaledInstance(10, 10, Image.SCALE_DEFAULT);
		}catch(IOException e) {	
		}
		
		//initializing hero images
		try {
			for(int x = 0; x < 16; x++)
			{
				guysRight[x] = ImageIO.read(getClass().getResource("output-onlinepngtools ("+x+").png"));
				guyImageRight[x] = guysRight[x].getScaledInstance(100,85,Image.SCALE_DEFAULT);
			}
			for(int x = 16; x < 32; x++)
			{
				guysLeft[x-16] = ImageIO.read(getClass().getResource("output-onlinepngtools ("+x+").png"));
				guyImageLeft[x-16] = guysLeft[x-16].getScaledInstance(100,85,Image.SCALE_DEFAULT);
			}
		}
		catch (IOException e) {
		}
		
		//initializing background images
		for(int i = backgrounds.length; i > 0; i--)
		{
			try{
				backgrounds2[i-1] = ImageIO.read(getClass().getResource("Layer"+i+"z.png"));
			}catch(IOException e){
				System.out.println("error");
			}
		}
		
		//initializing level
		File level1 = new File("src/level1.txt");
		levelOneLocs = new int[16][99];

		try
		{
			BufferedReader input = new BufferedReader(new FileReader(level1));
			String text = "";
			String[] line;
			int x = -2;
			while((text = input.readLine()) != null)
			{
				if(x == -2)
				{
					rows = Integer.parseInt(text);
				}
				if(x == -1)
				{
					cols = Integer.parseInt(text);
				}
				if(x >= 0)
				{
					line = text.split("");
					for(int i = 0; i < line.length; i++){
						levelOneLocs[x][i] = Integer.parseInt(line[i]);
					}
					System.out.println();
				}
				x++;
			}
		}catch(Exception exception)
		{
			System.out.println("ERROR: "+exception.getMessage());
		}

		for(int i = 0; i < levelOneLocs.length; i++)
		{
			for(int j = 0; j < levelOneLocs[0].length; j++)
			{
				if(levelOneLocs[i][j] == 1){
					blocks.add(new Block(j*75,i*75));
				}
				if(levelOneLocs[i][j] == 2){
					movingBlocksHorizontal.add(new MovingBlock(j*75,i*75));
				}
				if(levelOneLocs[i][j] == 3){
					movingBlocksVertical.add(new MovingBlock(j*75,i*75));
				}
				if(levelOneLocs[i][j] == 4){
					enemies.add(new Enemy(j*75,i*75));
				}
			}
		}
		
		//setting block hitboxes
		for(int i = 0; i < blocks.size(); i++)
		{
			blocks.get(i).setX(false);
			blocks.get(i).setHitbox(blocks.get(i).getX(),blocks.get(i).getY());
		}
		
		clip.play();

		frame.addKeyListener(this);
		frame.add(this);
		frame.setSize(1000,800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		t = new Thread(this);
		t.start();
	}

	public void run()
	{
		int num = 0;
		while(true)
		{
			if(gameOn)
			{	
				//setting idle hero image
				if(!right && !left)
				{
					imgCount = 9;
				}
				
				//setting delay for shooting
				shootingCounter++;
				if(shootingCounter == 100)
				{
					canShoot = true;
					shootingCounter = 0;
				}
				
				//setting delay for walking
				walkingCounter++;
				if(walkingCounter == 10)
				{
					canWalk = true;
					walkingCounter = 0;
				}
				
				//checking if hero on block moving horizontally
				if(onMovingBlockHorizontal)
				{
					//checking if block that hero is on is moving to the right
					if(movingBlockHorizontalHeroIsOn.getRight())
					{
						//setting background locations
						count--;
						if(count == 0)
							count = 5;
						for(int i = backgrounds.length; i > 0; i--){
							if(count % i == 0)
								bgX[i-1]--;
							if(bgX[i-1] < -1920)
								bgX[i-1] += 1920;
						}
						
						//setting the location in which the horizontal-moving blocks occupy
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setInitX(-1);
						}
						
						//setting the block locations
						for(int i = 0; i < blocks.size(); i++)
						{
							blocks.get(i).setX(false);
							blocks.get(i).setHitbox(blocks.get(i).getX(),blocks.get(i).getY());
						}
						
						//setting the horizontal-moving block locations
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setX(false);
							movingBlocksHorizontal.get(i).setHitbox(movingBlocksHorizontal.get(i).getX(),movingBlocksHorizontal.get(i).getY());
						}
						
						//setting the vertical-moving block locations
						for(int i = 0; i < movingBlocksVertical.size(); i++)
						{
							movingBlocksVertical.get(i).setX(false);
							movingBlocksVertical.get(i).setHitbox(movingBlocksVertical.get(i).getX(), movingBlocksVertical.get(i).getY());
						}
						
						//setting the bullet locations	
						for(int i = 0; i < bullets.size(); i++)
						{
							bullets.get(i).setX(-1);
							bullets.get(i).setHitbox(bullets.get(i).getX(), bullets.get(i).getY());
						}
						
						//setting the enemy locations
						for(int i = 0; i < enemies.size(); i++)
						{
							enemies.get(i).setX(-1);
							enemies.get(i).setHitbox(enemies.get(i).getX(), enemies.get(i).getY());
						}
					}
					
					//checking if block that hero is on is moving to the left
					if(movingBlockHorizontalHeroIsOn.getLeft())
					{
						//setting background locations
						count++;
						if(count == 5)
							count = 0;
						for(int i = backgrounds.length; i > 0; i--){
							if(count % i == 0)
								bgX[i-1]++;
							if(bgX[i-1] >= 1920)
								bgX[i-1] -= 1920;
						}
						
						//setting the location in which the horizontal-moving blocks occupy
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setInitX(1);
						}
						
						//setting the block locations
						for(int i = 0; i < blocks.size(); i++)
						{
							blocks.get(i).setX(true);
							blocks.get(i).setHitbox(blocks.get(i).getX(),blocks.get(i).getY());
						}
						
						//setting the horizontal-moving block locations
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setX(true);
							movingBlocksHorizontal.get(i).setHitbox(movingBlocksHorizontal.get(i).getX(),movingBlocksHorizontal.get(i).getY());
						}
						
						//setting the vertical-moving block locations
						for(int i = 0; i < movingBlocksVertical.size(); i++)
						{
							movingBlocksVertical.get(i).setX(true);
							movingBlocksVertical.get(i).setHitbox(movingBlocksVertical.get(i).getX(), movingBlocksVertical.get(i).getY());
						}
						
						//setting the bullet locations
						for(int i = 0; i < bullets.size(); i++)
						{
							bullets.get(i).setX(1);
							bullets.get(i).setHitbox(bullets.get(i).getX(), bullets.get(i).getY());
						}
						
						//setting the enemy locations
						for(int i = 0; i < enemies.size(); i++)
						{
							enemies.get(i).setX(1);
							enemies.get(i).setHitbox(enemies.get(i).getX(), enemies.get(i).getY());
						}
					}
				}
				
				//checking if hero on block moving horizontally
				if(onMovingBlockVertical)
				{
					//checking if block that hero is on is moving down
					if(movingBlockVerticalHeroIsOn.getDown())
					{
						//setting hero y location
						hero.setY(1);
					}
					
					//checking if block that hero is on is moving up
					if(movingBlockVerticalHeroIsOn.getUp())
					{
						//setting hero y location
						hero.setY(-1);
					}
				}
				
				//checking if hero can shoot and if hero is facing to the right
				if(canShoot && movingRight)
				{
					//checking if hero shot
					if(shot)
					{
						canShoot = false;
						bullets.add(new Bullet(hero.getX()+45,hero.getY()+40,true));
					}
				}
				
				//checking if hero can shoot and if hero is moving to the left
				if(canShoot && movingLeft)
				{
					//checking if hero shot
					if(shot)
					{
						canShoot = false;
						bullets.add(new Bullet(hero.getX()+45,hero.getY()+40,false));
					}
				}
				
				//setting bullet locations
				for(int i = 0; i < bullets.size(); i++)
				{
					if(bullets.get(i).getDir())
					{
						bullets.get(i).setX(2);
					}else {
						bullets.get(i).setX(-2);
					}
				}
				
				//setting bullet hitboxes
				for(int i = 0; i < bullets.size(); i++)
				{
					bullets.get(i).setHitbox(bullets.get(i).getX(), bullets.get(i).getY());
				}
				
				//setting hero hitbox
				hero.setHitbox(hero.getX()+30, hero.getY());
				
				//moving the horizontal blocks
				for(int i = 0; i < movingBlocksHorizontal.size(); i++)
				{
					if(movingBlocksHorizontal.get(i).getX() >= movingBlocksHorizontal.get(i).getInitX() && !movingBlocksHorizontal.get(i).getLeft())
					{
						movingBlocksHorizontal.get(i).setRight(true);
						movingBlocksHorizontal.get(i).setX(true);
						if(movingBlocksHorizontal.get(i).getX() == movingBlocksHorizontal.get(i).getDistanceThresholdX())
							movingBlocksHorizontal.get(i).setRight(false);
					}
					if(movingBlocksHorizontal.get(i).getX() <= movingBlocksHorizontal.get(i).getDistanceThresholdX() && !movingBlocksHorizontal.get(i).getRight())
					{
						movingBlocksHorizontal.get(i).setLeft(true);
						movingBlocksHorizontal.get(i).setX(false);
						if(movingBlocksHorizontal.get(i).getX() == movingBlocksHorizontal.get(i).getInitX())
							movingBlocksHorizontal.get(i).setLeft(false);
					}
					movingBlocksHorizontal.get(i).setHitbox(movingBlocksHorizontal.get(i).getX(),movingBlocksHorizontal.get(i).getY());
				}
				
				//moving the vertical blocks
				for(int i = 0; i < movingBlocksVertical.size(); i++)
				{
					if(movingBlocksVertical.get(i).getY() >= movingBlocksVertical.get(i).getInitY() && !movingBlocksVertical.get(i).getUp())
					{
						movingBlocksVertical.get(i).setDown(true);
						movingBlocksVertical.get(i).setY(true);
						if(movingBlocksVertical.get(i).getY() == movingBlocksVertical.get(i).getDistanceThresholdY())
							movingBlocksVertical.get(i).setDown(false);
					}
					if(movingBlocksVertical.get(i).getY() <= movingBlocksVertical.get(i).getDistanceThresholdY() && !movingBlocksVertical.get(i).getDown())
					{
						movingBlocksVertical.get(i).setUp(true);
						movingBlocksVertical.get(i).setY(false);
						if(movingBlocksVertical.get(i).getY() == movingBlocksVertical.get(i).getInitY())
							movingBlocksVertical.get(i).setUp(false);
					}
					movingBlocksVertical.get(i).setHitbox(movingBlocksVertical.get(i).getX(),movingBlocksVertical.get(i).getY());
				}
				
				//checking if hero is moving to the right
				if(right && !left)
				{	
					movingRight = true;
					movingLeft = false;
					
					//checking hero and block/moving block collisions
					boolean okToMove = true;
					for(int i = 0; i < blocks.size(); i++)
					{
						if(new Rectangle(hero.getX()+1,hero.getY(),hero.getWidth(),hero.getHeight()).intersects(blocks.get(i).getHitbox())){
							okToMove = false;
						}
					}
					for(int i = 0; i < movingBlocksHorizontal.size(); i++)
					{
						if(new Rectangle(hero.getX()+1,hero.getY(),hero.getWidth(),hero.getHeight()).intersects(movingBlocksHorizontal.get(i).getHitbox())){
							okToMove = false;
						}
					}
					for(int i = 0; i < movingBlocksVertical.size(); i++)
					{
						if(new Rectangle(hero.getX()+1,hero.getY(),hero.getWidth(),hero.getHeight()).intersects(movingBlocksVertical.get(i).getHitbox())){
							okToMove = false;
						}
					}

					//moving the world to the left
					if(okToMove)
					{
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setInitX(-1);
						}
						
						for(int i = 0; i < bullets.size(); i++)
						{
							bullets.get(i).setX(-1);
							bullets.get(i).setHitbox(bullets.get(i).getX(), bullets.get(i).getY());
						}
						
						for(int i = 0; i < enemies.size(); i++)
						{
							enemies.get(i).setX(-1);
							enemies.get(i).setHitbox(enemies.get(i).getX(), enemies.get(i).getY());
						}
						
						num++;
						
						if(!onMovingBlockHorizontal)
						{
							count--;
							if(count == 0)
								count = 5;
							for(int i = backgrounds.length; i > 0; i--){
								if(count % i == 0)
									bgX[i-1]--;
								if(bgX[i-1] < -1920)
									bgX[i-1] += 1920;
							}
						}
						
						for(int i = 0; i < blocks.size(); i++)
						{
							blocks.get(i).setX(false);
							blocks.get(i).setHitbox(blocks.get(i).getX(),blocks.get(i).getY());
						}
						
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setX(false);
							movingBlocksHorizontal.get(i).setHitbox(movingBlocksHorizontal.get(i).getX(),movingBlocksHorizontal.get(i).getY());
						}
						
						for(int i = 0; i < movingBlocksVertical.size(); i++)
						{
							movingBlocksVertical.get(i).setX(false);
							movingBlocksVertical.get(i).setHitbox(movingBlocksVertical.get(i).getX(), movingBlocksVertical.get(i).getY());
						}
					}
					
					//updating hero image
					if(canWalk)
					{
						canWalk = false;
						imgCount++;
						if(imgCount > 15)
							imgCount = 0;
					}
				}

				//checking if hero is moving to the left
				if(left && !right)
				{	
					movingLeft = true;
					movingRight = false;
					
					//checking hero and block/moving block collisions
					boolean okToMove = true;
					for(int i = 0; i < blocks.size(); i++)
					{
						if(new Rectangle(hero.getX()-1,hero.getY(),hero.getWidth(),hero.getHeight()).intersects(blocks.get(i).getHitbox())){
							okToMove = false;
						}
					}
					for(int i = 0; i < movingBlocksHorizontal.size(); i++)
					{
						if(new Rectangle(hero.getX()-1,hero.getY(),hero.getWidth(),hero.getHeight()).intersects(movingBlocksHorizontal.get(i).getHitbox())){
							okToMove = false;
						}
					}
					for(int i = 0; i < movingBlocksVertical.size(); i++)
					{
						if(new Rectangle(hero.getX()-1,hero.getY(),hero.getWidth(),hero.getHeight()).intersects(movingBlocksVertical.get(i).getHitbox())){
							okToMove = false;
						}
					}

					//moving the world to the right
					if(okToMove){
						
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setInitX(1);
						}
						
						for(int i = 0; i < bullets.size(); i++)
						{
							bullets.get(i).setX(1);
							bullets.get(i).setHitbox(bullets.get(i).getX(), bullets.get(i).getY());
						}
						
						for(int i = 0; i < enemies.size(); i++)
						{
							enemies.get(i).setX(1);
							enemies.get(i).setHitbox(enemies.get(i).getX(), enemies.get(i).getY());
						}
						
						if(!onMovingBlockHorizontal)
						{
							count++;
							if(count == 5)
								count = 0;
							for(int i = backgrounds.length; i > 0; i--){
								if(count % i == 0)
									bgX[i-1]++;
								if(bgX[i-1] >= 1920)
									bgX[i-1] -= 1920;
							}
						}
						
						for(int i = 0; i < blocks.size(); i++)
						{
							blocks.get(i).setX(true);
							blocks.get(i).setHitbox(blocks.get(i).getX(),blocks.get(i).getY());
						}
						
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							movingBlocksHorizontal.get(i).setX(true);
							movingBlocksHorizontal.get(i).setHitbox(movingBlocksHorizontal.get(i).getX(),movingBlocksHorizontal.get(i).getY());
						}
						
						for(int i = 0; i < movingBlocksVertical.size(); i++)
						{
							movingBlocksVertical.get(i).setX(true);
							movingBlocksVertical.get(i).setHitbox(movingBlocksVertical.get(i).getX(), movingBlocksVertical.get(i).getY());
						}
					}
					
					//updating hero image
					if(canWalk)
					{
						canWalk = false;
						imgCount++;
						if(imgCount > 15)
							imgCount = 0;	
					}
				}
				
				//checking bullet and block/moving block collisions
				Iterator<Bullet> iterator = bullets.iterator();
				while(iterator.hasNext())
				{
					bulletCollision = false;
					Bullet bullet = iterator.next();
					for(int j = 0; j < blocks.size(); j++)
					{
						if(bullet.getHitbox().intersects(blocks.get(j).getHitbox()))
						{
							bulletCollision = true;
						}
					}
					for(int j = 0; j < movingBlocksHorizontal.size(); j++)
					{
						if(bullet.getHitbox().intersects(movingBlocksHorizontal.get(j).getHitbox()))
						{
							bulletCollision = true;
						}
					}
					for(int j = 0; j < movingBlocksVertical.size(); j++)
					{
						if(bullet.getHitbox().intersects(movingBlocksVertical.get(j).getHitbox()))
						{
							bulletCollision = true;
						}
					}
					if(bulletCollision)
					{
						iterator.remove();
					}
				}
				
				//checking upwards and downwards hero and block/moving block collisions
				fall = true;
			 	for(Block block:blocks){
					if(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(block.getHitbox()))
					{
						if(fall)
						{
							canJump = true;
							fall = false;
							jumpCount = 0;
						}
					}
				}
			 	for(MovingBlock movingBlock:movingBlocksHorizontal)
			 	{
			 		if(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(movingBlock.getHitbox()))
					{
						if(fall)
						{
							canJump = true;
							fall = false;
							jumpCount = 0;
							onMovingBlockHorizontal = true;
							movingBlockHorizontalHeroIsOn = movingBlock;
							onMovingBlockVertical = false;
							movingBlockVerticalHeroIsOn = null;
						}
					}
			 	}
			 	for(MovingBlock movingBlock:movingBlocksVertical)
			 	{
			 		if(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(movingBlock.getHitbox()))
					{
						if(fall)
						{
							canJump = true;
							fall = false;
							jumpCount = 0;
							onMovingBlockVertical = true;
							movingBlockVerticalHeroIsOn = movingBlock;
							onMovingBlockHorizontal = false;
							movingBlockHorizontalHeroIsOn = null;
						}
					}
			 	}
			 	
			 	//checking hero and enemies collision
			 	Iterator<Enemy> iterator2 = enemies.iterator();
			 	while(iterator2.hasNext())
			 	{
			 		Enemy enemy = iterator2.next();
			 		if(enemy.getHitbox().intersects(new Rectangle(hero.getX(),hero.getY(),hero.getWidth(),hero.getHeight())))
			 		{	
			 			System.out.println("DEAD");
			 		}
			 	}
			 	
			 	//checking bullets and enemies collision
			 	for(int i = enemies.size()-1; i >= 0; i--)
			 	{
			 		for(int j = bullets.size()-1; j >= 0; j--)
			 		{
			 			if(enemies.get(i).getHitbox().intersects(bullets.get(j).getHitbox()))
				 		{	
				 			if(enemies.get(i).getHitCounter() == 2) {
				 				enemies.remove(i);
				 			}else {
				 				bullets.remove(j);
				 				enemies.get(i).setHitCounter();
				 				enemies.get(i).setIsHit(true);
				 			}
				 		}
			 		}
			 	}
			 	
			 	if(movingBlockHorizontalHeroIsOn != null)
			 	{
				 	if(!(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(movingBlockHorizontalHeroIsOn.getHitbox())))
				 	{
				 		onMovingBlockHorizontal = false;
				 		movingBlockHorizontalHeroIsOn = null;
				 	}
			 	}
			 	
			 	if(movingBlockVerticalHeroIsOn != null)
			 	{
				 	if(!(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(movingBlockVerticalHeroIsOn.getHitbox())))
				 	{
				 		onMovingBlockVertical = false;
				 		movingBlockVerticalHeroIsOn = null;
				 	}
			 	}
			 	
			 	//checking if hero is jumping
				if(up || fall)
				{	
					boolean okToMove = true;
					for(int i = 0; i < blocks.size(); i++)
					{
						if(new Rectangle(hero.getX(),hero.getY()-1,hero.getWidth(),hero.getHeight()).intersects(blocks.get(i).getHitbox())){
							okToMove = false;
							up = false;
						}
					}
					for(int i = 0; i < movingBlocksHorizontal.size(); i++)
					{
						if(new Rectangle(hero.getX(),hero.getY()-1,hero.getWidth(),hero.getHeight()).intersects(movingBlocksHorizontal.get(i).getHitbox())){
							okToMove = false;
							up = false;
						}
					}
					for(int i = 0; i < movingBlocksVertical.size(); i++)
					{
						if(new Rectangle(hero.getX(),hero.getY()-1,hero.getWidth(),hero.getHeight()).intersects(movingBlocksVertical.get(i).getHitbox())){
							okToMove = false;
							up = false;
						}
					}
					
					//checking if hero fell to death
					if(hero.getY() > 800)
					{
						System.out.println("DEAD");
					}
					
					if(okToMove) 
					{
						num++;
						jump();
					}else {
						boolean okToMoveDown = true;
						for(int i = 0; i < blocks.size(); i++)
						{
							if(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(blocks.get(i).getHitbox())){
								okToMoveDown = false;
							}
						}
						for(int i = 0; i < movingBlocksHorizontal.size(); i++)
						{
							if(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(movingBlocksHorizontal.get(i).getHitbox())){
								okToMoveDown = false;
							}
						}
						for(int i = 0; i < movingBlocksVertical.size(); i++)
						{
							if(new Rectangle(hero.getX(),hero.getY()+1,hero.getWidth(),hero.getHeight()).intersects(movingBlocksVertical.get(i).getHitbox())){
								okToMoveDown = false;
							}
						}

						if(okToMoveDown)
						{
							jumpCount = 51;
							fall(1);
						}
					}
				}
				repaint();
			}

			if(restart)
			{
				restart = false;
				gameOn = true;
			}
			
			try
			{
				t.sleep(3);
			}catch(InterruptedException e)
			{
			}
		}
	}

	//jump function
	public void jump(){
		int dir = -1;
		if(jumpCount < 50 && up)
		{
			jumpCount++;
		}else
		{
			jumpCount--;
			dir = 1;
			up = false;
			fall = true;
		}
		if(jumpCount == 0)
		{
			fall = false;
		}
		fall(dir);
	}

	//fall function
	public void fall(int dir){
		hero.setY(dir*3);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.CYAN);
		g2d.fillRect(0, 0, 1000, 800);

		//drawing parallax background
//		for(int i = backgrounds.length; i > 0; i--){
//			g2d.drawImage(backgrounds2[i-1],bgX[i-1]-1920-960,-25*(11-i),null);
//			g2d.drawImage(backgrounds2[i-1],bgX[i-1]-960,-25*(11-i),null);
//			g2d.drawImage(backgrounds2[i-1],bgX[i-1]+960,-25*(11-i),null);
//		}

		//drawing level
		for(int i = 0; i < blocks.size(); i++)
		{
			g2d.drawImage(sand,blocks.get(i).getX(),blocks.get(i).getY(),blocks.get(i).getWidth(),blocks.get(i).getHeight(),null);
		}
		
		for(int i = 0; i < movingBlocksHorizontal.size(); i++)
		{
			g2d.drawImage(wood1,movingBlocksHorizontal.get(i).getX(),movingBlocksHorizontal.get(i).getY(),movingBlocksHorizontal.get(i).getWidth(),movingBlocksHorizontal.get(i).getHeight(),null);
		}
		
		for(int i = 0; i < movingBlocksVertical.size(); i++)
		{
			g2d.drawImage(wood2,movingBlocksVertical.get(i).getX(),movingBlocksVertical.get(i).getY(),movingBlocksVertical.get(i).getWidth(),movingBlocksVertical.get(i).getHeight(),null);
		}
		
		for(int i = 0; i < bullets.size(); i++)
		{
			g2d.drawImage(bullet,bullets.get(i).getX(), bullets.get(i).getY(), bullets.get(i).getWidth(), bullets.get(i).getHeight(),null);
		}
		
		g2d.setColor(Color.GREEN);
		for(int i = 0; i < enemies.size(); i++)
		{
			if(!enemies.get(i).isHit())
			{
				g2d.setColor(Color.GREEN);
				g2d.draw(enemies.get(i).getHitbox());
			}else {
				enemies.get(i).setHitChangeCounter(true);
				g2d.setColor(Color.RED);
				g2d.draw(enemies.get(i).getHitbox());
				if(enemies.get(i).getHitChangeCounter() == 50)
				{
					enemies.get(i).setIsHit(false);
					enemies.get(i).setHitChangeCounter(false);
				}
			}
		}
		
		//drawing hero
		if(movingRight)
			g2d.drawImage(guyImageRight[imgCount], hero.getX(),hero.getY(), null);
		if(movingLeft)
			g2d.drawImage(guyImageLeft[imgCount], hero.getX(), hero.getY(), null);
	}
	
	public void keyPressed(KeyEvent key)
	{
		if(key.getKeyCode() == KeyEvent.VK_D)
		{
			right = true;
		}
		if(key.getKeyCode() == KeyEvent.VK_A)
		{
			left = true;
		}
		if(key.getKeyCode() == KeyEvent.VK_S)
		{
			shot = true;
		}
		if(key.getKeyCode() == 82)
		{
			restart = true;
		}
		if(canJump)
		{
			if(key.getKeyCode() == KeyEvent.VK_W)
			{
				up = true;
				canJump = false;
			}
		}
	}
	
	public void keyReleased(KeyEvent key)
	{
		if(key.getKeyCode() == KeyEvent.VK_D)
		{
			right = false;
		}
		if(key.getKeyCode() == KeyEvent.VK_A)
		{
			left = false;
		}
		if(key.getKeyCode() == KeyEvent.VK_S) {
			shot = false;
		}
	}
	
	public void keyTyped(KeyEvent key)
	{
	}
	
	public static void main(String args[])
	{
		SideScroller app = new SideScroller();
	}
}