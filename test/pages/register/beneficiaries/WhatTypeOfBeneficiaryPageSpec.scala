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

package pages.register.beneficiaries

import models.Status._
import models.core.pages.FullName
import models.registration.pages.{CharityOrTrust, WhatTypeOfBeneficiary}
import models.{CompanyOrEmploymentRelatedToAdd, Status, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus._
import pages.register.beneficiaries.charityortrust.{CharityOrTrustPage, charity, trust}
import pages.register.beneficiaries.companyoremploymentrelated.{CompanyOrEmploymentRelatedPage, company, employmentRelated => large}
import pages.register.beneficiaries.{classofbeneficiaries => cob, individual => ind}
import sections.beneficiaries._

class WhatTypeOfBeneficiaryPageSpec extends PageBehaviours {

  val name: FullName = FullName("Joe", None, "Bloggs")
  val string: String = "String"

  implicit class UserAnswersSetters(userAnswers: UserAnswers) {

    def setWithStatus(status: Status): UserAnswers = userAnswers
      .set(CharityOrTrustPage, CharityOrTrust.Charity).value
      .set(CompanyOrEmploymentRelatedPage, CompanyOrEmploymentRelatedToAdd.Company).value

      .set(ind.NamePage(0), name).value
      .set(IndividualBeneficiaryStatus(0), status).value
      .set(cob.ClassBeneficiaryDescriptionPage(0), string).value
      .set(ClassBeneficiaryStatus(0), status).value
      .set(charity.CharityNamePage(0), string).value
      .set(CharityBeneficiaryStatus(0), status).value
      .set(trust.NamePage(0), string).value
      .set(TrustBeneficiaryStatus(0), status).value
      .set(company.NamePage(0), string).value
      .set(CompanyBeneficiaryStatus(0), status).value
      .set(large.LargeBeneficiaryNamePage(0), string).value
      .set(LargeBeneficiaryStatus(0), status).value
      .set(other.DescriptionPage(0), string).value
      .set(OtherBeneficiaryStatus(0), status).value
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

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) mustNot be(defined)
              result.get(CompanyOrEmploymentRelatedPage) mustNot be(defined)
          }
        }
      }

      "class of beneficiary selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.ClassOfBeneficiary).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) mustNot be(defined)
              result.get(CompanyOrEmploymentRelatedPage) mustNot be(defined)
          }
        }
      }

      "charity or trust selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CharityOrTrust).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) must be(defined)
              result.get(CompanyOrEmploymentRelatedPage) mustNot be(defined)
          }
        }
      }

      "charity selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Charity).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) must be(defined)
              result.get(CompanyOrEmploymentRelatedPage) mustNot be(defined)
          }
        }
      }

      "trust selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Trust).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) must be(defined)
              result.get(CompanyOrEmploymentRelatedPage) mustNot be(defined)
          }
        }
      }

      "company or employment-related selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.CompanyOrEmployment).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) mustNot be(defined)
              result.get(CompanyOrEmploymentRelatedPage) must be(defined)
          }
        }
      }

      "company selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Company).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) mustNot be(defined)
              result.get(CompanyOrEmploymentRelatedPage) must be(defined)
          }
        }
      }

      "employment-related selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Employment).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 0

              result.get(CharityOrTrustPage) mustNot be(defined)
              result.get(CompanyOrEmploymentRelatedPage) must be(defined)
          }
        }
      }

      "other selected" when {
        "last of other beneficiary types are in progress" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers = initial.setWithStatus(InProgress)

              val result = answers.set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Other).value

              result.get(IndividualBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(ClassOfBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CharityBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(TrustBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(OtherBeneficiaries).getOrElse(Nil).size mustBe 1

              result.get(CharityOrTrustPage) mustNot be(defined)
              result.get(CompanyOrEmploymentRelatedPage) mustNot be(defined)
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
                val result = initial.set(WhatTypeOfBeneficiaryPage, beneficiaryType).value

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

                val result = answers.set(WhatTypeOfBeneficiaryPage, beneficiaryType).value

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

      runTestsForType(WhatTypeOfBeneficiary.Individual)
      runTestsForType(WhatTypeOfBeneficiary.ClassOfBeneficiary)
      runTestsForType(WhatTypeOfBeneficiary.CharityOrTrust)
      runTestsForType(WhatTypeOfBeneficiary.Charity)
      runTestsForType(WhatTypeOfBeneficiary.Trust)
      runTestsForType(WhatTypeOfBeneficiary.CompanyOrEmployment)
      runTestsForType(WhatTypeOfBeneficiary.Company)
      runTestsForType(WhatTypeOfBeneficiary.Employment)
      runTestsForType(WhatTypeOfBeneficiary.Other)
    }
  }
}
