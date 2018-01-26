public class Doc{
  private String docID; //the document id
  private double score; //the relevance score
  private int rank; //the document rank

  public Doc(String id, double s, int r){
    docID=id;
    score=s;
    rank=r;
  }

  public String getID(){
    return docID;
  }

  public double getScore(){
    return score;
  }

  public int getRank(){
    return rank;
  }

  public void setRank(int r){
    rank=r;
  }

  //standardize according to max and min
  public double normalize(double max, double min){
    score=(score-min)/(max-min);
    return score;
  }

  //trec standard format line
  public String toString(){
    return docID+"\t"+rank+"\t"+score;
  }

}
