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
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class PassportDetailsYesNoPageSpec extends PageBehaviours {

  "PassportDetailsYesNoPage" must {

    beRetrievable[Boolean](PassportDetailsYesNoPage(0))

    beSettable[Boolean](PassportDetailsYesNoPage(0))

    beRemovable[Boolean](PassportDetailsYesNoPage(0))

    "remove relevant data" when {

      val page = PassportDetailsYesNoPage(0)

      "set to true" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, false).value
              .set(IDCardDetailsYesNoPage(0), true).value
              .set(IDCardDetailsPage(0), PassportOrIdCardDetails("France", "98765546", LocalDate.now())).value

            val result = answers.set(page, true).value

            result.get(IDCardDetailsYesNoPage(0)) must not be defined
            result.get(IDCardDetailsPage(0)) must not be defined
        }
      }

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).value
              .set(PassportDetailsPage(0), PassportOrIdCardDetails("France", "234323", LocalDate.now())).value

            val result = answers.set(page, false).value

            result.get(PassportDetailsPage(0)) must not be defined
        }
      }

    }
  }
}
