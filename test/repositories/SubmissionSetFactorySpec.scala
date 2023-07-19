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

package repositories

import base.SpecBase
import models.RegistrationSubmission.AnswerSection
import models.Status.Completed
import models.UserAnswers
import pages.entitystatus._

class SubmissionSetFactorySpec extends SpecBase {

  "Submission set factory" must {

    val factory = injector.instanceOf[SubmissionSetFactory]

    "return no answer sections if there are no answers" in {

      factory.answerSections(emptyUserAnswers)
        .mustBe(Nil)
    }

    "return completed answer sections" when {

      "only one beneficiary" must {
        "have 'Beneficiaries' as section key" when {
          "individual beneficiary only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(IndividualBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.individualBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                )
              )
          }

          "class of beneficiary only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(ClassBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.classOfBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                )
              )
          }

          "charity beneficiary only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(CharityBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.charityBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                )
              )
          }

          "trust beneficiary only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(TrustBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.trustBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                )
              )
          }

          "company beneficiary only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(CompanyBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.companyBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                )
              )
          }

          "large beneficiary only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(LargeBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.largeBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                )
              )
          }

          "other beneficiary only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(OtherBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.otherBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                )
              )
          }
        }
      }

      "more than one beneficiary" must {
        "have 'Beneficiaries' as section key of the topmost section" when {
          "individual beneficiary and class of beneficiary" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(IndividualBeneficiaryStatus(0), Completed).value
              .set(ClassBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.individualBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                ),
                AnswerSection(
                  headingKey = Some("answerPage.section.classOfBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = None,
                  headingArgs = Seq("1")
                )
              )
          }

          "class of beneficiary and trust beneficiary" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(ClassBeneficiaryStatus(0), Completed).value
              .set(TrustBeneficiaryStatus(0), Completed).value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.classOfBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.beneficiaries.heading"),
                  headingArgs = Seq("1")
                ),
                AnswerSection(
                  headingKey = Some("answerPage.section.trustBeneficiary.subheading"),
                  rows = Nil,
                  sectionKey = None,
                  headingArgs = Seq("1")
                )
              )
          }
        }
      }

    }
  }

}
