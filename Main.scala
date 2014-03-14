/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scalaapplication1

import scala.io.Source

object Main {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      Console.err.println("Please enter filename")
      System.exit(0)
    }

    val levels = for {
      stringArray <- Source.fromFile(args(0)).getLines.toArray
      intArray = stringArray.split("\\s+").map(_.toInt)
    } yield intArray

    // Häntärekursion avulla tehty laskenta. Häntärekursiosta tehdään käännösaikana looppi, jolloin vältetään stackoverflow.
    def sum(previous: Int, level: Int, pointer: Int) {
      if (level + 1 >= levels.length)
        return

      if (previous < 0) {
        levels(level + 1)(pointer) = levels(level)(pointer) + levels(level + 1)(pointer)
      } else if (previous > 0 && previous + levels(level)(pointer) > levels(level + 1)(pointer)) {
        levels(level + 1)(pointer) = previous + levels(level)(pointer)
      }
      val original = levels(level + 1)(pointer + 1)
      levels(level + 1)(pointer + 1) += levels(level)(pointer)

      if (pointer + 1 == levels(level).length) {
        sum(-1, level + 1, 0)
      } else {
        sum(original, level, pointer + 1)
      }
    }

    val start = System.nanoTime
    sum(-1, 0, 0)
    println("Kumulatiivisen summan laskemisessa kesti " + (System.nanoTime - start) + " ns")
    println("Tykkaysten kokonaissumma on " + levels.last.max)
  }
}
