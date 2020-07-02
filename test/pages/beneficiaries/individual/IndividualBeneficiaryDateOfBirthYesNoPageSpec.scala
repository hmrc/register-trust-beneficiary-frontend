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

package pages.beneficiaries.individual

import java.time.LocalDate

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IndividualBeneficiaryDateOfBirthYesNoPageSpec extends PageBehaviours {

  "IndividualBeneficiaryDateOfBirthYesNoPage" must {

    beRetrievable[Boolean](IndividualBeneficiaryDateOfBirthYesNoPage(0))

    beSettable[Boolean](IndividualBeneficiaryDateOfBirthYesNoPage(0))

    beRemovable[Boolean](IndividualBeneficiaryDateOfBirthYesNoPage(0))
  }


  "remove IndividualBeneficiaryDateOfBirth when IndividualBeneficiaryDateOfBirthYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(IndividualBeneficiaryDateOfBirthPage(0), LocalDate.now).success.value
        val result = answers.set(IndividualBeneficiaryDateOfBirthYesNoPage(0), false).success.value

        result.get(IndividualBeneficiaryDateOfBirthPage(0)) mustNot be(defined)
    }
  }
}
