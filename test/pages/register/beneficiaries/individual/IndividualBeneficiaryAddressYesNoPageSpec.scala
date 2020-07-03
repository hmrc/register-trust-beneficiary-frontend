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
import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary

class IndividualBeneficiaryAddressYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryAddressYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryAddressYesNoPage(0))

    beSettable[Boolean](IndividualBeneficiaryAddressYesNoPage(0))

    beRemovable[Boolean](IndividualBeneficiaryAddressYesNoPage(0))
  }

  "remove relevant Data when IndividualBeneficiaryAddressYesNoPage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(IndividualBeneficiaryAddressYesNoPage(index), false).success.value
        val result = answers.set(IndividualBeneficiaryAddressYesNoPage(index), false).success.value

        result.get(IndividualBeneficiaryAddressUKYesNoPage(index)) mustNot be(defined)
        result.get(IndividualBeneficiaryAddressUKPage(index)) mustNot be(defined)
    }
  }

}
