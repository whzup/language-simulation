/**
  * @author Aaron
  */

class Word {
  public int length;     // Length of the word
  // int island;     // Island to whose vocabulary the word belongs
  // int parent;     // ID of the agent that created the word USEFUL?
  public String letters; // Letters which the word contains
  public String[] etym;  // Origin of the word; from which word it was derived
  public char vocClass;  // Classification of the word in the different vocabularies
                         // i = island; l = lingua

  /** Word class constructor
    * <p>
    * The word class includes all methods to modify words. It also
    * contains the tools to retrace the origin of words.
    * <p>
    * @param word String that represents the word
    * @param classification The classification of the vocabulary
    *                       "i" : island vocabulary
    *                       "l" : lingua franca vocabulary
    */
  public Word(String word, char classification) {
    letters = word;
    length = word.length();
    vocClass = classification;
  }

  /** Mutate the vowels in the word.
    * <p>
    * Mutates every vowel in a word with the chance of 0.2% (2/3 from the dir variable
    * times 3/100 of the if statement)
    */
  public void mutateVowel() {
    int max_random = 100;
    for(int i = 0; i < vowels.size(); i++) {
      int newIndex;
      String vowel = vowels.get(i);
      if(letters.contains(vowel)) {
        float prob = random(max_random);
        if(prob < 3) {
          // The vowels can shift either forwards or backwards in the array
          int dir = int(random(3)) - 1;
          if(i == vowels.size() - 1 && dir > 0) {
            newIndex = 0;
          }
          else if(i == 0 && dir < 0) {
            newIndex = vowels.size() - 1;
          }
          else{
            newIndex = i + dir;
          }
          String newVowel = vowels.get(newIndex);
          letters = letters.replaceFirst(vowel, newVowel);
          break;
        }
      }
    }
  }

  /** Mutate the consonants of the word.
    * <p>
    * Mutates consonants or consonant groups according to the shiftX-lists in
    * the this class with a probability of 0.03%
    */
  public void mutateConsonant() {
    int max_random = 100;
    float prob = random(max_random);
    for(int j = 0; j < 4; j++) {
      if(prob < 3) {
        int newIndex = (j+1) % 4;
        int switchVar = int(random(6));
        // Replace a random consonant in the string
        switch (switchVar) {
          case 1: if(letters.contains(shift1.get(j))) {
                    letters = letters.replace(shift1.get(j), shift1.get(newIndex));
                  }
                  break;
          case 2: if(letters.contains(shift2.get(j))) {
                    letters = letters.replace(shift2.get(j), shift2.get(newIndex));
                  }
                  break;
          case 3: if(letters.contains(shift3.get(j))) {
                    letters = letters.replace(shift3.get(j), shift3.get(newIndex));
                  }
                  break;
          case 4: if(letters.contains(shift4.get(j))) {
                    letters = letters.replace(shift4.get(j), shift4.get(newIndex));
                  }
                  break;
          case 5: if(letters.contains(shift5.get(j))) {
                    letters = letters.replace(shift5.get(j), shift5.get(newIndex));
                  }
                  break;
        }
      }
    }
  }

  /** Double the consonant in a word.
    * <p>
    * Give certain consonants the possibility to mutate to
    * a double consonant
    */
  void doubleConsonant() {
    char[] letterArray = letters.toCharArray();
    char[] newCharArr = new char[length+1];
    char doubleLetter = ' ';
    int max_random = 1000000;
    int index = 0;
    float prob = max_random;
    for(int i = 1; i < length; i++) {
      prob = random(max_random);
      // D's can only be doubled if the preceding letter is
      // an 'e' or an 'ä'
      if(letterArray[i] == 'd' && (letterArray[i-1] == 'e' || letterArray[i-1] == 'ä')) {
        doubleLetter = 'd';
        index = i;
        if(prob < 0.5 * max_random) {
          break;
        }
      }
      // T's can only be doubled if the preceding letter is
      // an 'i' or a 'j'
      if(letterArray[i] == 't' && (letterArray[i-1] == 'i' || letterArray[i-1] == 'j')) {
        doubleLetter = 't';
        index = i;
        if(prob < 0.5 * max_random) {
          break;
        }
      }
      // L's can only be doubled if the preceding letter is
      // an 'o' or a 'u'
      if(letterArray[i] == 'l' && (letterArray[i-1] == 'o' || letterArray[i-1] == 'u')) {
        doubleLetter = 'l';
        index = i;
        if(prob < 0.5 * max_random) {
          break;
        }
      }
    }
    // Chance to double: 3 ppm
    if (prob < 3) {
      for(int j = 0; j < index; j++) {
        newCharArr[j] = letterArray[j];
      }
      newCharArr[index+1] = doubleLetter;
      for(int k = length+1; k > index+1; k--) {
        newCharArr[k] = letterArray[k];
      }
      letters = new String(newCharArr);
    }
  }

}
