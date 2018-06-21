import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * 
 * @author Ziwei Wu
 *
 */
public class Fallout3Roller {
	public static final int STARTING_POINTS = 40, NUMBER_OF_STATS = 5;
	
	private static Stats[] stats;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Fallout 3 Random Stats Roller");
		// 4 JPanels
		// Order stated is how they appear on the JFrame from left to right

		// 1st one contains a JTextArea for the stats
		JPanel primaryStatsPanel = new JPanel();
		primaryStatsPanel.setLayout(new GridLayout(1, 0));
		JTextArea primaryStatsTextArea = new JTextArea();
		primaryStatsTextArea.setEditable(false);
		primaryStatsPanel.add(primaryStatsTextArea);
		primaryStatsPanel.setPreferredSize(new Dimension(210, 710));

		// 4th one has JTextArea to show derived stats and skills
		JPanel showDerivedStatsPanel = new JPanel();
		showDerivedStatsPanel.setLayout(new GridLayout(1, 0));
		JTextArea showDerivedStatsTextArea = new JTextArea();
		showDerivedStatsTextArea.setEditable(false);
		showDerivedStatsPanel.add(showDerivedStatsTextArea);

		// 2nd one contains a giant reroll button
		JPanel primaryStatsRollPanel = new JPanel();
		primaryStatsRollPanel.setLayout(new GridLayout(1, 0));
		JButton primaryStatsRollButton = new JButton();
		primaryStatsRollButton.setText("<html>Reroll<br /><br />Stats</html>");
		primaryStatsRollButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stats = StatsFactory.statsCreator(NUMBER_OF_STATS);
				String s = "";
				for (int i = 0; i < stats.length; i++) {
					s += "Stats for roll " + (i + 1) + ":\n";
					Stats st = stats[i];
					s += st.getString() + "\n";
				}
				primaryStatsTextArea.setText(s);
				showDerivedStatsTextArea.setText(
						"Derived Stats and " + "Skills for roll " 
				+ (1) + ":\n\n" + stats[0].calcDerivedStats());
			}

		});
		primaryStatsRollButton.doClick();
		primaryStatsRollPanel.add(primaryStatsRollButton);

		// 3rd one has buttons to show derived stats
		JPanel calcDerivedStatsPanel = new JPanel();
		calcDerivedStatsPanel.setLayout(new GridLayout(NUMBER_OF_STATS, 0));
		for (int i = 0; i < NUMBER_OF_STATS; i++) {
			JButton b = new JButton("See Stats for roll " + (i + 1));
			final int temp = i;
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showDerivedStatsTextArea.setText("Derived Stats and "
							+ "Skills for roll " + (temp + 1) + ":\n\n"
							+ stats[temp].calcDerivedStats());
				}
			});
			calcDerivedStatsPanel.add(b);
			if (i == 0)
				b.doClick();
		}

		frame.setLayout(new GridLayout(1, 4));
		frame.add(primaryStatsPanel);
		frame.add(primaryStatsRollPanel);
		frame.add(calcDerivedStatsPanel);
		frame.add(showDerivedStatsPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Gives a random number between min to max inclusive
	 * @param min: int
	 * @param max: int that must be bigger than min
	 * @return: a random number between min and max inclusive
	 */
	public static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}

class StatsFactory {
	/**
	 * Creates an array of Stats of size amount
	 * @param amount: an int of 1 or more
	 * @return: an array of Stats
	 */
	public static Stats[] statsCreator(int amount) {
		Stats[] stats = new Stats[amount];
		for (int i = 0; i < amount; i++) {
			stats[i] = new Stats(10, 10, 10, 10, 10, 10, 10);
		}
		return stats;
	}
}

class Stats {
	private int[] statArr, skillArr;
	private int tagged1, tagged2, tagged3;
	// 0 =str, 1 =per, 2 = end, 3 = cha, 4 =intel, 5= agi, 6 = luck
	private String[] skills;

	/**
	 * Constructor for the Stats class
	 * @param str: int from 1 to 10
	 * @param per: int from 1 to 10
	 * @param end: int from 1 to 10
	 * @param cha: int from 1 to 10
	 * @param intel: int from 1 to 10
	 * @param agi: int from 1 to 10
	 * @param luck: int from 1 to 10
	 */
	public Stats(int str, int per, int end, int cha, int intel, int agi, int luck) {
		this.statArr = new int[] { str, per, end, cha, intel, agi, luck };
		this.reBalance();
		this.setSkillArray();
		this.setTagged();
	}
	
	/**
	 * Rebalances the stats by putting them to 40 points total
	 */
	private void reBalance() {
		while (this.getTotalPoints() < Fallout3Roller.STARTING_POINTS) {
			int re = Fallout3Roller.getRandomNumberInRange(0, 6);
			if (this.statArr[re] < 10)
				this.statArr[re] += 1;
			else
				continue;
		}
		while (this.getTotalPoints() > 40) {
			int re = Fallout3Roller.getRandomNumberInRange(0, 6);
			if (this.statArr[re] > 1)
				this.statArr[re] -= 1;
			else
				continue;
		}
	}

