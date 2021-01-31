/*
 * Copyright 2021 HM Revenue & Customs
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

package pages.register.beneficiaries.charityortrust.charity.mld5

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import utils.Constants._

class CountryOfResidenceYesNoPageSpec extends PageBehaviours {

  "CountryOfResidenceYesNoPage" must {

    beRetrievable[Boolean](CountryOfResidenceYesNoPage(0))

    beSettable[Boolean](CountryOfResidenceYesNoPage(0))

    beRemovable[Boolean](CountryOfResidenceYesNoPage(0))
  }

  "remove pages when CountryOfResidenceYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(CountryOfResidenceInTheUkYesNoPage(0), false).success.value
          .set(CountryOfResidencePage(0), ES).success.value

        val result = answers.set(CountryOfResidenceYesNoPage(0), false).success.value

        result.get(CountryOfResidenceInTheUkYesNoPage(0)) mustNot be(defined)
        result.get(CountryOfResidencePage(0)) mustNot be(defined)
    }
  }
}