package hu.edudroid.blackmarkettmit.shared;

public class NameUtils {
	private static final String[] PREFIXES = { "Dr", "Mr", "Lord", "Sir",
			"Lady", "Mrs", "Ms", "Mc", "Mac" };
	private static final String[] ADJECTIFS = { "Small", "Fat", "Heavy",
			"Healthy", "Skinny", "Bold", "Hairy", "Loud", "Quite", "Lousy",
			"Red", "Green", "Blue", "White", "Black", "Yellow", "Orange",
			"Pink", "Gray", "Purple", "Poor", "Rich", "Stoned", "Wasted",
			"Sexy", "Smelly", "Sleepy", "Fit", "Timid", "Drunk", "Crazy",
			"Smokey", "Machinegun", "Wise", "Bullettooth", "Rotten", "Weak",
			"Old", "Young", "Stupid" };
	private static final String[] FIRST_NAMES = { "Abe", "Abraham", "Adolfo",
			"Adolph", "Allen", "Alejandro", "Alvin", "Anderson", "Angelo",
			"Andy", "Anthony", "Archie", "Arnold", "Arthur", "Barney",
			"Barrett", "Barry", "Bart", "Benny", "Bill", "Billie", "Billy",
			"Blake", "Bob", "Bobbie", "Bobby", "Booker", "Boris", "Brad",
			"Bradly", "Brendan", "Brice", "Burt", "Buster", "Byron", "Calvin",
			"Carson", "Carlos", "Carter", "Cecil", "Cedrick", "Chad",
			"Charles", "Charlie", "Chase", "Chester", "Chet", "Chris", "Chuck",
			"Claude", "Cliff", "Clifford", "Clint", "Clyde", "Cody", "Collin",
			"Cortez", "Craig", "Cruz", "Curt", "Curtis", "Cyrus", "Dale",
			"Dalton", "Damon", "Dan", "Danny", "Darryl", "Daryl", "Dave",
			"Denis", "Denny", "Denver", "Derek", "Derick", "Devon", "Dexter",
			"Dick", "Dillon", "Dominic", "Don", "Doug", "Douglas", "Drew",
			"Duncan", "Dustin", "Dwayne", "Dwight", "Earl", "Ed", "Eddie",
			"Eddy", "Edison", "Edward", "Elliot", "Elvis", "Emil", "Eric",
			"Erik", "Erin", "Ervin", "Erwin", "Eugene", "Evan", "Everett",
			"Ezekiel", "Felix", "Floyd", "Forrest", "Frank", "Frankie", "Fred",
			"Freddie", "Freddy", "Garry", "Gary", "Gavin", "Gerald", "Gerry",
			"Gil", "Gordon", "Grant", "Greg", "Gus", "Hans", "Harley",
			"Harold", "Harris", "Harvey", "Hector", "Howard", "Hubert",
			"Hunter", "Irving", "Irwin", "Issac", "Jack", "Jackie", "Jackson",
			"Jake", "James", "Jamie", "Jarred", "Jay", "Jed", "Jeff",
			"Jeffrey", "Jefferson", "Jeffrey", "Jeremy", "Jess", "Jesse",
			"Jessie", "Jim", "Jimmie", "Jimmy", "Joe", "Joseph", "Joey",
			"John", "Johnny", "Johnson", "Jonah", "Jonas", "Joseph", "Joshua",
			"Juan", "Jude", "Justin", "Keith", "Kelvin", "Keneth", "Kenny",
			"Kevin", "Kieth", "Kip", "Kirk", "Kraig", "Kurt", "Kurtis",
			"Laurence", "Lawrence", "Lee", "Lenny", "Leo", "Leon", "Leslie",
			"Lesley", "Levi", "Lincoln", "Louis", "Loyd", "Lucas", "Luke",
			"Lynn", "Malcolm", "Marc", "Marco", "Marcos", "Marcus", "Mario",
			"Mark", "Martin", "Marty", "Mathew", "Matt", "Max", "Mel",
			"Marlin", "Michael", "Mickey", "Mike", "Mikey", "Mitchel", "Myles",
			"Nathan", "Neal", "Ned", "Nelson", "Nick", "Nicky", "Noah",
			"Norman", "Oliver", "Oscar", "Otis", "Pat", "Perry", "Pete",
			"Phil", "Porter", "Preston", "Quincy", "Quinn", "Ray", "Reggie",
			"Rex", "Rey", "Richie", "Rick", "Rickie", "Ricky", "Rob", "Robbie",
			"Robby", "Rocco", "Rocky", "Rod", "Rodger", "Rodney", "Roy",
			"Rudy", "Russ", "Ryan", "Sal", "Sam", "Sammy", "Saul", "Scott",
			"Sean", "Seth", "Shane", "Sheldon", "Sherman", "Sid", "Sol", "Son",
			"Sonny", "Stacy", "Stan", "Stanley", "Steve", "Stevie", "Stewart",
			"Sydney", "Thomas", "Tim", "Timmy", "Toby", "Tod", "Tom", "Tomas",
			"Tommy", "Toney", "Tony", "Travis", "Trey", "Truman", "Tyler",
			"Tyson", "Valentine", "Van", "Vance", "Vito", "Wade", "Walter",
			"Wayne", "Wes", "Wesley", "Wilber", "Will", "William", "Willie",
			"Willy", "Wyatt", "Zachariah", "Zack", "Zane", "Bruce" };
	private static final String[] MIDDLE_LETTERS = { "A", "C", "D", "G", "I",
			"J", "T", "W", "X", "Y" };
	private static final String[] MIDDLE_NAMES = { "Dog", "Raper", "Artist",
			"Rock", "Clown", "Goon", "General", "Storm", "Trooper", "Limp",
			"Rocker", "Scar", "Butcher", "Pope", "Boss", "Good Samaritan",
			"Smartass", "Tongue", "Hound", "Russian", "Turc", "Jew", "Spanish",
			"Pocket", "Peach", "Kid", "Gent", "Bomb" };
	private static final String[] LAST_NAMES = { "Adams", "Allen", "Anderson",
			"Bailey", "Baker", "Barnes", "Bell", "Bennett", "Brooks", "Brown",
			"Butler", "Campbell", "Carter", "Clark", "Collins", "Cook",
			"Cooper", "Cox", "Cruz", "Davis", "Díaz", "Edwards", "Evans",
			"Fisher", "Flores", "Foster", "García", "González", "Gray",
			"Green", "Gutiérrez", "Gómez", "Hall", "Harris", "Hernández",
			"Hill", "Howard", "Hughes", "Jackson", "James", "Jenkins",
			"Johnson", "Jones", "Kelly", "King", "Lee", "Lewis", "Long",
			"López", "Martin", "Martínez", "Miller", "Mitchell", "Moore",
			"Morales", "Morgan", "Morris", "Murphy", "Myers", "Nelson",
			"Nguyen", "Ortiz", "Parker", "Perry", "Peterson", "Phillips",
			"Powell", "Price", "Pérez", "Ramírez", "Reed", "Reyes",
			"Richardson", "Rivera", "Roberts", "Robinson", "Rodríguez",
			"Rogers", "Ross", "Russell", "Sanders", "Scott", "Smith",
			"Stewart", "Sullivan", "Sánchez", "Taylor", "Thomas", "Thompson",
			"Torres", "Turner", "Walker", "Ward", "Watson", "White",
			"Williams", "Wilson", "Wood", "Wright", "Young", "O'Hara", "O'Neil"};
	private static final String[] SUFFIXES = { "Jnr.", "Snr.", "MC", "JD",
			"MD", "MBA", "MSc", "PhD" };

