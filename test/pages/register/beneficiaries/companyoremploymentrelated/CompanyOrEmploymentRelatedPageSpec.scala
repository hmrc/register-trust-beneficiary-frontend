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

package pages.register.beneficiaries.companyoremploymentrelated

import models.CompanyOrEmploymentRelatedToAdd._
import models.Status._
import models.{CompanyOrEmploymentRelatedToAdd, Status, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.entitystatus._
import pages.register.beneficiaries.companyoremploymentrelated.{employmentRelated => large}
import sections.beneficiaries._

class CompanyOrEmploymentRelatedPageSpec extends PageBehaviours {

  val string: String = "String"

  implicit class UserAnswersSetters(userAnswers: UserAnswers) {

    def setWithStatus(status: Status): UserAnswers = userAnswers
      .set(company.NamePage(0), string)
      .value
      .set(CompanyBeneficiaryStatus(0), status)
      .value
      .set(large.LargeBeneficiaryNamePage(0), string)
      .value
      .set(LargeBeneficiaryStatus(0), status)
      .value

  }

  "CompanyOrEmploymentRelatedPage" must {

    beRetrievable[CompanyOrEmploymentRelatedToAdd](CompanyOrEmploymentRelatedPage)

    beSettable[CompanyOrEmploymentRelatedToAdd](CompanyOrEmploymentRelatedPage)

    beRemovable[CompanyOrEmploymentRelatedToAdd](CompanyOrEmploymentRelatedPage)

    "implement cleanup" when {

      "company selected" when {
        "last of other beneficiary types are in progress" in
          forAll(arbitrary[UserAnswers]) { initial =>
            val answers = initial.setWithStatus(InProgress)

            val result = answers.set(CompanyOrEmploymentRelatedPage, Company).value

            result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 1
            result.get(LargeBeneficiaries).getOrElse(Nil).size   mustBe 0
          }
      }

      "employment-related selected" when {
        "last of other beneficiary types are in progress" in
          forAll(arbitrary[UserAnswers]) { initial =>
            val answers = initial.setWithStatus(InProgress)

            val result = answers.set(CompanyOrEmploymentRelatedPage, EmploymentRelated).value

            result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
            result.get(LargeBeneficiaries).getOrElse(Nil).size   mustBe 1
          }
      }
    }

    "not implement cleanup" when {

      def runTestsForType(beneficiaryType: CompanyOrEmploymentRelatedToAdd): Unit =

        s"$beneficiaryType selected" when {

          "no other beneficiary types" in
            forAll(arbitrary[UserAnswers]) { initial =>
              val result = initial.set(CompanyOrEmploymentRelatedPage, beneficiaryType).value

              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 0
              result.get(LargeBeneficiaries).getOrElse(Nil).size   mustBe 0
            }

          "last of other beneficiary types is complete" in
            forAll(arbitrary[UserAnswers]) { initial =>
              val answers = initial.setWithStatus(Completed)

              val result = answers.set(CompanyOrEmploymentRelatedPage, beneficiaryType).value

              result.get(CompanyBeneficiaries).getOrElse(Nil).size mustBe 1
              result.get(LargeBeneficiaries).getOrElse(Nil).size   mustBe 1
            }
        }

      runTestsForType(Company)
      runTestsForType(EmploymentRelated)
    }
  }

}
