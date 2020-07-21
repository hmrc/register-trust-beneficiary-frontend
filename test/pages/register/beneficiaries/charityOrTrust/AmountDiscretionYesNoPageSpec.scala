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

package pages.register.beneficiaries.charityOrTrust

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AmountDiscretionYesNoPageSpec extends PageBehaviours {

  "AmountDiscretionYesNoPage" must {

    beRetrievable[Boolean](AmountDiscretionYesNoPage(0))

    beSettable[Boolean](AmountDiscretionYesNoPage(0))

    beRemovable[Boolean](AmountDiscretionYesNoPage(0))
  }

  "remove pages when AmountDiscretionYesNoPage is set to true" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(AmountDiscretionYesNoPage(0), true).success.value
          .set(HowMuchIncomePage(0), "123").success.value

        val cleaned = answers.set(AmountDiscretionYesNoPage(0), true).success.value

        cleaned.get(HowMuchIncomePage(0)) mustNot be(defined)
    }
  }
}