	/**
	 * How names are generated FIRST_NAMES FIRST_NAMES + LAST_NAMES PREFIXES +
	 * FIRST_NAMES PREFIXES + ADJECTIFS PREFIXES + MIDDLE_NAMES PREFIXES +
	 * MIDDLE_LETTERS PREFIXES + LAST_NAMES PREFIXES + FIRST_NAMES + LAST_NAMES
	 * FIRST_NAMES + LAST_NAMES + SUFFIXES ADJECTIFS + FIRST_NAMES ADJECTIFS +
	 * LAST_NAMES FIRST_NAMES + "The + MIDDLE_NAMES + " + LAST_NAMES FIRST_NAMES
	 * + MIDDLE_LETTERS + LAST_NAMES
	 */

	private static final int FIRST_NAMES_COUNT = FIRST_NAMES.length;
	private static final int NORMAL_NAMES_COUNT = FIRST_NAMES.length
			* LAST_NAMES.length;
	private static final int PREFIXED_FIRST_NAMES_COUNT = FIRST_NAMES.length
			* PREFIXES.length;
	private static final int PREFIXED_ADJECTIFS_COUNT = ADJECTIFS.length
			* PREFIXES.length;
	private static final int PREFIXED_MIDDLE_NAMES_COUNT = MIDDLE_NAMES.length
			* PREFIXES.length;
	private static final int PREFIXED_MIDDLE_LETTERS_COUNT = MIDDLE_LETTERS.length
			* PREFIXES.length;
	private static final int PREFIXED_LAST_NAMES_COUNT = LAST_NAMES.length
			* PREFIXES.length;
	private static final int PREFIXED_NAMES_COUNT = FIRST_NAMES.length
			* LAST_NAMES.length * PREFIXES.length;
	private static final int SUFFIXED_FIRST_NAMES_COUNT = FIRST_NAMES.length
			* SUFFIXES.length;
	private static final int ADJECTIFS_WITH_FIRST_NAMES_COUNT = FIRST_NAMES.length
			* ADJECTIFS.length;
	private static final int ADJECTIFS_WITH_LAST_NAMES_COUNT = LAST_NAMES.length
			* ADJECTIFS.length;
	private static final int THREE_PART_NAMES_COUNT = FIRST_NAMES.length
			* MIDDLE_NAMES.length * LAST_NAMES.length;
	private static final int THREE_PART_NAMES_WITH_LETTERS_COUNT = FIRST_NAMES.length
			* MIDDLE_LETTERS.length * LAST_NAMES.length;

