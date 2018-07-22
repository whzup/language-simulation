/**
  * @author Aaron
  */

class Vocabulary {
  public int wordCount;
  public ArrayList<Word> vocabulary = new ArrayList<Word>();

  // Classification of the vocabulary (Lingua or island)
  public char vocCls;

  /** Vocabulary class constructor
    * <p>
    * The vocabulary class includes the methods to create a vocabulary
    * with a given length and with the appropriate distributions.
    * <p>
    * @param classification The classification of the vocabulary
    *                       "i" : island vocabulary
    *                       "l" : lingua franca vocabulary
    */
  public Vocabulary(char classification) {
    wordCount = 2;
    vocCls = classification;
    createVocabulary();
  }

  /** Helper method to calculate poisson distributed random numbers
    * <p>
    * Calculates a poisson distributed random number with lambda = 4.8.
    * This method is used to calculate the word length.
    * The algorithm was adapted from Donald E. Knuth's work.
    * <p>
    * @return A poisson distributed random number with a lambda of 4.8
    */
  private int poissonDist() {
    double lambda = 4.8;
    double p = 1.0;
    double l = Math.exp(-lambda);
    int k = 0;

    do {
      k += 1;
      double random = random(1);
      p *= random;
    } while(p > l);

    return k-1;
  }

  /** Calculate a Zipf distributed probability
    * <p>
    * @param rank Rank on a scale of word usage
    * @return The probability that this word occurs
    */
  private float zipfDist(int rank) {
    float sum = 0;
    for(int i = 0; i < wordCount; i++) {
      sum += 1/float(i);
    }
    return (1/float(rank)) / sum;
  }

  /** Returns a new syllable with either 2 or 3 letters
    * <p>
    * The syllables are randomly generate with different letter orders.
    * The letter orders are permutations of vocal and consonant combinations.
    * <p>
    * @param sylLength Length of the syllable
    * @return A syllable of the specified length
    */
  public String createSyllable(int sylLength) {
    String syll = new String();

    // If the length is 2 the syllable contains just a consonant and a vowel (C V)
    if(sylLength == 2) {
      int consonant = int(random(consonants.size()));
      int vowel = int(random(vowels.size()));

      syll += consonants.get(consonant);
      syll += vowels.get(vowel);
    }

    // If the length is 3 create a random order of vowels and consonants
    else if(sylLength == 3) {
      int ran = int(random(6));
      int vowel1 = int(random(vowels.size()));
      int vowel2 = int(random(vowels.size()));
      int consonant1 = int(random(consonants.size()));
      int consonant2 = int(random(consonants.size()));

      // V C V
      if(ran == 0) {
        syll += vowels.get(vowel1);
        syll += consonants.get(consonant1);
        syll += vowels.get(vowel2);
      }

      // C V C
      else if(ran == 1) {
        syll += consonants.get(consonant1);
        syll += vowels.get(vowel1);
        syll += consonants.get(consonant2);
      }

      // C C V
      else if(ran == 2) {
        syll += consonants.get(consonant1);
        syll += consonants.get(consonant2);
        syll += vowels.get(vowel1);
      }

      // V C C
      else if(ran == 3) {
        syll += vowels.get(vowel1);
        syll += consonants.get(consonant1);
        syll += consonants.get(consonant2);
      }

      // V V C
      else if(ran == 4) {
        syll += vowels.get(vowel1);
        syll += vowels.get(vowel2);
        syll += consonants.get(consonant1);
      }

      //C V V
      else if(ran == 5) {
        syll += consonants.get(consonant1);
        syll += vowels.get(vowel1);
        syll += vowels.get(vowel2);
      }
    }
    return syll;
  }

  /** Returns a new word
    * <p>
    * The word is created by adding randomly generated syllables
    * <p>
    * @param wordLength Length of the word
    * @return A word generated with random syllables
    */
  public String createWord(int wordLength) {
    int sylNumber = wordLength / 2;
    String newSyllable = new String();
    String newWord = new String();
    // If the word length is even then only create syllables of length 2
    if(wordLength % 2 == 0) {
      for(int i = 0; i < sylNumber; i++) {
        newSyllable = createSyllable(2);
        newWord += newSyllable;
      }
    }
    // If the word length is odd then create syllables of length 2 and one with length 3
    else {
      newSyllable += createSyllable(3);
      newWord += newSyllable;
      for(int j = 0; j < (sylNumber-1); j++) {
        newSyllable = createSyllable(2);
        newWord += newSyllable;
      }
    }
    return newWord;
  }

  /** Create a new vocabulary
    * <p>
    * Initializes the Vocabulary class with words.
    */
  public void createVocabulary() {
    for (int i = 0; i < wordCount; i++) {
      Word newWord = new Word(createWord(poissonDist()), vocCls);
      vocabulary.add(newWord);
    }
  }

}
