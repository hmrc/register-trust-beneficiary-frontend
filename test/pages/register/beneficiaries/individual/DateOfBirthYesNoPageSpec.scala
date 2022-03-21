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
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class DateOfBirthYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryDateOfBirthYesNoPage" must {

    beRetrievable[Boolean](DateOfBirthYesNoPage(0))

    beSettable[Boolean](DateOfBirthYesNoPage(0))

    beRemovable[Boolean](DateOfBirthYesNoPage(0))
  }


  "remove IndividualBeneficiaryDateOfBirth when IndividualBeneficiaryDateOfBirthYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(DateOfBirthPage(0), LocalDate.now).success.value
        val result = answers.set(DateOfBirthYesNoPage(0), false).success.value

        result.get(DateOfBirthPage(0)) mustNot be(defined)
    }
  }
}
