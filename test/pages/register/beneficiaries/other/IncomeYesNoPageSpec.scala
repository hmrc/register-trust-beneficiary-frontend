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

package pages.register.beneficiaries.other

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IncomeYesNoPageSpec extends PageBehaviours {

  "IncomeYesNoPage" must {

    beRetrievable[Boolean](IncomeDiscretionYesNoPage(0))

    beSettable[Boolean](IncomeDiscretionYesNoPage(0))

    beRemovable[Boolean](IncomeDiscretionYesNoPage(0))
  }

  "remove Income page when IncomeYesNoPage is set to yes/true" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, _) =>
        val answers: UserAnswers = initial.set(ShareOfIncomePage(index), 55).success.value

        val result = answers.set(IncomeDiscretionYesNoPage(index), true).success.value
        result.get(ShareOfIncomePage(index)) mustNot be(defined)
    }
  }

}
