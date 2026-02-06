/*
 * Copyright 2026 HM Revenue & Customs
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

package queries

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage

class RemoveClassOfBeneficiaryQuerySpec extends PageBehaviours {

  "RemoveClassOfBeneficiaryQuery" must {

    "remove class of beneficiary at index" in
      forAll(arbitrary[UserAnswers]) { initial =>
        val answers: UserAnswers = initial
          .set(ClassBeneficiaryDescriptionPage(0), "Future issue of grandchildren")
          .value
          .set(ClassBeneficiaryDescriptionPage(1), "Grandchildren of Sister")
          .value

        val result = answers.remove(RemoveClassOfBeneficiaryQuery(0)).value

        result.get(ClassBeneficiaryDescriptionPage(0)).value mustBe "Grandchildren of Sister"

        result.get(ClassBeneficiaryDescriptionPage(1)) mustNot be(defined)
      }

  }

}
