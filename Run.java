public class Run{
  private int topicID;//the topic ID
  private Doc[] docs; //the run
  private int order; //the order;
  private int n; //the number of documents
  private String run; //the run ID

  double max=0; //the max score
  double min=-1; //the min score

  //build a single topic run
  public Run(int topic){
    topicID=topic;
    docs=new Doc[1000];
    order=0; //default score;
    run=null;
    n=0;
  }

  //returns the topic ID
  public int getID(){
    return topicID;
  }

  //returns the number of elements
  public int getNum(){
    return n;
  }

  //set the RunID
  public int setID(String s){
    if(run!=null) return -1;
    run=s;
    return 0;
  }

  //returns the element at current position i
  public Doc getElement(int i){
    if((i>=0)&&(i<n)){
      return new Doc(docs[i].getID(),docs[i].getScore(),docs[i].getRank());
    }
    return null;
  }

  /* returns the current order
  *          0 if order is by score/rank
  *          1 if order is by document id
  */
  public int getOrder(){
    return order;
  }

  /* @return  0,1 the order int
  *         -1 if the order did not change
  *
  * @param s String containing the new ordering.
  *        can be either "score" or "name"
  */
  public int setOrder(String s){
    if(n>0) return -1;
    if(s.equals("score")){
      order=0;
      return 0;
    }
    if(s.equals("name")){
      order=1;
      return 1;
    }
    return -1;
  }

  //Inserts a new document, according to the current order
  public int insertDocument(Doc d){
    return insertDocument(d.getID(),d.getScore(),d.getRank());
  }

  //Inserts a new document, according to the current order
  public int insertDocument(String id,double score,int rank){
    if(n>=docs.length) docs=resize(); //if the document is full calls resize
    docs[n]=new Doc(id,score,rank);
    if(score>max) max=score; //keep track of the max score in the ran
    if(min==-1) min=score; //keep track of the min score in the ran
    if(score<min) min=score;
    int i=n;
    if(order==0){ //order by score
      while(i>0 && docs[i].getScore()<docs[i-1].getScore()){
        Doc temp=docs[i-1];
        docs[i-1]=docs[i];
        docs[i]=temp;
        i--;
      }
    }
    if(order==1){ //alphabetical
      while(i>0 && docs[i].getID().compareTo(docs[i-1].getID())<0){
        Doc temp=docs[i-1];
        docs[i-1]=docs[i];
        docs[i]=temp;
        i--;
      }
    }
    n++;
    return i;
  }

  //resize the documents array
  private Doc[] resize(){
    Doc[] q=new Doc[2*docs.length];
    for(int i=0;i<n;i++){
      q[i]=docs[i];
    }
    return q;
  }

  //assign a rank if all the rank are 0s, otherwise error
  public int defineRank(){
    for(int i=0;i<n;i++){
      if(docs[i].getRank()!=0) return -1;
      docs[i].setRank(n-1-i);
    }
    return 0;
  }

  //reverse the elements of the array
  public int reverse(){
    for(int i=0;i<n/2;i++){
      Doc temp=docs[i];
      docs[i]=docs[n-1-i];
      docs[n-1-i]=temp;
    }
    return 0;
  }

  //standardize the run according to max and min
  public int unit(){
    double r=0;
    for(int i=0;i<n;i++){
      r=docs[i].normalize(max,min);
      if (r>1) return 1;
    }
    return 0;
  }

  //search a document by its id
  public Doc searchDoc(String d){
    return binSearch(0,n-1,d);
  }

  //works if the documents array is sorted by alphabetical order
  private Doc binSearch(int from, int to, String d){
    if(from>to) return null;
    if(from==to){
      if(docs[from].getID().equals(d)) return docs[from];
      return null;
    }
    int mid=(from+to)/2;
    String id=docs[mid].getID();
    if(d.equals(id)){
      return docs[mid];
    }
    if(d.compareTo(id)<0){
      return binSearch(from,mid,d);
    }
    if(d.compareTo(id)>0){
      return binSearch(mid+1,to,d);
    }
    return null;
  }

  /* return n<0 if r1<r2
  *         n=0 if r1=r2
  *         n>0 if r1>r2
  */
  public double compareDsInRun(String s1, String s2){
    double r1=0;
    double r2=0;
    try{
      r1=searchDoc(s1).getScore();
    }
    catch(NullPointerException e){
      r1=-1;
    }
    try{
      r2=searchDoc(s2).getScore();
    }
    catch(NullPointerException e){
      r2=-1;
    }
    return r1-r2;
  }

  //trec standard format line
  public String toString(){
    String s="";
    for(int i=0;i<n;i++){
      s+=topicID+"\tQ0\t"+docs[i]+"\t"+run+"\n";
    }
    return s;
  }
}
