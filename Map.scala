import scala.io.Source

object Map {

  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      Console.err.println("Please enter filename")
      System.exit(0)
    }

    val levels = for {
      stringArray <- Source.fromFile(args(0)).getLines.toArray
      intArray = stringArray.split("\\s+").map(_.toInt)
    } yield intArray

    val start = System.nanoTime
    levels.zipWithIndex.map {
      case (level, levelIndex) => if (levelIndex > 0 && levelIndex < levels.length)
        level.zipWithIndex.map {
          case (number, i) =>
            if (i == 0)
              levels(levelIndex)(i) = number + levels(levelIndex - 1)(i)
            else if (i < levels(levelIndex - 1).length)
              levels(levelIndex)(i) = number + (if (levels(levelIndex - 1)(i) >= levels(levelIndex - 1)(i - 1)) levels(levelIndex - 1)(i) else levels(levelIndex - 1)(i - 1))
            else if (i == levels(levelIndex - 1).length)
              levels(levelIndex)(i) = number + levels(levelIndex - 1)(i - 1)
        }
    }
    println("Kumulatiivisen summan laskemisessa mapilla kesti " + (System.nanoTime - start) + " ns")
    println("Tykkaysten kokonaissumma on " + levels.last.max)
  }

}
