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

package pages.register.beneficiaries.charityortrust.trust

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class DiscretionYesNoPageSpec extends PageBehaviours {

  "DiscretionYesNo Page" must {

    beRetrievable[Boolean](DiscretionYesNoPage(0))

    beSettable[Boolean](DiscretionYesNoPage(0))

    beRemovable[Boolean](DiscretionYesNoPage(0))

    "implement cleanup logic when decision changed" when {

      "YES selected" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result: UserAnswers = userAnswers
              .set(ShareOfIncomePage(0), 100).right.get
              .set(DiscretionYesNoPage(0), true).right.get

            result.get(ShareOfIncomePage(0)) mustNot be(defined)
        }
      }
    }
  }
}