	private static final int NORMAL_NAMES_START = FIRST_NAMES_COUNT;
	private static final int PREFIXED_FIRST_NAMES_START = NORMAL_NAMES_START
			+ NORMAL_NAMES_COUNT;
	private static final int PREFIXED_ADJECTIFS_START = PREFIXED_FIRST_NAMES_START
			+ PREFIXED_FIRST_NAMES_COUNT;
	private static final int PREFIXED_MIDDLE_NAMES_START = PREFIXED_ADJECTIFS_START
			+ PREFIXED_ADJECTIFS_COUNT;
	private static final int PREFIXED_MIDDLE_LETTERS_START = PREFIXED_MIDDLE_NAMES_START
			+ PREFIXED_MIDDLE_NAMES_COUNT;
	private static final int PREFIXED_LAST_NAMES_START = PREFIXED_MIDDLE_LETTERS_START
			+ PREFIXED_MIDDLE_LETTERS_COUNT;
	private static final int PREFIXED_NAMES_START = PREFIXED_LAST_NAMES_START
			+ PREFIXED_LAST_NAMES_COUNT;
	private static final int SUFFIXED_FIRST_NAMES_START = PREFIXED_NAMES_START
			+ PREFIXED_NAMES_COUNT;
	private static final int ADJECTIFS_WITH_FIRST_NAMES_START = SUFFIXED_FIRST_NAMES_START
			+ SUFFIXED_FIRST_NAMES_COUNT;
	private static final int ADJECTIFS_WITH_LAST_NAMES_START = ADJECTIFS_WITH_FIRST_NAMES_START
			+ ADJECTIFS_WITH_FIRST_NAMES_COUNT;
	private static final int THREE_PART_NAMES_START = ADJECTIFS_WITH_LAST_NAMES_START
			+ ADJECTIFS_WITH_LAST_NAMES_COUNT;
	private static final int THREE_PART_NAMES_WITH_LETTERS_START = THREE_PART_NAMES_START
			+ THREE_PART_NAMES_COUNT;

	private static final int TOTAL_NAME_COUNT = FIRST_NAMES_COUNT
			+ NORMAL_NAMES_COUNT + PREFIXED_FIRST_NAMES_COUNT
			+ PREFIXED_ADJECTIFS_COUNT + PREFIXED_MIDDLE_NAMES_COUNT
			+ PREFIXED_MIDDLE_LETTERS_COUNT + PREFIXED_LAST_NAMES_COUNT
			+ PREFIXED_NAMES_COUNT + SUFFIXED_FIRST_NAMES_COUNT
			+ ADJECTIFS_WITH_FIRST_NAMES_COUNT
			+ ADJECTIFS_WITH_LAST_NAMES_COUNT + THREE_PART_NAMES_COUNT
			+ THREE_PART_NAMES_WITH_LETTERS_COUNT;

