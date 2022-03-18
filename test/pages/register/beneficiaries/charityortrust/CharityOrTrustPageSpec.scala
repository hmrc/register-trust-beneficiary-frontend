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

package pages.register.beneficiaries.charityortrust

import models.Status._
import models.registration.pages.CharityOrTrust
import models.registration.pages.CharityOrTrust._
import models.{Status, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus._
import sections.beneficiaries._

class CharityOrTrustPageSpec extends PageBehaviours {

  val string: String = "String"

  implicit class UserAnswersSetters(userAnswers: UserAnswers) {

    def setWithStatus(status: Status): UserAnswers = userAnswers
      .set(charity.CharityNamePage(0), string).success.value
      .set(CharityBeneficiaryStatus(0), status).success.value
      .set(trust.NamePage(0), string).success.value
      .set(TrustBeneficiaryStatus(0), status).success.value
  }

  "CharityOrTrustPage" must {

    beRetrievable[CharityOrTrust](CharityOrTrustPage)

    beSettable[CharityOrTrust](CharityOrTrustPage)

    beRemovable[CharityOrTrust](CharityOrTrustPage)

    "implement cleanup" when {

      "charity selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(CharityOrTrustPage, Charity).success.value

              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "trust selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(CharityOrTrustPage, Trust).success.value

              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 1
          }
        }
      }
    }

    "not implement cleanup" when {

      def runTestsForType(beneficiaryType: CharityOrTrust): Unit = {

        s"$beneficiaryType selected" when {

          "no other beneficiary types" in {
            forAll(arbitrary[UserAnswers]) {
              initial =>
                val result = initial.set(CharityOrTrustPage, beneficiaryType).success.value

                result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
                result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
            }
          }

          "last of other beneficiary types is complete" in {
            forAll(arbitrary[UserAnswers]) {
              initial =>
                val answers = initial.setWithStatus(Completed)

                val result = answers.set(CharityOrTrustPage, beneficiaryType).success.value

                result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 1
                result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 1
            }
          }
        }
      }

      runTestsForType(Charity)
      runTestsForType(Trust)
    }
  }
}
