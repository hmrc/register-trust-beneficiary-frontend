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

package pages.register.beneficiaries.other.mld5

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import utils.Constants.{ES, GB}

class UKResidentYesNoPageSpec extends PageBehaviours {

  "UKResidentYesNoPage" must {

    beRetrievable[Boolean](UKResidentYesNoPage(0))

    beSettable[Boolean](UKResidentYesNoPage(0))

    beRemovable[Boolean](UKResidentYesNoPage(0))
  }

  "Yes selected - set CountryOfResidencePage to 'GB' " in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(CountryOfResidenceYesNoPage(0), true).right.get
          .set(CountryOfResidencePage(0), ES).right.get

        val result = answers.set(UKResidentYesNoPage(0), true).right.get

        result.get(CountryOfResidencePage(0)).get mustBe GB
    }
  }

  "No selected" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(CountryOfResidenceYesNoPage(0), true).right.get
          .set(CountryOfResidencePage(0), ES).right.get

        val result = answers.set(UKResidentYesNoPage(0), false).right.get

        result.get(CountryOfResidencePage(0)).get mustBe ES
    }
  }
}
