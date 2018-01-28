import java.util.*;
import java.lang.*;
import java.io.*;
import java.nio.file.*;

public class RankFusion{

  static String[] checked=new String[1000]; //the document names for the same topic
  static int n=0; //elements stored in checked

  static Run[][] wholeRuns=new Run[10][50]; //data structure with all the input runs
  static int lowesttopic=351; //offset from index to topic

  public static void main(String[] args){
    //the runs filenames
    String runfiles[]={"TF_IDF_0.res","TF_IDF_2.res","TF_IDF_5.res","TF_IDF_8.res",
            "BM25b0.75_1.res","BM25b0.75_4.res","BM25b0.75_6.res","BM25b0.75_9.res",
            "BB2c1.0_3.res","BB2c1.0_7.res"};

    for(int i=0;i<10;i++){
      try{
        //reading one input run
        File file=new File("runs/"+runfiles[i]);
        Scanner input=new Scanner(file);
        System.out.println("Parsing input file: "+file);

        while(input.hasNextLine()){
          String record=input.nextLine(); //read a new record
          Scanner line=new Scanner(record);
          int topic=Integer.parseInt(line.next()); //read the record topic
          line.next(); //discard the q0 value
          String id=line.next(); //read the document id
          int r=Integer.parseInt(line.next()); //read the rank
          double s=Double.parseDouble(line.next()); //read the score
          //if it is a new topic in the current run, initialize the vector
          if(wholeRuns[i][topic-lowesttopic]==null){
            wholeRuns[i][topic-lowesttopic]=new Run(topic);
            wholeRuns[i][topic-lowesttopic].setOrder("name"); //insertion in alphabetical order
          }
          wholeRuns[i][topic-lowesttopic].insertDocument(id,s,r);
          line.close();
        }
        input.close();
      }
      catch(IOException e){
        System.out.println(e);
        System.exit(0);
      }
    }
    for(int j=0;j<10;j++){
       for(int y=0;y<50;y++){
         wholeRuns[j][y].unit(); //normalize each topic run
       }
    }

    System.out.println("Performing basic Strategies");

    //the names of the merged runs
    String[] runNames={"combMax","combMin","combMed","combSUM","combANZ","combMNZ"};

    //preparing a matrix to store the merged runs obtained with basic strategies
    Run[][] basic=new Run[6][50];
    for(int i=0;i<6;i++){
      for(int j=0;j<50;j++){
        basic[i][j]=new Run(j+lowesttopic);
        basic[i][j].setID(runNames[i]);
      }
    }

    for(int w=0;w<50;w++){//for every topic
      for(int j=0;j<10;j++){//for every different file
        //for every document of topic w in file j
        for(int i=0;i<wholeRuns[j][w].getNum();i++){
          double[] scores=new double[10]; //collects the 10 differnt scores
          int s=0; //the number of elements in scores
          Doc d1=wholeRuns[j][w].getElement(i);
          String s1=d1.getID();
          if(insertString(s1)>=0){ //if d1 has not already been read in a previous file
            scores[s]=d1.getScore();
            s++;
            tidyScore(scores,s); //add the score for d1 in scores
            for(int l=j+1;l<10;l++){ //for all the other files
              Doc d2=wholeRuns[l][w].searchDoc(s1); //search d1 in the other runs
              if(d2!=null){ //if d1 is in the file l
                scores[s]=d2.getScore();
                s++;
                tidyScore(scores,s); //add the score for d2 and sort the scores
              }
            }
            double min=scores[0]; //find minimum
            double max=scores[s-1]; //find maximum
            double med=scores[s/2]; //find median
            if(s/2==((s/2)*2+1)) med=(med+scores[s/2+1])/2; //if the array is even adjust med
            double sum=0; //sum of the scores
            double nz=0; //non-zero element
            for(int d=0;d<s;d++){
              sum+=scores[d];
              if(scores[d]!=0) nz++;
            }
            double anz=sum/nz; //find combANZ value
            double mnz=sum*nz; //find combMNZ value
            //updating combMax run for topic w
            basic[0][w].insertDocument(s1,max,0);
            //updating combMin run for topic w
            basic[1][w].insertDocument(s1,min,0);
            //updating combMed run for topic w
            basic[2][w].insertDocument(s1,med,0);
            //updating combSUM run for topic w
            basic[3][w].insertDocument(s1,sum,0);
            //updating combANZ run for topic w
            basic[4][w].insertDocument(s1,anz,0);
            //updating combMNZ run for topic w
            basic[5][w].insertDocument(s1,mnz,0);
          }
        }
      }
      checked=new String[1000]; //clear checked for the next topic
      n=0;
    }

    //write the run files for basic strategies
    for(int j=0;j<6;j++){
      String fileName="results/"+runNames[j]+".res";
      FileWriter fileWriter=null;
      try{
        fileWriter = new FileWriter(fileName);
      }
      catch(IOException e){
        System.out.println("Folder results not found (?) \n"+e);
        System.exit(1);
      }
      PrintWriter printWriter = new PrintWriter(fileWriter);
      for(int y=0;y<50;y++){
        basic[j][y].defineRank();
        basic[j][y].reverse();
        printWriter.print(basic[j][y]);
      }
      System.out.println("Output of "+runNames[j]+" in file "+fileName);
      printWriter.close();
    }

    System.out.println("Performing Condorcet-fuse");
    checked=new String[1000]; //clear the documents vector for Condorcet-fuse
    n=0;

    Run[] condorcet=new Run[50]; //contains the condorcet run

    for(int w=0;w<50;w++){//for the topic w
      condorcet[w]=new Run(w+lowesttopic);
      condorcet[w].setID("condorcet-fuse");
      for(int j=0;j<10;j++){//for the file j
        for(int i=0;i<wholeRuns[j][w].getNum();i++){//fill checked with all the documents for topic w
          Doc d1=wholeRuns[j][w].getElement(i);
          String s1=d1.getID();
          insertString(s1);
        }
      }
      checked=Arrays.copyOfRange(checked,0,n); //reduce checked to the number of elements
      quickSort(checked,w);
      for(int i=0;i<checked.length;i++){
        condorcet[w].insertDocument(checked[i],i*1.0/(checked.length-1),0);
      }
      checked=new String[1000];
      n=0;
    }

    //Writing the run file for condorcet strategy
    String fileName="results/condorcet-fuse.res";
    FileWriter fileWriter=null;
    try{
      fileWriter = new FileWriter(fileName);
    }
    catch(IOException e){
      System.exit(1);
    }
    PrintWriter printWriter = new PrintWriter(fileWriter);
    for(int y=0;y<50;y++){
      condorcet[y].defineRank();
      condorcet[y].reverse();
      printWriter.print(condorcet[y]);
    }
    System.out.println("Output of Condorcet-fuse in file "+fileName);
    printWriter.close();

  }

