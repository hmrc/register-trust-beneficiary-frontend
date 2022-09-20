/*
 * Copyright 2022 HM Revenue & Customs
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

class AddressUKPageSpec extends PageBehaviours {

  "IndividualBeneficiaryAddressUKPage" must {

    beRetrievable[UKAddress](AddressUKPage(0))

    beSettable[UKAddress](AddressUKPage(0))

    beRemovable[UKAddress](AddressUKPage(0))
  }

  "remove IndividualBeneficiaryAddressUK when IndividualBeneficiaryAddressUKYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(AddressUKPage(0), UKAddress("line1", "line2", Some("line3"), Some("line4"), "AB1 1AB")).right.get
        val result = answers.set(AddressYesNoPage(0), false).right.get

        result.get(AddressUKPage(0)) mustNot be(defined)
    }
  }

}