	public static final int getTotalNameCount() {
		return TOTAL_NAME_COUNT;
	}
	
	public static final String getName(int code) {
		code = code % TOTAL_NAME_COUNT;
		if (code < NORMAL_NAMES_START) {
			return FIRST_NAMES[code];
		}
		if (code < PREFIXED_FIRST_NAMES_START) {
			code = code - NORMAL_NAMES_START;
			return FIRST_NAMES[(code / LAST_NAMES.length)] + " "
					+ LAST_NAMES[(code % LAST_NAMES.length)];
		}
		if (code < PREFIXED_ADJECTIFS_START) {
			code = code - PREFIXED_FIRST_NAMES_START;
			return PREFIXES[(code / FIRST_NAMES.length)] + " "
					+ FIRST_NAMES[(code % FIRST_NAMES.length)];
		}
		if (code < PREFIXED_MIDDLE_NAMES_START) {
			code = code - PREFIXED_ADJECTIFS_START;
			return PREFIXES[(code / ADJECTIFS.length)] + " "
					+ ADJECTIFS[(code % ADJECTIFS.length)];
		}
		if (code < PREFIXED_MIDDLE_LETTERS_START) {
			code = code - PREFIXED_MIDDLE_NAMES_START;
			return PREFIXES[(code / MIDDLE_NAMES.length)] + " "
					+ MIDDLE_NAMES[(code % MIDDLE_NAMES.length)];
		}
		if (code < PREFIXED_LAST_NAMES_START) {
			code = code - PREFIXED_MIDDLE_LETTERS_START;
			return PREFIXES[(code / MIDDLE_LETTERS.length)] + " "
					+ MIDDLE_LETTERS[(code % MIDDLE_LETTERS.length)];
		}
		if (code < PREFIXED_NAMES_START) {
			code = code - PREFIXED_LAST_NAMES_START;
			return PREFIXES[(code / LAST_NAMES.length)] + " "
					+ LAST_NAMES[(code % LAST_NAMES.length)];
		}
		if (code < SUFFIXED_FIRST_NAMES_START) {
			code = code - PREFIXED_NAMES_START;
			return PREFIXES[(code / (FIRST_NAMES.length * LAST_NAMES.length))]
					+ " "
					+ FIRST_NAMES[(code % (FIRST_NAMES.length * LAST_NAMES.length))
							/ LAST_NAMES.length] + " "
					+ LAST_NAMES[(code % LAST_NAMES.length)];
		}
		if (code < ADJECTIFS_WITH_FIRST_NAMES_START) {
			code = code - SUFFIXED_FIRST_NAMES_START;
			return FIRST_NAMES[(code / SUFFIXES.length)] + " "
					+ SUFFIXES[(code % SUFFIXES.length)];
		}
		if (code < ADJECTIFS_WITH_LAST_NAMES_START) {
			code = code - ADJECTIFS_WITH_FIRST_NAMES_START;
			return ADJECTIFS[(code / FIRST_NAMES.length)] + " "
					+ FIRST_NAMES[(code % FIRST_NAMES.length)];
		}
		if (code < THREE_PART_NAMES_START) {
			code = code - ADJECTIFS_WITH_LAST_NAMES_START;
			return ADJECTIFS[(code / LAST_NAMES.length)] + " "
					+ LAST_NAMES[(code % LAST_NAMES.length)];
		}
		if (code < THREE_PART_NAMES_WITH_LETTERS_START) {
			code = code - THREE_PART_NAMES_START;
			return FIRST_NAMES[(code / (MIDDLE_NAMES.length * LAST_NAMES.length))]
					+ " \"The "
					+ MIDDLE_NAMES[(code % (MIDDLE_NAMES.length * LAST_NAMES.length))
							/ LAST_NAMES.length]
					+ "\" "
					+ LAST_NAMES[(code % LAST_NAMES.length)];
		}
		code = code - THREE_PART_NAMES_WITH_LETTERS_START;
		return FIRST_NAMES[(code / (MIDDLE_LETTERS.length * LAST_NAMES.length))]
				+ " "
				+ MIDDLE_LETTERS[(code % (MIDDLE_LETTERS.length * LAST_NAMES.length))
						/ LAST_NAMES.length]
				+ ". "
				+ LAST_NAMES[(code % LAST_NAMES.length)];
	}
}
