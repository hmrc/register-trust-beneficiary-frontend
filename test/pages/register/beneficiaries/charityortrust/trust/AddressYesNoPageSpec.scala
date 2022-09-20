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

package pages.register.beneficiaries.charityortrust.trust

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddressYesNoPageSpec extends PageBehaviours {

  "AddressYesNoPage" must {

    beRetrievable[Boolean](AddressYesNoPage(0))

    beSettable[Boolean](AddressYesNoPage(0))

    beRemovable[Boolean](AddressYesNoPage(0))

    "implement cleanup logic when decision changed" when {

      "NO selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers = userAnswers
              .set(AddressUKYesNoPage(0), true).right.get
              .set(AddressUKPage(0), UKAddress("Line 1", "Line 2", None, None, "POSTCODE")).right.get
              .set(AddressInternationalPage(0), InternationalAddress("Line 1", "Line 2", None, "COUNTRY")).right.get
              .set(AddressYesNoPage(0), false).right.get

            result.get(AddressUKYesNoPage(0)) mustNot be(defined)
            result.get(AddressUKPage(0)) mustNot be(defined)
            result.get(AddressInternationalPage(0)) mustNot be(defined)
        }
      }
    }
  }
}
