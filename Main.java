import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;


public class Main {

    public static void main(String args[]) throws IOException {

        //code block to read input file and create a output directory for further use

        if(args.length!=2){
            System.out.println("invalid arguments given, re-run the program");
            System.exit(-1);
            }

            System.out.println("\nReading the input file\n");

            File output = new File(args[1]);
            if(!output.exists())
            {
                output.mkdir();
                System.out.println("\nCreated an output directory\n");
            }
            else
            {
                output.delete();
                System.out.println("Existing output directory deleted, re-run the code");
            }


            File input = new File(args[0]);

            //initializing a Array List to store words
            List<String> words = new ArrayList<String>();

            //reference to input stream reader
            BufferedReader reader= null;

            //Error handling reading input through try,catch

            try {
                reader = new BufferedReader(new FileReader("input/amazon.txt"));
                String line = null;
                while ((line = reader.readLine()) != null)
                {

                    // Regex for removing all punctuations
                    line = line.replaceAll("\\p{Punct}", " ").toLowerCase();

                    // Regex for removing all numbers/digits
                    line = line.replaceAll("[^a-zA-Z ]", " ");

                    // Storing all the tokens of a single line of input temporarily(line by line iterations)

                    String[] midwords = line.split("\\s+");
                    for (int i = 0; i < midwords.length; i++) {
                        words.add(midwords[i]);
                        }
                }

                }

                catch(Exception e)
                {
                    // Printing Excess information about error
                    e.printStackTrace();
                    System.out.println("\namazon.txt file not found in the input folder\n");
                    System.out.println("\nInput File should be named as amazon.txt and moved to input folder before running");
                }

                finally

            {
                // Always close the reader after use
                reader.close();
            }

            System.out.println("\nTokens have been added to array list\n");


                // Removing all the entries in Array list which have either null value or no value

                while (words.remove("")) {
                }
                while (words.remove("null")) {
                }
                System.out.println("\nThere are a total of "+words.size()+" tokens in Array list");


                // Declaring hastables for storing unigrams and bigrams
                Hashtable<String, Integer> unigrams = new Hashtable<String, Integer>();
                Hashtable<String, Integer> bigrams = new Hashtable<String, Integer>();

                //Loop through the Array list for unigrams and bigrams

                for (int i = 0; i < words.size(); i++) {

                    String first = words.get(i);
                    Integer j;
                    Integer k;
                    j = unigrams.get(first);
                    unigrams.put(first, (j == null) ? 1 : j + 1);

                    // N unigrams and (N-1) bigrams, condition to avoid Index out of Bound error

                    try {
                        if (i < (words.size()-1)) {
                            String second = words.get(i + 1);
                            String bigram = first + " " + second;
                            k = bigrams.get(bigram);
                            bigrams.put(bigram, (k == null) ? 1 : k + 1);
                        }
                    } catch (IndexOutOfBoundsException Ae) {
                        Ae.printStackTrace();
                        System.out.println("for n unigrams, there will be on one less i.e., n-1 bigrams, loops for bigrams only until n-1");

                    }
                }

                System.out.println("\nUnigrams and bigrams stored in the respective hash tables");
                System.out.println("\nThere are "+unigrams.size()+" unique unigrams in the corpus\n");
                System.out.println("\nThere are "+bigrams.size()+" unique bigrams in the corpus\n");



                // Eliminating bigrams with frequency one

                Iterator<String> eliminatebigrams  = bigrams.keySet().iterator();
                while(eliminatebigrams.hasNext()) {
                    String key = eliminatebigrams.next();
                    if (bigrams.get(key) ==1 ) {
                        eliminatebigrams.remove();
                    }
                }

               // lambda expression to sort a hashtable in decreasing order of its value

                Map<String, Integer> bigramsort = bigrams
                        .entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(
                                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                        LinkedHashMap::new));

                // Writing the bigrams into bigrams.txt and storing it in output folder
                int ui = 1;
                BufferedWriter bigramwriter = new BufferedWriter(new FileWriter("./output/top100SortedbyFreq.txt"));
                Iterator<String> top100Bigram = bigramsort.keySet().iterator();
                System.out.println("\n*********** Top100 frequently occurring bigrams are ************\n");
                try {
                    while (top100Bigram.hasNext() && (ui<=100)) {
                        String key = top100Bigram.next();
                        System.out.printf("%d)  %15s  --->%6d\n",ui,key,bigramsort.get(key));
                        bigramwriter.write(key + " ---->" + bigramsort.get(key) + "\n\n");


                        ui++;
                    }
                }

                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                    finally
                    {
                        // Not closing/flushing the buffer won't write contents on a file.
                        bigramwriter.close();
                        System.out.println("\n\n");
                    }


                // Code to calculate mutual information of all the bigrams
                 System.out.println("\nInitiating code for mutual information method");
                Iterator<String> mutualiterator = bigrams.keySet().iterator();
                Hashtable<String, Double> mutualinfo = new Hashtable<String, Double>();
                int j = 0;
                while (mutualiterator.hasNext()) {
                    double N = bigrams.size();
                    String key = mutualiterator.next();
                    double p13 = (bigrams.get(key));
                    String tokens[] = key.split(" ");
                    if (tokens.length != 2) {
                        System.out.println("yes");
                        j++;
                    }
                    double C1 = (unigrams.get(tokens[0]));
                    double C2 = (unigrams.get(tokens[1]));
                    N = N - (p13 + C1 + C2);
                    double fin = Math.log((p13 / N) / ((C1 / N) * (C2 / N)));

                    // Adding bigram and its mutual information value as an entry to hashtable
                    mutualinfo.put(key, fin);


                }

                // Sorting bigrams based on their mutual information score
                Map<String, Double> sortedmutual = mutualinfo
                        .entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(
                                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));

                // Writing them into a text file and saving it in output folder

                BufferedWriter mutualwriter = new BufferedWriter(new FileWriter("./output/top100MutualScore.txt"));
                int u = 1;
                Iterator<String> iterators1 = sortedmutual.keySet().iterator();
            System.out.println("\n*********** Top100 bigrams sorted by mutual info score are ************\n");
            while (iterators1.hasNext() && u <= 100) {
                    String key = iterators1.next();
                    if(sortedmutual.get(key)>0) {
                        System.out.printf("%d)  %26s  --->%10f\n",u, key, sortedmutual.get(key));
                        u++;
                    }
                    try {
                        mutualwriter.write(key + " ---->" + sortedmutual.get(key) + "\n\n");
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                }
                mutualwriter.close();

            System.out.println("\n\nPROGRAM RUNTIME\n\n");

    }

}