  /*
  * @return the number of elements in s
  *
  * @param s the array to be sorted
  * @param id the topic id 
  */
  private static int quickSort(String[] s,int id){
    if(s.length<=1) return s.length;
    Random random=new Random();
    int p=random.nextInt(s.length); //the random pivot index
    String[] s1=new String[s.length]; //the array with losers
    int n1=0; //number of strings in s1
    String[] s2=new String[s.length]; //the array with winners
    int n2=0; //number of strings in s2
    String sp=s[p]; //the element to be managed as a pivot
    for(int i=0;i<s.length;i++){ //fill the two arrays
      int leader=winner(s[i],sp,id);
      if(i!=p){
        if(leader<0){
          s1[n1]=s[i];
          n1++;
        }
        else{
          s2[n2]=s[i];
          n2++;
        }
      }
    }
    s1=Arrays.copyOfRange(s1,0,n1); //trim s1 to size
    s2=Arrays.copyOfRange(s2,0,n2); //trim s2 to size
    quickSort(s1,id); //sort s1
    quickSort(s2,id); //sort s2
    s[n1]=sp;
    System.arraycopy(s1,0,s,0,n1);
    System.arraycopy(s2,0,s,n1+1,n2); //s contains the condorcet path
    return n1+n2+1;
  }

  /*
  * @return count<0 if d1 loses to d2
  *         count=0 if d1 and d2 tie
  *         count>0 if d1 wins to d2
  *
  * @param d1 first entry id
  * @param d2 second entry id
  * @param topicIndex topic id
  */
  private static int winner(String d1, String d2, int topicIndex){
    int count=0;
    for(int j=0;j<10;j++){
      double winner=wholeRuns[j][topicIndex].compareDsInRun(d1,d2);
      if(winner<0) count--;
      if(winner>0) count++;
    }
    return count;
  }

  /* @param array the array to tidy
  * @param s the number of elements in array
  */
  private static void tidyScore(double[] array, int s){
    int g=s-1;
    while(g>0 && array[g-1]>array[g]){
      double temp=array[g-1];
      array[g-1]=array[g];
      array[g]=temp;
      g--;
    }
  }

  /* @return the final position of the string
  *
  * @param s the string to put in the array
  */
  private static int insertString(String s){
    if(n>=checked.length){
      String[] q=new String[2*checked.length];
      for(int i=0;i<checked.length;i++){
        q[i]=checked[i];
      }
      checked=q;
    }
    if(binSearch(0,n-1,s)>=0) return -1;
    int i=n;
    checked[i]=s;
    while(i>0 && s.compareTo(checked[i-1])<0){
      String temp=checked[i-1];
      checked[i-1]=checked[i];
      checked[i]=temp;
      i--;
    }
    n++;
    return i;
  }

  /* @return the position of s or a negative number
  *
  * @param from the initial index where to search
  * @param to the last index where to search
  */
  private static int binSearch(int from,int to, String s){
    if(from>to) return -1;
    if(from==to){
      if(s.equals(checked[from])) return from;
      return -1;
    }
    int mid=(from+to)/2;
    if(s.equals(checked[mid])) return mid;
    if(s.compareTo(checked[mid])<0) return binSearch(from,mid,s);
    if(s.compareTo(checked[mid])>0) return binSearch(mid+1,to,s);
    return -1;
  }
}
