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

package pages.register.beneficiaries

import models.Status._
import models.core.pages.FullName
import models.registration.pages.WhatTypeOfBeneficiary
import models.registration.pages.WhatTypeOfBeneficiary._
import models.{Status, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus._
import pages.register.beneficiaries.charityortrust.{charity, trust}
import pages.register.beneficiaries.companyoremploymentrelated.{company, employmentRelated => large}
import pages.register.beneficiaries.{classofbeneficiaries => cob, individual => ind}
import sections.beneficiaries._

class WhatTypeOfBeneficiaryPageSpec extends PageBehaviours {

  val name: FullName = FullName("Joe", None, "Bloggs")
  val string: String = "String"

  implicit class UserAnswersSetters(userAnswers: UserAnswers) {

    def setWithStatus(status: Status): UserAnswers = userAnswers
      .set(ind.NamePage(0), name).success.value
      .set(IndividualBeneficiaryStatus(0), status).success.value
      .set(cob.ClassBeneficiaryDescriptionPage(0), string).success.value
      .set(ClassBeneficiaryStatus(0), status).success.value
      .set(charity.CharityNamePage(0), string).success.value
      .set(CharityBeneficiaryStatus(0), status).success.value
      .set(trust.NamePage(0), string).success.value
      .set(TrustBeneficiaryStatus(0), status).success.value
      .set(company.NamePage(0), string).success.value
      .set(CompanyBeneficiaryStatus(0), status).success.value
      .set(large.LargeBeneficiaryNamePage(0), string).success.value
      .set(LargeBeneficiaryStatus(0), status).success.value
      .set(other.DescriptionPage(0), string).success.value
      .set(OtherBeneficiaryStatus(0), status).success.value
  }

  "WhatTypeOfBeneficiaryPage" must {

    beRetrievable[WhatTypeOfBeneficiary](WhatTypeOfBeneficiaryPage)

    beSettable[WhatTypeOfBeneficiary](WhatTypeOfBeneficiaryPage)

    beRemovable[WhatTypeOfBeneficiary](WhatTypeOfBeneficiaryPage)

    "implement cleanup" when {

      "individual selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, Individual).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "class of beneficiary selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, ClassOfBeneficiary).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "charity or trust selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, CharityOrTrust).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "charity selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, Charity).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "trust selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, Trust).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "company or employment-related selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, CompanyOrEmployment).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "company selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, Company).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "employment-related selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, Employment).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
          }
        }
      }

      "other selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, Other).success.value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 1
          }
        }
      }
    }

    "not implement cleanup" when {

      def runTestsForType(beneficiaryType: WhatTypeOfBeneficiary): Unit = {

        s"$beneficiaryType selected" when {

          "no other beneficiary types" in {
            forAll(arbitrary[UserAnswers]) {
              initial =>
                val result = initial.set(WhatTypeOfBeneficiaryPage, beneficiaryType).success.value

                result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
                result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
                result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
                result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
                result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
                result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
                result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0
            }
          }

          "last of other beneficiary types is complete" in {
            forAll(arbitrary[UserAnswers]) {
              initial =>
                val answers = initial.setWithStatus(Completed)

                val result = answers.set(WhatTypeOfBeneficiaryPage, beneficiaryType).success.value

                result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 1
                result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 1
                result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 1
                result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 1
                result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 1
                result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 1
                result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 1
            }
          }
        }
      }

      runTestsForType(Individual)
      runTestsForType(ClassOfBeneficiary)
      runTestsForType(CharityOrTrust)
      runTestsForType(Charity)
      runTestsForType(Trust)
      runTestsForType(CompanyOrEmployment)
      runTestsForType(Company)
      runTestsForType(Employment)
      runTestsForType(Other)
    }
  }
}
