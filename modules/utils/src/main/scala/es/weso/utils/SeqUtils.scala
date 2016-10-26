package es.weso.utils
import cats._, data._
import implicits._

object SeqUtils {

  /**
    * transpose(List(("A",List(1,2)), ("B",List(2,3)),("C",List(4)))) =
    *    List(List(("A",1),("B",2),("C",4)),
    *         List(("A",1),("B",3),("C",4)),
    *         List(("A",2),("B",2),("C",4)),
    *         List(("A",2),("B",3),("C",4)))
    * TODO: Generalize this function using a Monoid to return a Stream...
    * @param ls
    * @tparam A
    * @tparam B
    * @return
    */
  def transpose[A,B](ls: List[(A,Set[B])]): List[List[(A,B)]] = {
    val as: List[A] = ls.map(_._1)
    val sequences: List[List[B]] = ls.map(_._2.toList).sequence
    for {
      s <- sequences
    } yield as.zip(s)
  }

  def filterOptions[A,B](ls: List[(A,Option[B])]): List[(A,B)] = {
    def cnv(p: (A,Option[B])): Option[(A,B)] = p._2 match {
      case None => None
      case Some(v) => Some((p._1, v))
    }
    ls.map(cnv).flatten
  }

}