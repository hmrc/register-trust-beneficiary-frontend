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

package pages.register.beneficiaries.individual

import models.UserAnswers
import models.core.pages.{InternationalAddress, UKAddress}
import models.registration.pages.PassportOrIdCardDetails
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class AddressYesNoPageSpec extends PageBehaviours {

  "AddressYesNoPage" must {

    beRetrievable[Boolean](AddressYesNoPage(0))

    beSettable[Boolean](AddressYesNoPage(0))

    beRemovable[Boolean](AddressYesNoPage(0))
  }

  "remove relevant Data when AddressYesNoPage is set to false" in {
    val index = 0
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(AddressUKYesNoPage(index), true).right.get
          .set(AddressUKPage(index), UKAddress(str, str, Some(str), Some(str), str)).right.get
          .set(AddressInternationalPage(index), InternationalAddress(str, str, Some(str), str)).right.get
          .set(PassportDetailsYesNoPage(index), true).right.get
          .set(IDCardDetailsYesNoPage(index), true).right.get
          .set(PassportDetailsPage(index), PassportOrIdCardDetails("a", "b", LocalDate.now)).right.get
          .set(IDCardDetailsPage(index), PassportOrIdCardDetails("c", "d", LocalDate.now)).right.get

        val result = answers.set(AddressYesNoPage(index), false).right.get

        result.get(AddressUKYesNoPage(index)) mustNot be(defined)
        result.get(AddressUKPage(index)) mustNot be(defined)
        result.get(AddressInternationalPage(index)) mustNot be(defined)
        result.get(PassportDetailsYesNoPage(index)) mustNot be(defined)
        result.get(IDCardDetailsYesNoPage(index)) mustNot be(defined)
        result.get(PassportDetailsPage(index)) mustNot be(defined)
        result.get(IDCardDetailsPage(index)) mustNot be(defined)
    }
  }

}
