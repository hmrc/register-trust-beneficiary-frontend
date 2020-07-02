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

package pages.beneficiaries.individual

import models.UserAnswers
import models.core.pages.UKAddress
import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary

class IndividualBeneficiaryNationalInsuranceYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryNationalInsuranceYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryNationalInsuranceYesNoPage(0))

    beSettable[Boolean](IndividualBeneficiaryNationalInsuranceYesNoPage(0))

    beRemovable[Boolean](IndividualBeneficiaryNationalInsuranceYesNoPage(0))
  }


  "remove IndividualBeneficiaryNationalInsuranceNumberPage when IndividualBeneficiaryNationalInsuranceYesNoPage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(IndividualBeneficiaryNationalInsuranceNumberPage(index), str).success.value
        val result = answers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), false).success.value

        result.get(IndividualBeneficiaryNationalInsuranceNumberPage(index)) mustNot be(defined)
    }
  }

  "remove relevant Data when IndividualBeneficiaryNationalInsuranceYesNoPage is set to true" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(IndividualBeneficiaryAddressYesNoPage(index), true).success.value
          .set(IndividualBeneficiaryAddressUKYesNoPage(index), true).success.value
          .set(IndividualBeneficiaryAddressUKPage(index), UKAddress(str, str, Some(str), Some(str), str)).success.value

        val result = answers.set(IndividualBeneficiaryNationalInsuranceYesNoPage(index), true).success.value

        result.get(IndividualBeneficiaryAddressYesNoPage(index)) mustNot be(defined)
        result.get(IndividualBeneficiaryAddressUKYesNoPage(index)) mustNot be(defined)
        result.get(IndividualBeneficiaryAddressUKPage(index)) mustNot be(defined)
    }
  }


}
