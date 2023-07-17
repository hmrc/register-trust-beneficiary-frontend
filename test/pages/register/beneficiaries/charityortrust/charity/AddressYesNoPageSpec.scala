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

package pages.register.beneficiaries.charityortrust.charity

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AddressYesNoPageSpec extends PageBehaviours {

  "AddressYesNoPage" must {

    beRetrievable[Boolean](AddressYesNoPage(0))

    beSettable[Boolean](AddressYesNoPage(0))

    beRemovable[Boolean](AddressYesNoPage(0))
  }

  "remove pages when AddressYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(AddressInTheUkYesNoPage(0), true).value

        val result = answers.set(AddressYesNoPage(0), false).value

        result.get(AddressInTheUkYesNoPage(0)) mustNot be(defined)
    }
  }
}
