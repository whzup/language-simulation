/* The vocabulary class includes the methods to create a vocabulary
   with a given length and with the appropriate distributions */

class Vocabulary {
  int count;
  ArrayList<Word> vocabulary = new ArrayList<Word>();

  // All vowels that can be used for words in mutation order
  StringList vowels = new StringList(
    "a", "ä", "e", "i", "j", "o", "ö", "u", "ü", "y"
  );

  // All consonants that can be used for words in mutation order
  StringList consonants = new StringList(
    "b", "p", "d", "t", "g", "h", "k", "c", "q",
    "m", "n", "l", "r", "s", "z", "f", "v", "w", "x"
  );

  // Classification of the vocabulary (Lingua or island)
  char vocCls;

  Vocabulary(char classification) {
    count = 2;
    vocCls = classification;
    createVocabulary();
  }

  // Method to calculate poisson distributed
  // random numbers with lambda = 4.8
  // algorithm by Donald E. Knuth
  int poissonDist() {
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

  // Calculate the Zipf distributed probability
  float zipfDist(int rank) {
    float sum = 0;
    for(int i = 0; i < count; i++) {
      sum += 1/float(i);
    }
    return (1/float(rank)) / sum;
  }

  // Returns a new syllable with either 2 or 3 letters
  String createSyllable(int sylLength) {
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

  // Returns a new word with randomly generated syllables
  String createWord(int wordLength) {
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

  // Create a new vocabulary
  void createVocabulary() {
    for (int i = 0; i < count; i++) {
      Word newWord = new Word(createWord(poissonDist()), vocCls);
      vocabulary.add(newWord);
    }
  }

}
