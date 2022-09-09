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

import java.time.LocalDate

import models.UserAnswers
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IDCardDetailsYesNoPageSpec extends PageBehaviours {

  "IDCardDetailsYesNoPage" must {

    beRetrievable[Boolean](IDCardDetailsYesNoPage(0))

    beSettable[Boolean](IDCardDetailsYesNoPage(0))

    beRemovable[Boolean](IDCardDetailsYesNoPage(0))

    "remove IDCardDetailsPage when IDCardDetailsYesNoPage is set to false" in {
      val index = 0
      forAll(arbitrary[UserAnswers]) {
        initial =>
          val answers: UserAnswers =
          initial.set(IDCardDetailsPage(index), PassportOrIdCardDetails("c", "d", LocalDate.now)).right.get

          val result = answers.set(IDCardDetailsYesNoPage(index), false).right.get

          result.get(IDCardDetailsPage(index)) mustNot be(defined)
      }
    }
  }
}
