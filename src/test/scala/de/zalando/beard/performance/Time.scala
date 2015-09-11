package de.zalando.beard.performance

/**
 * @author dpersa
 */
object Time {

  val REP = 1000

  def time[R](message: String)(block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println(s"Elapsed time $message: " + (t1 - t0) / 1000000 + "ms")
    result
  }
}
