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

package pages.register.beneficiaries.charityortrust.charity.nonTaxable

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.beneficiaries.charityortrust.charity._

class CountryOfResidenceInTheUkYesNoPageSpec extends PageBehaviours {

  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "COUNTRY")

  "CountryOfResidenceInTheUkYesNoPage" must {

    beRetrievable[Boolean](CountryOfResidenceInTheUkYesNoPage(0))

    beSettable[Boolean](CountryOfResidenceInTheUkYesNoPage(0))

    beRemovable[Boolean](CountryOfResidenceInTheUkYesNoPage(0))

//    "implement cleanup logic when decision changed" when {  // TODO
//
//      "YES selected" in {
//        forAll(arbitrary[UserAnswers]) {
//          userAnswers =>
//            val result: UserAnswers = userAnswers
//              .set(CharityInternationalAddressPage(0), internationalAddress).success.value
//              .set(CountryOfResidenceInTheUkYesNoPage(0), true).success.value
//
//            result.get(CharityInternationalAddressPage(0)) mustNot be(defined)
//        }
//      }
//
//      "NO selected" in {
//        forAll(arbitrary[UserAnswers]) {
//          userAnswers =>
//            val result: UserAnswers = userAnswers
//              .set(CharityAddressUKPage(0), ukAddress).success.value
//              .set(CountryOfResidenceInTheUkYesNoPage(0), false).success.value
//
//            result.get(CharityAddressUKPage(0)) mustNot be(defined)
//        }
//      }
//    }

//    "not implement cleanup logic when decision unchanged" when {  // TODO
//
//      "YES selected" in {
//        forAll(arbitrary[UserAnswers]) {
//          userAnswers =>
//            val result: UserAnswers = userAnswers
//              .set(CharityAddressUKPage(0), ukAddress).success.value
//              .set(CountryOfResidenceInTheUkYesNoPage(0), true).success.value
//
//            result.get(CharityAddressUKPage(0)).get mustBe ukAddress
//        }
//      }
//
//      "NO selected" in {
//        forAll(arbitrary[UserAnswers]) {
//          userAnswers =>
//            val result: UserAnswers = userAnswers
//              .set(CharityInternationalAddressPage(0), internationalAddress).success.value
//              .set(CountryOfResidenceInTheUkYesNoPage(0), false).success.value
//
//            result.get(CharityInternationalAddressPage(0)).get mustBe internationalAddress
//        }
//      }
//    }
  }
}
