/*
 * Copyright 2020 HM Revenue & Customs
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

package pages.register.beneficiaries.charityortrust

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.beneficiaries.charityortrust.charity.{AddressInTheUkYesNoPage, CharityAddressUKPage, CharityInternationalAddressPage}

class AddressInTheUkYesNoPageSpec extends PageBehaviours {

  "CharityOrTrustPage" must {

    beRetrievable[Boolean](AddressInTheUkYesNoPage(0))

    beSettable[Boolean](AddressInTheUkYesNoPage(0))

    beRemovable[Boolean](AddressInTheUkYesNoPage(0))
  }

  "remove CharityInternationalAddress when AddressInTheUkYesNoPage is set to true" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(CharityAddressUKPage(0), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB")).success.value
        val result = answers.set(AddressInTheUkYesNoPage(0), true).success.value

        result.get(CharityInternationalAddressPage(0)) mustNot be(defined)
    }
  }
  "remove CharityAddressUk when AddressInTheUkYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(CharityInternationalAddressPage(0), InternationalAddress("line1", "line2", Some("line3"), "AP")).success.value
        val result = answers.set(AddressInTheUkYesNoPage(0), false).success.value

        result.get(CharityAddressUKPage(0)) mustNot be(defined)
    }
  }
}
