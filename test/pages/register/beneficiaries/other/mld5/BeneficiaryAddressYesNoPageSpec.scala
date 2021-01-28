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

package pages.register.beneficiaries.other.mld5

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.beneficiaries.other.{AddressInternationalPage, AddressUKYesNoPage}

class BeneficiaryAddressYesNoPageSpec extends PageBehaviours {

  "BeneficiariesAddressYesNoPage" must {

    beRetrievable[Boolean](BeneficiariesAddressYesNoPage(0))

    beSettable[Boolean](BeneficiariesAddressYesNoPage(0))

    beRemovable[Boolean](BeneficiariesAddressYesNoPage(0))
  }

  "remove UK address when BeneficiariesAddressYesNoPage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers =
          initial.set(BeneficiariesAddressYesNoPage(index), UKAddress(str, str, Some(str), Some(str), str)).success.value
          .set(AddressInternationalPage(index), InternationalAddress(str, str, Some(str), str)).success.value

        val result = answers.set(AddressUKYesNoPage(index), false).success.value

        result.get(AddressInternationalPage(index)) must be(defined)
        result.get(BeneficiariesAddressYesNoPage(index)) mustNot be(defined)
    }
  }
  "remove international address page when AddressUKYesNoPage is set to true" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers =
          initial.set(BeneficiariesAddressYesNoPage(index), UKAddress(str, str, Some(str), Some(str), str)).success.value
            .set(AddressInternationalPage(index), InternationalAddress(str, str, Some(str), str)).success.value

        val result = answers.set(AddressUKYesNoPage(index), true).success.value

        result.get(AddressInternationalPage(index)) mustNot be(defined)
        result.get(BeneficiariesAddressYesNoPage(index)) must be(defined)
    }
  }

}
