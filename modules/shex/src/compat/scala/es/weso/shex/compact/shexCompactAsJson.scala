package es.weso.shex.compact
import org.scalatest.{EitherValues, FunSpec, Matchers, _}
import com.typesafe.config.{Config, ConfigFactory, _}
import java.io.File

import scala.io._
import es.weso.shex.implicits.showShEx._
import es.weso.shex.compact.Parser._
import cats.implicits._
import es.weso.json.{JsonTest, _}
import es.weso.utils.FileUtils._
import es.weso.shex._

import scala.util.{Failure, Success}

class shexCompactAsJson extends FunSpec with JsonTest with Matchers with EitherValues {

  val conf: Config = ConfigFactory.load()
  val schemasFolder = conf.getString("schemasFolder")

  val ignoreFiles = List("coverage")

  def getCompactFiles(schemasDir: String): List[File] = {
    getFilesFromFolderWithExt(schemasDir, "shex", ignoreFiles)
  }

  describe("Parsing Schemas from ShEx") {
    for(file <- getCompactFiles(schemasFolder)) {
      it(s"Should read Schema from file ${file.getName}") {
        val str = Source.fromFile(file)("UTF-8").mkString
        Schema.fromString(str,"SHEXC",None) match {
          case Success(schema) => {
            info(s"Parsed ${file.getName}")
            val (name,ext) = splitExtension(file.getName)
            // TODO: Check that parsed file equals schema file
          }
          case Failure(err) => fail(s"Parsing error: $err")
        }
      }
    }
  }
}
