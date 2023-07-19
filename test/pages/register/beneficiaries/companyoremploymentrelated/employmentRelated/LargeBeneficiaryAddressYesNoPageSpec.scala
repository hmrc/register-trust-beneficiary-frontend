/*
 * Copyright 2023 HM Revenue & Customs
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

package pages.register.beneficiaries.companyoremploymentrelated.employmentRelated

import models.UserAnswers
import models.core.pages.UKAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class LargeBeneficiaryAddressYesNoPageSpec extends PageBehaviours {

  "LargeBeneficiaryAddressYesNoPage" must {

    beRetrievable[Boolean](LargeBeneficiaryAddressYesNoPage(0))

    beSettable[Boolean](LargeBeneficiaryAddressYesNoPage(0))

    beRemovable[Boolean](LargeBeneficiaryAddressYesNoPage(0))
  }

  "remove pages when LargeBeneficiaryAddressYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(LargeBeneficiaryAddressUKYesNoPage(0), true).value
          .set(LargeBeneficiaryAddressPage(0), UKAddress("line 1", "line 2", Some("line 3"), Some("line 4"),"line 5")).value

        val result = answers.set(LargeBeneficiaryAddressYesNoPage(0), false).value

        result.get(LargeBeneficiaryAddressUKYesNoPage(0)) mustNot be(defined)
        result.get(LargeBeneficiaryAddressPage(0)) mustNot be(defined)
        result.get(LargeBeneficiaryAddressInternationalPage(0)) mustNot be(defined)
    }
  }
}
