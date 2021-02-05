/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.answers

import base.SpecBase
import models.core.pages.{Description, InternationalAddress, UKAddress}
import models.registration.pages.{HowManyBeneficiaries, PassportOrIdCardDetails}
import play.api.i18n.{Lang, MessagesImpl}
import play.twirl.api.Html

import java.time.LocalDate

class CheckAnswersFormattersSpec extends SpecBase {

  private val checkAnswersFormatters: CheckAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]

  "CheckAnswersFormatters" when {

    ".formatDate" when {

      def messages(langCode: String): MessagesImpl = {
        val lang: Lang = Lang(langCode)
        MessagesImpl(lang, messagesApi)
      }

      val recentDate: LocalDate = LocalDate.parse("2015-01-25")
      val oldDate: LocalDate = LocalDate.parse("1840-12-01")

      "in English mode" must {
        "format date in English" when {
          "recent date" in {
            val result: String = checkAnswersFormatters.formatDate(recentDate)(messages("en"))
            result mustBe "25 January 2015"
          }

          "old date" ignore {
            val result: String = checkAnswersFormatters.formatDate(oldDate)(messages("en"))
            result mustBe "1 December 1840"
          }
        }
      }

      "in Welsh mode" must {
        "format date in Welsh" when {
          "recent date" in {
            val result: String = checkAnswersFormatters.formatDate(recentDate)(messages("cy"))
            result mustBe "25 Ionawr 2015"
          }

          "old date" ignore {
            val result: String = checkAnswersFormatters.formatDate(oldDate)(messages("cy"))
            result mustBe "1 Rhagfyr 1840"
          }
        }
      }
    }

    ".utr" must {
      "render UTR" in {
        val result: Html = checkAnswersFormatters.utr("1234567890")
        result mustBe Html("1234567890")
      }
    }

    ".formatNino" must {
      "format NINO" in {
        val result: String = checkAnswersFormatters.formatNino("AB123456C")
        result mustBe "AB 12 34 56 C"
      }
    }

    ".yesOrNo" when {

      "true" must {
        "return Yes" in {
          val result: Html = checkAnswersFormatters.yesOrNo(answer = true)
          result mustBe Html("Yes")
        }
      }

      "false" must {
        "return No" in {
          val result: Html = checkAnswersFormatters.yesOrNo(answer = false)
          result mustBe Html("No")
        }
      }
    }

    ".currency" must {
      "prepend £ symbol to value" in {
        val result: Html = checkAnswersFormatters.currency("100")
        result mustBe Html("£100")
      }
    }

    ".percentage" must {
      "append % symbol to value" in {
        val result: Html = checkAnswersFormatters.percentage("100")
        result mustBe Html("100%")
      }
    }

    ".addressFormatter" when {

      "UK address" must {
        "return formatted address" when {

          "lines 3 and 4 provided" in {
            val address: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB1 1AB")
            val result: Html = checkAnswersFormatters.addressFormatter(address)
            result mustBe Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB1 1AB")
          }

          "lines 3 and 4 not provided" in {
            val address: UKAddress = UKAddress("Line 1", "Line 2", None, None, "AB1 1AB")
            val result: Html = checkAnswersFormatters.addressFormatter(address)
            result mustBe Html("Line 1<br />Line 2<br />AB1 1AB")
          }
        }
      }

      "non-UK address" must {
        "return formatted address" when {

          "line 3 provided" in {
            val address: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
            val result: Html = checkAnswersFormatters.addressFormatter(address)
            result mustBe Html("Line 1<br />Line 2<br />Line 3<br />France")
          }

          "line 3 not provided" in {
            val address: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "FR")
            val result: Html = checkAnswersFormatters.addressFormatter(address)
            result mustBe Html("Line 1<br />Line 2<br />France")
          }
        }
      }
    }

    ".passportOrIDCard" must {
      "render details" in {
        val passportOrIdCard = PassportOrIdCardDetails("FR", "1234567890", LocalDate.parse("2000-01-01"))
        val result: Html = checkAnswersFormatters.passportOrIDCard(passportOrIdCard)
        result mustBe Html("France<br />1234567890<br />1 January 2000")
      }
    }

    ".formatDescription" must {
      "render description" in {
        val description = Description("Description", Some("Description 1"), Some("Description 2"), Some("Description 3"), Some("Description 4"))
        val result: Html = checkAnswersFormatters.formatDescription(description)
        result mustBe Html("Description<br />Description 1<br />Description 2<br />Description 3<br />Description 4")
      }
    }

    ".formatNumberOfBeneficiaries" must {
      "display number of beneficiaries" when {
        "1 to 100" in {
          val result: Html = checkAnswersFormatters.formatNumberOfBeneficiaries(HowManyBeneficiaries.Over1)
          result mustBe Html("1 to 100")
        }

        "101 to 200" in {
          val result: Html = checkAnswersFormatters.formatNumberOfBeneficiaries(HowManyBeneficiaries.Over101)
          result mustBe Html("101 to 200")
        }

        "201 to 500" in {
          val result: Html = checkAnswersFormatters.formatNumberOfBeneficiaries(HowManyBeneficiaries.Over201)
          result mustBe Html("201 to 500")
        }

        "501 to 1,000" in {
          val result: Html = checkAnswersFormatters.formatNumberOfBeneficiaries(HowManyBeneficiaries.Over501)
          result mustBe Html("501 to 1,000")
        }

        "Over 1,001" in {
          val result: Html = checkAnswersFormatters.formatNumberOfBeneficiaries(HowManyBeneficiaries.Over1001)
          result mustBe Html("Over 1,001")
        }
      }
    }
  }
}
