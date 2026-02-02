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

package pages.register.beneficiaries.individual

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddressUKYesNoPageSpec extends PageBehaviours {

  private val ukAddress: UKAddress                       = UKAddress("Line 1", "Line 2", None, None, "POSTCODE")
  private val internationalAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", None, "COUNTRY")

  "IndividualBeneficiaryAddressUKYesNoPage" must {

    beRetrievable[Boolean](AddressUKYesNoPage(0))

    beSettable[Boolean](AddressUKYesNoPage(0))

    beRemovable[Boolean](AddressUKYesNoPage(0))

    "implement cleanup logic" when {

      "YES selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(AddressInternationalPage(0), internationalAddress)
            .value
            .set(AddressUKYesNoPage(0), true)
            .value

          result.get(AddressInternationalPage(0)) mustNot be(defined)
        }

      "NO selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(AddressUKPage(0), ukAddress)
            .value
            .set(AddressUKYesNoPage(0), false)
            .value

          result.get(AddressUKPage(0)) mustNot be(defined)
        }
    }

    "not implement cleanup logic" when {

      "previous selection YES selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(AddressUKPage(0), ukAddress)
            .value
            .set(AddressUKYesNoPage(0), true)
            .value

          result.get(AddressUKPage(0)).get mustBe ukAddress
        }

      "previous selection NO selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(AddressInternationalPage(0), internationalAddress)
            .value
            .set(AddressUKYesNoPage(0), false)
            .value

          result.get(AddressInternationalPage(0)).get mustBe internationalAddress
        }
    }
  }

}
