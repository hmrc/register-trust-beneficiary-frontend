/*
 * Copyright 2026 HM Revenue & Customs
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
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class LargeBeneficiaryAddressUKYesNoPageSpec extends PageBehaviours {

  "LargeBeneficiaryAddressUKYesNoPage" must {

    beRetrievable[Boolean](LargeBeneficiaryAddressUKYesNoPage(0))

    beSettable[Boolean](LargeBeneficiaryAddressUKYesNoPage(0))

    beRemovable[Boolean](LargeBeneficiaryAddressUKYesNoPage(0))
  }

  "remove UK address when LargeBeneficiaryAddressUKYesNoPage is set to false" in
    forAll(arbitrary[UserAnswers]) { initial =>
      val answers: UserAnswers =
        initial
          .set(LargeBeneficiaryAddressPage(0), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB"))
          .value
          .set(LargeBeneficiaryAddressInternationalPage(0), InternationalAddress("line1", "line2", Some("line3"), "AP"))
          .value

      val result = answers.set(LargeBeneficiaryAddressUKYesNoPage(0), false).value

      result.get(LargeBeneficiaryAddressInternationalPage(0)) must be(defined)
      result.get(LargeBeneficiaryAddressPage(0)) mustNot be(defined)
    }

  "remove international address page when LargeBeneficiaryAddressUKYesNoPage is set to true" in
    forAll(arbitrary[UserAnswers]) { initial =>
      val answers: UserAnswers =
        initial
          .set(LargeBeneficiaryAddressPage(0), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB"))
          .value
          .set(LargeBeneficiaryAddressInternationalPage(0), InternationalAddress("line1", "line2", Some("line3"), "AP"))
          .value

      val result = answers.set(LargeBeneficiaryAddressUKYesNoPage(0), true).value

      result.get(LargeBeneficiaryAddressInternationalPage(0)) mustNot be(defined)
      result.get(LargeBeneficiaryAddressPage(0)) must be(defined)
    }

}
