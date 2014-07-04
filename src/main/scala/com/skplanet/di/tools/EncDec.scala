package com.skplanet.di.tools

import java.io._
import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}

import com.di.security.CryptoUtils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{Logging, SparkConf, SparkContext}

/**
 * Created by jl on 6/30/14.
 */
object EncDec extends Logging {
  val publicKeyModulus = "7577257274191551097624277939619648834415258346195701624702241427507596352378240942764032510385901727639861216319309817735955405453593241345706416751624437"
  val publicKeyExponent = "65537"

  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println(s"Option required: <input dir> <output dir> <# of fields w/ ,>")
      System.exit(-1)
    }

    val conf = new SparkConf().setAppName("Encryption App")
    val sc: SparkContext = new SparkContext(conf)
    val encDec = new EncDec(publicKeyModulus, publicKeyExponent, null)
    val fileSystem = FileSystem.get(sc.hadoopConfiguration)

    val inputPath: Path = new Path(args(0))
    val outputPAth: Path = new Path(args(1))

    if (!fileSystem.exists(inputPath))
      throw new FileNotFoundException(s"Input path doesn't exists: ${inputPath.toString}")

    if (fileSystem.exists(outputPAth))
      throw new IOException(s"Output path already exists: ${outputPAth.toString}")

//    val bufferedReader = new BufferedReader(new InputStreamReader(fileSystem.open(inputPath)))
//    val maxIdx = args(2).split(",").toList.map(Integer.parseInt(_)).reduceLeft((x, y) => if (x >= y) x else y)
//    if (bufferedReader.readLine().split("\t").length > maxIdx)
//      throw new ArrayIndexOutOfBoundsException(s"Maximum of # of fields exceeds length of input data: ${maxIdx}")

    sc.textFile(args(0)).map(encDec.encLine(args(2).split(",").toList.map(Integer.parseInt(_)), _)).saveAsTextFile(args(1))
  }
}

class EncDec(modulus: String, publicKeyExponent: String, privateKeyExponent: String) extends java.io.Serializable {
  val rsaPublicKey: RSAPublicKey = if (null != modulus && null != publicKeyExponent) CryptoUtils.getPublicKey(modulus, publicKeyExponent) else throw new Exception("publicKey is not set")
  val rsaPrivateKey: Option[RSAPrivateKey] = if (null != modulus && null != privateKeyExponent) Some(CryptoUtils.getPrivateKey(modulus, privateKeyExponent)) else None

  val enc = (rawValue: String) => CryptoUtils.encrypt(rawValue, rsaPublicKey)

  val dec = (encryptedValue: String) => rsaPrivateKey match {
    case Some(key) => CryptoUtils.decrypt(encryptedValue, key)
    case None => throw new Exception("Private Key is not set")
  }

  val encLine = (fieldsNum: List[Int], rawLine: String) => {
    def encRec(curFieldNum: Int, rawLineRest: List[String]): List[String] = rawLineRest match {
      case x :: xs => (if (fieldsNum.contains(curFieldNum)) enc(rawLineRest.head) else rawLineRest.head) :: encRec(curFieldNum + 1, rawLineRest.tail)
      case List() => Nil
    }
    encRec(0, rawLine.split("\t").toList).mkString("\t")
  }

  val decLine = (fieldsNum: List[Int], encryptedLine: String) => ""
}
