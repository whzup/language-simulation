Map langMap;
Population population;
Vocabulary voc;
Word trying;

void setup() {
  size(1000, 1000);
  langMap = new Map();
  population = new Population();
  voc = new Vocabulary('l');
  for(int i = 0; i < voc.count; i++) {
    Word w = voc.vocabulary.get(i);
    print(w.letters,"\n");
  }
  randomSeed(1);
}

void draw() {
  frameRate(2.5);
  background(250);
  langMap.display();
  population.update();
}

/* TODO - Make the agent search more efficient. Maybe just check the neighbor
          cells for every agent instead of checking the whole map for every agent
          
        - Make the doubleConsonant method more efficient. It always checks every
          letter even if it doesn't act.
           
        - Make the map displaying more efficient.

        - solve all the word mutations with StringLists instead of
           character arrays

        - doubleConsonant is very complex. Maybe think of an easier way
           to implement it

        - you cannot jump from j to o -> from a to ä or to o

        - change the vowels of diphtongs

        - method which checks if the word has changed so it can be
           inserted in the etym array (hard) -> maybe create class-wide
            variables with random words so they can be checked inside the
             class

        - create a method to mutate the begin or the end of a word if
           it is often used with another word with a vowel on the beginning
            or the end
             -> maybe use another class to create sentences

        - maybe give certain letters a higher probability to be picked
           in the different languages

        - maybe include mountain regions which have a slower walking rate?
*/

/*
TODO Every island should have its own vocabulary, but there should also be a
lingua franca. The words of the lingua franca should have a chance to be included
into the vocabulary of an island but the other way should also be possible.
-> Maybe develop a model?
-> If an agent talks with an agent from his own island they should use the
   same vocabulary
*/

/*
TODO talking:
(1) get the ID of the nearest agent                                 done
(2) get the agent by ID                                             done
(3) exchange random (Zipf) words from his vocabulary                Zipf misses
(4) give the words the possibility to mutate (vowels, consonants)   done
(5) if the word mutates -> put it in the etym of itself
*/