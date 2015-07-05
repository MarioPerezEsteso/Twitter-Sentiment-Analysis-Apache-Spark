import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object TwitterStreaming {

  def main(args: Array[String]): Unit = {

    val pathPositiveWords = "src/main/resources/positive-words.txt"
    val pathNegativeWords = "src/main/resources/negative-words.txt"

    val conf = new SparkConf().
      setAppName("Twitter sentiment analysis Apache Spark").
      setMaster("local[*]")

    val sc = new SparkContext(conf)

    val ssc = new StreamingContext(sc, Seconds(5))

    val filters = new Array[String](1)
//    filters(0) = "word_to_filter"

    val twitterStream = TwitterUtils.createStream(ssc, None, filters)

    val positive_words =  sc.textFile(pathPositiveWords) //Random
      .filter(line => !line.isEmpty())
      .collect()
      .toSet

    val negative_words = sc.textFile(pathNegativeWords) //Random
      .filter(line => !line.isEmpty())
      .collect()
      .toSet

    /**
     * Gets the text of a tweet and returns a list of the words
     */
    def clean(tweetText: String): List[String] =
      tweetText.split(" ").
        map(_.toLowerCase).
        filter(_.matches("[a-z]+")).toList

    /**
     * Rate words.
     * Positive: score + 1
     * Negative: score - 1
     * None (neutral): score + 0
     */
    def rate(word: String): Int =
      if (positive_words.contains(word))       1
      else if (negative_words.contains(word)) -1
      else                                     0

    /**
     * Get score of a tweet.
     * Score > 0: positive tweet
     * Score < 0: negative tweet
     * Score = 0: neutral tweet
     */
    def rateWordList(words: List[String]): Int =
      words.foldRight(0) {
        (word, score) =>
          score + rate(word)
      }

    twitterStream.
      map { tweet => (tweet.getId(), tweet.getText()) }.
      map { case (id, text) => (id, clean(text)) }.
      map { case (id,  words) => (id, rateWordList(words)) }.
      print()

    ssc.start()
    ssc.awaitTermination()

  }

}
