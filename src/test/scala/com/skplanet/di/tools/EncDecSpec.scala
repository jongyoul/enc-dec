package com.skplanet.di.tools

import org.specs2.mutable._

/**
 * Created by jl on 6/30/14.
 */
class EncDecSpec extends Specification {
  val testModulus = "8553273969692152783212074286111370239308063796090064678429853650854811082267156341573082450947167635565457192773318819072351007143709478079578013942438287"
  val testPublicKeyExponent = "65537"
  val testPrivateKeyExponent = "2549263781527943304003562064644603733530744619513057255653605312451699419715896649542566374580157297702000025095306408501211158657032825008691098828249337"
  val encDec = new EncDec(testModulus, testPublicKeyExponent, testPrivateKeyExponent)

  "Funtion test" should {
    val testRawText = "Oh~ God!!!"
    val testEncryptedText = "mmSntaG2Qb5fCcrx8kD4axqCJI9svnmEwix5xvRPkWw2po6QbrSTmMq9UQ/LzXga4nlP1BhcX2NPEZy5FfBbvQ=="
    s"enc test '${testRawText}'" in {
      encDec.enc(testRawText) must beEqualTo(testEncryptedText)
    }
    s"dec test '${testEncryptedText}'" in {
      testRawText must beEqualTo(encDec.dec(testEncryptedText))
    }
  }

  "Enc/Dec Fileds test" should {
    val testRawText = "Field1\tField2\tOh~ God!!!\tField4"
    val testEncryptedText = "Field1\tField2\tmmSntaG2Qb5fCcrx8kD4axqCJI9svnmEwix5xvRPkWw2po6QbrSTmMq9UQ/LzXga4nlP1BhcX2NPEZy5FfBbvQ==\tField4"
    s"Fields Count '${testRawText}' is 4" in {
      testRawText.split("\t").toList.size must_== (4)
    }

    s"Fileds Enc '${testRawText}' to '${testEncryptedText}'" in {
      encDec.encLine(Array(2).toList, testRawText) must beEqualTo(testEncryptedText)
    }
  }
}
