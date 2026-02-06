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
import models.core.pages.{FullName, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.register.beneficiaries.individual.{AddressUKPage, AddressUKYesNoPage, AddressYesNoPage, NamePage}

class RemoveIndividualBeneficiaryQuerySpec extends PageBehaviours {

  val index: Int = 0

  "RemoveIndividualBeneficiaryQuery" must {

    "remove individual beneficiary at index" in
      forAll(arbitrary[UserAnswers]) { initial =>
        val answers: UserAnswers = initial
          .set(NamePage(0), FullName("First", None, "Last"))
          .value
          .set(AddressYesNoPage(0), true)
          .value
          .set(AddressUKYesNoPage(0), true)
          .value
          .set(AddressUKPage(0), UKAddress("1", "2", Some("3"), Some("4"), "5"))
          .value
          .set(NamePage(1), FullName("Second", None, "Last"))
          .value

        val result = answers.remove(RemoveIndividualBeneficiaryQuery(index)).value

        result.get(NamePage(0)).value mustBe FullName("Second", None, "Last")
        result.get(AddressYesNoPage(0)) mustNot be(defined)
        result.get(AddressUKYesNoPage(0)) mustNot be(defined)
        result.get(AddressUKPage(0)) mustNot be(defined)

        result.get(NamePage(1)) mustNot be(defined)
      }

  }

}