	/**
	 * Return the total amount of points
	 * @return: the total amount of points
	 */
	private int getTotalPoints() {
		int i = 0;
		for (int f : this.statArr) {
			i += f;
		}
		return i;
	}

	/**
	 * initializes some of the arrays
	 */
	private void setSkillArray() {
		int str = statArr[0], per = statArr[1], end = statArr[2], cha = statArr[3],
				intel = statArr[4], agi = statArr[5], luck = statArr[6];
		int strSkill = (int) ((str * 2) + 2 + Math.ceil(luck / 2.0));
		int perSkill = (int) ((per * 2) + 2 + Math.ceil(luck / 2.0));
		int endSkill = (int) ((end * 2) + 2 + Math.ceil(luck / 2.0));
		int charSkill = (int) ((cha * 2) + 2 + Math.ceil(luck / 2.0));
		int intSkill = (int) ((intel * 2) + 2 + Math.ceil(luck / 2.0));
		int agiSkill = (int) ((agi * 2) + 2 + Math.ceil(luck / 2.0));
		this.skillArr = new int[] { charSkill, endSkill, perSkill, perSkill, 
				perSkill, intSkill, strSkill, intSkill, intSkill, agiSkill, 
				agiSkill, charSkill, endSkill };
		this.skills = new String[] { "Barter: ", "Big Guns: ", "Energy Weapons: ", 
				"Explosives: ", "Lockpick: ","Medicine: ", "Melee Weapons: ", 
				"Repair: ", "Science: ", "Small Guns: ", "Sneak: ", "Speech: ",
				"Unarmed: " };
	}

	/**
	 * Set which skills are tagged (+15 points)
	 */
	private void setTagged() {
		tagged1 = Fallout3Roller.getRandomNumberInRange(0, skillArr.length - 1);
		tagged2 = Fallout3Roller.getRandomNumberInRange(0, skillArr.length - 1);
		tagged3 = Fallout3Roller.getRandomNumberInRange(0, skillArr.length - 1);
		while (tagged1 == tagged2 || tagged1 == tagged3 || tagged2 == tagged3) {
			tagged1 = Fallout3Roller.getRandomNumberInRange(0, skillArr.length - 1);
			tagged2 = Fallout3Roller.getRandomNumberInRange(0, skillArr.length - 1);
			tagged3 = Fallout3Roller.getRandomNumberInRange(0, skillArr.length - 1);
		}
		this.skillArr[tagged1] += 15;
		this.skillArr[tagged2] += 15;
		this.skillArr[tagged3] += 15;
	}

	/**
	 * Returns a string of the primary stats
	 * @return String containing the details of the primary stats
	 */
	public String getString() {
		// 0 =str, 1 =per, 2 = end, 3 = cha, 4 =intel, 5= agi, 6 = luck
		String s = "";
		s += "STR: " + this.statArr[0] + "\n";
		s += "PER: " + this.statArr[1] + "\n";
		s += "END: " + this.statArr[2] + "\n";
		s += "CHA: " + this.statArr[3] + "\n";
		s += "INT: " + this.statArr[4] + "\n";
		s += "AGI: " + this.statArr[5] + "\n";
		s += "LUCK: " + this.statArr[6] + "\n";
		return s;
	}

	/**
	 * Returns a string containing the derived stats and skill stats
	 * @return: String containing the derived stats and skill stats
	 */
	public String calcDerivedStats() {
		int str = statArr[0], end = statArr[2], intel = statArr[4],
				agi = statArr[5], luck = statArr[6];

		String s = "Skills: \n\n";

		// skills
		for (int i = 0; i < this.skills.length; i++) {
			s += this.skills[i] + this.skillArr[i];
			if (i == tagged1 || i == tagged2 || i == tagged3) {
				s += " (Tagged!)";
			}
			s += "\n";
		}

		s += "\nDerived Stats: \n\n";

		// Derived Stats
		s += "Action Points: " + ((agi * 2) + 65) + "\n";
		s += "Carry Weight: " + ((str * 10) + 150) + "\n";
		s += "Critical Chance: " + (luck) + "%\n";
		s += "Damage Resistance: " + (0) + "\n";
		s += "Fire Resistance: " + (0) + "\n";
		s += "Hit Points: " + ((end * 20) + 100) + "\n";
		s += "Melee Damage: " + ((str * 0.5)) + "\n";
		s += "Poison Resistance: " + ((end - 1) * 5) + "%\n";
		s += "Radiation Resistance: " + ((end - 1) * 2) + "%\n";
		s += "Hit Points: " + ((end * 20) + 100) + "\n";
		s += "Skill Rate: " + ((intel) + 10) + "\n";
		s += "Unarmed Damage: " + ((this.skillArr[this.skillArr.length - 1] / 20) +
				0.5) + "\n";
		return s;
	}

}
