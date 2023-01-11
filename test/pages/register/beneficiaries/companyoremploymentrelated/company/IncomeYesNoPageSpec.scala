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

package pages.register.beneficiaries.companyoremploymentrelated.company

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IncomeYesNoPageSpec extends PageBehaviours {

  "IncomeYesNoPage" must {

    beRetrievable[Boolean](IncomeYesNoPage(0))

    beSettable[Boolean](IncomeYesNoPage(0))

    beRemovable[Boolean](IncomeYesNoPage(0))
  }

  "remove Income page when IncomeYesNoPage is set to yes/true" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, _) =>
        val answers: UserAnswers = initial.set(IncomePage(index), 55).right.get

        val result = answers.set(IncomeYesNoPage(index), true).right.get
        result.get(IncomePage(index)) mustNot be(defined)
    }
  }

}
