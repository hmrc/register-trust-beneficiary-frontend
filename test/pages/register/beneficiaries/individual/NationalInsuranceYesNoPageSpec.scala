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

package pages.register.beneficiaries.individual

import models.UserAnswers
import models.core.pages.UKAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class NationalInsuranceYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryNationalInsuranceYesNoPage" must {

    beRetrievable[Boolean](NationalInsuranceYesNoPage(0))

    beSettable[Boolean](NationalInsuranceYesNoPage(0))

    beRemovable[Boolean](NationalInsuranceYesNoPage(0))
  }


  "remove IndividualBeneficiaryNationalInsuranceNumberPage when IndividualBeneficiaryNationalInsuranceYesNoPage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(NationalInsuranceNumberPage(index), str).success.value
        val result = answers.set(NationalInsuranceYesNoPage(index), false).success.value

        result.get(NationalInsuranceNumberPage(index)) mustNot be(defined)
    }
  }

  "remove relevant Data when IndividualBeneficiaryNationalInsuranceYesNoPage is set to true" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(AddressYesNoPage(index), true).success.value
          .set(AddressUKYesNoPage(index), true).success.value
          .set(AddressUKPage(index), UKAddress(str, str, Some(str), Some(str), str)).success.value

        val result = answers.set(NationalInsuranceYesNoPage(index), true).success.value

        result.get(AddressYesNoPage(index)) mustNot be(defined)
        result.get(AddressUKYesNoPage(index)) mustNot be(defined)
        result.get(AddressUKPage(index)) mustNot be(defined)
    }
  }


}
